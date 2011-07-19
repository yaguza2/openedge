package nl.openedge.baritus.interceptors;

import javax.servlet.ServletException;

import nl.openedge.baritus.FormBeanContext;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * Registered instances will have their command method executed if an uncaught exception
 * occurred while executing the perform method.
 * 
 * @author Eelco Hillenius
 */
public interface PerformExceptionInterceptor extends Interceptor
{
	/**
	 * Executed if an uncaught exception occurred while executing the perform method.
	 * 
	 * @param cctx
	 *            maverick context
	 * @param formBeanContext
	 *            context with form bean that failed to populate
	 * @param cause
	 *            the exception that occurred during perform
	 */
	public void doOnPerformException(ControllerContext cctx, FormBeanContext formBeanContext,
			Exception cause) throws ServletException, DispatchNowFlowException,
			ReturnNowFlowException;
}
