package nl.openedge.modules.types;

/**
 * will be thrown for exception that occur when using the types registry.
 * 
 * @author Eelco Hillenius
 */
public class RegistryException extends RuntimeException
{

	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 */
	public RegistryException()
	{
		super();
	}

	/**
	 * Construct.
	 * 
	 * @param message
	 *            the message
	 */
	public RegistryException(String message)
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
	public RegistryException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Construct.
	 * 
	 * @param cause
	 *            the cause
	 */
	public RegistryException(Throwable cause)
	{
		super(cause);
	}

}
