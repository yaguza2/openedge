/*
 * $Id: AfterValidator.java,v 1.1.1.1 2004-02-24 20:34:15 eelco12 Exp $
 * $Revision: 1.1.1.1 $
 * $Date: 2004-02-24 20:34:15 $
 *
 * ====================================================================
 * Copyright (c) 2003, Open Edge B.V.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions 
 * in binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution. Neither the name of OpenEdge B.V. 
 * nor the names of its contributors may be used to endorse or promote products 
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package nl.openedge.baritus.validation;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import nl.openedge.baritus.FormBeanContext;
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
	 * @see nl.openedge.baritus.validation.FieldValidator#isValid(org.infohazard.maverick.flow.ControllerContext, nl.openedge.baritus.FormBeanContext, java.lang.String, java.lang.Object)
	 * @throws IllegalArgumentException when value == null.
	 */
	public boolean isValid(
		ControllerContext cctx,
		FormBeanContext formBeanContext,
		String fieldName,
		Object value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException("Null cannot be validated.");
		}
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
	 * @see nl.openedge.baritus.validation.AbstractFieldValidator#getOverrideValue(java.lang.Object)
	 */
	public Object getOverrideValue(Object value)
	{
		String result = "";
		if (value instanceof String)
		{
			try
			{
				result =
					DateFormatHelper.format(
						datePattern,
						DateFormatHelper.fallbackParse((String)value));
			}
			catch (ParseException e)
			{
				// value is not a valid date; ignore
			}
		}
		else if (value instanceof Date)
		{
			result = DateFormatHelper.format(datePattern, (Date)value);
		}
		else if (value instanceof Calendar)
		{
			result =
				DateFormatHelper.format(
					datePattern,
					((Calendar)value).getTime());
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
	 * @see nl.openedge.baritus.validation.FieldValidator#getErrorMessage(org.infohazard.maverick.flow.ControllerContext, nl.openedge.baritus.FormBeanContext, java.lang.String, java.lang.Object, java.util.Locale)
	 */
	public String getErrorMessage(
		ControllerContext cctx,
		FormBeanContext formBeanContext,
		String fieldName,
		Object value,
		Locale locale)
	{
		return getLocalizedMessage(
			getMessagePrefix(),
			locale,
			new Object[] { getOverrideValue(value), fieldName });
	}

}
