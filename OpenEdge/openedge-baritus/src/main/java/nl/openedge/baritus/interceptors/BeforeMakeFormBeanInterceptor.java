package nl.openedge.baritus.interceptors;

import javax.servlet.ServletException;

import nl.openedge.baritus.FormBeanContext;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * Registered instances will have their command method executed before the method
 * makeFormBean is called.
 * 
 * @author Eelco Hillenius
 */
public interface BeforeMakeFormBeanInterceptor extends Interceptor
{
	/**
	 * Executed before the method makeFormBean is called.
	 * 
	 * @param cctx
	 *            maverick context
	 * @param formBeanContext
	 *            the context with the not-yet populated formBean
	 */
	public void doBeforeMakeFormBean(ControllerContext cctx, FormBeanContext formBeanContext)
			throws ServletException, DispatchNowFlowException, ReturnNowFlowException;
}
