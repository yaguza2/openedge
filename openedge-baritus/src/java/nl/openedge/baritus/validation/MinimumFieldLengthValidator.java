/*
 * $Id: MinimumFieldLengthValidator.java,v 1.1.1.1 2004-02-24 20:34:13 eelco12 Exp $
 * $Revision: 1.1.1.1 $
 * $Date: 2004-02-24 20:34:13 $
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

import java.util.Locale;

import nl.openedge.baritus.FormBeanContext;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * use this for fields that have a min length
 * @author Eelco Hillenius
 */
public final class MinimumFieldLengthValidator extends AbstractFieldValidator
{
	/** special value that indicates there's no min value to check on */
	public final static int NO_MINIMUM = -1;

	private int minLength = NO_MINIMUM;

	/**
	 * construct with key invalid.field.input.size for error messages
	 */
	public MinimumFieldLengthValidator()
	{
		super("invalid.field.input.size");
	}

	/**
	 * construct with message prefix for error message keys
	 * @param messagePrefix
	 */
	public MinimumFieldLengthValidator(String messagePrefix)
	{
		super(messagePrefix);
	}
	
	/**
	 * construct with message prefix for error message keys and set
	 * checking on maximum length with given length of fields only
	 * @param minLength maximum length allowed for values; use -1 for no maximum
	 */
	public MinimumFieldLengthValidator(String messagePrefix, int minLength)
	{
		super(messagePrefix);
		setMinLength(minLength);
	}
	
	/**
	 * @param rule
	 */
	public MinimumFieldLengthValidator(ValidatorActivationRule rule)
	{
		super("invalid.field.input.size", rule);
	}

	/**
	 * @param messagePrefix
	 * @param rule
	 */
	public MinimumFieldLengthValidator(
		String messagePrefix,
		ValidatorActivationRule rule)
	{
		super(messagePrefix, rule);
	}

	/**
	 * set checking on maximum length with given length of fields only
	 * @param minLength maximum length allowed for values; use -1 for no maximum
	 */
	public MinimumFieldLengthValidator(int minLength)
	{
		this("invalid.field.input.size", minLength);
	}

	/**
	 * in case the value is an instance of string: checks whether the length of the string
	 * is equal to or greater than the minimumLength property
	 * in case the value is an instance of number: checks whether the length of the integer
	 * value is equal to or greater than the minimumLength property
	 * @return boolean true if the length of value is equal to or greater than the
	 * 	minLength property, false otherwise
	 * @see nl.openedge.baritus.FieldValidator#isValid(org.infohazard.maverick.flow.ControllerContext, nl.openedge.baritus.FormBeanContext, java.lang.String, java.lang.Object)
	 */
	public boolean isValid(
		ControllerContext cctx,
		FormBeanContext formBeanContext,
		String fieldName,
		Object value)
	{
		boolean minExceeded = false;
		if(value != null)
		{
			if(value instanceof String)
			{
				String toCheck = (String)value;
				int length = toCheck.length();
				if(minLength != NO_MINIMUM)
				{
					minExceeded = (length < minLength);
				}
			}
			else if(value instanceof Number)
			{
				Number toCheck = (Number)value;
				int length = toCheck.intValue();
				if(minLength != NO_MINIMUM)
				{
					minExceeded = (length < minLength);
				}
			}
			else
			{
				// just ignore; wrong type to check on
				System.err.println(fieldName + " with value: " + value + 
					" is of the wrong type for checking on length"); // give a warning though
			}
		}
		return (!minExceeded);
	}
	
	/**
	 * get the error message. default returns the resource bundle message where
	 * key = messagePrefix, with {0} substituted with the value, {1} substituted 
	 * with the field name and {2} substituted with the minimum length
	 * @see nl.openedge.baritus.FieldValidator#getErrorMessage(org.infohazard.maverick.flow.ControllerContext, nl.openedge.baritus.FormBeanContext, java.lang.String, java.lang.Object, java.util.Locale)
	 */
	public String getErrorMessage(
		ControllerContext cctx,
		FormBeanContext formBeanContext,
		String fieldName,
		Object value,
		Locale locale)
	{

		return getLocalizedMessage(
			getMessagePrefix(), locale, 
			new Object[]{value, fieldName, new Integer(minLength)});
	}

	/**
	 * @return int maximum length that is checked on
	 */
	public int getMinLength()
	{
		return minLength;
	}

	/**
	 * @param i maximum length that is checked on 
	 */
	public void setMinLength(int i)
	{
		minLength = i;
	}

}
