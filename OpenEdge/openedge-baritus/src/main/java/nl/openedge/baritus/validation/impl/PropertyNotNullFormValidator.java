/*
 * $Id: PropertyNotNullFormValidator.java,v 1.5 2004-04-25 10:02:36 eelco12 Exp $
 * $Revision: 1.5 $
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

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.validation.AbstractFormValidator;
import nl.openedge.baritus.validation.ValidationActivationRule;

import ognl.Ognl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.infohazard.maverick.flow.ControllerContext;

/**
 * Checks whether the form contains a non null property with the name of property 
 * propertyName.
 * 
 * @author Eelco Hillenius
 */
public class PropertyNotNullFormValidator extends AbstractFormValidator
{
	
	private String propertyName;
	
	private String errorMessageKey = "object.not.found";
	
	private Logger log = LoggerFactory.getLogger(PropertyNotNullFormValidator.class);

	/**
	 * construct
	 */
	public PropertyNotNullFormValidator()
	{

	}

	/**
	 * construct with errorMessageKey
	 * @param errorMessageKey
	 */
	public PropertyNotNullFormValidator(String errorMessageKey)
	{
		setErrorMessageKey(errorMessageKey);
	}
	
	/**
	 * construct with rule
	 * @param rule validation rule
	 */
	public PropertyNotNullFormValidator(ValidationActivationRule rule)
	{
		setValidationRule(rule);
	}

	/**
	 * construct with error message and rule
	 * @param errorMessageKey message key
	 * @param rule validation rule
	 */
	public PropertyNotNullFormValidator(
		String errorMessageKey,
		ValidationActivationRule rule)
	{
		setErrorMessageKey(errorMessageKey);
		setValidationRule(rule);
	}

	/**
	 * construct with property name and errorMessageKey
	 * @param propertyName name of property
	 * @param errorMessageKey message key
	 */
	public PropertyNotNullFormValidator(String propertyName, String errorMessageKey)
	{
		setPropertyName(propertyName);
		setErrorMessageKey(errorMessageKey);
	}

	/**
	 * check whether the form contains a not null property with the name of property 
	 * propertyName
	 * @return boolean true if property in form with name of property propertyName exists
	 * 	and is not null, false otherwise
	 * @see nl.openedge.baritus.validation.FormValidator#isValid(org.infohazard.maverick.flow.ControllerContext, nl.openedge.baritus.FormBeanContext)
	 */
	public boolean isValid(ControllerContext cctx, FormBeanContext formBeanContext)
	{
		Object bean = formBeanContext.getBean();
		boolean valid = false;
		try
		{
			Object property = Ognl.getValue(propertyName, bean);
			valid = (property != null);
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}
		
		if(!valid)
		{
			setErrorMessage(formBeanContext, propertyName, getErrorMessageKey(), 
				new Object[]{getFieldName(formBeanContext, propertyName)});	
		}

		return valid;
		
	}

	/**
	 * get the name of the property
	 * @return String name of property
	 */
	public String getPropertyName()
	{
		return propertyName;
	}

	/**
	 * set the name of the property
	 * @param string name of the property
	 */
	public void setPropertyName(String string)
	{
		propertyName = string;
	}
	
	/**
	 * Get key of error message.
	 * @return String key of error message
	 */
	public String getErrorMessageKey()
	{
		return errorMessageKey;
	}

	/**
	 * Set key of error message.
	 * @param string key of error message
	 */
	public void setErrorMessageKey(String string)
	{
		errorMessageKey = string;
	}

}
