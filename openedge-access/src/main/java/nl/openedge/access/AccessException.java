package nl.openedge.access;

/**
 * General exception for use in the OpenEdge Access framework.
 * 
 * @author Eelco Hillenius
 */
public class AccessException extends Exception
{
	private static final long serialVersionUID = 1L;

	public AccessException(String message)
	{
		super(message);
	}

	public AccessException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public AccessException(Throwable cause)
	{
		super(cause);
	}
}
