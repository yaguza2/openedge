/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003, Promedico ICT B.V.
 * All rights reserved.
 */
package nl.openedge.maverick.framework.validation;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import nl.openedge.maverick.framework.AbstractForm;
import nl.openedge.util.DateFormatHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infohazard.maverick.flow.ControllerContext;

/**
 * Validates if a date is after a given date
 * 
 * @author hofstee
 */
public class AfterValidator extends AbstractFieldValidator
{
	private Log log = LogFactory.getLog(AfterValidator.class);
	
	private final static String DEFAULT_PREFIX = "invalid.field.input.after";
	
	/**
	 * The date check against.
	 */
	private Date before = new Date();
	
	/** 
	 * The pattern to use when parsing the date.
	 */
	private String datePattern = "dd-MM-yyyy";

	/**
	 * Creates a AfterValidator with the error key prefix and the 
	 * date to check against before.
	 * @param prefix
	 * @param before
	 */
	public AfterValidator(String prefix, Date before)
	{
		super(prefix);
		setBefore(before);
	}
	
	/**
	 * Creates a AfterValidator with the DEFAULT_PREFIX as error key
	 * and today as the date to check against.
	 *
	 */
	public AfterValidator()
	{
		this(DEFAULT_PREFIX, new Date());
	}
	
	/**
	 * Creates a AfterValidator with the error key DEFAULT_PREFIX and the 
	 * date to check against before.
	 * @param prefix
	 * @param before
	 */
	public AfterValidator(Date after)
	{
		this(DEFAULT_PREFIX, after);
	}
	
	/**
	 * Creates a AfterValidator with the error key prefix and today to check against before.
	 * @param prefix
	 * @param before
	 */
	public AfterValidator(String prefix)
	{
		this(prefix, new Date());
	}
	
	/**
	 * Creates a AfterValidator with the error key prefix and the 
	 * date to check against before.
	 * @param prefix
	 * @param before
	 */
	public AfterValidator(String prefix, Calendar before)
	{
		this(prefix, before.getTime());
	}
	
	/**
	 * @param rule
	 */
	public AfterValidator(ValidatorActivationRule rule)
	{
		super(rule);
	}

	/**
	 * @param messagePrefix
	 * @param rule
	 */
	public AfterValidator(String messagePrefix, ValidatorActivationRule rule)
	{
		super(messagePrefix, rule);
	}

	/**
	 * Creates a AfterValidator with the error key DEFAULT_PREFIX and the 
	 * date to check against before.
	 * @param prefix
	 * @param before
	 */
	public AfterValidator(Calendar before)
	{
		this(DEFAULT_PREFIX, before.getTime());
	}
	
	/**
	 * @return Returns the before.
	 */
	public Date getBefore()
	{
		return before;
	}

	/**
	 * @param before The before to set.
	 */
	public void setBefore(Date after)
	{
		if (after == null)
		{
			throw new IllegalArgumentException("unable to validate null value");
		}
		this.before = after;
	}

	/**
	 * @return true if value is a Date or Calendar and is before before.
	 * @see nl.openedge.maverick.framework.validation.FieldValidator#isValid(org.infohazard.maverick.flow.ControllerContext, nl.openedge.maverick.framework.AbstractForm, java.lang.String, java.lang.Object)
	 */
	public boolean isValid(ControllerContext cctx, AbstractForm form, String fieldName, Object value)
	{
		boolean after = false;
		if (value instanceof Date)
		{
			after = ((Date)value).after(before);
		}
		else if (value instanceof Calendar)
		{
			after = ((Calendar)value).getTime().after(before);
		}
		else
		{
			log.error(value.getClass() + " is not a valid date");
		}

		return after;
	}
	
	/**
	 * Tries to format value to a DateFormat. If this doesn't work, 
	 * returns value unchanged.
	 * @see nl.openedge.maverick.framework.validation.AbstractFieldValidator#getOverrideValue(java.lang.Object)
	 */
	public Object getOverrideValue(Object value)
	{
		String result = "";
		if (value instanceof String)
		{	
			try
			{
				result = DateFormatHelper.format(datePattern, 
					DateFormatHelper.fallbackParse((String)value));
			}
			catch(ParseException e)
			{
				// value is not a valid date; ignore
			}
		}
		else if (value instanceof Date)
		{
			result = DateFormatHelper.format(datePattern, (Date)value);
		}
		else if (value  instanceof Calendar)
		{
			result = DateFormatHelper.format(datePattern, ((Calendar)value).getTime());
		}
		return result;
	}

	/**
	 * Return the pattern used to format the error value if it is a date
	 * @return Returns the datePattern.
	 */
	public String getDatePattern()
	{
		return datePattern;
	}

	/**
     * Set the pattern used to format the error value if it is a date
	 * @param datePattern The datePattern to set.
	 */
	public void setDatePattern(String datePattern)
	{
		this.datePattern = datePattern;
	}
	
	/**
	 * Returns the error message.
	 */
	public String getErrorMessage(
		ControllerContext cctx,
		AbstractForm form,
		String fieldName,
		Object value,
		Locale locale)
	{
		return getLocalizedMessage(getMessagePrefix(), locale, new Object[]{getOverrideValue(value), fieldName});
	}

}
