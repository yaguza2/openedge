package nl.openedge.modules.impl.menumodule;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

import javax.security.auth.Subject;
import javax.servlet.ServletContext;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.config.DocumentLoader;
import nl.openedge.modules.types.base.SingletonType;
import nl.openedge.modules.types.initcommands.BeanType;
import nl.openedge.modules.types.initcommands.ConfigurableType;
import nl.openedge.modules.types.initcommands.ServletContextAwareType;

import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The menu module is responsible for building the menu tree from a configuration (xml)
 * document and for applying rules for specific users/ contexts to get the proper menu
 * tree for those users/ contexts.
 * 
 * @author Eelco Hillenius
 */
public final class MenuModule implements SingletonType, BeanType, ConfigurableType,
		ServletContextAwareType
{
	/** and pattern. */
	private static final Pattern PATTERN_AND = Pattern.compile("&");

	/** is pattern. */
	private static final Pattern PATTERN_IS = Pattern.compile("=");

	/** logger. */
	private static Logger log = LoggerFactory.getLogger(MenuModule.class);

	/** the location of the configuration document. */
	private String configLocation;

	/** the current servlet context. */
	private ServletContext servletContext;

	/** the shared menu model of this module instance. */
	private TreeModel menuModel = null;

	/** cache for user model. */
	private Map<Subject, TreeModel> userModelCache = Collections
		.synchronizedMap(new HashMap<Subject, TreeModel>());

	/** context for the current thread. */
	private ThreadLocal<Map<Object, Object>> contextHolder = new ThreadLocal<Map<Object, Object>>();

	/** list of configured application scope filters. */
	private List<MenuFilter> applicationScopedFilters = new ArrayList<MenuFilter>(1);

	/** list of configured session scope filters. */
	private List<MenuFilter> sessionScopedFilters = new ArrayList<MenuFilter>(1);

	/** list of configured request scope filters. */
	private List<MenuFilter> requestScopedFilters = new ArrayList<MenuFilter>(1);

	/**
	 * Whether to use the root (path) as the current path if no path was found based on
	 * the given context. If true, if a path was not found, the first level of menu items
	 * is allways returned. This can be usefull when working with several instances of
	 * this module.
	 */
	private boolean useRootForNullPath = false;

	/** The root menu item. */
	private MenuItem rootMenuItem = null;

	@Override
	public void init(Element configNode) throws ConfigException
	{
		buildTreeModel(true);
	}

	/**
	 * Read filters from the configuration and add them to this module instance.
	 */
	private void addFilters(Element rootElement) throws ConfigException
	{
		applicationScopedFilters.clear();
		sessionScopedFilters.clear();
		requestScopedFilters.clear();

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null)
			classLoader = MenuModule.class.getClassLoader();

		List< ? > filters = rootElement.getChildren("filter");
		for (Iterator< ? > i = filters.iterator(); i.hasNext();)
		{
			Element filterNode = (Element) i.next();
			String className = filterNode.getAttributeValue("class");
			MenuFilter temp = null;
			Class< ? > clazz = null;
			try
			{
				clazz = classLoader.loadClass(className);
				temp = (MenuFilter) clazz.newInstance();
			}
			catch (Exception e)
			{
				log.error(e.getMessage(), e);
				throw new ConfigException(e);
			}

			addAttributes(temp, filterNode);
			if (ApplicationScopeMenuFilter.class.isAssignableFrom(clazz))
			{
				applicationScopedFilters.add(temp);
				log.info(className + " registered as a filter with application scope");
			}
			else if (SessionScopeMenuFilter.class.isAssignableFrom(clazz))
			{
				sessionScopedFilters.add(temp);
				log.info(className + " registered as a filter with session scope");
			}
			else if (RequestScopeMenuFilter.class.isAssignableFrom(clazz))
			{
				requestScopedFilters.add(temp);
				log.info(className + " registered as a filter with request scope");
			}
			else
			{
				throw new ConfigException("filters must be of one of the following types: "
					+ ApplicationScopeMenuFilter.class.getName() + ", "
					+ SessionScopeMenuFilter.class.getName() + " or "
					+ RequestScopeMenuFilter.class.getName());
			}

		}
	}

	/**
	 * Look in cache for tree and build if needed.
	 * 
	 * @param rebuild
	 *            whether the model should be rebuilt
	 * @return TreeModel the tree model
	 * @throws ConfigException
	 *             when the configuration is broken
	 */
	private synchronized TreeModel buildTreeModel(boolean rebuild) throws ConfigException
	{
		if (rebuild || menuModel == null)
		{
			Document doc = null;
			URL configURL = null;
			try
			{
				configURL =
					URLHelper.convertToURL(configLocation, MenuModule.class, servletContext);
			}
			catch (MalformedURLException e)
			{
				log.error(e.getMessage(), e);
				throw new ConfigException(e);
			}
			doc = DocumentLoader.loadDocument(configURL);
			Element rootElement = doc.getRootElement();
			// add filters
			addFilters(rootElement);
			// get classloadder
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			if (classLoader == null)
			{
				classLoader = MenuModule.class.getClassLoader();
			}
			// build model
			this.menuModel = buildTreeModel(rootElement, classLoader);
		}
		return menuModel;
	}

	/**
	 * Build the tree model.
	 */
	private TreeModel buildTreeModel(Element rootElement, ClassLoader classLoader)
			throws ConfigException
	{
		TreeModel model = null;
		// build directory tree, starting with root dir
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
		rootMenuItem = new MenuItem();
		rootMenuItem.setTag("/");
		rootMenuItem.setLink("/");
		rootNode.setUserObject(rootMenuItem);
		Map<Object, Object> ctx = new HashMap<Object, Object>();
		ctx.put(ApplicationScopeMenuFilter.CONTEXT_KEY_CONFIGURATION, rootElement);

		addChilds(rootElement, rootNode, classLoader, ctx);
		model = new DefaultTreeModel(rootNode);

		if (log.isDebugEnabled())
		{
			debugTree(model);
		}
		return model;
	}

	/**
	 * Parse the request parameters and put into a properties object.
	 * 
	 * @param params
	 *            the request parameters as a string
	 * @return the request parameters as a properties object
	 */
	private Map<String, String> parseParameters(String params)
	{
		String[] all = PATTERN_AND.split(params);
		Hashtable<String, String> prop = new Hashtable<String, String>();
		String[] current = null;
		for (int i = 0; i < all.length; i++)
		{
			current = PATTERN_IS.split(all[i], 2);
			if (current.length >= 2)
			{
				prop.put(current[0], current[1]);
			}
			else
			{
				prop.put(current[0], "");
			}
		}
		return prop;
	}

	/**
	 * Add the childs recursively.
	 */
	private void addChilds(Element currentElement, DefaultMutableTreeNode currentNode,
			ClassLoader classLoader, Map<Object, Object> filterContext) throws ConfigException
	{
		List< ? > items = currentElement.getChildren("menu-item");
		if (!items.isEmpty())
		{
			for (Iterator< ? > i = items.iterator(); i.hasNext();)
			{
				Element childElement = (Element) i.next();
				MenuItem childItem = new MenuItem();
				// set tag (label)
				childItem.setTag(childElement.getAttributeValue("tag"));
				// set link
				String link = childElement.getAttributeValue("link");
				int ix = link.indexOf('?'); // strip parameter and ?
				if (ix != -1)
				{
					if (ix < link.length())
					{
						String queryString = link.substring(ix + 1, link.length());
						childItem.addParameters(parseParameters(queryString));
					}
					link = link.substring(0, ix);
				}
				addParameters(childItem, childElement);
				childItem.setLink(link);
				String key = childElement.getAttributeValue("key");
				if (key != null && (!key.trim().equals("")))
				{
					childItem.setShortCutKey(key.trim());
					log.info("key alt + '" + key + "' registered as a short cut key "
						+ childItem.getTag());
				}
				// filter on application scope
				boolean accepted = true;
				for (Iterator<MenuFilter> j = applicationScopedFilters.iterator(); j.hasNext();)
				{
					MenuFilter filter = j.next();
					accepted = filter.accept(childItem, filterContext);
					if (!accepted)
					{
						break;
					}
				}
				if (accepted)
				{
					DefaultMutableTreeNode childNode = new DefaultMutableTreeNode();
					childNode.setUserObject(childItem);
					currentNode.add(childNode);
					((MenuItem) currentNode.getUserObject()).addChild(childItem);

					log.debug("add " + childItem + " to " + currentNode.getUserObject());

					addChilds(childElement, childNode, classLoader, filterContext);
					addAliases(childItem, childElement);
					addNodeLevelFilters(childItem, childElement, classLoader);
					addAttributes(childItem, childElement);
				}
			}
		}
	}

	/**
	 * Add filters of the given node.
	 */
	private void addNodeLevelFilters(MenuItem childItem, Element currentElement,
			ClassLoader classLoader) throws ConfigException
	{
		List< ? > filters = currentElement.getChildren("filter");
		if (!filters.isEmpty())
		{
			List<MenuFilter> nodeFilters = new ArrayList<MenuFilter>();
			for (Iterator< ? > i = filters.iterator(); i.hasNext();)
			{
				Element filterNode = (Element) i.next();
				String className = filterNode.getAttributeValue("class");
				MenuFilter temp = null;
				Class< ? > clazz = null;
				try
				{
					clazz = classLoader.loadClass(className);
					temp = (MenuFilter) clazz.newInstance();
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
					throw new ConfigException(e);
				}
				addAttributes(temp, filterNode);
				if (RequestScopeMenuFilter.class.isAssignableFrom(clazz))
				{
					nodeFilters.add(temp);

					log.debug(className + " registered as a node filter for " + childItem.getTag());
				}
				else
				{
					throw new ConfigException("menu item filters should be of type: "
						+ RequestScopeMenuFilter.class.getName());
				}
			}
			childItem.setFilters(nodeFilters);
		}
	}

	/**
	 * Registers attributes for objects.
	 */
	private void addAttributes(AttributeEnabledObject target, Element currentElement)
	{
		List< ? > attributes = currentElement.getChildren("attribute");
		if (!attributes.isEmpty())
		{
			for (Iterator< ? > i = attributes.iterator(); i.hasNext();)
			{
				Element attribNode = (Element) i.next();
				String attribName = attribNode.getAttributeValue("name");
				String attribValue = attribNode.getTextNormalize();
				target.putAttribute(attribName, attribValue);

				log.debug("attribute " + attribName + "{" + attribValue + "} registered for "
					+ target.getClass().getName());
			}
		}
	}

	/**
	 * Add request parameters.
	 */
	private void addParameters(MenuItem childItem, Element currentElement)
	{
		List< ? > attributes = currentElement.getChildren("parameter");
		if (!attributes.isEmpty())
		{
			for (Iterator< ? > i = attributes.iterator(); i.hasNext();)
			{
				Element attribNode = (Element) i.next();
				String attribName = attribNode.getAttributeValue("name");
				String attribValue = attribNode.getTextNormalize();
				childItem.addParameter(attribName, attribValue);

				log.debug("parameter " + attribName + "{" + attribValue
					+ "} registered as a request parameter for " + childItem.getTag());
			}
		}
	}

	/**
	 * Add aliases for the given item.
	 */
	private void addAliases(MenuItem childItem, Element currentElement)
	{
		List< ? > aliases = currentElement.getChildren("alias");
		if (!aliases.isEmpty())
		{
			HashSet<String> aliasLinks = new HashSet<String>(aliases.size());
			for (Iterator< ? > i = aliases.iterator(); i.hasNext();)
			{
				Element aliasNode = (Element) i.next();
				String aliasLink = aliasNode.getAttributeValue("link");
				boolean check = aliasLinks.add(aliasLink);
				log.info("add alias '" + aliasLink + "' for " + childItem.getTag());
				if (!check)
				{
					log.warn("duplicate alias '" + aliasLink + "' for " + childItem.getTag());
				}
			}
			childItem.setAliases(aliasLinks);
		}
	}

	/**
	 * Get the tree model for the given subject.
	 * 
	 * @param subject
	 *            the subject to get the treemodel for
	 * @return TreeModel the treemodel for the given subject
	 */
	private TreeModel getModelForSubject(Subject subject)
	{
		Map<Object, Object> filterContext = contextHolder.get();
		if (filterContext == null) // fallthrough
		{
			filterContext = new HashMap<Object, Object>();
			contextHolder.set(filterContext);
		}
		filterContext.put(MenuFilter.CONTEXT_KEY_SUBJECT, subject);
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) menuModel.getRoot();
		DefaultMutableTreeNode workNode = new DefaultMutableTreeNode();
		workNode.setUserObject(rootNode.getUserObject());
		TreeModel model = userModelCache.get(subject);
		if (model == null)
		{
			addChildsForSubject(subject, rootNode, workNode, filterContext);
			// working root node is now root of tree for this subject
			model = new DefaultTreeModel(workNode); // model for session
			userModelCache.put(subject, model);
		}
		// build for request
		rootNode = (DefaultMutableTreeNode) model.getRoot();
		workNode = new DefaultMutableTreeNode();
		workNode.setUserObject(rootNode.getUserObject());
		addChildsForRequest(subject, rootNode, workNode, filterContext);
		model = new DefaultTreeModel(workNode); // model voor request
		return model;
	}

	/**
	 * Add childs to the given worknode for the given subject.
	 */
	private void addChildsForSubject(Subject subject, DefaultMutableTreeNode currentNode,
			DefaultMutableTreeNode workNode, Map<Object, Object> filterContext)
	{
		addFilteredChildsForSession(subject, currentNode, workNode, filterContext,
			sessionScopedFilters);
	}

	/**
	 * Add children to a worknode.
	 */
	private void addChildsForRequest(Subject subject, DefaultMutableTreeNode currentNode,
			DefaultMutableTreeNode workNode, Map<Object, Object> filterContext)
	{
		addFilteredChildsForRequest(subject, currentNode, workNode, filterContext,
			requestScopedFilters);
	}

	/**
	 * Add children for the session.
	 */
	private void addFilteredChildsForSession(Subject subject, DefaultMutableTreeNode currentNode,
			DefaultMutableTreeNode workNode, Map<Object, Object> filterContext,
			List<MenuFilter> filters)
	{
		Enumeration< ? > children = currentNode.children();
		while (children.hasMoreElements())
		{
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) children.nextElement();
			MenuItem menuItem = (MenuItem) childNode.getUserObject();
			// filter globally
			boolean accepted = true;
			for (Iterator<MenuFilter> j = filters.iterator(); j.hasNext();)
			{
				MenuFilter filter = j.next();
				accepted = filter.accept(menuItem, filterContext);
				if (!accepted)
				{
					break;
				}
			}

			if (accepted)
			{
				List<MenuFilter> nodeFilters = menuItem.getFilters();
				if (nodeFilters != null)
				{
					for (MenuFilter filter : nodeFilters)
					{
						if (filter instanceof ApplicationScopeMenuFilter
							|| filter instanceof SessionScopeMenuFilter)
						{
							accepted = filter.accept(menuItem, filterContext);
							if (!accepted)
								break;
						}
					}
				}
			}

			if (accepted)
			{
				DefaultMutableTreeNode newWorkNode = new DefaultMutableTreeNode();
				newWorkNode.setUserObject(menuItem);
				// add child
				workNode.add(newWorkNode);
				// recurse
				addFilteredChildsForSession(subject, childNode, newWorkNode, filterContext, filters);
			}
		}
	}

	/**
	 * Add filtered childs to the worknode.
	 */
	private void addFilteredChildsForRequest(Subject subject, DefaultMutableTreeNode currentNode,
			DefaultMutableTreeNode workNode, Map<Object, Object> filterContext,
			List<MenuFilter> filters)
	{
		Enumeration< ? > children = currentNode.children();
		while (children.hasMoreElements())
		{
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) children.nextElement();
			MenuItem menuItem = (MenuItem) childNode.getUserObject();
			// filter globaal
			boolean accepted = true;
			for (Iterator<MenuFilter> j = filters.iterator(); j.hasNext();)
			{
				MenuFilter filter = j.next();
				accepted = filter.accept(menuItem, filterContext);
				if (!accepted)
				{
					break;
				}
			}

			if (accepted)
			{
				List<MenuFilter> nodeFilters = menuItem.getFilters();
				if (nodeFilters != null)
				{
					for (MenuFilter filter : nodeFilters)
					{
						if (filter instanceof RequestScopeMenuFilter)
						{
							accepted = filter.accept(menuItem, filterContext);
							if (!accepted)
								break;
						}
					}
				}
			}

			if (accepted)
			{
				DefaultMutableTreeNode newWorkNode = new DefaultMutableTreeNode();
				newWorkNode.setUserObject(menuItem);
				// add child
				workNode.add(newWorkNode);
				// recurse
				addFilteredChildsForRequest(subject, childNode, newWorkNode, filterContext, filters);
			}
		}
	}

	/**
	 * Get the menu for the root level of the given subject.
	 * 
	 * @param subject
	 *            JAAS subject
	 * @return the menu options UNDER the root level
	 */
	public List<MenuItem>[] getMenuItems(Subject subject)
	{
		return getMenuItems(subject, null);
	}

	/**
	 * Get the menu items for the given subject and the current link.
	 * 
	 * @return List<MenuItem[] menu items (one level) UNDER the provided level.
	 */
	@SuppressWarnings("unchecked")
	public List<MenuItem>[] getMenuItems(Subject subject, String link)
	{
		String currentLink = link;
		Map<Object, Object> filterContext = contextHolder.get();
		if (filterContext == null)
		{
			filterContext = new HashMap<Object, Object>();
			contextHolder.set(filterContext);
		}
		filterContext.put(MenuFilter.CONTEXT_KEY_SUBJECT, subject);
		filterContext.put(MenuFilter.CONTEXT_KEY_REQUEST_FILTERS, requestScopedFilters);
		filterContext.put(MenuFilter.CONTEXT_KEY_SESSION_FILTERS, sessionScopedFilters);

		if (currentLink == null)
			currentLink = "/";

		MenuItem workItem = new MenuItem();
		workItem.setLink(currentLink);
		List<MenuItem>[] items = null;
		TreeStateCache treeState = getTreeState(subject);
		TreePath selection = findSelection(workItem, treeState);

		if (selection != null)
		{
			int depth = selection.getPathCount();
			items = new List[depth];
			for (int i = depth - 1; i >= 0; i--)
			{
				DefaultMutableTreeNode currentNode =
					(DefaultMutableTreeNode) selection.getPathComponent(i);
				workItem = (MenuItem) currentNode.getUserObject();

				int childCount = currentNode.getChildCount();
				if ((i == (depth - 1)) && (childCount == 0))
				{
					items = new List[depth - 1];
				}
				else
				{
					items[i] = new ArrayList<MenuItem>(currentNode.getChildCount());
					Enumeration< ? > children = currentNode.children();
					while (children.hasMoreElements())
					{
						DefaultMutableTreeNode childNode =
							(DefaultMutableTreeNode) children.nextElement();
						MenuItem menu = (MenuItem) childNode.getUserObject();
						MenuItem childItem = clone(menu);
						if (i < (depth - 1))
						{
							DefaultMutableTreeNode temp =
								(DefaultMutableTreeNode) selection.getPathComponent(i + 1);
							if (childNode.equals(temp))
								childItem.setActive(true);
						}
						childItem.applyFiltersOnChildren(filterContext);
						items[i].add(childItem);
					}
				}
			}
		}
		else
			items = new List[0];
		return items;
	}

	/**
	 * Clones a MenuItem.
	 * 
	 * @param menu
	 * @return a clone of the MenuItem
	 */
	public MenuItem clone(MenuItem menu)
	{
		MenuItem clone = new MenuItem();
		clone.setLink(menu.getLink());
		clone.setTag(menu.getTag());
		clone.setEnabled(menu.isEnabled());
		clone.setShortCutKey(menu.getShortCutKey());
		clone.setParameters(menu.getParameters());
		clone.setAttributes(menu.getAttributes());
		clone.setActive(menu.getActive());
		clone.setAliases(menu.getAliases());
		clone.setFilters(menu.getFilters());
		if (menu.getChildren() != null && !menu.getChildren().isEmpty())
		{
			Iterator< ? > it = menu.getChildren().iterator();
			// could be an inifinite loop if a child has its parent as a child
			while (it.hasNext())
				clone.addChild(clone((MenuItem) it.next()));
		}
		return clone;
	}

	/**
	 * Find the path of the item item.
	 * 
	 * @param workItem
	 *            the item to get the path for
	 * @param treeState
	 *            the tree state cache
	 * @return TreePath the path of the given item
	 */
	private TreePath findSelection(MenuItem workItem, TreeStateCache treeState)
	{
		TreePath selection;
		selection = treeState.findTreePath(workItem);
		if (selection == null && (useRootForNullPath))
		{
			selection = treeState.findTreePath(rootMenuItem);
		}
		return selection;
	}

	/**
	 * Get the tree state from cache haal tree state uit cache.
	 * 
	 * @param subject
	 *            JAAS subject
	 * @return TreeStateCache the tree state cache
	 */
	public TreeStateCache getTreeState(Subject subject)
	{
		TreeStateCache treeState = new TreeStateCache();
		TreeModel model = getModelForSubject(subject);
		treeState.setModel(model);
		TreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
		treeState.setSelectionModel(selectionModel);
		treeState.setRootVisible(true);

		return treeState;
	}

	/**
	 * reset the context for this Thread. Users of this library are responsible to calling
	 * this method.
	 */
	public void resetContextForCurrentThread()
	{
		Map<Object, Object> context = contextHolder.get();
		if (context != null)
		{
			context.clear();
		}
		else
		{
			context = new HashMap<Object, Object>();
			contextHolder.set(context);
		}
	}

	/**
	 * remove variable from the filter context.
	 * 
	 * @param key
	 *            key of the variable to remove
	 */
	public void removeFilterContextVariable(Object key)
	{
		Map<Object, Object> context = contextHolder.get();
		if (context != null)
			context.remove(key);
	}

	/**
	 * Save context variable with the given key.
	 * 
	 * @param key
	 *            the key of the context varaible
	 * @param value
	 *            the value of the context variable
	 */
	public void putFilterContextVariable(Object key, Object value)
	{
		Map<Object, Object> context = contextHolder.get();
		if (context == null)
		{
			context = new HashMap<Object, Object>();
			contextHolder.set(context);
		}
		context.put(key, value);
	}

	/**
	 * Get the context variable with the given key.
	 * 
	 * @param key
	 *            the key of the context variable
	 * @return Object or null if not found.
	 */
	public Object getFilterContextVariable(Object key)
	{
		Map<Object, Object> context = contextHolder.get();
		if (context != null)
		{
			return context.get(key);
		}
		else
		{
			return null;
		}
	}

	/**
	 * Get the configuration location.
	 * 
	 * @return String the configuration location
	 */
	public String getConfigLocation()
	{
		return configLocation;
	}

	/**
	 * Set the configuration location.
	 * 
	 * @param configLocation
	 *            the configuration location
	 */
	public void setConfigLocation(String configLocation)
	{
		this.configLocation = configLocation;
	}

	@Override
	public void setServletContext(ServletContext servletContext)
	{
		this.servletContext = servletContext;
	}

	/**
	 * Debug the given tree.
	 * 
	 * @param treeModel
	 *            the tree model to debug
	 */
	private void debugTree(TreeModel treeModel)
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeModel.getRoot();
		Enumeration< ? > en = node.breadthFirstEnumeration();
		en = node.preorderEnumeration();
		log.debug("-- MENU TREE DUMP --");
		while (en.hasMoreElements())
		{
			DefaultMutableTreeNode nd = (DefaultMutableTreeNode) en.nextElement();
			String tabs = "|";
			for (int i = 0; i < nd.getLevel(); i++)
				tabs += "\t";
			log.debug(tabs + nd);
		}
		log.debug("-------------------");
	}

	/**
	 * Get whether to use the root (path) as the current path if no path was found based
	 * on the given context. If true, if a path was not found, the first level of menu
	 * items is allways returned. This can be usefull when working with several instances
	 * of this module..
	 * 
	 * @return whether to use the root (path) as the current path if no path was found
	 *         based on the given context.
	 */
	public boolean isUseRootForNullPath()
	{
		return useRootForNullPath;
	}

	/**
	 * Set whether to use the root (path) as the current path if no path was found based
	 * on the given context. If true, if a path was not found, the first level of menu
	 * items is allways returned. This can be usefull when working with several instances
	 * of this module..
	 * 
	 * @param useRootForNullPath
	 *            whether to use the root (path) as the current path if no path was found
	 *            based on the given context.
	 */
	public void setUseRootForNullPath(boolean useRootForNullPath)
	{
		this.useRootForNullPath = useRootForNullPath;
	}
}