package nl.openedge.baritus.validation.impl;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.validation.AbstractFieldValidator;
import nl.openedge.baritus.validation.ValidationActivationRule;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * Checks for a non-EMPTY input. Use this for fields that should have a not null (empty
 * string) input. Note that as this validator is a field validator, and thus is registered
 * for a single field, it is only fired if a field (e.g. a request parameter) is actually
 * provided. In other words: if an instance of a required field validator was registered
 * for field name 'myprop', but 'myprop' is not part of the request parameters, this
 * validator never fires. Hence, if you want to be REALLY sure that a property of the form
 * is not null, use a form validator (PropertyNotNullValidator). RequiredFieldValidator
 * works fine for most cases where you have a HTML form with a field that should have a
 * non empty value, but that - if a user fools around - does not seriousely break anything
 * when a value is not provided (e.g. you probably have not null constraint in you
 * database as well).
 * 
 * @author Eelco Hillenius
 */
public class RequiredFieldValidator extends AbstractFieldValidator
{
	private String errorMessageKey = "input.field.required";

	/**
	 * Construct using 'input.field.required' as the message prefix.
	 */
	public RequiredFieldValidator()
	{

	}

	/**
	 * Construct using the provided activation rule and 'input.field.required' as the
	 * message prefix.
	 * 
	 * @param rule
	 */
	public RequiredFieldValidator(ValidationActivationRule rule)
	{
		setValidationRule(rule);
	}

	/**
	 * Construct using errorMessageKey and the activation rule
	 * 
	 * @param errorMessageKey
	 * @param rule
	 */
	public RequiredFieldValidator(String errorMessageKey, ValidationActivationRule rule)
	{
		setErrorMessageKey(errorMessageKey);
		setValidationRule(rule);
	}

	/**
	 * Construct with errorMessageKey for error message keys.
	 * 
	 * @param errorMessageKey
	 *            errorMessageKey
	 */
	public RequiredFieldValidator(String errorMessageKey)
	{
		setErrorMessageKey(errorMessageKey);
	}

	/**
	 * Checks whether the value is not null, and - if it is an instance of String -
	 * whether the trimmed value is not an empty string. Note that as this validator is a
	 * field validator, and thus is registered for a single field, it is only fired if a
	 * field (e.g. a request parameter) is actually provided. In other words: if an
	 * instance of a required field validator was registered for field name 'myprop', but
	 * 'myprop' is not part of the request parameters, this validator never fires. Hence,
	 * if you want to be REALLY sure that a property of the form is not null, use a form
	 * validator (PropertyNotNullValidator). RequiredFieldValidator works fine for most
	 * cases where you have a HTML form with a field that should have a non empty value,
	 * but that - if a user fools around - does not seriousely break anything when a value
	 * is not provided (e.g. you probably have not null constraint in you database as
	 * well).
	 * 
	 * @return boolean true if not null or empty, false otherwise
	 * @see nl.openedge.baritus.validation.FieldValidator#isValid(org.infohazard.maverick.flow.ControllerContext,
	 *      nl.openedge.baritus.FormBeanContext, java.lang.String, java.lang.Object)
	 */
	@Override
	public boolean isValid(ControllerContext cctx, FormBeanContext formBeanContext,
			String fieldName, Object value)
	{
		boolean isValid = true;
		if (value == null)
		{
			isValid = false;
		}
		else
		{
			if (value instanceof String)
			{
				String stringValue = ((String) value).trim();
				isValid = !stringValue.isEmpty();
			}
		}

		if (!isValid)
		{
			setErrorMessage(formBeanContext, fieldName, getErrorMessageKey(),
				new Object[] {getFieldName(formBeanContext, fieldName)});
		}

		return isValid;
	}

	/**
	 * Get key of error message.
	 * 
	 * @return String key of error message
	 */
	public String getErrorMessageKey()
	{
		return errorMessageKey;
	}

	/**
	 * Set key of error message.
	 * 
	 * @param string
	 *            key of error message
	 */
	public void setErrorMessageKey(String string)
	{
		errorMessageKey = string;
	}
}
