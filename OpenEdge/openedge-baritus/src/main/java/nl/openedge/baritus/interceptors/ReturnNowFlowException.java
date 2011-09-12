package nl.openedge.baritus.interceptors;

/**
 * Flow exception; is thrown when an interceptor wants an override of the default view,
 * and this view should be directed to immediately.
 * 
 * @author Eelco Hillenius
 */
public final class ReturnNowFlowException extends FlowException
{

	private static final long serialVersionUID = 1L;

	private String view;

	/**
	 * Construct with view.
	 * 
	 * @param view
	 *            view to return
	 */
	public ReturnNowFlowException(String view)
	{
		this.view = view;
	}

	/**
	 * Get the view to return.
	 * 
	 * @return String view to return
	 */
	public String getView()
	{
		return view;
	}

	/**
	 * String representation.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return super.toString() + " {view=" + view + "}";
	}

}
