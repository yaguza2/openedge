/*
 * $Id: AbstractFormValidator.java,v 1.3 2004-03-29 15:26:53 eelco12 Exp $
 * $Revision: 1.3 $
 * $Date: 2004-03-29 15:26:53 $
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
 * Convenience class with default error message handling.
 * @author Eelco Hillenius
 */
public abstract class AbstractFormValidator extends AbstractValidator
	implements FormValidator, ValidationRuleDependend
{
	
	/* 
	 * key that will be used for storing the error message 
	 * if null or not provided, property messagePrefix will be used instead
	 */
	private String errorKey = null;

	/**
	 * Construct emtpy.
	 */
	public AbstractFormValidator()
	{
		super();
	}
	
	/**
	 * Construct with the given rule.
	 * @param rule activation rule
	 */
	public AbstractFormValidator(ValidationActivationRule rule)
	{
		super(rule);
	}

	/**
	 * Construct with message prefix and activation rule.
	 * @param messagePrefix message prefix
	 * @param rule activation rule
	 */
	public AbstractFormValidator(
		String messagePrefix,
		ValidationActivationRule rule)
	{
		super(messagePrefix, rule);
	}

	/**
	 * Construct with message prefix.
	 * @param messagePrefix message prefix
	 */
	public AbstractFormValidator(String messagePrefix)
	{
		super(messagePrefix);
	}

	/**
	 * If the form is not valid, get custom error message here. Return null if
	 * no message should be saved. 
	 * @param cctx
	 * @param formBeanContext
	 * @param locale
	 * @return String[] the message key ([0]) and the localized error message ([1])
	 * or null if no message should be saved here.
	 * @see nl.openedge.baritus.validation.FormValidator#getErrorMessage(org.infohazard.maverick.flow.ControllerContext, nl.openedge.baritus.FormBeanContext, java.util.Locale)
	 */
	public String[] getErrorMessage(
		ControllerContext cctx,
		FormBeanContext formBeanContext,
		Locale locale)
	{
		String key = getMessagePrefix();
		String msg = getLocalizedMessage(key, locale);
		String storeKey = getErrorKey();
		if(storeKey == null) storeKey = key;
		return new String[]{storeKey, msg};
	}

	/**
	 * Get the key that will be used for storing the error message.
	 * if null or not provided, property messagePrefix will be used instead
	 * @return String key that will be used for storing the error message
	 */
	public String getErrorKey()
	{
		return errorKey;
	}

	/**
	 * Set the key that will be used for storing the error message.
	 * if null or not provided, property messagePrefix will be used instead
	 * @param string the key that will be used for storing the error message
	 */
	public void setErrorKey(String string)
	{
		errorKey = string;
	}

}
