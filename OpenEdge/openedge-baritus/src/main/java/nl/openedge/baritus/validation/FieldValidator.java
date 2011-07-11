/*
 * $Id: FieldValidator.java,v 1.5 2004-11-21 13:20:22 eelco12 Exp $
 * $Revision: 1.5 $
 * $Date: 2004-11-21 13:20:22 $
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
	 * You should register errors directly with the formBeanContext.
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
	 * if value is not valid, get the custom value to set as the field override
	 * in the form.
	 * @param value the original input value
	 * @return the value that should be used as override value
	 */
	public Object getOverrideValue(Object value);
	
}