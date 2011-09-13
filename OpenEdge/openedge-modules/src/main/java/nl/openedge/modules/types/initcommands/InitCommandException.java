package nl.openedge.modules.types.initcommands;

/**
 * Exception for InitCommands.
 * 
 * @author Eelco Hillenius
 */
public class InitCommandException extends Exception
{
	private static final long serialVersionUID = 1L;

	public InitCommandException(String message)
	{
		super(message);
	}

	public InitCommandException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InitCommandException(Throwable cause)
	{
		super(cause);
	}
}
