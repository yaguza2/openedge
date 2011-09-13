package nl.openedge.modules.impl.menumodule;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class for menu filters.
 * 
 * @author Maurice Marrink
 */
public abstract class AbstractMenuFilter implements MenuFilter
{
	private HashMap<String, Object> attributes = new HashMap<String, Object>();

	@Override
	public Object getAttribute(String name)
	{
		return attributes.get(name);
	}

	@Override
	public Map<String, Object> getAttributes()
	{
		return attributes;
	}

	@Override
	public void putAttribute(String name, Object value)
	{
		attributes.put(name, value);
	}

	@Override
	public void putAllAttributes(Map<String, Object> newAttributes)
	{
		this.attributes.putAll(newAttributes);
	}

	@Override
	public void removeAttribute(String name)
	{
		attributes.remove(name);
	}
}
