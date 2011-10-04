package nl.openedge.baritus.converters;

/**
 * Modified ConversionException that saves the desired pattern as an extra field
 * 
 * @author Eelco Hillenius
 */
public final class ConversionException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	private String desiredPattern;

	/**
	 * Construct exception with message.
	 * 
	 * @param message
	 *            message
	 */
	public ConversionException(String message)
	{
		super(message);
	}

	/**
	 * Construct exception with message and desired pattern.
	 * 
	 * @param message
	 *            message
	 * @param desiredPattern
	 *            the desired pattern
	 */
	public ConversionException(String message, String desiredPattern)
	{
		super(message);
		setDesiredPattern(desiredPattern);
	}

	/**
	 * Construct exception with message and cause.
	 * 
	 * @param message
	 *            message
	 * @param cause
	 *            cause
	 */
	public ConversionException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Construct exception with message, cause and desired pattern.
	 * 
	 * @param message
	 *            message
	 * @param cause
	 *            cause
	 * @param desiredPattern
	 *            the desired pattern
	 */
	public ConversionException(String message, Throwable cause, String desiredPattern)
	{
		super(message, cause);
		setDesiredPattern(desiredPattern);
	}

	/**
	 * Construct exception with cause.
	 * 
	 * @param cause
	 *            cause
	 */
	public ConversionException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Construct exception with cause and desired pattern.
	 * 
	 * @param cause
	 *            cause
	 * @param desiredPattern
	 *            the desired pattern
	 */
	public ConversionException(Throwable cause, String desiredPattern)
	{
		super(cause);
		setDesiredPattern(desiredPattern);
	}

	/**
	 * Get the desired pattern.
	 * 
	 * @return String optionally the desired pattern
	 */
	public String getDesiredPattern()
	{
		return desiredPattern;
	}

	/**
	 * Set the desired pattern.
	 * 
	 * @param string
	 *            the desired pattern
	 */
	public void setDesiredPattern(String string)
	{
		desiredPattern = string;
	}

	/**
	 * String representation of the exception.
	 * 
	 * @return String string representation
	 */
	@Override
	public String toString()
	{
		String s = getClass().getName();
		String message = getLocalizedMessage();
		message = (message != null) ? (s + ": " + message) : s;

		if (getDesiredPattern() != null)
		{
			message += "; pattern should be: " + getDesiredPattern();
		}

		return message;
	}
}
