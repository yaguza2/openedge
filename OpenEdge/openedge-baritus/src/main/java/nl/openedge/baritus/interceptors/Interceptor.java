/*
 * $Id: Interceptor.java,v 1.2 2004-05-23 10:26:57 eelco12 Exp $
 * $Revision: 1.2 $
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

/**
 * Tagging interface for interceptors. Clients should use one or more of the specific
 * interfaces to actually intercept on something.
 * 
 * Interceptors provide a means to encapsulate cross-cutting code that is 
 * executed on pre-defined points in the line of execution.
 * 
 * Interceptors can be used to decorate the normal execution. Also, by throwing FlowExceptions, interceptors can
 * alter the flow of execution. An interceptor can throw a FlowException if it wants Baritus to stop
 * normal processing and go to the given declared view (using ReturnNowFlowException) such as 'error', 
 * or dispatch to an arbitrary - non declared - URL (using DispatchNowFlowException) location.
 *  
 * @author Eelco Hillenius
 */
public interface Interceptor
{

}
