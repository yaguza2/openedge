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
import java.util.Set;

import javax.security.auth.Subject;
import javax.servlet.ServletContext;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;

import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.config.DocumentLoader;
import nl.openedge.modules.types.base.SingletonType;
import nl.openedge.modules.types.initcommands.BeanType;
import nl.openedge.modules.types.initcommands.ConfigurableType;
import nl.openedge.modules.types.initcommands.ServletContextAwareType;
import nl.openedge.util.tree.TreeStateCache;

/**
 * De menumodule is verantwoordelijk voor het opbouwen van de menu-opties
 * vanuit de configuratie, en het uitdelen van menu-opties (per niveau)
 * voor specifieke gebruikers.
 * 
 * @author Eelco Hillenius
 */
public final class MenuModule 
	implements SingletonType, BeanType, ConfigurableType, ServletContextAwareType
{

	/** principal sets that hold combination of principals en menu items */
	private List principalSets = new ArrayList();
	
	private String configLocation;
	
	private ServletContext servletContext;
	
	private TreeModel menuModel = null;

	/* logger */
	private static Log log = LogFactory.getLog(MenuModule.class);
	
	/*
	 * @TODO: zorg ervoor dat nadat sessies zijn verlopen, cache elementen worden verwijderd
	 */
	private static Map userModelCache = new HashMap();
	//private static Map userStateCache = new HashMap();
	private static Set sessionScopedFiltersApplied = new HashSet();
	
	private static ThreadLocal contextHolder = new ThreadLocal();
	
	private List applicationScopedFilters = new ArrayList(1);
	private List sessionScopedFilters = new ArrayList(1);
	private List requestScopedFilters = new ArrayList(1);

	/**
	 * @see nl.openedge.components.ConfigurableType#init(org.jdom.Element)
	 */
	public void init(Element configNode) throws ConfigException
	{
		try
		{	
			buildTreeModel(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new ConfigException(e);
		}
	}
	
	/**
	 * lees filters uit configuratie en voeg toe aan lijsten
	 * @param rootElement root element menu xml document
	 * @throws Exception
	 */
	private void addFilters(Element rootElement)
		throws Exception
	{
		
		applicationScopedFilters.clear();
		sessionScopedFilters.clear();
		requestScopedFilters.clear();
		
		List filters = rootElement.getChildren("filter");
		ClassLoader classLoader = 
			Thread.currentThread().getContextClassLoader();	
		if (classLoader == null)
		{
			classLoader = MenuModule.class.getClassLoader();
		}
		
		for(Iterator i = filters.iterator(); i.hasNext(); )
		{
			Element filterNode = (Element)i.next();
			String className = filterNode.getAttributeValue("class");
			Class clazz = classLoader.loadClass(className);
			
			if(ApplicationScopeMenuFilter.class.isAssignableFrom(clazz))
			{
				applicationScopedFilters.add(clazz.newInstance());
				log.info(className + " geregistreerd als als filter met applicatie scope");
			}
			else if(SessionScopeMenuFilter.class.isAssignableFrom(clazz))
			{
				sessionScopedFilters.add(clazz.newInstance());
				log.info(className + " geregistreerd als als filter met sessie scope");				
			}
			else if(RequestScopeMenuFilter.class.isAssignableFrom(clazz))
			{
				requestScopedFilters.add(clazz.newInstance());
				log.info(className + " geregistreerd als als filter met request scope");				
			}
			else
			{
				throw new ConfigException(
					"filters moeten van een van de volgende types zijn: " +
					ApplicationScopeMenuFilter.class.getName() + ", " +
					SessionScopeMenuFilter.class.getName() + " of " + 
					RequestScopeMenuFilter.class.getName());
			}
			
		}
		
	}
	
	/**
	 * bouw tree, kijk in cache of het nodig is
	 * @param rebuild indien waar, bouw altijd
	 * @return TreeModel
	 * @throws Exception
	 */
	private synchronized TreeModel buildTreeModel(
		boolean rebuild)
		throws Exception
	{

		if (rebuild || menuModel == null)
		{
			Document doc = null;
			URL configURL = URLHelper.convertToURL(configLocation, 
				MenuModule.class, servletContext);
			doc = DocumentLoader.loadDocument(configURL);
			Element rootElement = doc.getRootElement();
			
			addFilters(rootElement);
			
			ClassLoader classLoader = 
				Thread.currentThread().getContextClassLoader();	
			if (classLoader == null)
			{
				classLoader = MenuModule.class.getClassLoader();
			}
			
			this.menuModel = buildTreeModel(rootElement, classLoader);
		}
		return menuModel;
	}

	/**
	 * bouw tree model
	 * @param rootElement
	 * @param classLoader
	 * @return TreeModel
	 * @throws Exception
	 */
	private TreeModel buildTreeModel(
		Element rootElement,
		ClassLoader classLoader) 
		throws Exception
	{
		
		TreeModel model = null;
		// build directory tree, starting with root dir
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
		MenuItem rootMenuItem = new MenuItem();
		rootMenuItem.setTag("/");
		rootMenuItem.setLink("/");
		
		rootNode.setUserObject(rootMenuItem);

		Map ctx = new HashMap();
		ctx.put(ApplicationScopeMenuFilter.CONTEXT_KEY_CONFIGURATION, rootElement);
		
		addChilds(rootElement, rootNode, classLoader, ctx);
		model = new DefaultTreeModel(rootNode);
		
		if(log.isDebugEnabled())
		{
			debugTree(model);
		}
		
		return model;
	}
	
	/* voeg childs recursief toe */
	private void addChilds(
		Element currentElement,
		DefaultMutableTreeNode currentNode,
		ClassLoader classLoader,
		Map filterContext)
		throws Exception
	{

		// voeg childs toe
		List items = currentElement.getChildren("menu-item");
		if(!items.isEmpty())
		{
			
			for(Iterator i = items.iterator(); i.hasNext(); )
			{
				Element childElement = (Element)i.next();
				MenuItem childItem = new MenuItem();
				
				// zet tag (label)
				childItem.setTag(childElement.getAttributeValue("tag"));
				
				// zet link
				String link = childElement.getAttributeValue("link");
				int ix = link.indexOf('?'); // strip parameter en ?
				if(ix != -1)
				{
					if(ix < link.length())
					{
						String queryString = link.substring(ix+1, link.length());
						childItem.setQueryString(queryString);	
					}
					link = link.substring(0, ix);
				}
				childItem.setLink(link);
	
				String key = childElement.getAttributeValue("key");
				if(key != null && (!key.trim().equals("")))
				{
					childItem.setShortCutKey(key.trim());
					log.info("sneltoets alt + '" + key + 
						"' geregistreerd als sneltoets voor " + 
						childItem.getTag());	
				}

				// filter op applicatie scope
				boolean accepted = true;
				for(Iterator j = applicationScopedFilters.iterator(); j.hasNext(); )
				{
					MenuFilter filter = (MenuFilter)j.next();
					accepted = filter.accept(childItem, filterContext);
					if(!accepted)
					{
						break;			
					}
				}

				if(accepted)
				{					
					DefaultMutableTreeNode childNode = new DefaultMutableTreeNode();
					childNode.setUserObject(childItem);
					currentNode.add(childNode);		

					if (log.isDebugEnabled())
					{
						log.debug("voeg " + childItem + " toe aan " 
									+ currentNode.getUserObject());	
					}
					addChilds(childElement, childNode, classLoader, filterContext);
									
					addAliases(
						childItem, childElement, childNode, classLoader, filterContext);
										
					addNodeLevelFilters(
						childItem, childElement, childNode, classLoader, filterContext);					
				}
			}
		}
	}
	
	/* voeg filters voor node toe (nooit voor root) */
	private void addNodeLevelFilters(
		MenuItem childItem,
		Element currentElement,
		DefaultMutableTreeNode currentNode,
		ClassLoader classLoader,
		Map filterContext)
		throws Exception
	{
		
		// voeg filters voor node toe indien aanwezig
		List filters = currentElement.getChildren("filter");
		if(!filters.isEmpty())
		{
			List nodeFilters = new ArrayList();
			for(Iterator i = filters.iterator(); i.hasNext(); )
			{
				Element filterNode = (Element)i.next();
				String className = filterNode.getAttributeValue("class");
				Class clazz = classLoader.loadClass(className);
			
				if(RequestScopeMenuFilter.class.isAssignableFrom(clazz))
				{
					nodeFilters.add(clazz.newInstance());
					if(log.isDebugEnabled())
					{
						log.debug(className + 
							" geregistreerd als een node filter voor " + 
							childItem.getTag());	
					}				
				}
				else
				{
					throw new ConfigException(
						"filters bij een menu node moeten van het volgende type zijn: " + 
						RequestScopeMenuFilter.class.getName());
				}	
			}
			childItem.setFilters(nodeFilters);			
		}

	}
	
	/* voeg aliases voor menu items toe (nooit voor root) */
	private void addAliases(
		MenuItem childItem,
		Element currentElement,
		DefaultMutableTreeNode currentNode,
		ClassLoader classLoader,
		Map filterContext)
		throws Exception
	{
		// voeg filters voor node toe indien aanwezig
		List aliases = currentElement.getChildren("alias");
		if(!aliases.isEmpty())
		{
			HashSet aliasLinks = new HashSet(aliases.size());
			for(Iterator i = aliases.iterator(); i.hasNext(); )
			{
				Element aliasNode = (Element)i.next();
				String aliasLink = aliasNode.getAttributeValue("link");
				boolean check = aliasLinks.add(aliasLink);
				log.info("voeg alias '" + aliasLink + "' voor " + childItem.getTag());
				if(!check)
				{
					log.warn("duplicaat alias '" + aliasLink + 
						"' voor " + childItem.getTag());
				}
			}
			childItem.setAliases(aliasLinks);	
		}

	}

	/* model voor subject */
	private synchronized TreeModel getModelForSubject(Subject subject)
	{
		Map filterContext = (Map)contextHolder.get();
		if(filterContext == null) // fallthrough (handig voor buiten webapp framework)
		{
			filterContext = new HashMap();
			contextHolder.set(filterContext);
		}
		filterContext.put(MenuFilter.CONTEXT_KEY_SUBJECT, subject);
		
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)menuModel.getRoot();
		DefaultMutableTreeNode workNode = new DefaultMutableTreeNode();
		workNode.setUserObject(rootNode.getUserObject());
		
		// opbouw voor sessie
		TreeModel model = (TreeModel)userModelCache.get(subject);
		if(model == null)
		{
			// vul tree mbv subject
			addChildsForSubject(subject, rootNode, workNode, filterContext);
			// workrootnode is nu root van tree speciaal voor subject;
			// zet model op met deze node
			model = new DefaultTreeModel(workNode); // model voor session
			userModelCache.put(subject, model);
		}
		
		// opbouw voor request
		rootNode = (DefaultMutableTreeNode)model.getRoot();
		workNode = new DefaultMutableTreeNode();
		workNode.setUserObject(rootNode.getUserObject());
		addChildsForRequest(subject, rootNode, workNode, filterContext);
		model = new DefaultTreeModel(workNode); // model voor request
		
		return model;
	}
	
	/* voeg childs to aan worknode voor subject */
	private void addChildsForSubject(
		Subject subject, 
		DefaultMutableTreeNode currentNode,
		DefaultMutableTreeNode workNode,
		Map filterContext)
	{
		addFilteredChilds(subject, currentNode, workNode, filterContext, sessionScopedFilters);
	}
	
	/* voeg childs to aan worknode voor request */
	private void addChildsForRequest(
		Subject subject, 
		DefaultMutableTreeNode currentNode,
		DefaultMutableTreeNode workNode,
		Map filterContext)
	{
		
		addFilteredChilds(subject, currentNode, workNode, filterContext, requestScopedFilters);
	}
	
	/* voeg childs to aan worknode voor filter */
	private void addFilteredChilds(
		Subject subject, 
		DefaultMutableTreeNode currentNode,
		DefaultMutableTreeNode workNode,
		Map filterContext,
		List filters)
	{
		
		Enumeration children = currentNode.children();
		while(children.hasMoreElements())
		{
			DefaultMutableTreeNode childNode = 
				(DefaultMutableTreeNode)children.nextElement();
			MenuItem menuItem = (MenuItem)childNode.getUserObject();
			
			// filter globaal
			boolean accepted = true;
			for(Iterator j = filters.iterator(); j.hasNext(); )
			{
				MenuFilter filter = (MenuFilter)j.next();
				accepted = filter.accept(menuItem, filterContext);
				if(!accepted)
				{
					break;			
				}
			}

			// filter per node indien aanwezig en voorgaande filtering is geslaagd
			if(accepted) // filtering is gelukt
			{
				List nodeFilters = menuItem.getFilters();
				if(nodeFilters != null)
				{
					for(Iterator k = nodeFilters.iterator(); k.hasNext(); )
					{
						MenuFilter filter = (MenuFilter)k.next();
						accepted = filter.accept(menuItem, filterContext);
						if(!accepted)
						{
							break;			
						}
					}	
				}	
			}
			
			if(accepted) // filtering is gelukt
			{
				DefaultMutableTreeNode newWorkNode = 
					new DefaultMutableTreeNode();
				newWorkNode.setUserObject(menuItem);
				// voeg child toe
				workNode.add(newWorkNode);
				// recurse
				addFilteredChilds(subject, childNode, newWorkNode, filterContext, filters);	
			}
			
		}
	}

	/**
	 * haal menu op voor root level voor gegeven subject
	 * @param subject jaas subject
	 * @return List de menuopties die ONDER de root level hangen
	 */
	public List[] getMenuItems(Subject subject)
	{
		return getMenuItems(subject, null);
	}
	
	/**
	 * haal menu op vanaf de kinderen van het huidig geselecteerde menu item voor
	 * het gegeven subject
	 * @param subject jaas subject
	 * @param idCurrentLevel id huidge menu level of null voor root
	 * @return List[] array van de menuopties die ONDER de gegeven level hangen; 1 niveau
	 */
	public List[] getMenuItems(
		Subject subject, 
		String currentLink)
	{
		if(currentLink == null)
		{
			currentLink = "/";
		}
		
		MenuItem workItem = new MenuItem();
		workItem.setLink(currentLink);
		List[] items = null;
		
		TreeModel model = getModelForSubject(subject);
		TreeStateCache treeState = getTreeState(subject);
		TreePath selection = treeState.findTreePath(workItem);
		if(selection != null)
		{

			int depth = selection.getPathCount();
			items = new List[depth];
			
			for(int i = depth - 1; i >= 0; i--)
			{
				DefaultMutableTreeNode currentNode = 
					(DefaultMutableTreeNode)selection.getPathComponent(i);
				workItem = (MenuItem)currentNode.getUserObject();			
				
				int childCount = currentNode.getChildCount();
				if((i == (depth - 1)) && (childCount == 0))
				{
					items = new List[depth - 1];
				}
				else
				{
					items[i] = new ArrayList(currentNode.getChildCount());
					Enumeration children = currentNode.children();
					while(children.hasMoreElements())
					{
						DefaultMutableTreeNode childNode = 
							(DefaultMutableTreeNode)children.nextElement();
						MenuItem tempItem = (MenuItem)childNode.getUserObject();
						MenuItem childItem = new MenuItem();
						childItem.setLink(tempItem.getLink());
						childItem.setTag(tempItem.getTag());
						childItem.setEnabled(tempItem.isEnabled());
						childItem.setShortCutKey(tempItem.getShortCutKey());
						childItem.setQueryString(tempItem.getQueryString());
						
						if(i < (depth - 1))
						{
							DefaultMutableTreeNode temp =
								(DefaultMutableTreeNode)selection.getPathComponent(i + 1);
							if(childNode.equals(temp))
							{
								childItem.setActive(true);
							}
						}
						
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
	 * haal tree state uit cache
	 * @TODO Helaas, de treeState kan niet gecached worden omdat de uiteindelijke
	 * 		per request kan verschillen. Misschien met een andere aanpak? 
	 * @param subject jaas subject
	 * @return TreeStateCache
	 */
	public TreeStateCache getTreeState(Subject subject)
	{
		//TreeStateCache treeState = (TreeStateCache)userStateCache.get(subject);
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
	 * reset context voor deze Thread
	 */
	public void resetContextForCurrentThread()
	{
		Map context = (Map)contextHolder.get();
		if(context != null)
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
	 * bewaar context variable value onder key
	 * @param key sleutel
	 * @param value waarde
	 */
	public void putFilterContextVariable(Object key, Object value)
	{
		Map context = (Map)contextHolder.get();
		if(context == null)
		{
			context = new HashMap();
			contextHolder.set(context);			
		}
		context.put(key, value);
	}
	
	/**
	 * haal context variabele met key
	 * @param key sleutel
	 * @return Object of null indien niet gevonden
	 */
	public Object getFilterContextVariable(Object key)
	{
		Map context = (Map)contextHolder.get();
		if(context != null)
		{
			return context.get(key);			
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * @return String
	 */
	public String getConfigLocation()
	{
		return configLocation;
	}

	/**
	 * @param configLocation
	 */
	public void setConfigLocation(String configLocation)
	{
		this.configLocation = configLocation;
	}
	
	/**
	 * @see nl.openedge.modules.types.initcommands.ServletContextAwareType#setServletContext(javax.servlet.ServletContext)
	 */
	public void setServletContext(ServletContext servletContext)
		throws ConfigException
	{
		this.servletContext = servletContext;
	}
	
	/* 
	 * debug tree
	 */
	private void debugTree(TreeModel m)
	{

		DefaultMutableTreeNode node = (DefaultMutableTreeNode)m.getRoot();
		Enumeration enum = node.breadthFirstEnumeration();
		enum = node.preorderEnumeration();
		log.debug("-- MENU TREE DUMP --");
		while (enum.hasMoreElements())
		{
			DefaultMutableTreeNode nd = 
				(DefaultMutableTreeNode)enum.nextElement();
			String tabs = "|";
			for (int i = 0; i < nd.getLevel(); i++)
				tabs += "\t";
			log.debug(tabs + nd);
		}
		log.debug("-------------------");
	}

}

/* wrapper hulp object */
final class SetWrapper
{
	protected Set principals = new HashSet();
	protected Set menuItems = new HashSet();
}

/* url helper */
final class URLHelper
{

	/**
	 * Interprets some absolute URLs as external paths or from classpath
	 * @param path path to translate
	 * @param caller caller class of method
	 * @return URL
	 * @throws MalformedURLException
	 */
	public static URL convertToURL(String path, Class caller) 
		throws MalformedURLException
	{

		return convertToURL(path, caller, null);
	}

	/**
	 * Interprets some absolute URLs as external paths, otherwise generates URL
	 * appropriate for loading from internal webapp or, servletContext is null,
	 * loading from the classpath.
	 * @param path path to translate
	 * @param caller caller of method
	 * @param servletContext servlet context of webapp
	 * @return URL
	 * @throws MalformedURLException
	 */
	public static URL convertToURL(
		String path, Class caller, ServletContext servletContext)
		throws MalformedURLException
	{

		URL url = null;
		if (path.startsWith("file:")
			|| path.startsWith("http:")
			|| path.startsWith("https:")
			|| path.startsWith("ftp:"))
		{
			url = new URL(path);
		}
		else if (servletContext != null)
		{
			// Quick sanity check
			if (!path.startsWith("/"))
				path = "/" + path;
			url = servletContext.getResource(path);
		}
		else
		{
			ClassLoader clsLoader = 
				Thread.currentThread().getContextClassLoader();
			if (clsLoader == null)
			{
				url = (caller != null) ? 
					caller.getResource(path) : 
					ClassLoader.getSystemResource(path);
			}
			else
			{
				url = clsLoader.getResource(path);
				// fallthrough
				if (url == null)
				{
					url = (caller != null) ? 
						caller.getResource(path) : 
						ClassLoader.getSystemResource(path);
				}
			}
		}
		return url;
	}
}
