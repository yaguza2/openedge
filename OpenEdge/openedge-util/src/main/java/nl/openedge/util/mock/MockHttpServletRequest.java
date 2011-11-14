package nl.openedge.util.mock;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import nl.openedge.util.IteratorToEnumeratorDecorator;

/**
 * Mock servlet request.
 * 
 * @author Eelco Hillenius
 */
public class MockHttpServletRequest extends com.mockobjects.servlet.MockHttpServletRequest
{

	/**
	 * Request attributes.
	 */
	private Map attributes = new HashMap();

	/**
	 * @see javax.servlet.http.HttpServletRequest#getDateHeader(java.lang.String)
	 */
	@Override
	public long getDateHeader(final String arg0)
	{
		return System.currentTimeMillis();
	}

	/**
	 * Geeft null allways.
	 * 
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
	public Object getAttribute(final String anAttributeName)
	{
		return attributes.get(anAttributeName);
	}

	/**
	 * @see javax.servlet.ServletRequest#getAttributeNames()
	 */
	@Override
	public Enumeration getAttributeNames()
	{
		return new IteratorToEnumeratorDecorator(attributes.keySet().iterator());
	}

	/**
	 * @see javax.servlet.ServletRequest#removeAttribute(java.lang.String)
	 */
	@Override
	public void removeAttribute(final String anAttributeToRemove)
	{
		attributes.remove(anAttributeToRemove);
	}

	/**
	 * @see javax.servlet.ServletRequest#setAttribute(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setAttribute(final String attributeName, final Object attributeValue)
	{
		attributes.put(attributeName, attributeValue);
	}

}
