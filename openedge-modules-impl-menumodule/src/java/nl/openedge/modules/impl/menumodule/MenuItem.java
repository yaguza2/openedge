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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Een item in een menu 
 * @author Eelco Hillenius
 */
public final class MenuItem implements Serializable
{
	private String tag;
	private String link;
	// als er aliases zijn, stoppen we die hier als String in
	private HashSet aliases = null;
	private Map attributes = null;
	private String queryString;
	private boolean enabled = true;
	private String shortCutKey = null;
	
	// evt filters voor dit item
	private List filters = null;
	
	// is dit item onderdeel van het pad?
	private boolean active = false;

	/**
	 * @return String
	 */
	public String getLink()
	{
		return link;
	}

	/**
	 * @return String
	 */
	public String getTag()
	{
		return tag;
	}

	/**
	 * @param string
	 */
	public void setLink(String string)
	{
		link = string;
	}

	/**
	 * @param string
	 */
	public void setTag(String string)
	{
		tag = string;
	}
	
	/**
	 * add/ update an attribute
	 * @param key key to store attribute with
	 * @param value value of attribute
	 */
	public void putAttribute(String key, Object value)
	{
		if(attributes == null)
		{
			attributes = new HashMap();
		}
		attributes.put(key, value);
	}
	
	/**
	 * delete attribute
	 * @param key key of attribute
	 */
	public void removeAttribute(String key)
	{
		if(attributes != null)
		{
			attributes.remove(key);
		}
	}
	
	/**
	 * get attribute with given key
	 * @param key key of attribute
	 * @return Object attribute or null if not found
	 */
	public Object get(String key)
	{
		return (attributes != null) ? attributes.get(key) : null;
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
			
		MenuItem that = (MenuItem)o;
		
		// vergelijken op link
		if (this.link.equals(that.link))
		{
			return true;
		}
		
		// vergelijken op aliases
		if(aliases != null)
		{
			if(aliases.contains(that.link))
			{
				return true;
			}
		}
			
		return false;
	}
	
	/**
	 * @return String string representatie
	 */
	public String toString()
	{
		StringBuffer b = new StringBuffer("menu -> " )
			.append(link)
			.append((active) ? " (actief)" : "");
			
		if( (aliases != null) || (filters != null) || (attributes != null) )
		{
			b.append(" {");
		}
		if(attributes != null)
		{
			b.append(" attributes:");
			for(Iterator i = attributes.keySet().iterator(); i.hasNext(); )
			{
				String key = (String)i.next();
				b.append(key).append("=").append(attributes.get(key));
				if(i.hasNext())
				{
					b.append(",");
				}
			}
		}
		if(aliases != null)
		{
			b.append(" aliases:");
			for(Iterator i = aliases.iterator(); i.hasNext(); )
			{
				b.append(i.next());
				if(i.hasNext())
				{
					b.append(",");
				}
			}
		}
		if(filters != null)
		{
			b.append(" filters:");
			for(Iterator i = filters.iterator(); i.hasNext(); )
			{
				RequestScopeMenuFilter f = (RequestScopeMenuFilter)i.next();
				b.append(f);
				if(i.hasNext())
				{
					b.append(",");
				}
			}
		}
		if( (aliases != null) || (filters != null) || (attributes != null) )
		{
			b.append(" }");
		}
		return b.toString();
	}

	/**
	 * @return boolean is dit item onderdeel van het actieve pad
	 */
	public boolean isActive()
	{
		return active;
	}

	/**
	 * @param active
	 */
	public void setActive(boolean active)
	{
		this.active = active;
	}

	/**
	 * @return String
	 */
	public String getQueryString()
	{
		return queryString;
	}

	/**
	 * @param string
	 */
	public void setQueryString(String string)
	{
		queryString = string;
	}

	/**
	 * @return List
	 */
	public List getFilters()
	{
		return filters;
	}

	/**
	 * @param list
	 */
	public void setFilters(List list)
	{
		filters = list;
	}

	/**
	 * render hint; indien false kan item grijs/ niet clickable worden gezet
	 * @return enabled
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * @param b
	 */
	public void setEnabled(boolean b)
	{
		enabled = b;
	}

	/**
	 * @return String snelmenu toets
	 */
	public String getShortCutKey()
	{
		return shortCutKey;
	}

	/**
	 * @param string snelmenu toets
	 */
	public void setShortCutKey(String string)
	{
		shortCutKey = string;
	}

	/**
	 * @return HashSet
	 */
	public HashSet getAliases()
	{
		return aliases;
	}

	/**
	 * @param set
	 */
	public void setAliases(HashSet set)
	{
		aliases = set;
	}

	/**
	 * @return Map
	 */
	public Map getAttributes()
	{
		return attributes;
	}

	/**
	 * @param map
	 */
	public void setAttributes(Map map)
	{
		attributes = map;
	}

}
