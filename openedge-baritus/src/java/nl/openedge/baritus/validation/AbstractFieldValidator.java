/*
 * $Id: AbstractFieldValidator.java,v 1.1.1.1 2004-02-24 20:34:13 eelco12 Exp $
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
 * convenience class with default error message handling
 * @author Eelco Hillenius
 */
public abstract class AbstractFieldValidator extends AbstractValidator
	implements FieldValidator, ValidationRuleDependend
{

	/**
	 * construct
	 */
	public AbstractFieldValidator()
	{
		super();
	}
	
	/**
	 * @param rule
	 */
	public AbstractFieldValidator(ValidatorActivationRule rule)
	{
		super(rule);
	}

	/**
	 * @param messagePrefix
	 * @param rule
	 */
	public AbstractFieldValidator(
		String messagePrefix,
		ValidatorActivationRule rule)
	{
		super(messagePrefix, rule);
	}

	/**
	 * construct with message prefix
	 * @param messagePrefix message prefix
	 */
	public AbstractFieldValidator(String messagePrefix)
	{
		super(messagePrefix);
	}

	/**
	 * get the error message. default returns the resource bundle message where
	 * key = messagePrefix + fieldName, with {0} substituted with the value
	 * and {1} substituted with the field name
	 * @see nl.openedge.baritus.FieldValidator#getErrorMessage(org.infohazard.maverick.flow.ControllerContext, nl.openedge.baritus.FormBeanContext, java.lang.String, java.lang.Object, java.util.Locale)
	 */
	public String getErrorMessage(
		ControllerContext cctx,
		FormBeanContext formBeanContext,
		String fieldName,
		Object value,
		Locale locale)
	{
		
		String key = getMessagePrefix() + fieldName;
		return getLocalizedMessage(key, locale, new Object[]{value, fieldName});
	}
	
	/**
	 * get the override value. default returns the value unchanged
	 * @return Object unchanged value
	 * @see nl.openedge.baritus.validation.FieldValidator#getOverrideValue(java.lang.Object)
	 */
	public Object getOverrideValue(Object value)
	{
		return value;
	}
	
}
