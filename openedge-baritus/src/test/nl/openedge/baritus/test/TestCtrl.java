/*
 * $Id: TestCtrl.java,v 1.8 2004-06-05 09:18:28 eelco12 Exp $
 * $Revision: 1.8 $
 * $Date: 2004-06-05 09:18:28 $
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

package nl.openedge.baritus.test;

import java.util.Locale;
import java.util.regex.Pattern;

import nl.openedge.baritus.ExecutionParams;
import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.FormBeanCtrlBase;
import nl.openedge.baritus.population.IgnoreFieldPopulator;

import org.infohazard.maverick.flow.ConfigException;
import org.infohazard.maverick.flow.ControllerContext;
import org.jdom.Element;

/**
 * @author Eelco Hillenius
 */
public class TestCtrl extends FormBeanCtrlBase
{

	private TestBean bean = null;
	private FormBeanContext formBeanContext = null;
	private String view = SUCCESS;

	/**
	 * @see nl.openedge.baritus.FormBeanBase#perform(nl.openedge.baritus.FormBeanContext, org.infohazard.maverick.flow.ControllerContext)
	 */
	protected String perform(
		FormBeanContext formBeanContext,
		ControllerContext cctx)
		throws Exception
	{
		return view;
	}

	/**
	 * @see nl.openedge.baritus.FormBeanBase#makeFormBean(org.infohazard.maverick.flow.ControllerContext)
	 */
	protected Object makeFormBean(
		FormBeanContext formBeanContext,
		ControllerContext cctx)
	{
		this.bean = new TestBean();
		return bean;
	}

	/**
	 * @see org.infohazard.maverick.flow.ControllerSingleton#init(org.jdom.Element)
	 */
	public void init(Element controllerNode) throws ConfigException
	{
		ExecutionParams params = getExecutionParams(null);
		params.setIncludeSessionAttributes(true);
		params.setIncludeRequestAttributes(true);
		fixExecutionParams(params);
		
		addPopulator("uppercaseTest", new ToUpperCasePopulator());
		addPopulator("ignore", new IgnoreFieldPopulator());
		// block property by field name
		addPopulator(
			Pattern.compile("(.)*ByRegex$"),
			new IgnoreFieldPopulator());
		// block property by regex pattern
		
		addValidator("toValidate1", new TestFieldValidator());
		addValidator("toValidate2", new TestFieldValidator()); // test form toValidate2[..]
		addValidator("toValidate3[0]", new TestFieldValidator()); // test form toValidate3[..]
	
		addValidator(new TestFormValidator1());
	}

	/**
	 * get test bean
	 * @return TestBean instance of test bean
	 */
	public TestBean getTestBean()
	{
		return bean;
	}

	/**
	 * get view
	 * @return String name of view
	 */
	public String getView()
	{
		return view;
	}

	/**
	 * @see nl.openedge.baritus.FormBeanBase#getLocaleForRequest(org.infohazard.maverick.flow.ControllerContext, nl.openedge.baritus.FormBeanContext)
	 */
	protected Locale getLocaleForRequest(
		ControllerContext cctx,
		FormBeanContext formBeanContext)
	{
		// hack to be able to get the formBeanContext
		this.formBeanContext = formBeanContext;

		return super.getLocaleForRequest(cctx, formBeanContext);
	}

	/**
	 * get formBeanContext
	 * @return FormBeanContext
	 */
	public FormBeanContext getFormBeanContext()
	{
		return formBeanContext;
	}
	
	/**
	 * Get error view. This is 'error' by default.
	 * 
	 * @param cctx controller context
	 * @param formBeanContext context
	 * @return String logical name of view
	 */
	protected String getErrorView(
		ControllerContext cctx, 
		FormBeanContext formBeanContext)
	{
		this.view = ERROR;
		return ERROR;
	}


}
