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
package nl.openedge.util.baritus;

import java.util.Locale;
import java.util.regex.Pattern;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.FormBeanCtrlBase;
import nl.openedge.baritus.interceptors.Interceptor;
import nl.openedge.baritus.population.FieldPopulator;
import nl.openedge.baritus.util.MultiHashMap;
import nl.openedge.baritus.validation.FieldValidator;
import nl.openedge.baritus.validation.FormValidator;
import nl.openedge.baritus.validation.ValidationActivationRule;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * Abstract class for testing controls in a simulated Baritus/ Maverick/ webapp environment, so that
 * constructs like validators, populators etc can be tested in isolation without much effort. This
 * basecontrol keeps references to things like the formBean, formBeanContext and logical view and
 * overrides some of the propected methods from Baritus with public methods in order to make testing
 * easier.
 * 
 * @author Eelco Hillenius
 */
public abstract class AbstractBaritusTestCtrl extends FormBeanCtrlBase
{

	/**
	 * reference to the current form bean.
	 */
	protected Object bean = null;

	/**
	 * reference to the current form bean context.
	 */
	protected FormBeanContext formBeanContext = null;

	/**
	 * reference to the current logical view.
	 */
	protected String view = SUCCESS;

	/**
	 * get current test bean.
	 * 
	 * @return Object current test bean
	 */
	public Object getTestBean()
	{
		return bean;
	}

	/**
	 * get view.
	 * 
	 * @return String view
	 */
	public String getView()
	{
		return view;
	}

	/**
	 * @see nl.openedge.baritus.FormBeanBase#getLocaleForRequest(org.infohazard.maverick.flow.ControllerContext,
	 *      nl.openedge.baritus.FormBeanContext)
	 */
	protected Locale getLocaleForRequest(final ControllerContext cctx,
			final FormBeanContext deFormBeanContext)
	{
		// hack to be able to get the formBeanContext
		this.formBeanContext = deFormBeanContext;

		return super.getLocaleForRequest(cctx, formBeanContext);
	}

	/**
	 * get formBeanContext.
	 * 
	 * @return FormBeanContext form bean context
	 */
	public FormBeanContext getFormBeanContext()
	{
		return formBeanContext;
	}

	/**
	 * Get error view. This is 'error' by default.
	 * 
	 * @param cctx
	 *            controller context
	 * @param deFormBeanContext
	 *            context
	 * @return String logical name of view
	 */
	protected String getErrorView(final ControllerContext cctx,
			final FormBeanContext deFormBeanContext)
	{
		this.view = ERROR;
		return ERROR;
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#addValidator(java.lang.String,
	 *      nl.openedge.baritus.validation.FieldValidator)
	 */
	public void addValidator(final String fieldName, final FieldValidator validator)
	{
		super.addValidator(fieldName, validator);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#addValidator(nl.openedge.baritus.validation.FormValidator)
	 */
	public void addValidator(final FormValidator validator)
	{
		super.addValidator(validator);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#addInterceptor(int,
	 *      nl.openedge.baritus.interceptors.Interceptor)
	 */
	protected void addInterceptor(final int index, final Interceptor interceptor)
	{
		super.addInterceptor(index, interceptor);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#addInterceptor(nl.openedge.baritus.interceptors.Interceptor)
	 */
	protected void addInterceptor(final Interceptor interceptor)
	{
		super.addInterceptor(interceptor);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#addPopulator(java.util.regex.Pattern,
	 *      nl.openedge.baritus.population.FieldPopulator)
	 */
	protected void addPopulator(final Pattern pattern, final FieldPopulator populator)
	{
		super.addPopulator(pattern, populator);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#addPopulator(java.lang.String,
	 *      nl.openedge.baritus.population.FieldPopulator)
	 */
	protected void addPopulator(final String fieldName, final FieldPopulator populator)
	{
		super.addPopulator(fieldName, populator);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#addValidationActivationRule(nl.openedge.baritus.validation.ValidationActivationRule)
	 */
	protected void addValidationActivationRule(final ValidationActivationRule rule)
	{
		super.addValidationActivationRule(rule);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#getDefaultPopulator()
	 */
	protected FieldPopulator getDefaultPopulator()
	{
		return super.getDefaultPopulator();
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#getValidators(java.lang.String)
	 */
	protected MultiHashMap getValidators(final String fieldName)
	{
		return super.getValidators(fieldName);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#removeInterceptor(nl.openedge.baritus.interceptors.Interceptor)
	 */
	protected void removeInterceptor(final Interceptor interceptor)
	{
		super.removeInterceptor(interceptor);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#removePopulator(java.util.regex.Pattern)
	 */
	protected void removePopulator(final Pattern pattern)
	{
		super.removePopulator(pattern);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#removePopulator(java.lang.String)
	 */
	protected void removePopulator(final String fieldName)
	{
		super.removePopulator(fieldName);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#removeValidationActivationRule(nl.openedge.baritus.validation.ValidationActivationRule)
	 */
	protected void removeValidationActivationRule(final ValidationActivationRule rule)
	{
		super.removeValidationActivationRule(rule);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#removeValidator(nl.openedge.baritus.validation.FormValidator)
	 */
	protected void removeValidator(final FormValidator validator)
	{
		super.removeValidator(validator);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#removeValidator(java.lang.String,
	 *      nl.openedge.baritus.validation.FieldValidator)
	 */
	protected void removeValidator(final String fieldName, final FieldValidator validator)
	{
		super.removeValidator(fieldName, validator);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#removeValidators(java.lang.String)
	 */
	protected void removeValidators(final String fieldName)
	{
		super.removeValidators(fieldName);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#setDefaultPopulator(nl.openedge.baritus.population.FieldPopulator)
	 */
	protected void setDefaultPopulator(final FieldPopulator populator)
	{
		super.setDefaultPopulator(populator);
	}
}