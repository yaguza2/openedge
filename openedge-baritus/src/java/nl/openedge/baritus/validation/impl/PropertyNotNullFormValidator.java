/*
 * $Id: PropertyNotNullFormValidator.java,v 1.2 2004-04-04 18:22:33 eelco12 Exp $
 * $Revision: 1.2 $
 * $Date: 2004-04-04 18:22:33 $
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

import org.infohazard.maverick.flow.ControllerContext;

/**
 * check whether the form contains a not null property with the name of property 
 * propertyName
 * @author Eelco Hillenius
 */
public class PropertyNotNullFormValidator extends AbstractFormValidator
{
	
	private String propertyName;

	/**
	 * construct
	 */
	public PropertyNotNullFormValidator()
	{
		super("object.not.found");
	}

	/**
	 * construct with message prefix
	 * @param messagePrefix
	 */
	public PropertyNotNullFormValidator(String messagePrefix)
	{
		super(messagePrefix);
	}
	
	/**
	 * @param rule
	 */
	public PropertyNotNullFormValidator(ValidationActivationRule rule)
	{
		super("object.not.found", rule);
	}

	/**
	 * @param messagePrefix
	 * @param rule
	 */
	public PropertyNotNullFormValidator(
		String messagePrefix,
		ValidationActivationRule rule)
	{
		super(messagePrefix, rule);
	}

	/**
	 * construct with property name and message prefix
	 * @param propertyName
	 * @param messagePrefix
	 */
	public PropertyNotNullFormValidator(String propertyName, String messagePrefix)
	{
		super(messagePrefix);
		setPropertyName(propertyName);
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
			System.err.println(e.getMessage());
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

}
