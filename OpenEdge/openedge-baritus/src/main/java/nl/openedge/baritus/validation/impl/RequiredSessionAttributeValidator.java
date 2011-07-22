/*
 * $Id: RequiredSessionAttributeValidator.java,v 1.4 2004-04-25 10:02:36 eelco12 Exp $
 * $Revision: 1.4 $
 * $Date: 2004-04-25 10:02:36 $
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

import javax.servlet.http.HttpSession;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.validation.AbstractFormValidator;
import nl.openedge.baritus.validation.ValidationActivationRule;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * Checks whether a session attribute exists with the key that was set for property
 * sessionAttributeKey.
 * 
 * @author Eelco Hillenius
 */
public class RequiredSessionAttributeValidator extends AbstractFormValidator
{

	private String sessionAttributeKey = null;

	private String errorMessageKey = "required.session.attribute.not.found";

	/**
	 * Construct.
	 */
	public RequiredSessionAttributeValidator()
	{

	}

	/**
	 * Construct with errorMessageKey.
	 * 
	 * @param errorMessageKey
	 */
	public RequiredSessionAttributeValidator(String errorMessageKey)
	{
		setErrorMessageKey(errorMessageKey);
	}

	/**
	 * Construct with rule.
	 * 
	 * @param rule
	 */
	public RequiredSessionAttributeValidator(ValidationActivationRule rule)
	{
		super(rule);
	}

	/**
	 * Construct with errorMessageKey and rule.
	 * 
	 * @param errorMessageKey
	 * @param rule
	 */
	public RequiredSessionAttributeValidator(String errorMessageKey, ValidationActivationRule rule)
	{
		setErrorMessageKey(errorMessageKey);
		setValidationRule(rule);
	}

	/**
	 * Construct with message prefix and session attribute key to check for.
	 * 
	 * @param errorMessageKey
	 */
	public RequiredSessionAttributeValidator(String errorMessageKey, String sessionAttributeKey)
	{
		setErrorMessageKey(errorMessageKey);
		setSessionAttributeKey(sessionAttributeKey);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * nl.openedge.baritus.validation.FormValidator#isValid(org.infohazard.maverick.flow
	 * .ControllerContext, nl.openedge.baritus.FormBeanContext)
	 */
	@Override
	public boolean isValid(ControllerContext cctx, FormBeanContext formBeanContext)
	{
		boolean valid = false;
		HttpSession session = cctx.getRequest().getSession(false);
		if (session != null)
		{
			valid = (session.getAttribute(sessionAttributeKey) != null);
		}

		if (!valid)
		{
			setErrorMessage(formBeanContext, sessionAttributeKey, getErrorMessageKey(),
				new Object[] {getFieldName(formBeanContext, sessionAttributeKey)});
		}

		return valid;
	}

	/**
	 * get the session attribute key that will be checked for
	 * 
	 * @return String the session attribute key that will be checked for
	 */
	public String getSessionAttributeKey()
	{
		return sessionAttributeKey;
	}

	/**
	 * set the session attribute key that will be checked for
	 * 
	 * @param string
	 *            the session attribute key that will be checked for
	 */
	public void setSessionAttributeKey(String string)
	{
		sessionAttributeKey = string;
	}

	/**
	 * Get key of error message.
	 * 
	 * @return String key of error message
	 */
	public String getErrorMessageKey()
	{
		return errorMessageKey;
	}

	/**
	 * Set key of error message.
	 * 
	 * @param string
	 *            key of error message
	 */
	public void setErrorMessageKey(String string)
	{
		errorMessageKey = string;
	}

}
