package nl.openedge.modules.types.initcommands;

/**
 * Thrown when a cyclic dependency is detected.
 * 
 * @author Eelco Hillenius
 */
public class CyclicDependencyException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public CyclicDependencyException(String message)
	{
		super(message);
	}

	public CyclicDependencyException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public CyclicDependencyException(Throwable cause)
	{
		super(cause);
	}
}
