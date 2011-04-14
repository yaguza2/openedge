/*
 * $Id: MaximumFieldLengthValidator.java,v 1.7 2004-04-25 10:02:37 eelco12 Exp $
 * $Revision: 1.7 $
 * $Date: 2004-04-25 10:02:37 $
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infohazard.maverick.flow.ControllerContext;

/**
 * This validator checks on maximum length. E.g. if property maxLength is 4, "hello" will
 * fail, but "hi" will pass, and number 47535 will fail, but 635 will pass.
 * @author Eelco Hillenius
 */
public final class MaximumFieldLengthValidator extends AbstractFieldValidator
{
	/** special value that indicates there's no maximum value to check on */
	public final static int NO_MAXIMUM = -1;

	private int maxLength = NO_MAXIMUM;

	private static Log log = LogFactory.getLog(MaximumFieldLengthValidator.class);

	private String errorMessageKey = "invalid.field.input.size";

	/**
	 * construct with 'invalid.field.input.size' as message prefix.
	 */
	public MaximumFieldLengthValidator()
	{

	}

	/**
	 * Construct with errorMessageKey for error message keys.
	 * @param errorMessageKey
	 */
	public MaximumFieldLengthValidator(String errorMessageKey)
	{
		setErrorMessageKey(errorMessageKey);
	}

	/**
	 * Construct with message prefix for error message keys and set checking on maximum
	 * length with given length of fields only.
	 * @param errorMessageKey message key
	 * @param maxLength maximum length allowed for values; use -1 for no maximum
	 */
	public MaximumFieldLengthValidator(String errorMessageKey, int maxLength)
	{
		setErrorMessageKey(errorMessageKey);
		setMaxLength(maxLength);
	}

	/**
	 * Construct with activation rule.
	 * @param rule activation rule
	 */
	public MaximumFieldLengthValidator(ValidationActivationRule rule)
	{
		setValidationRule(rule);
	}

	/**
	 * Construct with errorMessageKey and activation rule.
	 * @param errorMessageKey error message key
	 * @param rule activation rule
	 */
	public MaximumFieldLengthValidator(String errorMessageKey, ValidationActivationRule rule)
	{
		setErrorMessageKey(errorMessageKey);
		setValidationRule(rule);
	}

	/**
	 * Construct with maxLength.
	 * @param maxLength maximum length allowed for values; use -1 for no maximum
	 */
	public MaximumFieldLengthValidator(int maxLength)
	{
		setMaxLength(maxLength);
	}

	/**
	 * Checks whether the lenth of the provided value is less than the maximum.
	 * @return boolean true if the length of value is equal to or less than the maxLength
	 *         property, false otherwise
	 * @see nl.openedge.baritus.validation.FieldValidator#isValid(org.infohazard.maverick.flow.ControllerContext,
	 *      nl.openedge.baritus.FormBeanContext, java.lang.String, java.lang.Object)
	 */
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
				log.warn(fieldName
						+ " with value: " + value + " is of the wrong type for checking on length");
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
	 * @return int maximum length that is checked on
	 */
	public int getMaxLength()
	{
		return maxLength;
	}

	/**
	 * Set maximum length that is checked on .
	 * @param i maximum length that is checked on
	 */
	public void setMaxLength(int i)
	{
		maxLength = i;
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