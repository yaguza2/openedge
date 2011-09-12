package nl.openedge.baritus.test.mock;

/**
 * @author Eelco Hillenius
 */
public class MockHttpServletResponse extends com.mockobjects.servlet.MockHttpServletResponse
{

	/**
	 * @see javax.servlet.http.HttpServletResponse#addDateHeader(java.lang.String, long)
	 */
	@Override
	public void addDateHeader(String arg0, long arg1)
	{
		// ignore
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#setDateHeader(java.lang.String, long)
	 */
	@Override
	public void setDateHeader(String arg0, long arg1)
	{
		// ignore
	}

}
