package nl.openedge.modules.impl.menumodule;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Maurice Marrink
 * @since Apr 21, 2004
 */
public abstract class AbstractMenuFilter implements MenuFilter
{
	private HashMap attributes;
	
	/**
	 * 
	 */
	public AbstractMenuFilter()
	{
		attributes=new HashMap(2);
	}
	/**
	 * @see nl.openedge.modules.impl.menumodule.MenuFilter#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name)
	{
		return attributes.get(name);
	}
	/**
	 * @see nl.openedge.modules.impl.menumodule.MenuFilter#getAttributes()
	 */
	public Map getAttributes()
	{
		return attributes;
	}
	/**
	 * @see nl.openedge.modules.impl.menumodule.MenuFilter#putAttribute(java.lang.String, java.lang.Object)
	 */
	public void putAttribute(String name, Object value)
	{
		attributes.put(name,value);
	}
	/**
	 * @see nl.openedge.modules.impl.menumodule.MenuFilter#putAllAttributes(java.util.Map)
	 */
	public void putAllAttributes(Map attributes)
	{
		this.attributes.putAll(attributes);
	}
	/**
	 * @see nl.openedge.modules.impl.menumodule.AttributeEnabledObject#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String name)
	{
		attributes.remove(name);
	}

}
