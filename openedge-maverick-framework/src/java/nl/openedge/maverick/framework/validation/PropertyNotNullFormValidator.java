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

import nl.openedge.maverick.framework.AbstractForm;

import org.apache.commons.beanutils.PropertyUtils;
import org.infohazard.maverick.flow.ControllerContext;

/**
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
	 * check if the
	 * @see nl.openedge.maverick.framework.validation.FormValidator#isValid(org.infohazard.maverick.flow.ControllerContext, nl.openedge.maverick.framework.AbstractForm)
	 */
	public boolean isValid(ControllerContext cctx, AbstractForm form)
	{
		boolean valid = false;
		try
		{
			if(PropertyUtils.getProperty(form, propertyName) != null)
			{
				valid = true;		
			}
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
