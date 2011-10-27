package nl.openedge.baritus.validation;

import nl.openedge.baritus.FormBeanContext;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * Use this for custom validation
 * 
 * @author Eelco Hillenius
 */
public interface FieldValidator
{
	/**
	 * Checks if value is valid. This method should return true if validation succeeded or
	 * false otherwise. You should register errors directly with the formBeanContext.
	 * 
	 * @param cctx
	 *            maverick context
	 * @param formBeanContext
	 *            context with bean for this currentRequest
	 * @param fieldName
	 *            field name of parameter
	 * @param value
	 *            the value of this parameter
	 * @return true if valid, false if not.
	 */
	public boolean isValid(ControllerContext cctx, FormBeanContext formBeanContext,
			String fieldName, Object value);

	/**
	 * if value is not valid, get the custom value to set as the field override in the
	 * form.
	 * 
	 * @param value
	 *            the original input value
	 * @return the value that should be used as override value
	 */
	public Object getOverrideValue(Object value);

}
