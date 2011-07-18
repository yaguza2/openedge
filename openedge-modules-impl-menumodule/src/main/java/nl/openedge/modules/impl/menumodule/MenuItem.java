package nl.openedge.modules.impl.menumodule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * An item that represents one menu item within a menu tree.
 * 
 * @author Eelco Hillenius
 * @author Maurice Marrink
 */
public final class MenuItem implements Serializable, AttributeEnabledObject
{
	private static final long serialVersionUID = 1L;

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
	private HashSet<String> aliases = null;

	/**
	 * Free-form attributes to be used with filters and views.
	 */
	private Map<String, Object> attributes = null;

	/**
	 * Request parameters.
	 */
	private Map<String, String> parameters;

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
	private List<MenuFilter> filters = null;

	/**
	 * Whether this item is part of the current (active) path.
	 */
	private boolean active = false;

	/**
	 * Childs of this menu node.
	 */
	private List<MenuItem> children = new ArrayList<MenuItem>();

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
	 * @param string
	 *            the link
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
	@Override
	public void putAttribute(String key, Object value)
	{
		if (attributes == null)
		{
			attributes = new HashMap<String, Object>();
		}
		attributes.put(key, value);
	}

	/**
	 * delete attribute.
	 * 
	 * @param key
	 *            key of attribute
	 */
	@Override
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
	 * @param active
	 *            whether this menu item part of the active path
	 */
	public void setActive(boolean active)
	{
		this.active = active;
	}

	/**
	 * Get the query string.
	 * 
	 * @return all request parameters in a String (not urlencoded!) or null if there are
	 *         no parameters
	 */
	public String getQueryString()
	{
		// TODO we might want to support keys with a String[] as value and have
		// that parameter appear multiple times
		// like p1=1&p1=2&p1=3
		if (parameters == null || parameters.isEmpty())
			return null;
		else
		{
			StringBuilder result = new StringBuilder();
			Iterator<String> it = parameters.keySet().iterator();
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
	public List<MenuFilter> getFilters()
	{
		return filters;
	}

	/**
	 * Set list of filters.
	 * 
	 * @param list
	 *            filters
	 */
	public void setFilters(List<MenuFilter> list)
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
	public HashSet<String> getAliases()
	{
		return aliases;
	}

	/**
	 * Set the aliases.
	 * 
	 * @param set
	 *            the aliases
	 */
	public void setAliases(HashSet<String> set)
	{
		aliases = set;
	}

	/**
	 * Get the attributes.
	 * 
	 * @return Map attributes
	 */
	@Override
	public Map<String, Object> getAttributes()
	{
		return attributes;
	}

	/**
	 * Set the attributes.
	 * 
	 * @param map
	 *            the attributes
	 */
	public void setAttributes(Map<String, Object> map)
	{
		attributes = map;
	}

	/**
	 * Get the request parameters.
	 * 
	 * @return Map the request parameters
	 */
	public Map<String, String> getParameters()
	{
		return parameters;
	}

	/**
	 * Set the request parameters. Although it is possible to specify objects as
	 * parameters, it is not supported. You should only use Strings as keys and values. We
	 * only allow a Map because Velocity always creates a Map in VLT
	 * 
	 * @param map
	 *            the request parameters
	 */
	public void setParameters(Map<String, String> map)
	{
		parameters = map;
	}

	/**
	 * Add request parameters to the current request parameters. Although it is possible
	 * to specify objects as parameters, it is not supported. You should only use Strings
	 * as keys and values. We only allow a Map because Velocity always creates a Map in
	 * VLT
	 * 
	 * @param params
	 *            request parameters to add
	 */
	public void addParameters(Map<String, String> params)
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
	 * Adds a request parameter to the url. Like we said only Strings are allowed. At some
	 * time we might allow String[] as value but right now we dont, so dont even try.
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
			parameters = new HashMap<String, String>();
		}
		parameters.put(name, value);
	}

	@Override
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

	@Override
	public void putAllAttributes(Map<String, Object> newAttributes)
	{
		if (this.attributes == null)
			this.attributes = new HashMap<String, Object>(newAttributes);
		else
			this.attributes.putAll(newAttributes);
	}

	/**
	 * Filters out any child menu items that should not be be in the list based on the
	 * current context. Basically a little hack to keep this list in sync with the results
	 * you get if you access the children of this item via MenuModule.getMenuItems() for
	 * the level above. Since Session and RequestScopeMenuFilters are not stored inside
	 * the menu items themselves you can put them in the filterContext under the keys
	 * obtainable from MenuFilter.
	 * 
	 * @param filterContext
	 *            environment variables for the filters
	 */
	@SuppressWarnings("unchecked")
	public void applyFiltersOnChildren(Map<Object, Object> filterContext)
	{
		if (children == null)
			return;

		Iterator<MenuFilter> filtersForChilds = null;

		boolean accept = true;
		ArrayList<MenuItem> filtered = new ArrayList<MenuItem>();
		for (MenuItem child : children)
		{
			accept = true;

			List<MenuFilter> filters = child.getFilters();
			if (filters == null)
			{
				filters = new ArrayList<MenuFilter>();
				filters.addAll((List<MenuFilter>) filterContext
					.get(MenuFilter.CONTEXT_KEY_REQUEST_FILTERS));
				filters.addAll((List<MenuFilter>) filterContext
					.get(MenuFilter.CONTEXT_KEY_SESSION_FILTERS));
			}
			filtersForChilds = filters.iterator();
			while (filtersForChilds.hasNext() && accept)
			{
				MenuFilter filter = filtersForChilds.next();
				accept = filter.accept(child, filterContext);
			}
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
	public List<MenuItem> getChildren()
	{
		return children;
	}

	/**
	 * Set the children of this item.
	 * 
	 * @param list
	 *            the children of this item
	 */
	public void setChildren(List<MenuItem> list)
	{
		children = list;
	}

	/**
	 * Adds the child to the end of the list. Note this does not insert the item in the
	 * cached menu tree.
	 * 
	 * @param item
	 *            the item to add
	 */
	public void addChild(MenuItem item)
	{
		children.add(item);
	}

	/**
	 * Inserts the child at the specified index Note this does not insert the item in the
	 * cached menu tree.
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
	 * Removes the item from the list of children. Note this does not remove the item from
	 * the cached tree as being a child of this menu.
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
	@Override
	public int hashCode()
	{
		return super.hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
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
	@Override
	public String toString()
	{
		StringBuilder b = new StringBuilder("menu -> ").append(link);
		printItemDetail(b);
		return b.toString();
	}

	/**
	 * Print part of string rep.
	 * 
	 * @param b
	 *            current StringBuilder
	 */
	private void printItemDetail(StringBuilder b)
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
	 * 
	 * @param b
	 *            current StringBuilder
	 */
	private void printFilters(StringBuilder b)
	{
		if (filters != null)
		{
			b.append(" filters:");
			for (Iterator<MenuFilter> i = filters.iterator(); i.hasNext();)
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
	 * 
	 * @param b
	 *            current StringBuilder
	 */
	private void printAliases(StringBuilder b)
	{
		if (aliases != null)
		{
			b.append(" aliases:");
			for (Iterator< ? > i = aliases.iterator(); i.hasNext();)
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
	 * 
	 * @param b
	 *            current StringBuilder
	 */
	private void printAttributes(StringBuilder b)
	{
		if (attributes != null)
		{
			b.append(" attributes:");
			for (Iterator<String> i = attributes.keySet().iterator(); i.hasNext();)
			{
				String key = i.next();
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
	 * 
	 * @param b
	 *            current StringBuilder
	 */
	private void printActive(StringBuilder b)
	{
		if (active)
		{
			b.append(" (active)");
		}
	}
}
