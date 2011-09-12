package nl.openedge.baritus.validation.impl;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.validation.AbstractFieldValidator;
import nl.openedge.baritus.validation.ValidationActivationRule;

import org.infohazard.maverick.flow.ControllerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This validator checks on minimum length. E.g. if property minLength is 4, "hello" will
 * pass, but "hi" will fail, and number 98765 will pass, but 159 will fail.
 * 
 * @author Eelco Hillenius
 */
public final class MinimumFieldLengthValidator extends AbstractFieldValidator
{
	/** special value that indicates there's no min value to check on */
	public final static int NO_MINIMUM = -1;

	private int minLength = NO_MINIMUM;

	private static Logger log = LoggerFactory.getLogger(MinimumFieldLengthValidator.class);

	private String errorMessageKey = "invalid.field.input.size";

	/**
	 * Construct with key invalid.field.input.size for error messages.
	 */
	public MinimumFieldLengthValidator()
	{

	}

	/**
	 * Construct with message prefix for error message keys.
	 * 
	 * @param errorMessageKey
	 *            message key of error
	 */
	public MinimumFieldLengthValidator(String errorMessageKey)
	{
		setErrorMessageKey(errorMessageKey);
	}

	/**
	 * Construct with message key for error message keys and set checking on minimum
	 * length with given length of fields only.
	 * 
	 * @param errorMessageKey
	 *            key for error messages
	 * @param minLength
	 *            minimum length allowed for values; use -1 for no minimum
	 */
	public MinimumFieldLengthValidator(String errorMessageKey, int minLength)
	{
		setErrorMessageKey(errorMessageKey);
		setMinLength(minLength);
	}

	/**
	 * Construct with activation rule.
	 * 
	 * @param rule
	 *            activation rule
	 */
	public MinimumFieldLengthValidator(ValidationActivationRule rule)
	{
		setValidationRule(rule);
	}

	/**
	 * Construct with errorMessageKey and activation rule.
	 * 
	 * @param errorMessageKey
	 * @param rule
	 *            activation rule
	 */
	public MinimumFieldLengthValidator(String errorMessageKey, ValidationActivationRule rule)
	{
		setErrorMessageKey(errorMessageKey);
		setValidationRule(rule);
	}

	/**
	 * Construct with min length.
	 * 
	 * @param minLength
	 *            minimum length allowed for values; use -1 for no minimum
	 */
	public MinimumFieldLengthValidator(int minLength)
	{
		setMinLength(minLength);
	}

	/**
	 * Checks whether the provided value is greater than the minimum. In case the value is
	 * an instance of string: checks whether the length of the string is equal to or
	 * smaller than the minLength property. In case the value is an instance of number:
	 * checks whether the length of the integer value is equal to or smaller than the
	 * minLength property.
	 * 
	 * @return boolean true if the length of value is equal to or less than the minLength
	 *         property, false otherwise
	 * @see nl.openedge.baritus.validation.FieldValidator#isValid(org.infohazard.maverick.flow.ControllerContext,
	 *      nl.openedge.baritus.FormBeanContext, java.lang.String, java.lang.Object)
	 */
	@Override
	public boolean isValid(ControllerContext cctx, FormBeanContext formBeanContext,
			String fieldName, Object value)
	{
		if (minLength == NO_MINIMUM)
			return true;

		boolean minExceeded = false;
		if (value != null)
		{
			if (value instanceof String)
			{
				String toCheck = (String) value;
				int length = toCheck.length();
				minExceeded = (length < minLength);
			}
			else if (value instanceof Number)
			{
				Number toCheck = (Number) value;
				int length = toCheck.toString().length();
				minExceeded = (length < minLength);
			}
			else
			{
				// just ignore; wrong type to check on
				log.warn(fieldName + " with value: " + value
					+ " is of the wrong type for checking on length");
			}
		}

		if (minExceeded)
		{
			setErrorMessage(formBeanContext, fieldName, getErrorMessageKey(), new Object[] {
				getFieldName(formBeanContext, fieldName), value, Integer.valueOf(minLength)});
		}

		return (!minExceeded);
	}

	/**
	 * @return int minimum length that is checked on
	 */
	public int getMinLength()
	{
		return minLength;
	}

	/**
	 * @param i
	 *            minimum length that is checked on
	 */
	public void setMinLength(int i)
	{
		minLength = i;
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
