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
 
package nl.openedge.maverick.framework.test;

import java.util.Locale;
import java.util.regex.Pattern;

import nl.openedge.maverick.framework.FormBeanContext;
import nl.openedge.maverick.framework.FormBeanCtrl;
import nl.openedge.maverick.framework.population.IgnoreFieldPopulator;

import org.infohazard.maverick.flow.ConfigException;
import org.infohazard.maverick.flow.ControllerContext;
import org.jdom.Element;

/**
 * @author Eelco Hillenius
 */
public class TestCtrl extends FormBeanCtrl
{

	private TestBean bean = null;
	private FormBeanContext formBeanContext = null;
	private String view = SUCCESS;

	/**
	 * @see nl.openedge.maverick.framework.FormBeanCtrl#perform(nl.openedge.maverick.framework.FormBeanContext, org.infohazard.maverick.flow.ControllerContext)
	 */
	protected String perform(
		FormBeanContext formBeanContext,
		ControllerContext cctx)
		throws Exception
	{
		return view;
	}

	/**
	 * @see nl.openedge.maverick.framework.FormBeanCtrl#makeFormBean(org.infohazard.maverick.flow.ControllerContext)
	 */
	protected Object makeFormBean(ControllerContext cctx)
	{
		this.bean = new TestBean();
		return bean;
	}
	
	/**
	 * @see org.infohazard.maverick.flow.ControllerSingleton#init(org.jdom.Element)
	 */
	public void init(Element controllerNode) throws ConfigException
	{
		// note that we will not have a valid controllerNode when testing
		addInterceptor(new TestBeforePerformInterceptor());
		addPopulator("uppercaseTest", new ToUpperCasePopulator());
		addPopulator("ignore", new IgnoreFieldPopulator()); // block property by field name
		addPopulator(Pattern.compile("(.)*ByRegex$"), 
			new IgnoreFieldPopulator()); // block property by regex pattern
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
	 * @see nl.openedge.maverick.framework.FormBeanCtrl#getLocaleForRequest(org.infohazard.maverick.flow.ControllerContext, nl.openedge.maverick.framework.FormBeanContext)
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

}
