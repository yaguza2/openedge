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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * An item that represents one menu item within a menu tree.
 * 
 * @author Eelco Hillenius
 * @author Maurice Marrink
 */
public final class MenuItem implements Serializable, AttributeEnabledObject
{
	/**
	 * The tag (text to be rendered) of the menu item.
	 */
	private String tag;

	/**
	 * The link of the menu item.
	 */
	private String link;

	/**
	 * Possible aliases.
	 */
	private HashSet aliases = null;

	/**
	 * Free-form attributes to be used with filters and views.
	 */
	private Map attributes = null;

	/**
	 * Request parameters.
	 */
	private Map parameters;

	/**
	 * Whether this item is enabled; to be used with filters and views.
	 */
	private boolean enabled = true;

	/**
	 * Key for shortCuts (standard attribute).
	 */
	private String shortCutKey = null;

	/**
	 * Filters for this menu item.
	 */
	private List filters = null;

	/**
	 * Whether this item is part of the current (active) path.
	 */
	private boolean active = false;

	/**
	 * Childs of this menu node.
	 */
	private List children = new ArrayList();

	/**
	 * Get the link.
	 * 
	 * @return String the link
	 */
	public String getLink()
	{
		return link;
	}

	/**
	 * Get the tag.
	 * 
	 * @return String the tag
	 */
	public String getTag()
	{
		return tag;
	}

	/**
	 * Set the link.
	 * 
	 * @param string the link
	 */
	public void setLink(String string)
	{
		link = string;
	}

	/**
	 * Set the tag.
	 * 
	 * @param string
	 *            the tag.
	 */
	public void setTag(String string)
	{
		tag = string;
	}

	/**
	 * add/ update an attribute.
	 * 
	 * @param key
	 *            key to store attribute with
	 * @param value
	 *            value of attribute
	 */
	public void putAttribute(String key, Object value)
	{
		if (attributes == null)
		{
			attributes = new HashMap();
		}
		attributes.put(key, value);
	}

	/**
	 * delete attribute.
	 * 
	 * @param key
	 *            key of attribute
	 */
	public void removeAttribute(String key)
	{
		if (attributes != null)
		{
			attributes.remove(key);
		}
	}

	/**
	 * Alternative method for getAttribute. Makes $model.myattrib possible.
	 * 
	 * @param key
	 *            key of attribute
	 * @return Object attribute or null if not found
	 */
	public Object get(String key)
	{
		if (attributes != null)
		{
			return attributes.get(key);
		}
		else
		{
			return null;
		}
	}

	/**
	 * is this menu item part of the active path.
	 * 
	 * @return boolean is this menu item part of the active path
	 */
	public boolean getActive()
	{
		return active;
	}

	/**
	 * is this menu item part of the active path.
	 * 
	 * @return boolean is this menu item part of the active path
	 */
	public boolean isActive()
	{
		return active;
	}

	/**
	 * set whether this menu item part of the active path.
	 * 
	 * @param active whether this menu item part of the active path
	 */
	public void setActive(boolean active)
	{
		this.active = active;
	}

	/**
	 * Get the query string.
	 * 
	 * @return all request parameters in a String (not urlencoded!) or null if there are no
	 *         parameters
	 */
	public String getQueryString()
	{
		//TODO we might want to support keys with a String[] as value and have
		// that parameter appear multiple times
		//like p1=1&p1=2&p1=3
		if (parameters == null || parameters.isEmpty())
			return null;
		else
		{
			StringBuffer result = new StringBuffer();
			Iterator it = parameters.keySet().iterator();
			String key, value;
			Object tempValue = null;
			while (it.hasNext())
			{
				Object item = it.next();
				if (item == null)
				{
					continue;
				}
				if (item instanceof String)
				{
					key = (String) item;
				}
				else
				{
					key = String.valueOf(item);
				}
				tempValue = parameters.get(key);
				if (tempValue == null)
				{
					value = "";
				}
				else
				{
					value = String.valueOf(tempValue);
				}
				result.append(key).append("=").append(value);
				if (it.hasNext())
				{
					result.append("&");
				}
			}
			return result.toString();
		}
	}

	/**
	 * Get list of filters.
	 * 
	 * @return List filters
	 */
	public List getFilters()
	{
		return filters;
	}

	/**
	 * Set list of filters.
	 * 
	 * @param list
	 *            filters
	 */
	public void setFilters(List list)
	{
		filters = list;
	}

	/**
	 * Get whether this item is enabled.
	 * 
	 * @return whether this item is enabled
	 */
	public boolean getEnabled()
	{
		return enabled;
	}

	/**
	 * get rendering hint; if false this item could be grayed out.
	 * 
	 * @return boolean enabled
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * set rendering hint; if false this item could be grayed out.
	 * 
	 * @param b
	 *            enabled
	 */
	public void setEnabled(boolean b)
	{
		enabled = b;
	}

	/**
	 * Get short cut key (if any).
	 * 
	 * @return String short cut key (if any)
	 */
	public String getShortCutKey()
	{
		return shortCutKey;
	}

	/**
	 * set short cut key.
	 * 
	 * @param string
	 *            short cut key
	 */
	public void setShortCutKey(String string)
	{
		shortCutKey = string;
	}

	/**
	 * Get the aliases.
	 * 
	 * @return HashSet aliases
	 */
	public HashSet getAliases()
	{
		return aliases;
	}

	/**
	 * Set the aliases.
	 * 
	 * @param set
	 *            the aliases
	 */
	public void setAliases(HashSet set)
	{
		aliases = set;
	}

	/**
	 * Get the attributes.
	 * 
	 * @return Map attributes
	 */
	public Map getAttributes()
	{
		return attributes;
	}

	/**
	 * Set the attributes.
	 * 
	 * @param map
	 *            the attributes
	 */
	public void setAttributes(Map map)
	{
		attributes = map;
	}

	/**
	 * Get the request parameters.
	 * 
	 * @return Map the request parameters
	 */
	public Map getParameters()
	{
		return parameters;
	}

	/**
	 * Set the request parameters. Although it is possible to specify objects as parameters, it is
	 * not supported. You should only use Strings as keys and values. We only allow a Map because
	 * Velocity always creates a Map in VLT
	 * 
	 * @param map
	 *            the request parameters
	 */
	public void setParameters(Map map)
	{
		parameters = map;
	}

	/**
	 * Add request parameters to the current request parameters. Although it is possible to specify
	 * objects as parameters, it is not supported. You should only use Strings as keys and values.
	 * We only allow a Map because Velocity always creates a Map in VLT
	 * 
	 * @param params
	 *            request paramteres to add
	 */
	public void addParameters(Map params)
	{
		if (parameters == null)
		{
			parameters = params;
		}
		else
		{
			parameters.putAll(params);
		}
	}

	/**
	 * Adds a request parameter to the url. Like we said only Strings are allowed. At some time we
	 * might allow String[] as value but right now we dont, so dont even try.
	 * 
	 * @param name
	 *            name of the request parameter
	 * @param value
	 *            value of the request parameter
	 */
	public void addParameter(String name, String value)
	{
		if (parameters == null)
		{
			parameters = new Properties();
		}
		parameters.put(name, value);
	}

	/**
	 * @see nl.openedge.modules.impl.menumodule.AttributeEnabledObject#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name)
	{
		if (attributes != null)
		{
			return attributes.get(name);
		}
		else
		{
			return null;
		}
	}

	/**
	 * @see nl.openedge.modules.impl.menumodule.AttributeEnabledObject#putAllAttributes(java.util.Map)
	 */
	public void putAllAttributes(Map newAttributes)
	{
		if (this.attributes == null)
		{
			this.attributes = new HashMap(newAttributes);
		}
		else
		{
			this.attributes.putAll(newAttributes);
		}
	}

	/**
	 * Filters out any child menuitems that should not be be in the list based on the current
	 * context. Basicly a little hack to keep this list in sync with the results you get if you
	 * access the children of this item via MenuModule.getMenuItems() for the level above. Since
	 * Session and RequestScopeMenuFilters are not stored inside the menuitems themselfs you can put
	 * them in the filterContext under the keys obtainable from MenuFilter.
	 * 
	 * @param filterContext
	 *            environment variables for the filters
	 */
	public void applyFiltersOnChildren(Map filterContext)
	{
		List list = getChildren();
		if (children == null)
			return;
		Iterator it = children.iterator();
		Iterator filtersForChilds = null;
		MenuItem child = null;
		boolean accept = true;
		ArrayList filtered = new ArrayList();
		while (it.hasNext())
		{
			accept = true;
			child = (MenuItem) it.next();
			list = child.getFilters();
			if (list == null)
			{
				list = new ArrayList();
				list.addAll((List) filterContext.get(MenuFilter.CONTEXT_KEY_REQUEST_FILTERS));
				list.addAll((List) filterContext.get(MenuFilter.CONTEXT_KEY_SESSION_FILTERS));
			}
			filtersForChilds = list.iterator();
			while (filtersForChilds.hasNext() && accept)
				accept = ((MenuFilter) filtersForChilds.next()).accept(child, filterContext);
			if (accept)
			{
				child.applyFiltersOnChildren(filterContext);
				filtered.add(child);
			}
		}
		setChildren(filtered);
	}

	/**
	 * Get the children of this item.
	 * 
	 * @return the children of this item
	 */
	public List getChildren()
	{
		return children;
	}

	/**
	 * Set the children of this item.
	 * 
	 * @param list
	 *            the children of this item
	 */
	public void setChildren(List list)
	{
		children = list;
	}

	/**
	 * Adds the child to the end of the list. Note this does not insert the item in the cached menu
	 * tree.
	 * 
	 * @param item
	 *            the item to add
	 */
	public void addChild(MenuItem item)
	{
		children.add(item);
	}

	/**
	 * Inserts the child at the specified index Note this does not insert the item in the cached
	 * menu tree.
	 * 
	 * @param index
	 *            position where the item should be inserted
	 * @param item
	 *            the item to insert
	 */
	public void insertChild(int index, MenuItem item)
	{
		children.add(index, item);
	}

	/**
	 * Removes the item from the list of children. Note this does not remove the item from the
	 * cached tree as being a child of this menu.
	 * 
	 * @param item
	 *            the item to remove
	 */
	public void deleteChild(MenuItem item)
	{
		int index = children.indexOf(item);
		if (index >= 0)
		{
			children.remove(index);
		}
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return super.hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o)
	{
		if (o == null)
		{
			return false;
		}
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof MenuItem))
		{
			return false;
		}
		MenuItem that = (MenuItem) o;
		if (this.link.equals(that.link))
		{
			return true;
		}
		if (aliases != null)
		{
			if (aliases.contains(that.link))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the string representation with some extra info.
	 * 
	 * @return String the string representation with some extra info.
	 */
	public String toString()
	{
		StringBuffer b = new StringBuffer("menu -> ").append(link);
		printItemDetail(b);
		return b.toString();
	}

	/**
	 * Print part of string rep.
	 * @param b current stringbuffer
	 */
	private void printItemDetail(StringBuffer b)
	{
		printActive(b);
		if ((aliases != null) || (filters != null) || (attributes != null))
		{
			b.append(" {");
		}
		printAttributes(b);
		printAliases(b);
		printFilters(b);
		if ((aliases != null) || (filters != null) || (attributes != null))
		{
			b.append(" }");
		}
	}

	/**
	 * Print part of string rep.
	 * @param b current stringbuffer
	 */
	private void printFilters(StringBuffer b)
	{
		if (filters != null)
		{
			b.append(" filters:");
			for (Iterator i = filters.iterator(); i.hasNext();)
			{
				RequestScopeMenuFilter f = (RequestScopeMenuFilter) i.next();
				b.append(f);
				if (i.hasNext())
				{
					b.append(",");
				}
			}
		}
	}

	/**
	 * Print part of string rep.
	 * @param b current stringbuffer
	 */
	private void printAliases(StringBuffer b)
	{
		if (aliases != null)
		{
			b.append(" aliases:");
			for (Iterator i = aliases.iterator(); i.hasNext();)
			{
				b.append(i.next());
				if (i.hasNext())
				{
					b.append(",");
				}
			}
		}
	}

	/**
	 * Print part of string rep.
	 * @param b current stringbuffer
	 */
	private void printAttributes(StringBuffer b)
	{
		if (attributes != null)
		{
			b.append(" attributes:");
			for (Iterator i = attributes.keySet().iterator(); i.hasNext();)
			{
				String key = (String) i.next();
				b.append(key).append("=").append(attributes.get(key));
				if (i.hasNext())
				{
					b.append(",");
				}
			}
		}
	}

	/**
	 * Print part of string rep.
	 * @param b current stringbuffer
	 */
	private void printActive(StringBuffer b)
	{
		if (active)
		{
			b.append(" (active)");
		}		
	}
}