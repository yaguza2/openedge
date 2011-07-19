package nl.openedge.baritus.interceptors;

import javax.servlet.ServletException;

import nl.openedge.baritus.FormBeanContext;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * Registered instances will have their command method executed before population and
 * validation is done.
 * 
 * @author Eelco Hillenius
 */
public interface BeforePopulationInterceptor extends Interceptor
{
	/**
	 * Registered instances will have their command method executed before population and
	 * validation is done.
	 * 
	 * @param cctx
	 *            maverick context
	 * @param formBeanContext
	 *            the context with the not-yet populated formBean
	 */
	public void doBeforePopulation(ControllerContext cctx, FormBeanContext formBeanContext)
			throws ServletException, DispatchNowFlowException, ReturnNowFlowException;
}
