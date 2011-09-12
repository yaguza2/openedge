package nl.openedge.baritus.validation;

import nl.openedge.baritus.FormBeanContext;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * Use this for custom validation on form level formValidators will be called AFTER field
 * validators executed
 * 
 * @author Eelco Hillenius
 */
public interface FormValidator
{
	/**
	 * Checks if form is valid.
	 * 
	 * @param cctx
	 *            maverick context
	 * @param formBeanContext
	 *            context with form for this currentRequest
	 * @return true if valid, false if not.
	 */
	public boolean isValid(ControllerContext cctx, FormBeanContext formBeanContext);
}
