package nl.openedge.modules.impl.menumodule;

import java.util.Map;

/**
 * common interface for all things accepting attributes.
 * 
 * @author Maurice Marrink
 */
public interface AttributeEnabledObject
{
	/**
	 * Get attribute.
	 * 
	 * @param name
	 *            the name of attribute
	 * @return the value of the attribute or null if it does not exist
	 */
	Object getAttribute(String name);

	/**
	 * Get attributes.
	 * 
	 * @return the (possibly empty) Map with all the attributes
	 */
	Map<String, Object> getAttributes();

	/**
	 * Registers a new Attribute with this filter, overriding any attribute already
	 * registered under that name.
	 * 
	 * @param name
	 *            name of the attribute
	 * @param value
	 *            value of the attribute
	 */
	void putAttribute(String name, Object value);

	/**
	 * Registers all attributes in the Map, overriding any existing attribute with the
	 * same name.
	 * 
	 * @param attributes
	 *            the attributes
	 */
	void putAllAttributes(Map<String, Object> attributes);

	/**
	 * Removes the attribute with the specified name if it exists, if it doesnt exist
	 * nothing happens.
	 * 
	 * @param name
	 *            the name of the attribute
	 */
	void removeAttribute(String name);
}
