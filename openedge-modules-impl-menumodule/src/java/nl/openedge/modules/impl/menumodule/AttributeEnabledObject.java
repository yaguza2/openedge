package nl.openedge.modules.impl.menumodule;

import java.util.Map;

/**
 * common interface for all things accepting attributes
 * 
 * @author Maurice Marrink
 * @since Apr 21, 2004
 */
public interface AttributeEnabledObject
{
	/**
	 * 
	 * @param name the name of attribute
	 * @return the value of the attribute or null if it does not exist
	 */
	public Object getAttribute(String name);
	/**
	 * 
	 * @return the (possibly empty) Map with all the attributes
	 */
	public Map getAttributes();
	/**
	 * Registers a new Attribute with this filter, overriding any attribute already registered under that name.
	 * 
	 * @param name
	 * @param value
	 */
	public void putAttribute(String name, Object value);
	/**
	 * Registers all attributes in the Map, overriding any existing attribute with the same name.
	 * 
	 * @param attributes
	 */
	public void putAllAttributes(Map attributes);
	/**
	 * Removes the attribute with the specified name if it exists, if it doesnt exist nothing happens.
	 * 
	 * @param name the name of the attribute
	 */
	public void removeAttribute(String name);
}
