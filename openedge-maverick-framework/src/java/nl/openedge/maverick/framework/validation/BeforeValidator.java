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
 * Validates if a date is before a given date
 * 
 * @author hofstee
 */
public class BeforeValidator extends AbstractFieldValidator
{
	private Log log = LogFactory.getLog(BeforeValidator.class);
	
	private final static String DEFAULT_PREFIX = "invalid.field.input.after";
	
	/**
	 * The date check against.
	 */
	private Date after = new Date();
	
	/** 
	 * The pattern to use when parsing the date.
	 */
	private String datePattern = "dd-MM-yyyy";
	
	/**
	 * Creates a BeforeValidator with the error key prefix and the 
	 * date to check against after.
	 * @param prefix
	 * @param after
	 */
	public BeforeValidator(String prefix, Date after)
	{
		super(prefix);
		setAfter(after);
	}
	
	/**
	 * Creates a BeforeValidator with the DEFAULT_PREFIX as error key
	 * and today as the date to check against.
	 *
	 */
	public BeforeValidator()
	{
		this(DEFAULT_PREFIX, new Date());
	}
	
	/**
	 * Creates a BeforeValidator with the error key DEFAULT_PREFIX and the 
	 * date to check against after.
	 * @param prefix
	 * @param after
	 */
	public BeforeValidator(Date after)
	{
		this(DEFAULT_PREFIX, after);
	}
	
	/**
	 * Creates a BeforeValidator with the error key prefix and today to check against after.
	 * @param prefix
	 * @param after
	 */
	public BeforeValidator(String prefix)
	{
		this(prefix, new Date());
	}
	
	/**
	 * Creates a BeforeValidator with the error key prefix and the 
	 * date to check against after.
	 * @param prefix
	 * @param after
	 */
	public BeforeValidator(String prefix, Calendar after)
	{
		this(prefix, after.getTime());
	}

	/**
	 * @param messagePrefix
	 * @param rule
	 */
	public BeforeValidator(String messagePrefix, ValidatorActivationRule rule)
	{
		super(messagePrefix, rule);
	}

	/**
	 * Creates a BeforeValidator with the error key DEFAULT_PREFIX and the 
	 * date to check against after.
	 * @param prefix
	 * @param after
	 */
	public BeforeValidator(Calendar after)
	{
		this(DEFAULT_PREFIX, after.getTime());
	}
	
	/**
	 * @return Returns the after.
	 */
	public Date getAfter()
	{
		return after;
	}

	/**
	 * @param after The after to set.
	 */
	public void setAfter(Date after)
	{
		if (after == null)
		{
			throw new IllegalArgumentException("unable to validate null value");
		}
		this.after = after;
	}

	/**
	 * @return true if value is a Date or Calendar and is before after.
	 * @see nl.openedge.maverick.framework.validation.FieldValidator#isValid(org.infohazard.maverick.flow.ControllerContext, nl.openedge.maverick.framework.AbstractForm, java.lang.String, java.lang.Object)
	 */
	public boolean isValid(ControllerContext cctx, AbstractForm form, String fieldName, Object value)
	{
		boolean before = false;
		if (value instanceof Date)
		{
			before = ((Date)value).before(after);
		}
		else if (value instanceof Calendar)
		{
			before = ((Calendar)value).getTime().before(after);
		}
		else
		{
			log.error(value.getClass() + " is not a valid date");
		}

		return before;
	}
	
	/**
	 * Tries to format value to a DateFormat.
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
	 * @return Returns the datePattern.
	 */
	public String getDatePattern()
	{
		return datePattern;
	}

	/**
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
