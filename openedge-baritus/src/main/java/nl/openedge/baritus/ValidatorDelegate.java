/*
 * $Id: ValidatorDelegate.java,v 1.2 2004-04-09 09:47:37 eelco12 Exp $
 * $Revision: 1.2 $
 * $Date: 2004-04-09 09:47:37 $
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
package nl.openedge.baritus;

import java.util.Map;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * ValidatorDelegates can do validation on input and populated form beans. Besides the
 * allways used DefaultValidatorDelegate, users of Baritus can register additional
 * delegates, for instance to be able to plug in validator mechanisms like FormProc or
 * Commons Validator.
 * 
 * @author Eelco Hillenius
 */
interface ValidatorDelegate
{
	/**
	 * handle the validation for all provided parameters. The parameters consist typically
	 * of the request parameters, possibly (depending on the also provided instance of
	 * ExecutionParams) with the addition of Maverick configuration parameters, session
	 * attributes and request attributes.
	 * 
	 * Implementers should take care to only use the fields stored in parameters and not
	 * to get the field directly from e.g. the http request.
	 * 
	 * The populated form is stored in the formBeanContext. If implementors have
	 * validation errors, they should store the error messages in the instance of
	 * FormBeanContext (using method(s) setError/ setErrors), and they should save the
	 * original input values (as stored in Map parameters) as override values in the
	 * formBeanContext (using method(s) setOverrideField)
	 * 
	 * @param cctx
	 *            controller context
	 * @param formBeanContext
	 *            form bean context with populated bean
	 * @param execParams
	 *            execution parameters
	 * @param parameters
	 *            the map in which the requested values are stored. This map has at least
	 *            the request parameters stored and depending on the execution parameters
	 *            the maverick configuration parameters, session attributes and request
	 *            attributes.
	 * @param succeeded
	 *            whether population/ validation succeeded so far (did not generate any
	 *            errors).
	 * @return whether validation passed
	 * 
	 *         NOTE: implementors should take note that it is possible that population/
	 *         validation allready failed before this method is called (in that case
	 *         succeeded is false). If you do not want to override the errors (what is
	 *         probably is good idea), you can check the current error map or overwrite
	 *         value map in the formBeanContext. This allways works for properties that
	 *         failed to populate, though it might not work for failed validations as that
	 *         depends on the registrations that the individual validators make in the
	 *         error map.
	 */
	public boolean doValidation(ControllerContext cctx, FormBeanContext formBeanContext,
			ExecutionParams execParams, Map<String, Object> parameters, boolean succeeded);
}
