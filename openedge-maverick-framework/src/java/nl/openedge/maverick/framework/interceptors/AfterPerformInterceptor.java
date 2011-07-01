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
package nl.openedge.maverick.framework.interceptors;

import javax.servlet.ServletException;

import nl.openedge.maverick.framework.FormBeanContext;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * Registered instances will have their command method executed after the
 * normal action execution took place. That means that makeFormBean was called,
 * the form was populated and - if that population was succesfull - the 
 * command method was called prior to this execution.
 * 
 * @author Eelco Hillenius
 */
public interface AfterPerformInterceptor extends Interceptor
{

	/**
	 * Executed after the normal action execution took place. That means that 
	 * makeFormBean was called, the form was populated and - if that population 
	 * was succesfull - the command method was called prior to this execution.
	 * 
	 * NOTE. You cannot be sure that the form was populated successfully. Therefore
	 * it's dangerous and generally bad practice to rely on form properties that are 
	 * populated from the http request. A good usage example: a lot of views need
	 * data to fill their dropdown lists etc. In this method, you could load that data and
	 * save it in the form (or as a request attribute if that's your style). As this method
	 * is allways executed, you have a guaranteed data delivery to your view, regardless
	 * the normal execution outcome of the control. 
	 * 
	 * @param cctx maverick context
	 * @param formBeanContext the context with the (possibly succesfull) populated formBean
	 * @throws ServletException
	 */
	public void doAfterPerform(
		ControllerContext cctx,
		FormBeanContext formBeanContext) 
		throws ServletException;

}
