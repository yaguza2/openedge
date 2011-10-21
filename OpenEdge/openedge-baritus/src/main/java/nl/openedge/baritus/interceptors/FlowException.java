package nl.openedge.baritus.interceptors;

/**
 * Flow exception; base exception for flow exceptions.
 * 
 * @author Eelco Hillenius
 */
public abstract class FlowException extends Exception
{

	private static final long serialVersionUID = 1L;

	/**
	 * Whether, in case of the action being ACTION_SHOW_VIEW or ACTION_DISPATCH, the
	 * registered other interceptors should be executed before displaying the view. The
	 * default is true. If true, and the current stage of intercepting is
	 * 'beforeMakeFormBean', the BeforePopulationInterceptors will be executed. If true,
	 * for all other stages except 'afterPerform' the AfterPerformInterceptors will be
	 * executed.
	 */
	private boolean executeOtherInterceptors = true;

	/**
	 * @return boolean
	 */
	public boolean isExecuteOtherInterceptors()
	{
		return executeOtherInterceptors;
	}

	/**
	 * @param b
	 */
	public void setExecuteOtherInterceptors(boolean b)
	{
		executeOtherInterceptors = b;
	}

}
