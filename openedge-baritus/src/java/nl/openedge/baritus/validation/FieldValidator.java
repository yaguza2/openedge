/*
 * $Id: FieldValidator.java,v 1.1.1.1 2004-02-24 20:34:12 eelco12 Exp $
 * $Revision: 1.1.1.1 $
 * $Date: 2004-02-24 20:34:12 $
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
 * Use this for custom validation
 * @author Eelco Hillenius
 */
public interface FieldValidator
{
	/**
	 * Checks if value is valid. 
	 * This method should return true if validation succeeded or false otherwise.
	 * For error handling you have two options:
	 * <ul>
	 * 	<li>
	 * 	Use the callback method getErrorMessage. You should use this if the error 
	 * 	message is allways the same (except for maybe some parameters in the message).
	 * 	</li>
	 * 	<li>
	 * 	For more flexibility, like if you have more stages of validation that each generates
	 * 	its own message(s), you can register errors directly with the formBeanContext
	 * 	</li>
	 * </ul>
	 * @param cctx maverick context
	 * @param formBeanContext context with bean for this currentRequest
	 * @param fieldName field name of parameter
	 * @param value the value of this parameter
	 * @return true if valid, false if not.
	 */
	public boolean isValid(
		ControllerContext cctx,
		FormBeanContext formBeanContext,
		String fieldName,
		Object value);

	/**
	 * if value is not valid, get custom error message here
	 * @param cctx maverick context
	 * @param formBeanContext context for this currentRequest
	 * @param fieldName field name of parameter
	 * @param value the value of this parameter
	 * @param locale the locale that should be used to get the message
	 * @return String the localized error message
	 */		
	public String getErrorMessage(
		ControllerContext cctx,
		FormBeanContext formBeanContext,
		String fieldName,
		Object value,
		Locale locale);
		
	/**
	 * if value is not valid, get the custom value to set as the field override
	 * in the form
	 * @param value the original input value
	 * @return the value that should be used as override value
	 */
	public Object getOverrideValue(Object value);
	
}
