package nl.openedge.util.net;

/**
 * Exception class for HttpHelper.
 * 
 * @author Eelco Hillenius
 */
public final class HttpHelperException extends Exception
{

	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 */
	public HttpHelperException()
	{
		super();
	}

	/**
	 * Construct.
	 * 
	 * @param message
	 *            the message
	 */
	public HttpHelperException(String message)
	{
		super(message);
	}

	/**
	 * Construct.
	 * 
	 * @param cause
	 *            the cause
	 */
	public HttpHelperException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Construct.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public HttpHelperException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
