/*
 * $Id: RequiredFieldValidator.java,v 1.2 2004-03-29 15:26:54 eelco12 Exp $
 * $Revision: 1.2 $
 * $Date: 2004-03-29 15:26:54 $
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

import java.util.Locale;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.validation.AbstractFieldValidator;
import nl.openedge.baritus.validation.ValidationActivationRule;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * Use this for fields that should have a not null (empty string) input.
 * Note that as this validator is a field validator, and thus is registered
 * for a single field, it is only fired if a field (e.g. a request parameter)
 * is actually provided. In other words: if an instance of a required field
 * validator was registered for field name 'myprop', but 'myprop' is not part
 * of the request parameters, this validator never fires. Hence, if you want
 * to be REALLY sure that a property of the form is not null, use a form validator
 * (PropertyNotNullValidator). RequiredFieldValidator works fine for most cases
 * where you have a HTML form with a field that should have a non empty value, but
 * that - if a user fools around - does not seriousely break anything when a value
 * is not provided (e.g. you probably have not null constraint in you database as well).
 * 
 * @author Eelco Hillenius
 */
public class RequiredFieldValidator extends AbstractFieldValidator
{

	/**
	 * Construct using 'input.field.required' as the message prefix.
	 */
	public RequiredFieldValidator()
	{
		super("input.field.required");
	}

	/**
	 * Construct using the provided activation rule and 'input.field.required' as the message prefix.
	 * @param rule
	 */
	public RequiredFieldValidator(ValidationActivationRule rule)
	{
		super("input.field.required", rule);
	}

	/**
	 * Construct using messagePrefix and the activation rule
	 * @param messagePrefix
	 * @param rule
	 */
	public RequiredFieldValidator(
		String messagePrefix,
		ValidationActivationRule rule)
	{
		super(messagePrefix, rule);
	}

	/**
	 * Construct with message prefix for error message keys.
	 * @param messagePrefix message prefix
	 */
	public RequiredFieldValidator(String messagePrefix)
	{
		super(messagePrefix);
	}

	/**
	 * Checks whether the value is not null, and - if it is an instance of String - whether
	 * the trimmed value is not an empty string.
	 * Note that as this validator is a field validator, and thus is registered
	 * for a single field, it is only fired if a field (e.g. a request parameter)
	 * is actually provided. In other words: if an instance of a required field
	 * validator was registered for field name 'myprop', but 'myprop' is not part
	 * of the request parameters, this validator never fires. Hence, if you want
	 * to be REALLY sure that a property of the form is not null, use a form validator
	 * (PropertyNotNullValidator). RequiredFieldValidator works fine for most cases
	 * where you have a HTML form with a field that should have a non empty value, but
	 * that - if a user fools around - does not seriousely break anything when a value
	 * is not provided (e.g. you probably have not null constraint in you database as well).
	 * 
	 * @return boolean true if not null or empty, false otherwise
	 * 
	 * @see nl.openedge.baritus.validation.FieldValidator#isValid(org.infohazard.maverick.flow.ControllerContext, nl.openedge.baritus.FormBeanContext, java.lang.String, java.lang.Object)
	 */
	public boolean isValid(
		ControllerContext cctx,
		FormBeanContext formBeanContext,
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
	 * Get the error message. default returns the resource bundle message where
	 * key = messagePrefix, with {0} substituted with the field name.
	 * 
	 * @see nl.openedge.baritus.validation.FieldValidator#getErrorMessage(org.infohazard.maverick.flow.ControllerContext, nl.openedge.baritus.FormBeanContext, java.lang.String, java.lang.Object, java.util.Locale)
	 */
	public String getErrorMessage(
		ControllerContext cctx,
		FormBeanContext formBeanContext,
		String fieldName,
		Object value,
		Locale locale)
	{

		return getLocalizedMessage(getMessagePrefix(), locale, new Object[]{fieldName});
	}

}
