package nl.openedge.baritus.interceptors;

/**
 * Flow exception; is thrown when an interceptor wants to dispatch to another URL
 * immediately.
 * 
 * @author Eelco Hillenius
 */
public final class DispatchNowFlowException extends FlowException
{

	private static final long serialVersionUID = 1L;

	// url to dispatch to
	private String dispatchPath;

	/**
	 * Construct with dispatch path.
	 * 
	 * @param dispatchPath
	 */
	public DispatchNowFlowException(String dispatchPath)
	{
		this.dispatchPath = dispatchPath;
	}

	/**
	 * Get the path to dispatch to
	 * 
	 * @return
	 */
	public String getDispatchPath()
	{
		return dispatchPath;
	}

	/**
	 * String representation.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return super.toString() + " {dispatchPath=" + dispatchPath + "}";
	}

}
