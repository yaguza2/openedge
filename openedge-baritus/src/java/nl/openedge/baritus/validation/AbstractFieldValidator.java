/*
 * $Id: AbstractFieldValidator.java,v 1.6 2004-04-07 10:43:24 eelco12 Exp $
 * $Revision: 1.6 $
 * $Date: 2004-04-07 10:43:24 $
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

/**
 * Convenience class with default error message handling.
 * @author Eelco Hillenius
 */
public abstract class AbstractFieldValidator extends AbstractValidator
	implements FieldValidator, ValidationRuleDependend
{

	/**
	 * Construct emtpy.
	 */
	public AbstractFieldValidator()
	{

	}
	
	/**
	 * Construct with the given rule.
	 * @param rule activation rule
	 */
	public AbstractFieldValidator(ValidationActivationRule rule)
	{
		super(rule);
	}
	
	/**
	 * Get the override value. By default returns the value unchanged.
	 * @return Object unchanged value
	 * @see nl.openedge.baritus.validation.FieldValidator#getOverrideValue(java.lang.Object)
	 */
	public Object getOverrideValue(Object value)
	{
		return value;
	}
	
	/**
	 * set error message in formBeanContext using the provided name
	 * (try to lookup the translated field name with key of provided name),
	 * the current locale that is stored in the form bean context, the provided
	 * errorMessageKey and the provided arguments for parsing the localized message;
	 * the message will be stored in the form bean context with key that was
	 * provided as argument name (the untranslated name of the field). If no
	 * message was found, the provided errorMessageKey will be used.
	 * 
	 * @param formBeanContext form bean context
	 * @param name untransalated name of the field
	 * @param errorMessageKey message key for error
	 * @param messageArguments arguments for parsing the message
	 */
	protected void setErrorMessage(
		FormBeanContext formBeanContext, 
		String name,
		String errorMessageKey,
		Object[] messageArguments)
	{
		Locale locale = formBeanContext.getCurrentLocale();
		String fieldName = getFieldName(formBeanContext, name);
		String msg = getLocalizedMessage(errorMessageKey, locale, messageArguments);
		if(msg == null) msg = errorMessageKey;
		formBeanContext.setError(name, msg);	
	}
	
}
