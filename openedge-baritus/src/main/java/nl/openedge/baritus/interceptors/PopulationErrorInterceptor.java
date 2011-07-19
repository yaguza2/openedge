package nl.openedge.baritus.interceptors;

import javax.servlet.ServletException;

import nl.openedge.baritus.FormBeanContext;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * Registered instances will have their command method executed if the model failed to
 * populate, or did not pass validation.
 * 
 * @author Eelco Hillenius
 */
public interface PopulationErrorInterceptor extends Interceptor
{
	/**
	 * Executed if the model failed to populate, or did not pass validation.
	 * 
	 * @param cctx
	 *            maverick context
	 * @param formBeanContext
	 *            context with form bean that failed to populate
	 * @param cause
	 *            possibly the exception that caused the population error
	 */
	public void doOnPopulationError(ControllerContext cctx, FormBeanContext formBeanContext,
			Exception cause) throws ServletException, DispatchNowFlowException,
			ReturnNowFlowException;
}
