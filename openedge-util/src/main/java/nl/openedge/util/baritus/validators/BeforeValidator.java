package nl.openedge.util.baritus.validators;

import java.util.Calendar;
import java.util.Date;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.validation.ValidationActivationRule;
import nl.openedge.util.DateComparator;

import org.infohazard.maverick.flow.ControllerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validates if a date is before a given date.
 * 
 * @author hofstee
 */
public class BeforeValidator extends AbstractDateFieldValidator
{
	/**
	 * Error key.
	 */
	private static final String DEFAULT_MESSAGE_KEY = "invalid.field.input.before";

	/**
	 * Logger.
	 */
	private Logger log = LoggerFactory.getLogger(BeforeValidator.class);

	/**
	 * Construct.
	 */
	public BeforeValidator()
	{
		setMessageKey(DEFAULT_MESSAGE_KEY);
	}

	/**
	 * Construct.
	 * 
	 * @param dateToCheck
	 *            date to check
	 */
	public BeforeValidator(Calendar dateToCheck)
	{
		super(dateToCheck);
		setMessageKey(DEFAULT_MESSAGE_KEY);
	}

	/**
	 * Construct.
	 * 
	 * @param dateToCheck
	 *            date to check
	 */
	public BeforeValidator(Date dateToCheck)
	{
		super(dateToCheck);
		setMessageKey(DEFAULT_MESSAGE_KEY);
	}

	/**
	 * Construct.
	 * 
	 * @param messageKey
	 *            the message key
	 */
	public BeforeValidator(String messageKey)
	{
		super(messageKey);
	}

	/**
	 * Construct.
	 * 
	 * @param messageKey
	 *            the message key
	 * @param dateToCheck
	 *            date to check
	 */
	public BeforeValidator(String messageKey, Calendar dateToCheck)
	{
		super(messageKey, dateToCheck);
	}

	/**
	 * Construct.
	 * 
	 * @param messageKey
	 *            the message key
	 * @param dateToCheck
	 *            date to check
	 */
	public BeforeValidator(String messageKey, Date dateToCheck)
	{
		super(messageKey, dateToCheck);
	}

	/**
	 * Construct.
	 * 
	 * @param messageKey
	 *            the message key
	 * @param rule
	 *            validation activation rule
	 */
	public BeforeValidator(String messageKey, ValidationActivationRule rule)
	{
		super(messageKey, rule);
	}

	/**
	 * Check valid. If false an error message is set with parameters: {value, fieldName,
	 * checkDate}.
	 * 
	 * @param cctx
	 *            controller context
	 * @param formBeanContext
	 *            form bean context
	 * @param fieldName
	 *            field name
	 * @param value
	 *            object to check
	 * @return true if value is a Date or Calendar and is before or equal to after.
	 * @throws IllegalArgumentException
	 *             when value == null.
	 */
	@Override
	public boolean isValid(ControllerContext cctx, FormBeanContext formBeanContext,
			String fieldName, Object value)
	{
		boolean before = false;
		Date compareAfter = null;
		Date dateToCheck = getDateToCheck();
		DateComparator comp = new DateComparator();

		if (value == null)
		{
			throw new IllegalArgumentException("Null cannot be validated.");
		}

		// Als before datum niet is ingesteld (null) wordt de huidige datum
		// genomen om mee te vergelijken.
		if (dateToCheck == null)
		{
			compareAfter = new Date();
		}
		else
		{
			compareAfter = dateToCheck;
		}

		// vergelijk datums
		if (value instanceof Date)
		{
			before = comp.compare((Date) value, compareAfter) >= 0;
		}
		else if (value instanceof Calendar)
		{
			before = comp.compare(((Calendar) value).getTime(), compareAfter) >= 0;
		}
		else
		{
			log.error(value.getClass() + " is not a valid date");
		}

		if (!before)
		{
			setErrorMessage(formBeanContext, fieldName, getMessageKey(), new Object[] {value,
				fieldName, compareAfter});
		}

		return before;
	}

}
