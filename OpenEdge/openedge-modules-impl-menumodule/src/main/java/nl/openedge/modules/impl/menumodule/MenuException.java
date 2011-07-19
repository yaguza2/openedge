package nl.openedge.modules.impl.menumodule;

/**
 * Exception for menu related stuff.
 * 
 * @author Eelco Hillenius
 */
public final class MenuException extends Exception
{
	private static final long serialVersionUID = 1L;

	public MenuException(String message)
	{
		super(message);
	}

	public MenuException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public MenuException(Throwable cause)
	{
		super(cause);
	}
}
