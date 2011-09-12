package nl.openedge.modules.config;

/**
 * ConfigExceptions are exceptions that are related to invalid configurations.
 * 
 * @author Eelco Hillenius
 */
public final class ConfigException extends Exception
{

	private static final long serialVersionUID = 1L;

	public ConfigException()
	{
	}

	public ConfigException(String message)
	{
		super(message);
	}

	public ConfigException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ConfigException(Throwable cause)
	{
		super(cause);
	}
}
