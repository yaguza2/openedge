/*
 * $Id: FlowInterceptorResult.java,v 1.2 2004-04-25 10:02:49 eelco12 Exp $
 * $Revision: 1.2 $
 * $Date: 2004-04-25 10:02:49 $
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
 
package nl.openedge.baritus.interceptors.flow;

/**
 * Used by flowinterceptors to indicate the action that should be taken
 * by the framework after the interceptor executed.
 * 
 * @author Eelco Hillenius
 */
public final class FlowInterceptorResult
{
	/** 
	 * Constant that indicates that normal processing should be done.
	 */
	public final static int ACTION_GO_ON = 0;
	
	/** 
	 * Constant that indicates that the framework should display the
	 * logical view represented by property 'view'.
	 */
	public final static int ACTION_SHOW_VIEW = 1;
	
	/** 
	 * Constant that indicates that the framework should display the 
	 * absolute view represented by property 'view' (by using the dispatcher).
	 */
	public final static int ACTION_DISPATCH = 2;
	
	/**
	 * Whether, in case of the action being ACTION_SHOW_VIEW or ACTION_DISPATCH, the registered
	 * 'normal' interceptors should be exected before displaying the view.
	 * The default is true.
	 * If true, and the current stage of intercepting is 'beforeMakeFormBean',
	 * the BeforePopulationInterceptors will be executed. If true, for all
	 * other stages except 'afterPerform' the AfterPerformInterceptors will
	 * be executed.
	 */
	private boolean executeOtherNonFlowInterceptors = true;
	
	/**
	 * The view that should be displayed. If the action == ACTION_SHOW_VIEW,
	 * this property should refer to a logical view as defined in the Maverick
	 * configuration file, eg 'success'. If the action == ACTION_DISPATH, this property
	 * refers to the absolute (but possible relative to the webapp) path
	 * of the view, eg 'mydir/mypage.jsp'.
	 */
	private String view;
	
	/**
	 * The action that should be taken by the framework after execution of this interceptor,
	 * 	refering to one of the public ACTION_... constants of this class.
	 */
	private int action;
	
	/**
	 * Get the action that should be taken by the framework after execution of this interceptor,
	 * 	refering to one of the public ACTION_... constants of this class.
	 * @return int action that should be taken by the framework after execution of this interceptor,
	 * 	refering to one of the public ACTION_... constants of this class.
	 */
	public int getAction()
	{
		return action;
	}

	/**
	 * Set the action that should be taken by the framework after execution of this interceptor,
	 * 	refering to one of the public ACTION_... constants of this class.
	 * @param action the action that should be taken by the framework after execution of this interceptor,
	 * 	refering to one of the public ACTION_... constants of this class.
	 */
	public void setAction(int action)
	{
		this.action = action;
	}

	/**
	 * Get the view that should be displayed. If the action == ACTION_SHOW_VIEW,
	 * this property should refer to a logical view as defined in the Maverick
	 * configuration file, eg 'success'. If the action == ACTION_DISPATH, this property
	 * refers to the absolute (but possible relative to the webapp) path
	 * of the view, eg 'mydir/mypage.jsp'.
	 * 
	 * @return String the view that should be displayed.
	 */
	public String getView()
	{
		return view;
	}

	/**
	 * Set the view that should be displayed. If the action == ACTION_SHOW_VIEW,
	 * this property should refer to a logical view as defined in the Maverick
	 * configuration file, eg 'success'. If the action == ACTION_DISPATH, this property
	 * refers to the absolute (but possible relative to the webapp) path
	 * of the view, eg 'mydir/mypage.jsp'.
	 * @param view the view that should be displayed.
	 */
	public void setView(String view)
	{
		this.view = view;
	}

	/**
	 * get whether, in case of the action being ACTION_SHOW_VIEW or ACTION_DISPATCH, the registered
	 * 'normal' interceptors should be exected before displaying the view.
	 * The default is true.
	 * If true, and the current stage of intercepting is 'beforeMakeFormBean',
	 * the BeforePopulationInterceptors will be executed. If true, for all
	 * other stages except 'afterPerform' the AfterPerformInterceptors will
	 * be executed.
	 * @return boolean whether, in case of the action being ACTION_SHOW_VIEW or ACTION_DISPATCH, the registered
	 * 'normal' interceptors should be exected before displaying the view.
	 */
	public boolean isExecuteOtherNonFlowInterceptors()
	{
		return executeOtherNonFlowInterceptors;
	}

	/**
	 * Set whether, in case of the action being ACTION_SHOW_VIEW or ACTION_DISPATCH, the registered
	 * 'normal' interceptors should be exected before displaying the view.
	 * The default is true.
	 * If true, and the current stage of intercepting is 'beforeMakeFormBean',
	 * the BeforePopulationInterceptors will be executed. If true, for all
	 * other stages except 'afterPerform' the AfterPerformInterceptors will
	 * be executed.
	 * @param executeOtherNonFlowInterceptors whether, in case of the action being ACTION_SHOW_VIEW or ACTION_DISPATCH, the registered
	 * 'normal' interceptors should be exected before displaying the view.
	 */
	public void setExecuteOtherNonFlowInterceptors(boolean executeOtherNonFlowInterceptors)
	{
		this.executeOtherNonFlowInterceptors = executeOtherNonFlowInterceptors;
	}

}
