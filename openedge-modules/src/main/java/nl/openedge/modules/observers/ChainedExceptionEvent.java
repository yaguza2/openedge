package nl.openedge.modules.observers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * event that can be fired by implementors of ChainedEventCaster.
 * 
 * @author Eelco Hillenius
 */
public final class ChainedExceptionEvent extends ChainedEvent
{
	private static final long serialVersionUID = 1L;

	/** original exception. */
	private Exception exception = null;

	/**
	 * Construct.
	 * 
	 * @param source
	 *            sender of event
	 * @param exception
	 *            original exception
	 */
	public ChainedExceptionEvent(Object source, Exception exception)
	{
		super(source);
		this.exception = exception;
	}

	/**
	 * get the embedded exception.
	 * 
	 * @return the exception
	 */
	public Exception getException()
	{
		return exception;
	}

	/**
	 * return stacktrace as a string.
	 * 
	 * @return String stacktrace as string
	 */
	public String getStackTraceAsString()
	{

		String errorMsg = "";
		if (exception != null)
		{
			try
			{
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				PrintWriter pw = new PrintWriter(bos);
				exception.printStackTrace(pw);
				pw.flush();
				pw.close();
				bos.flush();
				bos.close();
				errorMsg = bos.toString();
			}
			catch (IOException e)
			{
				errorMsg = exception.getMessage();
			}
		}
		return errorMsg;
	}

}
