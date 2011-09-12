package nl.openedge.util.mock;

/**
 * Mock HttpServletResponse.
 * 
 * @author Eelco Hillenius
 */
public class MockHttpServletResponse extends com.mockobjects.servlet.MockHttpServletResponse
{

	/**
	 * Empty implementation.
	 * 
	 * @see javax.servlet.http.HttpServletResponse#addDateHeader(java.lang.String, long)
	 */
	@Override
	public void addDateHeader(final String arg0, final long arg1)
	{
		// ignore
	}

	/**
	 * Empty implementation.
	 * 
	 * @see javax.servlet.http.HttpServletResponse#setDateHeader(java.lang.String, long)
	 */
	@Override
	public void setDateHeader(final String arg0, final long arg1)
	{
		// ignore
	}

}
