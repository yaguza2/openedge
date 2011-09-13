package nl.openedge.modules;

/**
 * ComponentLookupException can be thrown when querying the ComponentRepository for
 * components.
 * 
 * @author Eelco Hillenius
 */
public final class ComponentLookupException extends RuntimeException
{

	private static final long serialVersionUID = 1L;

	/**
	 * construct exception.
	 */
	public ComponentLookupException()
	{
		super();
	}

	/**
	 * construct exception with message.
	 * 
	 * @param message
	 *            the message
	 */
	public ComponentLookupException(String message)
	{
		super(message);
	}

	/**
	 * construct exception with message and cause.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public ComponentLookupException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * construct exception with cause.
	 * 
	 * @param cause
	 *            the cause
	 */
	public ComponentLookupException(Throwable cause)
	{
		super(cause);
	}

}
