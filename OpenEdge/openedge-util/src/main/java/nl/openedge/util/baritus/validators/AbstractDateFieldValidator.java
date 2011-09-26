package nl.openedge.util.baritus.validators;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import nl.openedge.baritus.validation.AbstractFieldValidator;
import nl.openedge.baritus.validation.ValidationActivationRule;
import nl.openedge.util.DateFormatHelper;

/**
 * Abstract class for date validations on field level.
 * 
 * @author hofstee
 * @author Eelco Hillenius
 */
public abstract class AbstractDateFieldValidator extends AbstractFieldValidator
{
	/**
	 * Error key.
	 */
	private static final String DEFAULT_MESSAGE_KEY = "invalid.field.input";

	/**
	 * Message key.
	 */
	private String messageKey = DEFAULT_MESSAGE_KEY;

	/**
	 * The date to check against.
	 */
	private Date dateToCheck = null;

	/**
	 * The pattern to use when parsing the date.
	 */
	private String datePattern = "dd-MM-yyyy";

	/**
	 * Creates a AfterValidator with the error message key and the date to check against.
	 * 
	 * @param messageKey
	 *            message key
	 * @param dateToCheck
	 *            date to check
	 */
	public AbstractDateFieldValidator(String messageKey, Date dateToCheck)
	{
		this.messageKey = messageKey;
		this.dateToCheck = dateToCheck;
	}

	/**
	 * Creates a AfterValidator with the DEFAULT_PREFIX as error key and today as the date
	 * to check against.
	 */
	public AbstractDateFieldValidator()
	{
		this(DEFAULT_MESSAGE_KEY);
	}

	/**
	 * Creates a AfterValidator with the error key DEFAULT_PREFIX and the date to check
	 * against before.
	 * 
	 * @param dateToCheck
	 *            date to check
	 */
	public AbstractDateFieldValidator(Date dateToCheck)
	{
		this(DEFAULT_MESSAGE_KEY, dateToCheck);
	}

	/**
	 * Creates a AfterValidator with the error key prefix and today to check against
	 * before.
	 * 
	 * @param messageKey
	 *            message key
	 */
	public AbstractDateFieldValidator(String messageKey)
	{
		this(messageKey, new Date());
	}

	/**
	 * Creates a AfterValidator with the error message key and the date to check against.
	 * 
	 * @param messageKey
	 *            message key
	 * @param dateToCheck
	 *            date to check
	 */
	public AbstractDateFieldValidator(String messageKey, Calendar dateToCheck)
	{
		this(messageKey, dateToCheck.getTime());
	}

	/**
	 * Creates a AfterValidator with the error message key and the date to check against.
	 * 
	 * @param messageKey
	 *            message key
	 * @param rule
	 *            activation rule
	 */
	public AbstractDateFieldValidator(String messageKey, ValidationActivationRule rule)
	{
		super(rule);
		this.messageKey = messageKey;
	}

	/**
	 * Creates a AfterValidator with the error key DEFAULT_PREFIX and the date to check
	 * against.
	 * 
	 * @param dateToCheck
	 *            date to check
	 */
	public AbstractDateFieldValidator(Calendar dateToCheck)
	{
		this(DEFAULT_MESSAGE_KEY, dateToCheck.getTime());
	}

	/**
	 * Gets the date to check.
	 * 
	 * @return the date to check
	 */
	public Date getDateToCheck()
	{
		return dateToCheck;
	}

	/**
	 * Sets the date to check.
	 * 
	 * @param dateToCheck
	 *            The date to check.
	 */
	public void setDateToCheck(Date dateToCheck)
	{
		if (dateToCheck == null)
		{
			throw new IllegalArgumentException("unable to validate null value");
		}
		this.dateToCheck = dateToCheck;
	}

	/**
	 * Tries to format value to a DateFormat. If this doesn't work, returns value
	 * unchanged.
	 */
	@Override
	public Object getOverrideValue(Object value)
	{
		String result = "";
		if (value instanceof String)
		{
			try
			{
				result =
					DateFormatHelper.format(datePattern,
						DateFormatHelper.fallbackParse((String) value));
			}
			catch (ParseException e)
			{
				// value is not a valid date; ignore
			}
		}
		else if (value instanceof Date)
		{
			result = DateFormatHelper.format(datePattern, (Date) value);
		}
		else if (value instanceof Calendar)
		{
			result = DateFormatHelper.format(datePattern, ((Calendar) value).getTime());
		}
		return result;
	}

	/**
	 * Return the pattern used to format the error value if it is a date.
	 * 
	 * @return Returns the datePattern.
	 */
	public String getDatePattern()
	{
		return datePattern;
	}

	/**
	 * Set the pattern used to format the error value if it is a date.
	 * 
	 * @param datePattern
	 *            The datePattern to set.
	 */
	public void setDatePattern(String datePattern)
	{
		this.datePattern = datePattern;
	}

	public String getErrorMessage(String fieldName, Object value, Locale locale)
	{
		return getLocalizedMessage(getMessageKey(), locale, new Object[] {getOverrideValue(value),
			fieldName});
	}

	/**
	 * Get prefix.
	 * 
	 * @return String Returns the prefix.
	 */
	public String getMessageKey()
	{
		return messageKey;
	}

	/**
	 * Set prefix.
	 * 
	 * @param prefix
	 *            prefix to set.
	 */
	public void setMessageKey(String prefix)
	{
		this.messageKey = prefix;
	}
}
