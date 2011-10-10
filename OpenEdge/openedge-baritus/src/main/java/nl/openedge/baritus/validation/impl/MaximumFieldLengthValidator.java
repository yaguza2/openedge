package nl.openedge.baritus.validation.impl;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.validation.AbstractFieldValidator;
import nl.openedge.baritus.validation.ValidationActivationRule;

import org.infohazard.maverick.flow.ControllerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This validator checks on maximum length. E.g. if property maxLength is 4, "hello" will
 * fail, but "hi" will pass, and number 47535 will fail, but 635 will pass.
 * 
 * @author Eelco Hillenius
 */
public final class MaximumFieldLengthValidator extends AbstractFieldValidator
{
	/** special value that indicates there's no maximum value to check on */
	public final static int NO_MAXIMUM = -1;

	private int maxLength = NO_MAXIMUM;

	private static Logger log = LoggerFactory.getLogger(MaximumFieldLengthValidator.class);

	private String errorMessageKey = "invalid.field.input.size";

	/**
	 * construct with 'invalid.field.input.size' as message prefix.
	 */
	public MaximumFieldLengthValidator()
	{

	}

	/**
	 * Construct with errorMessageKey for error message keys.
	 * 
	 * @param errorMessageKey
	 */
	public MaximumFieldLengthValidator(String errorMessageKey)
	{
		setErrorMessageKey(errorMessageKey);
	}

	/**
	 * Construct with message prefix for error message keys and set checking on maximum
	 * length with given length of fields only.
	 * 
	 * @param errorMessageKey
	 *            message key
	 * @param maxLength
	 *            maximum length allowed for values; use -1 for no maximum
	 */
	public MaximumFieldLengthValidator(String errorMessageKey, int maxLength)
	{
		setErrorMessageKey(errorMessageKey);
		setMaxLength(maxLength);
	}

	/**
	 * Construct with activation rule.
	 * 
	 * @param rule
	 *            activation rule
	 */
	public MaximumFieldLengthValidator(ValidationActivationRule rule)
	{
		setValidationRule(rule);
	}

	/**
	 * Construct with errorMessageKey and activation rule.
	 * 
	 * @param errorMessageKey
	 *            error message key
	 * @param rule
	 *            activation rule
	 */
	public MaximumFieldLengthValidator(String errorMessageKey, ValidationActivationRule rule)
	{
		setErrorMessageKey(errorMessageKey);
		setValidationRule(rule);
	}

	/**
	 * Construct with maxLength.
	 * 
	 * @param maxLength
	 *            maximum length allowed for values; use -1 for no maximum
	 */
	public MaximumFieldLengthValidator(int maxLength)
	{
		setMaxLength(maxLength);
	}

	/**
	 * Checks whether the lenth of the provided value is less than the maximum.
	 * 
	 * @return boolean true if the length of value is equal to or less than the maxLength
	 *         property, false otherwise
	 * @see nl.openedge.baritus.validation.FieldValidator#isValid(org.infohazard.maverick.flow.ControllerContext,
	 *      nl.openedge.baritus.FormBeanContext, java.lang.String, java.lang.Object)
	 */
	@Override
	public boolean isValid(ControllerContext cctx, FormBeanContext formBeanContext,
			String fieldName, Object value)
	{
		if (maxLength == NO_MAXIMUM)
			return true;

		boolean maxExceeded = false;
		if (value != null)
		{
			if (value instanceof String)
			{
				String toCheck = (String) value;
				int length = toCheck.length();
				maxExceeded = (length > maxLength);
			}
			else if (value instanceof Number)
			{
				Number toCheck = (Number) value;
				int length = toCheck.toString().length();
				maxExceeded = (length > maxLength);
			}
			else
			{
				// just ignore; wrong type to check on
				log.warn(fieldName + " with value: " + value
					+ " is of the wrong type for checking on length");
				// give a warning though
			}
		}

		if (maxExceeded)
		{
			setErrorMessage(formBeanContext, fieldName, getErrorMessageKey(), new Object[] {
				getFieldName(formBeanContext, fieldName), value, Integer.valueOf(maxLength)});
		}

		return (!maxExceeded);
	}

	/**
	 * Get maximum length that is checked on.
	 * 
	 * @return int maximum length that is checked on
	 */
	public int getMaxLength()
	{
		return maxLength;
	}

	/**
	 * Set maximum length that is checked on .
	 * 
	 * @param i
	 *            maximum length that is checked on
	 */
	public void setMaxLength(int i)
	{
		maxLength = i;
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
