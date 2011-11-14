package nl.openedge.baritus.validation;

import nl.openedge.baritus.FormBeanContext;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * interface that can be used to switch whether validation with custom fieldValidators
 * should be performed in the current request.
 * 
 * @author Eelco Hillenius
 */
public interface ValidationActivationRule
{
	/**
	 * returns whether validation with custom fieldValidators should be performed in this
	 * currentRequest.
	 * 
	 * @param cctx
	 *            maverick context
	 * @param formBeanContext
	 *            context with form for this currentRequest
	 * @return whether validation with custom fieldValidators should be performed in this
	 *         currentRequest.
	 */
	public boolean allowValidation(ControllerContext cctx, FormBeanContext formBeanContext);
}
