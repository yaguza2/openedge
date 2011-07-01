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

import java.util.Calendar;
import java.util.Date;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.validation.ValidationActivationRule;
import nl.openedge.util.DateComparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infohazard.maverick.flow.ControllerContext;

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
	private static Log log = LogFactory.getLog(AfterValidator.class);

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
	 * @param dateToCheck date to check
	 */
	public AfterValidator(Calendar dateToCheck)
	{
		super(dateToCheck);
		setMessageKey(DEFAULT_MESSAGE_KEY);
	}

	/**
	 * Construct.
	 * 
	 * @param dateToCheck date to check
	 */
	public AfterValidator(Date dateToCheck)
	{
		super(dateToCheck);
		setMessageKey(DEFAULT_MESSAGE_KEY);
	}

	/**
	 * Construct.
	 * 
	 * @param messageKey the message key
	 */
	public AfterValidator(String messageKey)
	{
		super(messageKey);
	}

	/**
	 * Construct.
	 * 
	 * @param messageKey the message key
	 * @param dateToCheck date to check
	 */
	public AfterValidator(String messageKey, Calendar dateToCheck)
	{
		super(messageKey, dateToCheck);
	}

	/**
	 * Construct.
	 * 
	 * @param messageKey the message key
	 * @param dateToCheck date to check
	 */
	public AfterValidator(String messageKey, Date dateToCheck)
	{
		super(messageKey, dateToCheck);
	}

	/**
	 * Construct.
	 * 
	 * @param messageKey the message key
	 * @param rule the validation activation rule
	 */
	public AfterValidator(String messageKey, ValidationActivationRule rule)
	{
		super(messageKey, rule);
	}

	/**
	 * Check valid. If false an error message is set with parameters: {value, fieldName, checkDate}.
	 * @param cctx controller context
	 * @param formBeanContext form bean context
	 * @param fieldName field name
	 * @param value object to check
	 * @return true if value is a Date or Calendar and is before or equal to before.
	 * @see nl.openedge.maverick.framework.validation.FieldValidator#isValid(org.infohazard.maverick.flow.ControllerContext,
	 *      nl.openedge.maverick.framework.FormBeanContext, java.lang.String, java.lang.Object)
	 * @throws IllegalArgumentException
	 *             when value == null.
	 */
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
			setErrorMessage(formBeanContext, fieldName, getMessageKey(), new Object[]
				{value, fieldName, compareBefore});
		}

		return after;
	}

}