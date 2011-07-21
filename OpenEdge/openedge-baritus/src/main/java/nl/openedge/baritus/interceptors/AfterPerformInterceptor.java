package nl.openedge.baritus.interceptors;

import javax.servlet.ServletException;

import nl.openedge.baritus.FormBeanContext;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * Registered instances will have their command method executed after the normal action
 * execution took place. That means that makeFormBean was called, the form was populated
 * and - if that population was succesfull - the command method was called prior to this
 * execution. Hence, this interceptor will allways be executed, regardless population/
 * validation and regardless whether the perform method actually was executed.
 * 
 * @author Eelco Hillenius
 */
public interface AfterPerformInterceptor extends Interceptor
{

	/**
	 * Executed after the normal action execution took place. That means that makeFormBean
	 * was called, the form was populated and - if that population was succesfull - the
	 * command method was called prior to this execution. Hence, this interceptor will
	 * allways be executed, regardless population/ validation and regardless whether the
	 * perform method actually was executed.
	 * 
	 * NOTE. You cannot be sure that the form was populated successfully. Therefore it's
	 * dangerous and generally bad practice to rely on form properties that are populated
	 * from the http request. A good usage example: a lot of views need data to fill their
	 * dropdown lists etc. In this method, you could load that data and save it in the
	 * form (or as a request attribute if that's your style). As this method is allways
	 * executed, you have a guaranteed data delivery to your view, regardless the normal
	 * execution outcome of the control.
	 * 
	 * @param cctx
	 *            maverick context
	 * @param formBeanContext
	 *            the context with the (possibly succesfull) populated formBean
	 */
	public void doAfterPerform(ControllerContext cctx, FormBeanContext formBeanContext)
			throws ServletException, DispatchNowFlowException, ReturnNowFlowException;

}
