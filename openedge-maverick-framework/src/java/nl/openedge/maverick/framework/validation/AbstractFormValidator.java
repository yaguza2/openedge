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

import nl.openedge.maverick.framework.FormBeanContext;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * convenience class with default error message handling
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
	 * construct
	 */
	public AbstractFormValidator()
	{
		super();
	}
	
	/**
	 * @param rule
	 */
	public AbstractFormValidator(ValidatorActivationRule rule)
	{
		super(rule);
	}

	/**
	 * @param messagePrefix
	 * @param rule
	 */
	public AbstractFormValidator(
		String messagePrefix,
		ValidatorActivationRule rule)
	{
		super(messagePrefix, rule);
	}

	/**
	 * construct with message prefix
	 * @param messagePrefix message prefix
	 */
	public AbstractFormValidator(String messagePrefix)
	{
		super(messagePrefix);
	}

	/**
	 * @see nl.openedge.maverick.framework.FieldValidator#getErrorMessage(org.infohazard.maverick.flow.ControllerContext, nl.openedge.maverick.framework.FormBeanContext, java.lang.String, java.lang.Object, java.util.Locale)
	 */
	public String[] getErrorMessage(
		ControllerContext cctx,
		FormBeanContext form,
		Locale locale)
	{
		String key = getMessagePrefix();
		String msg = getLocalizedMessage(key, locale);
		String storeKey = getErrorKey();
		if(storeKey == null) storeKey = key;
		return new String[]{storeKey, msg};
	}

	/**
	 * get the key that will be used for storing the error message
	 * if null or not provided, property messagePrefix will be used instead
	 * @return String key that will be used for storing the error message
	 */
	public String getErrorKey()
	{
		return errorKey;
	}

	/**
	 * set the key that will be used for storing the error message
	 * if null or not provided, property messagePrefix will be used instead
	 * @param string the key that will be used for storing the error message
	 */
	public void setErrorKey(String string)
	{
		errorKey = string;
	}

}
