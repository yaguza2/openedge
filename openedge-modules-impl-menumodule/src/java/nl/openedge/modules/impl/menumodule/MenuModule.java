/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003, Open Edge B.V.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions 
 * in binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution. Neither the name of OpenEdge B.V. 
 * nor the names of its contributors may be used to endorse or promote products 
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package nl.openedge.modules.impl.menumodule;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;

/**
 * The menu module is responsible for building the menu tree from a configuration (xml) document and
 * for applying rules for specific users/ contexts to get the proper menu tree for those users/
 * contexts.
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
	private static Log log = LogFactory.getLog(MenuModule.class);

	/** the location of the configuration document. */
	private String configLocation;

	/** the current servlet context. */
	private ServletContext servletContext;

	/** the shared menu model of this module instance. */
	private TreeModel menuModel = null;

	/** cache for user model. */
	private Map userModelCache = new HashMap();

	/** context for the current thread. */
	private ThreadLocal contextHolder = new ThreadLocal();

	/** list of configured application scope filters. */
	private List applicationScopedFilters = new ArrayList(1);

	/** list of configured session scope filters. */
	private List sessionScopedFilters = new ArrayList(1);

	/** list of configured request scope filters. */
	private List requestScopedFilters = new ArrayList(1);

	/**
	 * Whether to use the root (path) as the current path if no path was found based on the given
	 * context. If true, if a path was not found, the first level of menu items is allways returned.
	 * This can be usefull when working with several instances of this module.
	 */
	private boolean useRootForNullPath = false;

	/** The root menu item. */
	private MenuItem rootMenuItem = null;

	/**
	 * @see nl.openedge.components.ConfigurableType#init(org.jdom.Element)
	 */
	public void init(Element configNode) throws ConfigException
	{
		buildTreeModel(true);
	}

	/**
	 * Read filters from the configuration and add them to this module instance.
	 * 
	 * @param rootElement
	 *            root element of menu xml document
	 * @throws ConfigException
	 *             when the configuration is broken
	 */
	private void addFilters(Element rootElement) throws ConfigException
	{
		applicationScopedFilters.clear();
		sessionScopedFilters.clear();
		requestScopedFilters.clear();

		List filters = rootElement.getChildren("filter");
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null)
		{
			classLoader = MenuModule.class.getClassLoader();
		}

		for (Iterator i = filters.iterator(); i.hasNext();)
		{
			Element filterNode = (Element) i.next();
			String className = filterNode.getAttributeValue("class");
			MenuFilter temp = null;
			Class clazz = null;
			try
			{
				clazz = classLoader.loadClass(className);
				temp = (MenuFilter) clazz.newInstance();
			}
			catch (ClassNotFoundException e)
			{
				log.error(e.getMessage(), e);
				throw new ConfigException(e);
			}
			catch (InstantiationException e)
			{
				log.error(e.getMessage(), e);
				throw new ConfigException(e);
			}
			catch (IllegalAccessException e)
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
				configURL = URLHelper
						.convertToURL(configLocation, MenuModule.class, servletContext);
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
	 * 
	 * @param rootElement
	 *            config root element
	 * @param classLoader
	 *            the class loader to use
	 * @return TreeModel the tree model
	 * @throws ConfigException
	 *             when the configuration is broken
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
		Map ctx = new HashMap();
		ctx.put(ApplicationScopeMenuFilter.CONTEXT_KEY_CONFIGURATION, rootElement);
		// add the childs
		addChilds(rootElement, rootNode, classLoader, ctx);
		model = new DefaultTreeModel(rootNode);
		// debug tree if debug enabled
		if (log.isDebugEnabled())
		{
			debugTree(model);
		}
		return model;
	}

	/**
	 * Parse the request paramters and put into a properties object.
	 * 
	 * @param params
	 *            the request parameters as a string
	 * @return the request parameters as a properties object
	 */
	private Properties parseParameters(String params)
	{
		String[] all = PATTERN_AND.split(params);
		Properties prop = new Properties();
		String[] current = null;
		for (int i = 0; i < all.length; i++)
		{
			current = PATTERN_IS.split(all[i], 2);
			if (current.length >= 2)
			{
				prop.setProperty(current[0], current[1]);
			}
			else
			{
				prop.setProperty(current[0], "");
			}
		}
		return prop;
	}

	/**
	 * Add the childs recursively.
	 * 
	 * @param currentElement
	 *            the current element
	 * @param currentNode
	 *            the current node
	 * @param classLoader
	 *            the classloader to use
	 * @param filterContext
	 *            the current filter context
	 * @throws ConfigException
	 *             when the configuration is broken
	 */
	private void addChilds(Element currentElement, DefaultMutableTreeNode currentNode,
			ClassLoader classLoader, Map filterContext) throws ConfigException
	{
		List items = currentElement.getChildren("menu-item");
		if (!items.isEmpty())
		{
			for (Iterator i = items.iterator(); i.hasNext();)
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
					log.info("key alt + '"
							+ key + "' registered as a short cut key " + childItem.getTag());
				}
				// filter on application scope
				boolean accepted = true;
				for (Iterator j = applicationScopedFilters.iterator(); j.hasNext();)
				{
					MenuFilter filter = (MenuFilter) j.next();
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

					if (log.isDebugEnabled())
					{
						log.debug("add " + childItem + " to " + currentNode.getUserObject());
					}
					addChilds(childElement, childNode, classLoader, filterContext);
					// add aliases
					addAliases(childItem, childElement, childNode, classLoader, filterContext);
					// add filters
					addNodeLevelFilters(childItem, childElement, childNode, classLoader,
							filterContext);
					// add attributes
					addAttributes(childItem, childElement);
				}
			}
		}
	}

	/**
	 * Add filters of the given node.
	 * 
	 * @param childItem
	 *            the node
	 * @param currentElement
	 *            the xml node of the current node
	 * @param currentNode
	 *            the tree node for the node
	 * @param classLoader
	 *            the classloader to use
	 * @param filterContext
	 *            the current filter context
	 * @throws ConfigException
	 *             when the configuration is broken
	 */
	private void addNodeLevelFilters(MenuItem childItem, Element currentElement,
			DefaultMutableTreeNode currentNode, ClassLoader classLoader, Map filterContext)
			throws ConfigException
	{
		List filters = currentElement.getChildren("filter");
		if (!filters.isEmpty())
		{
			List nodeFilters = new ArrayList();
			for (Iterator i = filters.iterator(); i.hasNext();)
			{
				Element filterNode = (Element) i.next();
				String className = filterNode.getAttributeValue("class");
				MenuFilter temp = null;
				Class clazz = null;
				try
				{
					clazz = classLoader.loadClass(className);
					temp = (MenuFilter) clazz.newInstance();
				}
				catch (ClassNotFoundException e)
				{
					log.error(e.getMessage(), e);
					throw new ConfigException(e);
				}
				catch (InstantiationException e)
				{
					log.error(e.getMessage(), e);
					throw new ConfigException(e);
				}
				catch (IllegalAccessException e)
				{
					log.error(e.getMessage(), e);
					throw new ConfigException(e);
				}
				addAttributes(temp, filterNode);
				if (RequestScopeMenuFilter.class.isAssignableFrom(clazz))
				{
					nodeFilters.add(temp);
					if (log.isDebugEnabled())
					{
						log.debug(className
								+ " registered as a node filter for " + childItem.getTag());
					}
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
	 * 
	 * @param target
	 *            object to add attributes
	 * @param currentElement
	 *            the xml node
	 */
	private void addAttributes(AttributeEnabledObject target, Element currentElement)
	{
		List attributes = currentElement.getChildren("attribute");
		if (!attributes.isEmpty())
		{
			for (Iterator i = attributes.iterator(); i.hasNext();)
			{
				Element attribNode = (Element) i.next();
				String attribName = attribNode.getAttributeValue("name");
				String attribValue = attribNode.getTextNormalize();
				target.putAttribute(attribName, attribValue);
				if (log.isDebugEnabled())
				{
					log.debug("attribute "
							+ attribName + "{" + attribValue + "} registered for "
							+ target.getClass().getName());
				}
			}
		}
	}

	/**
	 * Add request parameters.
	 * 
	 * @param childItem
	 *            the item to add the parameters to
	 * @param currentElement
	 *            the xml node
	 */
	private void addParameters(MenuItem childItem, Element currentElement)
	{
		List attributes = currentElement.getChildren("parameter");
		if (!attributes.isEmpty())
		{
			for (Iterator i = attributes.iterator(); i.hasNext();)
			{
				Element attribNode = (Element) i.next();
				String attribName = attribNode.getAttributeValue("name");
				String attribValue = attribNode.getTextNormalize();
				childItem.addParameter(attribName, attribValue);
				if (log.isDebugEnabled())
				{
					log.debug("parameter "
							+ attribName + "{" + attribValue
							+ "} registered as a request parameter for " + childItem.getTag());
				}
			}
		}
	}

	/**
	 * Add aliases for the given item.
	 * 
	 * @param childItem
	 *            the item
	 * @param currentElement
	 *            the xml node of the item
	 * @param currentNode
	 *            the tree node of the item
	 * @param classLoader
	 *            the classloader to use
	 * @param filterContext
	 *            the current filter context
	 * @throws Exception
	 */
	private void addAliases(MenuItem childItem, Element currentElement,
			DefaultMutableTreeNode currentNode, ClassLoader classLoader, Map filterContext)
	{
		List aliases = currentElement.getChildren("alias");
		if (!aliases.isEmpty())
		{
			HashSet aliasLinks = new HashSet(aliases.size());
			for (Iterator i = aliases.iterator(); i.hasNext();)
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
	private synchronized TreeModel getModelForSubject(Subject subject)
	{
		Map filterContext = (Map) contextHolder.get();
		if (filterContext == null) // fallthrough
		{
			filterContext = new HashMap();
			contextHolder.set(filterContext);
		}
		filterContext.put(MenuFilter.CONTEXT_KEY_SUBJECT, subject);
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) menuModel.getRoot();
		DefaultMutableTreeNode workNode = new DefaultMutableTreeNode();
		workNode.setUserObject(rootNode.getUserObject());
		TreeModel model = (TreeModel) userModelCache.get(subject);
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
	 * 
	 * @param subject
	 *            the subject
	 * @param currentNode
	 *            the treenode
	 * @param workNode
	 *            the worknode to add childs to
	 * @param filterContext
	 *            the current filter context
	 */
	private void addChildsForSubject(Subject subject, DefaultMutableTreeNode currentNode,
			DefaultMutableTreeNode workNode, Map filterContext)
	{
		addFilteredChildsForSession(subject, currentNode, workNode, filterContext,
				sessionScopedFilters);
	}

	/**
	 * Add childs to a worknode.
	 * 
	 * @param subject
	 *            the subject
	 * @param currentNode
	 *            the current tree node
	 * @param workNode
	 *            the worknode to add childs to
	 * @param filterContext
	 *            context for filters
	 */
	private void addChildsForRequest(Subject subject, DefaultMutableTreeNode currentNode,
			DefaultMutableTreeNode workNode, Map filterContext)
	{
		addFilteredChildsForRequest(subject, currentNode, workNode, filterContext,
				requestScopedFilters);
	}

	/**
	 * Add childs for the session.
	 * 
	 * @param subject
	 *            the subject
	 * @param currentNode
	 *            the current node
	 * @param workNode
	 *            the working copy of a node
	 * @param filterContext
	 *            the current filter context
	 * @param filters
	 *            list of filters to (possibly) apply
	 */
	private void addFilteredChildsForSession(Subject subject, DefaultMutableTreeNode currentNode,
			DefaultMutableTreeNode workNode, Map filterContext, List filters)
	{
		Enumeration children = currentNode.children();
		while (children.hasMoreElements())
		{
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) children.nextElement();
			MenuItem menuItem = (MenuItem) childNode.getUserObject();
			// filter globally
			boolean accepted = true;
			for (Iterator j = filters.iterator(); j.hasNext();)
			{
				MenuFilter filter = (MenuFilter) j.next();
				accepted = filter.accept(menuItem, filterContext);
				if (!accepted)
				{
					break;
				}
			}

			if (accepted)
			{
				List nodeFilters = menuItem.getFilters();
				if (nodeFilters != null)
				{
					for (Iterator k = nodeFilters.iterator(); k.hasNext();)
					{
						MenuFilter filter = (MenuFilter) k.next();
						if (filter instanceof ApplicationScopeMenuFilter
								|| filter instanceof SessionScopeMenuFilter)
						{
							accepted = filter.accept(menuItem, filterContext);
							if (!accepted)
							{
								break;
							}
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
	 * 
	 * @param subject
	 *            the subject
	 * @param currentNode
	 *            the current node
	 * @param workNode
	 *            the working copy of a node
	 * @param filterContext
	 *            the current filter context
	 * @param filters
	 *            the filters
	 */
	private void addFilteredChildsForRequest(Subject subject, DefaultMutableTreeNode currentNode,
			DefaultMutableTreeNode workNode, Map filterContext, List filters)
	{
		Enumeration children = currentNode.children();
		while (children.hasMoreElements())
		{
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) children.nextElement();
			MenuItem menuItem = (MenuItem) childNode.getUserObject();
			// filter globaal
			boolean accepted = true;
			for (Iterator j = filters.iterator(); j.hasNext();)
			{
				MenuFilter filter = (MenuFilter) j.next();
				accepted = filter.accept(menuItem, filterContext);
				if (!accepted)
				{
					break;
				}
			}

			if (accepted)
			{
				List nodeFilters = menuItem.getFilters();
				if (nodeFilters != null)
				{
					for (Iterator k = nodeFilters.iterator(); k.hasNext();)
					{
						MenuFilter filter = (MenuFilter) k.next();
						if (filter instanceof RequestScopeMenuFilter)
						{
							accepted = filter.accept(menuItem, filterContext);
							if (!accepted)
							{
								break;
							}
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
	public List[] getMenuItems(Subject subject)
	{
		return getMenuItems(subject, null);
	}

	/**
	 * Get the menu items for the given subject and the current link.
	 * 
	 * @param subject
	 *            jaas subject
	 * @param link
	 *            the link
	 * @return List[] menuoptions (one level) UNDER the proided level.
	 */
	public List[] getMenuItems(Subject subject, String link)
	{
		String currentLink = link;
		Map filterContext = (Map) contextHolder.get();
		if (filterContext == null)
		{
			filterContext = new HashMap();
			contextHolder.set(filterContext);
		}
		filterContext.put(MenuFilter.CONTEXT_KEY_SUBJECT, subject);
		filterContext.put(MenuFilter.CONTEXT_KEY_REQUEST_FILTERS, requestScopedFilters);
		filterContext.put(MenuFilter.CONTEXT_KEY_SESSION_FILTERS, sessionScopedFilters);
		if (currentLink == null)
		{
			currentLink = "/";
		}

		MenuItem workItem = new MenuItem();
		workItem.setLink(currentLink);
		List[] items = null;
		TreeStateCache treeState = getTreeState(subject);
		TreePath selection = findSelection(workItem, treeState);

		if (selection != null)
		{
			int depth = selection.getPathCount();
			items = new List[depth];
			for (int i = depth - 1; i >= 0; i--)
			{
				DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) selection
						.getPathComponent(i);
				workItem = (MenuItem) currentNode.getUserObject();

				int childCount = currentNode.getChildCount();
				if ((i == (depth - 1)) && (childCount == 0))
				{
					items = new List[depth - 1];
				}
				else
				{
					items[i] = new ArrayList(currentNode.getChildCount());
					Enumeration children = currentNode.children();
					while (children.hasMoreElements())
					{
						DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) children
								.nextElement();
						MenuItem menu = (MenuItem) childNode.getUserObject();
						MenuItem childItem = clone(menu);
						if (i < (depth - 1))
						{
							DefaultMutableTreeNode temp = (DefaultMutableTreeNode) selection
									.getPathComponent(i + 1);
							if (childNode.equals(temp))
							{
								childItem.setActive(true);
							}
						}
						childItem.applyFiltersOnChildren(filterContext);
						items[i].add(childItem);
					}
				}
			}
		}
		else
		{
			items = new List[0];
		}
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
			Iterator it = menu.getChildren().iterator();
			//could be an inifinite loop if a child has its parent as a child
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
	 *            jaas subject
	 * @return TreeStateCache the tree state cache
	 */
	public TreeStateCache getTreeState(Subject subject)
	{
		//TreeStateCache treeState =
		// (TreeStateCache)userStateCache.get(subject);
		TreeStateCache treeState = null;
		if (treeState == null)
		{
			treeState = new TreeStateCache();
			TreeModel model = getModelForSubject(subject);
			treeState.setModel(model);
			TreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
			treeState.setSelectionModel(selectionModel);
			treeState.setRootVisible(true);

			//userStateCache.put(subject, treeState);
		}
		return treeState;
	}

	/**
	 * reset the context for this Thread. Users of this library are responsible to calling this
	 * method.
	 */
	public void resetContextForCurrentThread()
	{
		Map context = (Map) contextHolder.get();
		if (context != null)
		{
			context.clear();
		}
		else
		{
			context = new HashMap();
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
		Map context = (Map) contextHolder.get();
		if (context != null)
		{
			context.remove(key);
		}
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
		Map context = (Map) contextHolder.get();
		if (context == null)
		{
			context = new HashMap();
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
		Map context = (Map) contextHolder.get();
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

	/**
	 * @see nl.openedge.modules.types.initcommands.ServletContextAwareType#setServletContext(javax.servlet.ServletContext)
	 */
	public void setServletContext(ServletContext servletContext) throws ConfigException
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
		Enumeration enum = node.breadthFirstEnumeration();
		enum = node.preorderEnumeration();
		log.debug("-- MENU TREE DUMP --");
		while (enum.hasMoreElements())
		{
			DefaultMutableTreeNode nd = (DefaultMutableTreeNode) enum.nextElement();
			String tabs = "|";
			for (int i = 0; i < nd.getLevel(); i++)
				tabs += "\t";
			log.debug(tabs + nd);
		}
		log.debug("-------------------");
	}

	/**
	 * Get whether to use the root (path) as the current path if no path was found based on the
	 * given context. If true, if a path was not found, the first level of menu items is allways
	 * returned. This can be usefull when working with several instances of this module..
	 * 
	 * @return whether to use the root (path) as the current path if no path was found based on the
	 *         given context.
	 */
	public boolean isUseRootForNullPath()
	{
		return useRootForNullPath;
	}

	/**
	 * Set whether to use the root (path) as the current path if no path was found based on the
	 * given context. If true, if a path was not found, the first level of menu items is allways
	 * returned. This can be usefull when working with several instances of this module..
	 * 
	 * @param useRootForNullPath
	 *            whether to use the root (path) as the current path if no path was found based on
	 *            the given context.
	 */
	public void setUseRootForNullPath(boolean useRootForNullPath)
	{
		this.useRootForNullPath = useRootForNullPath;
	}
}