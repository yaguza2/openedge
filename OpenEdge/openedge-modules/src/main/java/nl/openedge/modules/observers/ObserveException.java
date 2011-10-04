package nl.openedge.modules.observers;

/**
 * Exception that is used for observer related exceptions.
 * 
 * @author Eelco Hillenius
 */
public final class ObserveException extends RuntimeException
{

	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 */
	public ObserveException()
	{
		super();
	}

	/**
	 * Construct.
	 * 
	 * @param message
	 *            the message
	 */
	public ObserveException(String message)
	{
		super(message);
	}

	/**
	 * Construct.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public ObserveException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Construct.
	 * 
	 * @param cause
	 *            the cause
	 */
	public ObserveException(Throwable cause)
	{
		super(cause);
	}

}
