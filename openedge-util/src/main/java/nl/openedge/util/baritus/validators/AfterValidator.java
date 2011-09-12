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
 * Validates if a date is after a given date.
 * 
 * @author hofstee
 */
public class AfterValidator extends AbstractDateFieldValidator
{
	/**
	 * Error key.
	 */
	private static final String DEFAULT_MESSAGE_KEY = "invalid.field.input.after";

	/** Log. */
	private static Logger log = LoggerFactory.getLogger(AfterValidator.class);

	/**
	 * Construct.
	 */
	public AfterValidator()
	{
		setMessageKey(DEFAULT_MESSAGE_KEY);
	}

	/**
	 * Construct.
	 * 
	 * @param dateToCheck
	 *            date to check
	 */
	public AfterValidator(Calendar dateToCheck)
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
	public AfterValidator(Date dateToCheck)
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
	public AfterValidator(String messageKey)
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
	public AfterValidator(String messageKey, Calendar dateToCheck)
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
	public AfterValidator(String messageKey, Date dateToCheck)
	{
		super(messageKey, dateToCheck);
	}

	/**
	 * Construct.
	 * 
	 * @param messageKey
	 *            the message key
	 * @param rule
	 *            the validation activation rule
	 */
	public AfterValidator(String messageKey, ValidationActivationRule rule)
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
	 * @return true if value is a Date or Calendar and is before or equal to before.
	 * @throws IllegalArgumentException
	 *             when value == null.
	 */
	@Override
	public boolean isValid(ControllerContext cctx, FormBeanContext formBeanContext,
			String fieldName, Object value)
	{
		boolean after = false;
		Date compareBefore = null;
		DateComparator comp = new DateComparator();
		Date dateToCheck = getDateToCheck();

		if (value == null)
		{
			throw new IllegalArgumentException("Null cannot be validated.");
		}

		// Als before datum niet is ingesteld (null) wordt de huidige datum
		// genomen om mee te vergelijken.
		if (dateToCheck == null)
		{
			compareBefore = new Date();
		}
		else
		{
			compareBefore = dateToCheck;
		}

		// vergelijk datums
		if (value instanceof Date)
		{
			after = comp.compare((Date) value, compareBefore) <= 0;
		}
		else if (value instanceof Calendar)
		{
			after = comp.compare(((Calendar) value).getTime(), compareBefore) <= 0;
		}
		else
		{
			log.error(value.getClass() + " is not a valid date");
		}

		if (!after)
		{
			setErrorMessage(formBeanContext, fieldName, getMessageKey(), new Object[] {value,
				fieldName, compareBefore});
		}

		return after;
	}

}
