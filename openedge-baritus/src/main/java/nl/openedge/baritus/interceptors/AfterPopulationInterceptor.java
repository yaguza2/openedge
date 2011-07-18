/*
 * $Id: AfterPopulationInterceptor.java,v 1.3 2004-05-23 10:26:57 eelco12 Exp $
 * $Revision: 1.3 $
 * $Date: 2004-05-23 10:26:57 $
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
package nl.openedge.baritus.interceptors;

import javax.servlet.ServletException;

import nl.openedge.baritus.FormBeanContext;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * Registered instances will have their command method executed before the normal action
 * execution took place, but after the form bean was populated.
 * 
 * @author Eelco Hillenius
 */
public interface AfterPopulationInterceptor extends Interceptor
{

	/**
	 * Executed before the normal action execution takes place, but after the form bean
	 * was populated.
	 * 
	 * @param cctx
	 *            maverick context
	 * @param formBeanContext
	 *            the context with the not-yet populated formBean
	 * @throws ServletException
	 * @throws RedirectingException
	 *             when an interceptor wants to redirect
	 * @throws DirectReturnFlowException
	 *             when an interceptor wants to return directly
	 */
	public void doAfterPopulation(ControllerContext cctx, FormBeanContext formBeanContext)
			throws ServletException, DispatchNowFlowException, ReturnNowFlowException;

}
