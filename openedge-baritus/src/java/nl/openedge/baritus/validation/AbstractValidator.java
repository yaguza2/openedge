/*
 * $Id: AbstractValidator.java,v 1.1.1.1 2004-02-24 20:34:14 eelco12 Exp $
 * $Revision: 1.1.1.1 $
 * $Date: 2004-02-24 20:34:14 $
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

import nl.openedge.baritus.util.MessageUtils;

/**
 * convenience class
 * @author Eelco Hillenius
 */
public abstract class AbstractValidator
{

	private ValidatorActivationRule validatorActivationRule = null;

	private String messagePrefix = "input.field.";

	/**
	 * construct
	 */
	public AbstractValidator()
	{
		// nothing here
	}
	
	/**
	 * construct with message prefix
	 * @param messagePrefix message prefix
	 */
	public AbstractValidator(String messagePrefix)
	{
		setMessagePrefix(messagePrefix);
	}
	
	/**
	 * construct with validator activation rule
	 * @param rule validator activation rule
	 */
	public AbstractValidator(ValidatorActivationRule rule)
	{
		setValidationRule(rule);
	}

	/**
	 * construct with validator activation rule and message prefix
	 * @param messagePrefix message prefix
	 * @param rule validator activation rule
	 */
	public AbstractValidator(String messagePrefix, ValidatorActivationRule rule)
	{
		setValidationRule(rule);
		setMessagePrefix(messagePrefix);
	}
	
	/**
	 * get localized message for given key
	 * @param key key of message
	 * @return String localized message
	 */
	public String getLocalizedMessage(String key)
	{
		return getLocalizedMessage(key, Locale.getDefault());
	}
	
	/**
	 * get localized message for given key and locale. 
	 * If locale is null, the default locale will be used
	 * @param key key of message
	 * @param locale locale for message
	 * @return String localized message
	 */
	public String getLocalizedMessage(String key, Locale locale)
	{	
		return MessageUtils.getLocalizedMessage(key, locale);
	}
	
	/**
	 * get localized message for given key and locale
	 * and format it with the given parameters. 
	 * If locale is null, the default locale will be used
	 * @param key key of message
	 * @param locale locale for message
	 * @param parameters parameters for the message
	 * @return String localized message
	 */
	public String getLocalizedMessage(
			String key, Object[] parameters)
	{	
		return getLocalizedMessage(key, null, parameters);
	}
	
	/**
	 * get localized message for given key and locale
	 * and format it with the given parameters. 
	 * If locale is null, the default locale will be used
	 * @param key key of message
	 * @param locale locale for message
	 * @param parameters parameters for the message
	 * @return String localized message
	 */
	public String getLocalizedMessage(
			String key, Locale locale, Object[] parameters)
	{	
		return MessageUtils.getLocalizedMessage(key, locale, parameters);
	}

	/**
	 * get the prefix for error message keys
	 * @return String
	 */
	public String getMessagePrefix()
	{
		return messagePrefix;
	}

	/**
	 * set the prefix for error message keys
	 * @param string
	 */
	public void setMessagePrefix(String string)
	{
		messagePrefix = string;
	}

	/**
	 * @see nl.openedge.baritus.validation.ValidationRuleDependend#getValidationActivationRule()
	 */
	public ValidatorActivationRule getValidationActivationRule()
	{
		return this.validatorActivationRule;
	}

	/**
	 * @see nl.openedge.baritus.validation.ValidationRuleDependend#removeValidationActivationRule()
	 */
	public void removeValidationActivationRule()
	{
		this.validatorActivationRule = null;
	}

	/**
	 * @see nl.openedge.baritus.validation.ValidationRuleDependend#setValidationRule(nl.openedge.baritus.validation.ValidatorActivationRule)
	 */
	public void setValidationRule(ValidatorActivationRule rule)
	{
		this.validatorActivationRule = rule;
	}

}
