/*
 * $Id: MinimumFieldLengthValidator.java,v 1.7 2004-04-25 10:02:36 eelco12 Exp $
 * $Revision: 1.7 $
 * $Date: 2004-04-25 10:02:36 $
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
package nl.openedge.baritus.validation.impl;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.validation.AbstractFieldValidator;
import nl.openedge.baritus.validation.ValidationActivationRule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.infohazard.maverick.flow.ControllerContext;

/**
 * This validator checks on minimum length. E.g. if property minLength is 4, "hello" will
 * pass, but "hi" will fail, and number 98765 will pass, but 159 will fail.
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
	 * @param errorMessageKey message key of error
	 */
	public MinimumFieldLengthValidator(String errorMessageKey)
	{
		setErrorMessageKey(errorMessageKey);
	}

	/**
	 * Construct with message key for error message keys and set checking on minimum
	 * length with given length of fields only.
	 * @param errorMessageKey key for error messages
	 * @param minLength minimum length allowed for values; use -1 for no minimum
	 */
	public MinimumFieldLengthValidator(String errorMessageKey, int minLength)
	{
		setErrorMessageKey(errorMessageKey);
		setMinLength(minLength);
	}

	/**
	 * Construct with activation rule.
	 * @param rule activation rule
	 */
	public MinimumFieldLengthValidator(ValidationActivationRule rule)
	{
		setValidationRule(rule);
	}

	/**
	 * Construct with errorMessageKey and activation rule.
	 * @param errorMessageKey
	 * @param rule activation rule
	 */
	public MinimumFieldLengthValidator(String errorMessageKey, ValidationActivationRule rule)
	{
		setErrorMessageKey(errorMessageKey);
		setValidationRule(rule);
	}

	/**
	 * Construct with min length.
	 * @param minLength minimum length allowed for values; use -1 for no minimum
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
	 * @return boolean true if the length of value is equal to or less than the minLength
	 *         property, false otherwise
	 * @see nl.openedge.baritus.validation.FieldValidator#isValid(org.infohazard.maverick.flow.ControllerContext,
	 *      nl.openedge.baritus.FormBeanContext, java.lang.String, java.lang.Object)
	 */
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
				log.warn(fieldName
						+ " with value: " + value + " is of the wrong type for checking on length");
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
	 * @param i minimum length that is checked on
	 */
	public void setMinLength(int i)
	{
		minLength = i;
	}

	/**
	 * Get key of error message.
	 * @return String key of error message
	 */
	public String getErrorMessageKey()
	{
		return errorMessageKey;
	}

	/**
	 * Set key of error message.
	 * @param string key of error message
	 */
	public void setErrorMessageKey(String string)
	{
		errorMessageKey = string;
	}
}
