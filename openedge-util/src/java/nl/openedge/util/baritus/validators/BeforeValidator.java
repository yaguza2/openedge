/*
 * $Id$
 * $Revision$
 * $Date$
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
package nl.openedge.util.baritus.validators;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.validation.AbstractFieldValidator;
import nl.openedge.baritus.validation.ValidationActivationRule;
import nl.openedge.util.DateComparator;
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

	private final static String DEFAULT_PREFIX = "invalid.field.input.before";

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
	public BeforeValidator(String messagePrefix, ValidationActivationRule rule)
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
	 * @return true if value is a Date or Calendar and is before or equal to after.
	 * @see nl.openedge.maverick.framework.validation.FieldValidator#isValid(org.infohazard.maverick.flow.ControllerContext, nl.openedge.maverick.framework.FormBeanContext, java.lang.String, java.lang.Object)
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
	
		boolean before = false;
		DateComparator comp = new DateComparator();
		if (value instanceof Date)
		{
			before = comp.compare((Date)value, after) >= 0;
		}
		else if (value instanceof Calendar)
		{
			before = comp.compare(((Calendar)value).getTime(), after) >= 0;
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
	 * @see nl.openedge.maverick.framework.validation.FieldValidator#getErrorMessage(org.infohazard.maverick.flow.ControllerContext, nl.openedge.maverick.framework.FormBeanContext, java.lang.String, java.lang.Object, java.util.Locale)
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
