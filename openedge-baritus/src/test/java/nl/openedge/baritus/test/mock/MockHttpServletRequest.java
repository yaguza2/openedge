package nl.openedge.baritus.test.mock;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * @author Eelco Hillenius
 */
public class MockHttpServletRequest extends com.mockobjects.servlet.MockHttpServletRequest
{

	private Map<String, Object> attributes = new HashMap<String, Object>();

	/**
	 * @see javax.servlet.http.HttpServletRequest#getDateHeader(java.lang.String)
	 */
	@Override
	public long getDateHeader(String arg0)
	{
		return System.currentTimeMillis();
	}

	/**
	 * @see javax.servlet.ServletRequest#getLocale()
	 */
	@Override
	public Locale getLocale()
	{
		return null;
	}

	/**
	 * @see javax.servlet.ServletRequest#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String anAttributeName)
	{
		return attributes.get(anAttributeName);
	}

	/**
	 * @see javax.servlet.ServletRequest#getAttributeNames()
	 */
	@Override
	public Enumeration<String> getAttributeNames()
	{
		return new ItEnum(attributes.keySet().iterator());
	}

	/**
	 * @see javax.servlet.ServletRequest#removeAttribute(java.lang.String)
	 */
	@Override
	public void removeAttribute(String anAttributeToRemove)
	{
		attributes.remove(anAttributeToRemove);
	}

	/**
	 * @see javax.servlet.ServletRequest#setAttribute(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setAttribute(String attributeName, Object attributeValue)
	{
		attributes.put(attributeName, attributeValue);
	}

}

class ItEnum implements Enumeration<String>
{
	Iterator<String> i = null;

	public ItEnum(Iterator<String> i)
	{
		this.i = i;
	}

	@Override
	public boolean hasMoreElements()
	{
		return i.hasNext();
	}

	@Override
	public String nextElement()
	{
		return i.next();
	}
}
