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
package nl.openedge.maverick.framework.validation;

import java.util.Locale;

import nl.openedge.maverick.framework.AbstractForm;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * use this for fields that have a min length
 * @author Eelco Hillenius
 */
public final class MinimumFieldLengthValidator extends AbstractValidator
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
	 * set checking on maximum length with given length of fields only
	 * @param minLength maximum length allowed for values; use -1 for no maximum
	 */
	public MinimumFieldLengthValidator(int minLength)
	{
		this("invalid.field.input.size", minLength);
	}

	/**
	 * @see nl.openedge.maverick.framework.Validator#isValid(org.infohazard.maverick.flow.ControllerContext, nl.openedge.maverick.framework.AbstractForm, java.lang.String, java.lang.Object)
	 */
	public boolean isValid(
		ControllerContext cctx,
		AbstractForm form,
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
					minExceeded = (length <= minLength);
				}
			}
			else if(value instanceof Number)
			{
				Number toCheck = (Number)value;
				int length = toCheck.intValue();
				if(minLength != NO_MINIMUM)
				{
					minExceeded = (length <= minLength);
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
	 * @see nl.openedge.maverick.framework.Validator#getErrorMessage(org.infohazard.maverick.flow.ControllerContext, nl.openedge.maverick.framework.AbstractForm, java.lang.String, java.lang.Object, java.util.Locale)
	 */
	public String getErrorMessage(
		ControllerContext cctx,
		AbstractForm form,
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
