package nl.openedge.baritus.interceptors;

import javax.servlet.ServletException;

import nl.openedge.baritus.FormBeanContext;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * Registered instances will have their command method executed before the normal action
 * execution took place, but after the form bean was populated.
 * 
 * @author Eelco Hillenius
 */
public interface AfterPopulationInterceptor extends Interceptor
{

	/**
	 * Executed before the normal action execution takes place, but after the form bean
	 * was populated.
	 * 
	 * @param cctx
	 *            maverick context
	 * @param formBeanContext
	 *            the context with the not-yet populated formBean
	 */
	public void doAfterPopulation(ControllerContext cctx, FormBeanContext formBeanContext)
			throws ServletException, DispatchNowFlowException, ReturnNowFlowException;

}
