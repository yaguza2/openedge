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
 * use this for fields that should have their lenghts checked
 * by default, the messagePrefix itself (or 'input.field.required' if you use the
 * default constructor) will be used to get the error message
 * @author Eelco Hillenius
 */
public class RequiredFieldValidator extends AbstractFieldValidator
{

	/**
	 * construct
	 */
	public RequiredFieldValidator()
	{
		super("input.field.required");
	}

	/**
	 * @param rule
	 */
	public RequiredFieldValidator(ValidatorActivationRule rule)
	{
		super(rule);
	}

	/**
	 * @param messagePrefix
	 * @param rule
	 */
	public RequiredFieldValidator(
		String messagePrefix,
		ValidatorActivationRule rule)
	{
		super(messagePrefix, rule);
	}

	/**
	 * construct with message prefix for error message keys
	 * @param messagePrefix
	 */
	public RequiredFieldValidator(String messagePrefix)
	{
		super(messagePrefix);
	}

	/**
	 * checks whether the value is not null, and - if it is an instance of String - whether
	 * the trimmed value is not an empty string
	 * @return boolean true if not null or empty, false otherwise
	 * @see nl.openedge.maverick.framework.FieldValidator#isValid(org.infohazard.maverick.flow.ControllerContext, nl.openedge.maverick.framework.AbstractForm, java.lang.String, java.lang.Object)
	 */
	public boolean isValid(
		ControllerContext cctx,
		AbstractForm form,
		String fieldName,
		Object value)
	{
		boolean isValid = true;
		if(value == null)
		{
			isValid = false;
		}
		else
		{
			if(value instanceof String)
			{
				if("".equals(((String)value).trim()))
				{
					isValid = false;
				}
			}
		}
		return isValid;
	}

	/**
	 * get the error message. default returns the resource bundle message where
	 * key = messagePrefix, with {0} substituted with the field name
	 * 
	 * @see nl.openedge.maverick.framework.FieldValidator#getErrorMessage(org.infohazard.maverick.flow.ControllerContext, nl.openedge.maverick.framework.AbstractForm, java.lang.String, java.lang.Object, java.util.Locale)
	 */
	public String getErrorMessage(
		ControllerContext cctx,
		AbstractForm form,
		String fieldName,
		Object value,
		Locale locale)
	{

		return getLocalizedMessage(getMessagePrefix(), locale, new Object[]{fieldName});
	}

}
