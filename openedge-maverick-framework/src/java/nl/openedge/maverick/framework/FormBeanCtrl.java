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
package nl.openedge.maverick.framework;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nl.openedge.maverick.framework.interceptors.AfterPerformInterceptor;
import nl.openedge.maverick.framework.interceptors.BeforePerformInterceptor;
import nl.openedge.maverick.framework.interceptors.Interceptor;
import nl.openedge.maverick.framework.interceptors.PopulationErrorInterceptor;
import nl.openedge.maverick.framework.population.DefaultFieldPopulator;
import nl.openedge.maverick.framework.population.FieldPopulator;
import nl.openedge.maverick.framework.population.PropertyUtil;
import nl.openedge.maverick.framework.population.TargetPropertyMeta;
import nl.openedge.maverick.framework.validation.FieldValidator;
import nl.openedge.maverick.framework.validation.FormValidator;
import nl.openedge.maverick.framework.validation.ValidationRuleDependend;
import nl.openedge.maverick.framework.validation.ValidatorActivationRule;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.MappedPropertyDescriptor;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infohazard.maverick.flow.ConfigException;
import org.infohazard.maverick.flow.ControllerContext;
import org.infohazard.maverick.flow.ControllerSingleton;
import org.jdom.Element;

/**
 * FormBeanCtrl is a base class for singleton controllers which use
 * external FormBeans rather than populating themselves. 
 * 
 * It is the more refined version of FormBeanUser from the maverick project.
 * Using (extending) this class gives you:
 * <ul>
 * 	<li>automatic population of the form</li>
 * 	<li>plugable conversion mechanism (using BeanUtils)</li>
 * 	<li>flexible, localized error handling</li>
 * 	<li>plugable form-level and field-level validation</li>
 * 	<li>mechanism to get the last input for fields if conversion failed</li>
 * </ul>
 * 
 * Please read the 'usage' document for examples of how to use this class.
 * 
 * @see org.infohazard.maverick.ctl.FormBeanUser
 * @see org.apache.commons.beanutils.BeanUtils
 * 
 * @author Eelco Hillenius
 */
public abstract class FormBeanCtrl implements ControllerSingleton
{
	
	/** Common name for the typical "success" view */
	public static final String SUCCESS = "success";

	/** Common name for the typical "logon" view */
	public static final String LOGON = "logon";

	/** Common name for the typical "error" view */
	public static final String ERROR = "error";

	/** Common name for the "redirect" view */
	public static final String REDIRECT = "doredirect";
	
	public static final String SESSION_KEY_CURRENT_LOCALE = "_currentLocale";
	
	/** log for this class */
	private static Log log = LogFactory.getLog(FormBeanCtrl.class);
	
	/** population log */
	private static Log populationLog = LogFactory.getLog(LogConstants.POPULATION_LOG);

	/** special performance log */
	private static Log performanceLog = 
		LogFactory.getLog(LogConstants.PERFORMANCE_LOG);
	
	/** if true, the no cache headers will be set */
	private boolean noCache = true;
	
	/**
	 * If we get an empty string, should it be translated to a null (true) or should
	 * the empty String be kept (false). This property is true by default
	 */
	private boolean setNullForEmptyString = true;
	
	/**
	 * Indicates whether we should use the configuration parameters of the controller 
	 * for the population process as well. This property is false by default.
	 * 
	 * The order in which parameters/ attributes are used for population:
	 * 1. controller parameters (if includeControllerParameters == true)
	 * 2. session attributes (if includeSessionAttributes == true)
	 * 3. request parameters
	 * 4. request attributes (if includeRequestAttributes == true)
	 * 
	 * A map with parameters for the population process is filled in the above order, which
	 * means that every step can overwrite it's predecessor.
	 *  
	 */	
	private boolean includeControllerParameters = false;
	
	/**
	 * Indicates whether we should use the session attributes of the current session
	 * for the population process as well. This property is false by default.
	 * 
	 * The order in which parameters/ attributes are used for population:
	 * 1. controller parameters (if includeControllerParameters == true)
	 * 2. session attributes (if includeSessionAttributes == true)
	 * 3. request parameters
	 * 4. request attributes (if includeRequestAttributes == true)
	 * 
	 * A map with parameters for the population process is filled in the above order, which
	 * means that every step can overwrite it's predecessor.
	 * 
	 */	
	private boolean includeSessionAttributes = false;
	
	/**
	 * Indicates whether we should use the currentRequest attributes for the population process
	 * as well. This property is false by default.
	 * 
	 * The order in which parameters/ attributes are used for population:
	 * 1. controller parameters (if includeControllerParameters == true)
	 * 2. session attributes (if includeSessionAttributes == true)
	 * 3. request parameters
	 * 4. request attributes (if includeRequestAttributes == true)
	 * 
	 * A map with parameters for the population process is filled in the above order, which
	 * means that every step can overwrite it's predecessor.
	 * 
	 */	
	private boolean includeRequestAttributes = false;
	
	/**
	 * Indicates whether the form validators should be executed when one of the 
	 * field validators failed. Default == true
	 */
	private boolean doFormValidationIfFieldValidationFailed = true;
	
	/**
	 * Indicates whether the perform method of the control should be executed, even if the population/
	 * validation failed. Use this only in very special cases; extended usage will
	 * probably result in messy code. Default == false
	 */
	private boolean doPerformIfFieldValidationFailed = false;

	/**
	 * subclasses can register fieldValidators for custom validation on field level
	 */
	private MultiMap fieldValidators = null;
	
	/**
	 * Subclasses can register custom populators that override the default population
	 * process for a given request parameter (that usually corresponds to a property with the
	 * same name in the form. Custom populators are stored by name of the property. 
	 */
	private Map fieldPopulators = null;
	
	/**
	 * Subclasses can register custom populators that override the default population
	 * process for all request parameters that match the regular expressions.
	 * The registered populators are tried for a match in order of registration. For each match
	 * that was found, the populator that was registered for it will be used, and the
	 * request parameter(s) will be removed from the map that is used for population.
	 * As a consequence, regexFieldPopulators overrule 'normal' field populators, and
	 * if more than one regex populator would match the parameters, only the first match is used.
	 * Custom populators are stored by name of the property. 
	 */
	private Map regexFieldPopulators = null;
	
	/**
	 * the default field populator
	 */
	private FieldPopulator defaultFieldPopulator = new DefaultFieldPopulator(this);
	
	/**
	 * subclasses can register formValidators for custom validation on form level
	 * formValidators will be called AFTER field validators executed SUCCESSFULLY
	 */
	private List formValidators = null;
	
	/**
	 * Optional objects that can be used to switch whether validation with
	 * custom fieldValidators should be performed in this currentRequest
	 */
	private List globalValidatorActivationRules = null;
	
	/**
	 * option list of interceptors. An interceptor can add behaviour to an action command.
	 */
	private List interceptors = null;

	
	/**
	 * Executes this controller.  Override one of the other perform()
	 * methods to provide application logic.
	 *
	 * @param cctx maverick controller context
	 * @return String name of the view
	 * @throws ServletException
	 *
	 * @see ControllerSingleton#perform
	 */
	public final String go(ControllerContext cctx) throws ServletException 
	{
		long tsBegin = System.currentTimeMillis();
		
		String viewName = SUCCESS;
		if(isNoCache())
		{
			doSetNoCache(cctx);
		}

		FormBeanContext formBeanContext = new FormBeanContext();
		Object bean = null;
		try 
		{	
			// let controller create form
			long tsBeginMakeFormBean = System.currentTimeMillis();

			bean = this.makeFormBean(cctx);
			formBeanContext.setBean(bean);
			
			if(performanceLog.isDebugEnabled())
			{
				long tsEndMakeFormBean = System.currentTimeMillis();
				performanceLog.debug("execution of " + this + ".makeFormBean: " +
					(tsEndMakeFormBean - tsBeginMakeFormBean) + " milis");
			}
			
			long tsBeginBefore = System.currentTimeMillis();
			
			// intercept before
			interceptBeforePerform(cctx, formBeanContext);
			
			if(performanceLog.isDebugEnabled())
			{
				long tsEndBefore = System.currentTimeMillis();
				performanceLog.debug("execution of " + this + ".doBefore: " +
					(tsEndBefore - tsBeginBefore) + " milis");
			}

			cctx.setModel(formBeanContext);
			Locale locale = getLocaleForRequest(cctx, formBeanContext);
			formBeanContext.setCurrentLocale(locale);
			
			// populate form
			boolean populated = populateFormBean(cctx, formBeanContext, locale);
			// go on?
			if(populated || isDoPerformIfFieldValidationFailed()) 
			{
				// execute command
				long tsBeginPerform = System.currentTimeMillis();
				viewName = this.perform(formBeanContext, cctx);
				
				if(performanceLog.isDebugEnabled())
				{
					long tsEndPerform = System.currentTimeMillis();
					performanceLog.debug("execution of " + this + ".perform: " +
						(tsEndPerform - tsBeginPerform) + " milis");
				}	

			}
			else // an error occured
			{
				// prepare for error command and execute it
				internalPerformError(cctx, formBeanContext);
				
				viewName = getErrorView(cctx, formBeanContext);
			}
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			if(formBeanContext != null) 
			{
				// save the exception so it can be displayed in the view
				if(e.getMessage() != null) 
				{
					formBeanContext.setError(e, false);	
				} 
				else 
				{
					// as a fallback, save the stacktrace
					formBeanContext.setError(e, true); 
				}
			}
			
			// prepare for error command and execute it
			internalPerformError(cctx, formBeanContext);
			
			viewName = getErrorView(cctx, formBeanContext);
		}
		
		long tsBeginAfter = System.currentTimeMillis();

		// intercept after
		interceptAfterPerform(cctx, formBeanContext);

		if(performanceLog.isDebugEnabled())
		{
			long tsEndAfter = System.currentTimeMillis();
			performanceLog.debug("execution of " + this + ".doAfter: " +
				(tsEndAfter - tsBeginAfter) + " milis");
		}
		
		if(performanceLog.isDebugEnabled())
		{
			long tsEnd = System.currentTimeMillis();
			performanceLog.debug("total execution of " + this + ": " +
				(tsEnd - tsBegin) + " milis");
		}
		
		return viewName;
	}
	
	/*
	 * populate the form
	 * @param cctx controller context
	 * @param formBeanContext context with bean to populate
	 * @param locale
	 * @throws Exception
	 * @return true if populate did not have any troubles, false otherwise
	 */
	private boolean populateFormBean(
		ControllerContext cctx, 
		FormBeanContext formBeanContext, 
		Locale locale) 
		throws Exception 
	{
		long tsBegin = System.currentTimeMillis();
		boolean success = true;
		
		// map to store the request parameters in
		Map parameters = new HashMap();
		// map to cache converted values in for efficiency
		Map convertedValuesCache = new HashMap();
		
		// The order in which parameters/ attributes are used for population:
		//	1. controller parameters (if includeControllerParameters == true)
		//	2. session attributes (if includeSessionAttributes == true)
		//	3. request parameters
		//	4. request attributes (if includeRequestAttributes == true)
		
		// controller parameters
		if(isIncludeControllerParameters())
		{
			parameters.putAll(cctx.getControllerParams());
		}		

		// session attributes
		if(isIncludeSessionAttributes())
		{
			HttpSession httpSession = cctx.getRequest().getSession();
			Enumeration enum = httpSession.getAttributeNames();
			while(enum.hasMoreElements())
			{
				String attrName = (String)enum.nextElement();
				parameters.put(attrName, httpSession.getAttribute(attrName));	
			}
		}

		// request parameters
		parameters.putAll(cctx.getRequest().getParameterMap());

		// request attributes
		if(isIncludeRequestAttributes())
		{
			HttpServletRequest request = cctx.getRequest();
			Enumeration enum = request.getAttributeNames();
			while(enum.hasMoreElements())
			{
				String attrName = (String)enum.nextElement();
				parameters.put(attrName, request.getAttribute(attrName));	
			}
		}
		
		if(populationLog.isDebugEnabled())
		{
			traceParameters(parameters, formBeanContext.getBean());
		}
		
		// populate using the paramters map		
		success = populateWithErrorReport(cctx, formBeanContext, parameters, locale);

		if(performanceLog.isDebugEnabled())
		{
			long tsEnd = System.currentTimeMillis();
			performanceLog.debug("execution of " + this + ".populateForm: " +
				(tsEnd - tsBegin) + " milis");
		}
		
		return success;
	}
	
	/* extra debug info */
	private final void traceParameters(Map parameters, Object bean)
	{
		populationLog.debug("trace ctrl " + this + "; populate of bean " 
			+ bean + " with parameters:");
		for(Iterator i = parameters.keySet().iterator(); i.hasNext(); )
		{
			Object key = i.next();
			Object value = parameters.get(key);
			populationLog.debug("\t " + key + " == " + ConvertUtils.convert(value));
		}
		populationLog.debug("----------------------------------------------------------");
	}
	
	/*
	 * default populate of form: BeanUtils way; set error if anything goes wrong
	 * @param cctx controller context
	 * @param formBeanContext context with current form bean
	 * @param parameters map with name/ values
	 * @param locale the prefered locale
	 * @return true if populate did not have any troubles, false otherwise
	 */
	private boolean populateWithErrorReport(
		ControllerContext cctx, 
		FormBeanContext formBeanContext, 
		Map parameters,
		Locale locale)
	{
		
		// Do nothing unless both arguments have been specified
		if ((formBeanContext == null) || (parameters == null)) 
		{
			return true;
		}

		boolean succeeded = true;
		
		// try regex population
		succeeded = regexPopulateWithErrorReport(cctx, formBeanContext, parameters, locale);
		
		// populate remainder
		succeeded = fieldPopulateWithErrorReport(cctx, formBeanContext, parameters, locale, succeeded);
		
		// do custom validation
		succeeded = doCustomValidation(cctx, formBeanContext, parameters, locale, succeeded);
		
		return succeeded;
	}
	
	/*
	 * populate with regex populators if any
	 * @param cctx controller context
	 * @param formBeanContext context with current form bean
	 * @param parameters map with name/ values. parameters that are found within this method
	 * 		will be removed, and are thus skipped in the remaining population process
	 * @param locale the prefered locale
	 * @return true if populate did not have any troubles, false otherwise
	 */
	private boolean regexPopulateWithErrorReport(
		ControllerContext cctx, 
		FormBeanContext formBeanContext, 
		Map parameters,
		Locale locale)
	{
		
		boolean succeeded = true;
		Object bean = formBeanContext.getBean();
		PropertyDescriptor propertyDescriptor = null;
		TargetPropertyMeta targetPropertyMeta = null;
		
		// first, see if there are matches with registered regex populators
		if(regexFieldPopulators != null) // there are registrations
		{
			List keysToBeRemoved = new ArrayList();
			for(Iterator i = regexFieldPopulators.keySet().iterator(); i.hasNext(); )
			{
				Pattern pattern = (Pattern)i.next();
				
				for(Iterator j = parameters.keySet().iterator(); j.hasNext(); )
				{
					String name = (String)j.next();
					Object value = parameters.get(name);

					try
					{						
						Matcher matcher = pattern.matcher(name);
						if(matcher.matches())
						{
							FieldPopulator fieldPopulator = (FieldPopulator)
								regexFieldPopulators.get(pattern);
							
							keysToBeRemoved.add(name);
	
							TargetPropertyMeta propInfo = null;
							try 
							{
								// get the descriptor
								propertyDescriptor = getPropertyDescriptor(bean, name);
	
								if (propertyDescriptor == null) 
								{
									continue; // Skip this property setter
								}
							}
							catch (NoSuchMethodException e) 
							{
								continue; // Skip this property setter
							}
							
							// resolve and get some more info we need for the target
							targetPropertyMeta = PropertyUtil.calculate(
								bean, name, propertyDescriptor);
	
							boolean success;
							try
							{
								// execute population on form
								success = fieldPopulator.setProperty(
									cctx, formBeanContext, name, value, 
									targetPropertyMeta, locale);
							}
							catch (Exception e)
							{
								populationLog.error(e);
								if(populationLog.isDebugEnabled())
								{
									e.printStackTrace();
								}
								continue;
							}
							if(!success)
							{
								succeeded = false;
							}
						}
					}
					catch (Exception e)
					{
						log.error(e);
						if(log.isDebugEnabled())
						{
							e.printStackTrace();
						}
						continue;
					}
				}
			}
			if(!keysToBeRemoved.isEmpty()) // for all found matches, remove the parameter
			{
				for(Iterator i = keysToBeRemoved.iterator(); i.hasNext(); )
				{
					parameters.remove(i.next());
				}
			}
		} // else nothing to do
		
		return succeeded;
	}
	
	/*
	 * Populate with field populators. 
	 * @param cctx controller context
	 * @param formBeanContext context with current form bean
	 * @param parameters map with name/ values.
	 * @param locale the prefered locale
	 * @param succeeded if any
	 * @return true if populate did not have any troubles AND succeeded was true, false otherwise
	 */
	private boolean fieldPopulateWithErrorReport(
		ControllerContext cctx, 
		FormBeanContext formBeanContext, 
		Map parameters,
		Locale locale,
		boolean succeeded)
	{

		Object bean = formBeanContext.getBean();
		
		// Loop through the property name/value pairs to be set
		Iterator names = parameters.keySet().iterator();
		while(names.hasNext()) 
		{
			boolean success = true;
			// Identify the property name and value(s) to be assigned
			String name = (String)names.next();
			if (name == null) continue;
			
			Object value = parameters.get(name);
	
			try
			{	
				// get the descriptor
				PropertyDescriptor propertyDescriptor = getPropertyDescriptor(bean, name);
				
				if(propertyDescriptor != null)
				{
					// resolve and get some more info we need for the target
					TargetPropertyMeta targetPropertyMeta = PropertyUtil.calculate(
						bean, name, propertyDescriptor);

					// See if we have a custom populator registered for the given field
					FieldPopulator fieldPopulator = null;
					if(fieldPopulators != null)
					{
						fieldPopulator = (FieldPopulator)fieldPopulators.get(name);
					}
				
					if(fieldPopulator == null) // if no custom populator was found, we use the default
					{
						fieldPopulator = defaultFieldPopulator;
					}
				
					// execute population on form
					success = fieldPopulator.setProperty(
						cctx, formBeanContext, name, value, targetPropertyMeta, locale);
							
				}
			}
			catch (Exception e)
			{
				populationLog.error(e);
				if(populationLog.isDebugEnabled())
				{
					e.printStackTrace();
				}
				continue;
			}
			if(!success)
			{
				succeeded = false;
			}
		}
		return succeeded;
	}
	
	/*
	 * get the property descriptor
	 * @param bean the bean
	 * @param name property name
	 * @return PropertyDescriptor descriptor if found AND if has writeable method 
	 */
	private PropertyDescriptor getPropertyDescriptor(
		Object bean, 
		String name) 
		throws IllegalAccessException, 
		InvocationTargetException, 
		NoSuchMethodException
	{
		PropertyDescriptor propertyDescriptor = 
			PropertyUtils.getPropertyDescriptor(bean, name);

		if(propertyDescriptor == null) return null;

		if (propertyDescriptor instanceof MappedPropertyDescriptor) 
		{
			MappedPropertyDescriptor pd = (MappedPropertyDescriptor)propertyDescriptor;
			if(pd.getMappedWriteMethod() == null) propertyDescriptor = null;
		}
		else if (propertyDescriptor instanceof IndexedPropertyDescriptor) 
		{
			IndexedPropertyDescriptor pd = (IndexedPropertyDescriptor)propertyDescriptor;
			if(pd.getIndexedWriteMethod() == null) propertyDescriptor = null;
		}
		else 
		{
			if(propertyDescriptor.getWriteMethod() == null) propertyDescriptor = null;
		}
		
		return propertyDescriptor;
	}

	
	/* handle custom validation for all fields */
	private boolean doCustomValidation(
		ControllerContext cctx, 
		FormBeanContext formBeanContext, 
		Map parameters,
		Locale locale,
		boolean succeeded)
	{
		
		if( (fieldValidators != null && (!fieldValidators.isEmpty())) ||
			(formValidators != null && (!formValidators.isEmpty())))
		{
			
			long tsBegin = System.currentTimeMillis();
	
			boolean doCustomValidation = true;
			// see if there's any globally (form level) defined rules
			if(globalValidatorActivationRules != null && (!globalValidatorActivationRules.isEmpty()))
			{
				for(Iterator i = globalValidatorActivationRules.iterator(); i.hasNext(); )
				{
					ValidatorActivationRule rule = (ValidatorActivationRule)i.next();
					doCustomValidation = rule.allowValidation(cctx, formBeanContext); // fire rule
					if(!doCustomValidation) break;
				}
			}
			
			if(doCustomValidation)
			{
				// if fieldValidators were registered
				if(fieldValidators != null && (!fieldValidators.isEmpty()))
				{
					Iterator names = parameters.keySet().iterator(); // loop through the properties
					while(names.hasNext())
					{
						String name = (String)names.next();
						if (name == null) continue;
						if(formBeanContext.getOverrideField(name) == null) 
							// see if there allready was an error registered
						{
							Collection propertyValidators = (Collection)fieldValidators.get(name);
							// these are the fieldValidators for one property
							if(propertyValidators != null)
							{
								try
								{
									succeeded = doCustomValidationForOneField(
										cctx, formBeanContext, locale, succeeded, 
										name, propertyValidators);
								}
								catch (Exception e)
								{
									if(populationLog.isDebugEnabled())
									{
										populationLog.debug(e.getMessage());
									}
									// ignore
								}
							}	
						} // else an error allready occured; do not validate
					}	
				}
				// if we are still successful so far, check with the form level validators
				if( (succeeded || isDoFormValidationIfFieldValidationFailed()) 
					&& (formValidators != null))
				{
					// check all registered until either all fired successfully or
					// one did not fire succesfully
					for(Iterator i = formValidators.iterator(); i.hasNext(); )
					{
						FormValidator fValidator = (FormValidator)i.next();
						boolean fireValidator = true;
						if(fValidator instanceof ValidationRuleDependend)
						{
							ValidatorActivationRule fRule = 
								((ValidationRuleDependend)fValidator).getValidationActivationRule();
							if(fRule != null)
							{
								if(!fRule.allowValidation(cctx, formBeanContext))
								{
									fireValidator = false;
								}	
							}
						}
						if(fireValidator)
						{
							if(!fValidator.isValid(cctx, formBeanContext))
							{
								succeeded = false;
								String[] msg = fValidator.getErrorMessage(
									cctx, formBeanContext, locale);
									
								if(msg != null && (msg.length > 0))
								{
									formBeanContext.setError(msg[0], msg[1]);	
								}
							}	
						}
						// else ignore
					}
				}
			}
			
			if(performanceLog.isDebugEnabled())
			{
				long tsEnd = System.currentTimeMillis();
				performanceLog.debug("execution of " + 
					this + ".doCustomValidation: " +
					(tsEnd - tsBegin) + " milis");
			}
		}
		
		return succeeded;
	}
	
	/* handle the custom validation for one field */
	private boolean doCustomValidationForOneField(
		ControllerContext cctx,
		FormBeanContext formBeanContext,
		Locale locale,
		boolean succeeded,
		String name,
		Collection propertyValidators)
		throws Exception
	{
		// get target value;
		// this could be done a bit more efficient, as we allready had
		// the (converted) value when populating. Working more efficient 
		// (like with a converted value cache) would make the API of
		// populators less straightforward, and by getting the property
		// from the bean instead of using the converted value, we are
		// sure that we get the property the proper (java beans) way.
		Object value = PropertyUtils.getProperty(formBeanContext.getBean(), name);
		
		// for all validators for this field
		for(Iterator j = propertyValidators.iterator(); j.hasNext(); )
		{
			FieldValidator validator = (FieldValidator)j.next();
			boolean validateField = true;
								
			if(validator instanceof ValidationRuleDependend) // should we execute rule
			{
				ValidatorActivationRule rule = 
					((ValidationRuleDependend)validator)
						.getValidationActivationRule();
				if(rule != null)
				{
					validateField = rule.allowValidation(cctx, formBeanContext); //test
					
					if(populationLog.isDebugEnabled())
					{
						populationLog.debug( "rule " + rule + 
							((validateField) ? " ALLOWS" : " DISALLOWS") +
							" validation with " + validator);
					}
				}
			}
								
			if(validateField)
			{
				// execute validation method
				boolean success = validator.isValid(cctx, formBeanContext, name, value);
				
				if(populationLog.isDebugEnabled())
				{
					populationLog.debug( "validation" +
						((success) ? " PASSED" : " FAILED") +
						" for field " + name + " using validator " + validator);
				}
				
				if(!success)
				{
					succeeded = false;
					try
					{
						String msgName = getLocalizedMessage(getPropertyNameKey(name));
						String msg = validator.getErrorMessage(
							cctx, formBeanContext, (msgName != null) ? msgName : name, value, locale);
						
						if(msg != null)
						{
							formBeanContext.setError(name, msg);	
						}
					}
					catch (Exception e)
					{
						if(populationLog.isDebugEnabled())
						{
							e.printStackTrace();	
						}
						else
						{
							populationLog.warn(e.getMessage(), e);
						}
						formBeanContext.setError(name, e.getMessage());
					}
					setOverrideField(cctx, formBeanContext, name, value, null, validator);
					break;
				}	
			}
		}
		return succeeded;	
	}
	
	/*
	 * Called when populating the form failed.
	 * @param cctx maverick context
	 * @param formBeanContext context with form bean
	 */
	private void internalPerformError(
		ControllerContext cctx, 
		FormBeanContext formBeanContext)
		throws ServletException
	{
		if(formBeanContext == null) return;
		// set the current model
		cctx.setModel(formBeanContext);

		// first, set overrides for the current request parameters
		formBeanContext.setOverrideField(cctx.getRequest().getParameterMap());	
		
		if(populationLog.isDebugEnabled()) traceErrors(formBeanContext);
		
		// do further (possibly user specific) error handling and/ or
		// view preparing
		interceptPopulationError(cctx, formBeanContext);	
	}
	
	/* extra debug info */
	private final void traceErrors(FormBeanContext formBeanContext)
	{
		Map errors = formBeanContext.getErrors();
		if(errors != null)
		{
			populationLog.debug("population of bean " + formBeanContext.getBean() + 
				" did not succeed; errors:");
				
			for(Iterator i = errors.keySet().iterator(); i.hasNext(); )
			{
				Object key = i.next();
				Object value = errors.get(key);
				populationLog.debug("\t " + key + " == " + ConvertUtils.convert(value));
			}
			populationLog.debug("----------------------------------------------------------");	
		}
	}
	
	/**
	 * Set error for field with name 'name' in case of a conversion error. 
	 * uses getConversionErrorLabelKey to get the specific label.
	 * NOTE: this will be used in case of conversion errors ONLY. If a validator
	 * causes an error (after normal conversion) the error message of the validator
	 * will be used, not this method
	 * 
	 * The message is formatted with objects triedValue, name (by calling getPropertyNameKey)
	 * and t, so you can use {0}, {1} and {2} resp. with your custom message.
	 * 
	 * If there is an entry in the default resource bundle that has form:
	 * 		formname.[name] (eg. formname.firstname and formname.lastname)
	 * 		the name parameter {1} will be replaced with this value
	 * 
	 * @param cctx controller context
	 * @param formBeanContext context with form bean
	 * @param targetType type of target property
	 * @param name name of field
	 * @param triedValue value that was tried for population
	 * @param t exception
	 */
	public void setConversionErrorForField(
		ControllerContext cctx, 
		FormBeanContext formBeanContext,
		Class targetType,
		String name, 
		Object triedValue, 
		Throwable t) 
	{

		try
		{
			String key = getConversionErrorLabelKey(targetType, name, triedValue);
		
			String msg = null;
			String msgName = null;
			msgName = getLocalizedMessage(getPropertyNameKey(name));

			String desiredPattern = null;
			if(t instanceof nl.openedge.maverick.framework.converters.ConversionException)
			{
				nl.openedge.maverick.framework.converters.ConversionException ex = 
					(nl.openedge.maverick.framework.converters.ConversionException)t;
				desiredPattern = ex.getDesiredPattern();
			}

			if(msgName != null)
			{
				msg = getLocalizedMessage(key, new Object[]{triedValue, msgName, t, desiredPattern});	
			}
			else
			{
				msg = getLocalizedMessage(key, new Object[]{triedValue, name, t, desiredPattern});
			}

			formBeanContext.setError(name, msg);
		}		
		catch (Exception e)
		{
			log.error(e.getMessage());
			formBeanContext.setError(name, e.getMessage());
		}
	}
	
	/**
	 * get the message bundle key for the given property name
	 * @param name property name
	 * @return String the message bundle key of the property, defaults to "formname." + name
	 */
	protected String getPropertyNameKey(String name)
	{
		return "formname." + name;
	}
	
	/**
	 * get the message bundle key for a conversion error for the given type 
	 * and field with the given name
	 * @param type type of the target property that threw the conversion error
	 * @param name name of the target property
	 * @param triedValue the value that could not be converted to the type of the 
	 * 	target property
	 * @return String message bundle key
	 */
	protected String getConversionErrorLabelKey(Class type, String name, Object triedValue)
	{
		String key = null; 
		
		if(Date.class.isAssignableFrom(type))
		{
			key = "invalid.field.input.date";
		}
		else if(Integer.TYPE.isAssignableFrom(type) 
			|| (Integer.class.isAssignableFrom(type)))
		{
			key = "invalid.field.input.integer";
		}
		else if(Double.TYPE.isAssignableFrom(type) 
			|| (Double.class.isAssignableFrom(type)))
		{
			key = "invalid.field.input.double";
		}
		else if(Long.TYPE.isAssignableFrom(type) 
			|| (Long.class.isAssignableFrom(type)))
		{
			key = "invalid.field.input.long";
		}
		else if(Boolean.TYPE.isAssignableFrom(type) 
			|| (Boolean.class.isAssignableFrom(type)))
		{
			key = "invalid.field.input.boolean";
		}
		else
		{
			key = "invalid.field.input";
		}
		
		return key;		
	}
	
	/**
	 * set override value for field. this method will be called if a property could not be
	 * set on the form or did not pass validation. by registering the 'original' value
	 * (possibly modified by overrides of either this method or the 'getOverrideValue'
	 * of the validator that was the cause of the validation failure) end users can have
	 * their 'wrong' input value shown
	 * @param cctx controller context
	 * @param formBeanContext context with form bean
	 * @param name name of the field
	 * @param triedValue the user input value/ currentRequest parameter
	 * @param t exception if known (may be null)
	 * @param validator the validator that was the cause of the validation failure, if one
	 * 	(is null if this was a conversion error)
	 */
	public void setOverrideField(
		ControllerContext cctx, 
		FormBeanContext formBeanContext, 
		String name, 
		Object triedValue, 
		Throwable t,
		FieldValidator validator) 
	{
		if(validator != null)
		{
			Object value = validator.getOverrideValue(triedValue);
			formBeanContext.setOverrideField(name, value);
		}
		else
		{
			formBeanContext.setOverrideField(name, triedValue);
		}
	}
	
	/**
	 * This method can be overriden to perform application logic.
	 *
	 * @param formBeanContext context with the populated bean returned by makeFormBean().
	 * @param cctx maverick controller context.
	 */
	protected abstract String perform(
		FormBeanContext formBeanContext, 
		ControllerContext cctx) 
		throws Exception;
												
	/**
	 * This method will be called to produce a bean whose properties
	 * will be populated with the http currentRequest parameters.  The parameters
	 * are useful for doing things like persisting beans across requests.
	 */
	protected abstract Object makeFormBean(ControllerContext cctx);
	
	/**
	 * @see ControllerSingleton@init
	 */
	public void init(Element controllerNode) throws ConfigException 
	{
		// initialise here...
	}
	
	//**************************** interceptors ******************************************/
	
	/*
	 * is called before any handling like form population etc.
	 * @param cctx maverick context
	 * @param formBeanContext context with unpopulated formBean
	 * @throws ServletException
	 */
	private void interceptBeforePerform(
		ControllerContext cctx,
		FormBeanContext formBeanContext) 
		throws ServletException
	{
		Interceptor[] commands = getInterceptors(BeforePerformInterceptor.class);
		if(commands != null)
		{
			int nbrcmds = commands.length;
			for(int i = 0; i < nbrcmds; i++)
			{
				((BeforePerformInterceptor)commands[i]).doBeforePerform(cctx, formBeanContext);
			}
		}
	}
	
	/*
	 * is called after all handling like form population etc. is done
	 * @param cctx maverick context
	 * @param formBeanContext context with populated (if succesful) formBean
	 * @throws ServletException
	 */
	private void interceptAfterPerform(
		ControllerContext cctx,
		FormBeanContext formBeanContext) 
		throws ServletException
	{
		Interceptor[] commands = getInterceptors(AfterPerformInterceptor.class);
		if(commands != null)
		{
			int nbrcmds = commands.length;
			for(int i = 0; i < nbrcmds; i++)
			{
				((AfterPerformInterceptor)commands[i]).doAfterPerform(cctx, formBeanContext);
			}
		}	
	}
	
	/*
	 * prepare error model for view by calling registered interceptors.
	 * This method will be called if the model failed to populate,
	 * or did not pass validation
	 * @param cctx maverick context
	 * @param formBeanContext context with form bean that failed to populate
	 * @throws ServletException
	 */
	private void interceptPopulationError(
		ControllerContext cctx, 
		FormBeanContext formBeanContext)
		throws ServletException
	{
		Interceptor[] commands = getInterceptors(PopulationErrorInterceptor.class);
		if(commands != null)
		{
			int nbrcmds = commands.length;
			for(int i = 0; i < nbrcmds; i++)
			{
				((PopulationErrorInterceptor)commands[i]).performError(cctx, formBeanContext);
			}
		}
	}
	
	/*
	 * get all registered interceptors of the provided type 
	 * @param type the type
	 * @return array of Interceptors or null if none
	 */
	private Interceptor[] getInterceptors(Class type)
	{
		Interceptor[] result = null;
		if(interceptors != null && (!interceptors.isEmpty()))
		{
			List temp = new ArrayList();
			for(Iterator i = interceptors.listIterator(); i.hasNext(); )
			{
				Interceptor intc = (Interceptor)i.next();
				if(type.isAssignableFrom(intc.getClass()))
				{
					temp.add(intc);
				}
			}
			if(!temp.isEmpty())
			{
				result = (Interceptor[])temp.toArray(new Interceptor[temp.size()]);
			}
		}
		return result;
	}
		
	//**************************** validators ********************************************/
	
	/**
	 * register a field validator for the given fieldName. 
	 * multiple fieldValidators for one key are allowed 
	 * @param fieldName name of field
	 * @param validator validator instance
	 */
	protected void addValidator(String fieldName, FieldValidator validator)
	{
		if(fieldValidators == null)
		{
			fieldValidators = new MultiHashMap();
		}
		fieldValidators.put(fieldName, validator);
	}
	
	/**
	 * register a form validator
	 * form validators will be called after the field level validators executed
	 * successfully, and thus can be used to check consistency etc.
	 * @param validator the form level validator
	 */
	protected void addValidator(FormValidator validator)
	{
		if(formValidators == null)
		{
			formValidators = new ArrayList();
		}
		formValidators.add(validator);
	}
	
	/**
	 * de-register the fieldValidators that were registered with the given fieldName
	 * @param fieldName name of field
	 */
	protected void removeValidators(String fieldName)
	{
		if(fieldValidators != null)
		{
			fieldValidators.remove(fieldName);
		}
	}
	
	/**
	 * de-register the given validator that was registered with the given fieldName
	 * @param fieldName name of field
	 * @param the validator to remove for the given field
	 */
	protected void removeValidator(String fieldName, FieldValidator validator)
	{
		if(fieldValidators != null)
		{
			fieldValidators.remove(fieldName, validator);
		}
	}
	
	/**
	 * de-register the given form level validator
	 * @param validator form validator
	 */
	protected void removeValidator(FormValidator validator)
	{
		if(formValidators != null)
		{
			formValidators.remove(validator);
		}
	}
	
	/**
	 * register the rule for the whole form
	 * @param fieldName name of field
	 * @param validator validator instance
	 */
	protected void addGlobalValidatorActivationRule(ValidatorActivationRule rule)
	{
		if(globalValidatorActivationRules == null)
		{
			globalValidatorActivationRules = new ArrayList();
		}
		globalValidatorActivationRules.add(rule);
	}
	
	/**
	 * de-register the given rule for the whole form
	 * @param fieldName
	 */
	protected void removeGlobalValidatorActivationRule(ValidatorActivationRule rule)
	{
		if(globalValidatorActivationRules != null)
		{
			globalValidatorActivationRules.remove(rule);
			if(globalValidatorActivationRules.isEmpty()) globalValidatorActivationRules = null;
		}
	}
	
	/**
	 * get the fieldValidators that were registered with the given fieldName
	 * @param fieldName name of the field
	 * @return FieldValidator the instance of FieldValidator that was registered with the given 
	 * 		fieldName or null if none was registered with that name
	 */
	protected Collection getValidators(String fieldName)
	{
		return (fieldValidators != null) ? (Collection)fieldValidators.get(fieldName) : null;
	}
	
	
	//**************************** populators ********************************************/
	
	/**
	 * Register a field populator for the given fieldName. 
	 * Field populators override the default population of a property on the current form
	 * @param fieldName name of field
	 * @param populator populator instance
	 */
	protected void addPopulator(String fieldName, FieldPopulator populator)
	{
		if(fieldPopulators == null)
		{
			fieldPopulators = new HashMap();
		}
		fieldPopulators.put(fieldName, populator);
	}

	
	/**
	 * de-register the field populator that was registered with the given fieldName
	 * @param fieldName name of field
	 */
	protected void removePopulator(String fieldName)
	{
		if(fieldPopulators != null)
		{
			fieldPopulators.remove(fieldName);
			if(fieldPopulators.isEmpty()) fieldPopulators = null;
		}
	}
	
	/**
	 * Register a custom populator that override the default population
	 * process for all request parameters that match the regular expression stored in
	 * the provided pattern.
	 * 
	 * The registered populators are tried for a match in order of registration. For each match
	 * that was found, the populator that was registered for it will be used, and the
	 * request parameter(s) will be removed from the map that is used for population.
	 * As a consequence, regexFieldPopulators overrule 'normal' field populators, and
	 * if more than one regex populator would match the parameters, only the first match is used.
	 * Custom populators are stored by name of the property.
	 * 
	 * @param pattern regex pattern
	 * @param populator populator instance
	 */
	protected void addPopulator(Pattern pattern, FieldPopulator populator)
	{
		if(regexFieldPopulators == null)
		{
			regexFieldPopulators = new HashMap();
		}
		regexFieldPopulators.put(pattern, populator);
	}

	
	/**
	 * Register a custom populator that override the default population
	 * process for all request parameters that match the regular expression stored in
	 * the provided pattern.
	 * 
	 * The registered populators are tried for a match in order of registration. For each match
	 * that was found, the populator that was registered for it will be used, and the
	 * request parameter(s) will be removed from the map that is used for population.
	 * As a consequence, regexFieldPopulators overrule 'normal' field populators, and
	 * if more than one regex populator would match the parameters, only the first match is used.
	 * Custom populators are stored by name of the property.
	 * 
	 * @param pattern regex pattern
	 */
	protected void removePopulator(Pattern pattern)
	{
		if(regexFieldPopulators != null)
		{
			regexFieldPopulators.remove(pattern);
		}
	}
	
	//**************************** interceptors *******************************************/
	
	/**
	 * add an interceptor to the current list of interceptors
	 * @param interceptor the interceptor to add to the current list of interceptors
	 */
	protected void addInterceptor(Interceptor interceptor)
	{
		if(interceptors == null)
		{
			interceptors = new ArrayList();
		}
		interceptors.add(interceptor);
	}

	
	/**
	 * remove an interceptor from the current list of interceptors
	 * @param interceptor the interceptor to remove from the current list of interceptors
	 */
	protected void removeInterceptor(Interceptor interceptor)
	{
		if(interceptors != null)
		{
			interceptors.remove(interceptor);
			if(interceptors.isEmpty()) interceptors = null;
		}
	}

	//*************************** utility methods for localized messages ********************/
	
	/**
	 * get localized message for given key
	 * @param key key of message
	 * @return String localized message
	 */
	public String getLocalizedMessage(String key)
	{
		return getLocalizedMessage(key, Locale.getDefault());
	}
	
	/**
	 * get localized message for given key and locale. 
	 * If locale is null, the default locale will be used
	 * @param key key of message
	 * @param locale locale for message
	 * @return String localized message
	 */
	public String getLocalizedMessage(String key, Locale locale)
	{	
		String msg = null;
		try
		{
			msg = getBundle(locale).getString(key);
		}
		catch (Exception e)
		{
			if(log.isDebugEnabled())
			{
				e.printStackTrace();
			}
		}
		return msg;
	}
	
	/**
	 * get localized message for given key and locale
	 * and format it with the given parameters. 
	 * If locale is null, the default locale will be used
	 * @param key key of message
	 * @param locale locale for message
	 * @param parameters parameters for the message
	 * @return String localized message
	 */
	public String getLocalizedMessage(String key, Object[] parameters)
	{	
		return getLocalizedMessage(key, null, parameters);
	}
	
	/**
	 * get localized message for given key and locale
	 * and format it with the given parameters. 
	 * If locale is null, the default locale will be used
	 * @param key key of message
	 * @param locale locale for message
	 * @param parameters parameters for the message
	 * @return String localized message
	 */
	public String getLocalizedMessage(String key, Locale locale, Object[] parameters)
	{
		String msg = null;
		try
		{
			msg = getBundle(locale).getString(key);
			msg = MessageFormat.format(msg, parameters);
		}
		catch (Exception e)
		{
			if(log.isDebugEnabled())
			{
				e.printStackTrace();
			}
		}
		return msg;
	}

	
	/* get resource bundle */
	private ResourceBundle getBundle(Locale locale)
	{
		ResourceBundle res = null;
		if(locale != null)
		{
			res = ResourceBundle.getBundle("resources", locale);
		}
		else
		{
			res = ResourceBundle.getBundle("resources");
		}
		return res;		
	}
	
	// ************************ misc utility- and property methods *********************/
	
	/**
	 * get the prefered locale for this currentRequest
	 * IF a user is set in the form, the preferedLocale will be checked for this user.
	 * IF a locale is found as an attribute in the session with key 
	 * 		SESSION_KEY_CURRENT_LOCALE, the previous found locale(s) 
	 * 		will be replaced with this value
	 * @param cctx controller context
	 * @param formBeanContext context
	 * @return Locale the prefered locale
	 */
	protected Locale getLocaleForRequest(
		ControllerContext cctx, 
		FormBeanContext formBeanContext)
	{	
		Locale locale = null;
		HttpSession session = cctx.getRequest().getSession();
		Locale temp = (Locale)session.getAttribute(SESSION_KEY_CURRENT_LOCALE);
		if(temp != null)
		{
			locale = temp;
		}

		if(locale == null)
		{
			locale = cctx.getRequest().getLocale();
		}	
		
		return locale;		
	}
	
	/**
	 * check if the value is null or empty
	 * @param value object to check on
	 * @return true if value is not null AND not empty (e.g. 
	 * in case of a String or Collection)
	 */
	protected boolean isNullOrEmpty(Object value)
	{
		if(value instanceof String)
		{
			return (value == null || (((String)value).trim().equals("")));
		}
		if(value instanceof Object[])
		{
			if(value == null)
			{
				return true;
			}
			else if(((Object[])value).length == 0)
			{
				return true;
			}
			else if(((Object[])value).length == 1)
			{
				return isNullOrEmpty(((Object[])value)[0]);
			}
			else
			{
				return false;
			}
		}
		else if(value instanceof Collection)
		{
			return (value == null || (((Collection)value).isEmpty()));
		}
		else if(value instanceof Map)
		{
			return (value == null || (((Map)value).isEmpty()));
		}
		else
		{
			return (value == null);
		}
	}
	
	/**
	 * get error view. 'error' by default
	 * @param cctx controller context
	 * @param formBeanContext context
	 * @return String logical name of view
	 */
	protected String getErrorView(
		ControllerContext cctx, 
		FormBeanContext formBeanContext)
	{
		return ERROR;
	}
	
	/*
	 * set no cache headers
	 */
	private void doSetNoCache(ControllerContext cctx)
	{
		HttpServletResponse response = cctx.getResponse();
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 1);
	}

	/**
	 * if true, the no cache headers will be set
	 * @return boolean
	 */
	protected boolean isNoCache()
	{
		return noCache;
	}

	/**
	 * if true, the no cache headers will be set
	 * @param noCache
	 */
	protected void setNoCache(boolean noCache)
	{
		this.noCache = noCache;
	}

	/**
	 * If we get an empty string it should be translated to a null (true) or to
	 * an empty String false. This property is true by default
	 * @return boolean
	 */
	public boolean isSetNullForEmptyString()
	{
		return setNullForEmptyString;
	}

	/**
	 * If we get an empty string it should be translated to a null (true) or to
	 * an empty String false. This property is true by default
	 * @param b
	 */
	public void setSetNullForEmptyString(boolean b)
	{
		setNullForEmptyString = b;
	}
	
	/**
	 * include controller parameters in the population process
	 * 
	 * The order in which parameters/ attributes are used for population:
	 * 1. controller parameters (if includeControllerParameters == true)
	 * 2. session attributes (if includeSessionAttributes == true)
	 * 3. request parameters
	 * 4. request attributes (if includeRequestAttributes == true)
	 * 
	 * @return boolean include controller parameters in the population process
	 */
	protected boolean isIncludeControllerParameters()
	{
		return includeControllerParameters;
	}
	
	/**
	 * set whether to include controller parameters in the population process
	 * 
	 * The order in which parameters/ attributes are used for population:
	 * 1. controller parameters (if includeControllerParameters == true)
	 * 2. session attributes (if includeSessionAttributes == true)
	 * 3. request parameters
	 * 4. request attributes (if includeRequestAttributes == true)
	 * 
	 * @param b set whether to include controller parameters in the population process
	 */
	protected void setIncludeControllerParameters(boolean b)
	{
		includeControllerParameters = b;
	}

	/**
	 * include session attributes in the population process
	 * 
	 * The order in which parameters/ attributes are used for population:
	 * 1. controller parameters (if includeControllerParameters == true)
	 * 2. session attributes (if includeSessionAttributes == true)
	 * 3. request parameters
	 * 4. request attributes (if includeRequestAttributes == true)
	 * 
	 * @return boolean include controller parameters in the population process
	 */
	protected boolean isIncludeSessionAttributes()
	{
		return includeSessionAttributes;
	}

	/**
	 * set whether to include session attributes in the population process
	 * 
	 * The order in which parameters/ attributes are used for population:
	 * 1. controller parameters (if includeControllerParameters == true)
	 * 2. session attributes (if includeSessionAttributes == true)
	 * 3. request parameters
	 * 4. request attributes (if includeRequestAttributes == true)
	 * 
	 * @param b set whether to include session attributes in the population process
	 */
	protected void setIncludeSessionAttributes(boolean b)
	{
		includeSessionAttributes = b;
	}

	/**
	 * include request attributes in the population process
	 * 
	 * The order in which parameters/ attributes are used for population:
	 * 1. controller parameters (if includeControllerParameters == true)
	 * 2. session attributes (if includeSessionAttributes == true)
	 * 3. request parameters
	 * 4. request attributes (if includeRequestAttributes == true)
	 * 
	 * @return boolean include request parameters in the population process
	 */
	protected boolean isIncludeRequestAttributes()
	{
		return includeRequestAttributes;
	}

	/**
	 * set whether to include request attributes in the population process
	 * 
	 * The order in which parameters/ attributes are used for population:
	 * 1. controller parameters (if includeControllerParameters == true)
	 * 2. session attributes (if includeSessionAttributes == true)
	 * 3. request parameters
	 * 4. request attributes (if includeRequestAttributes == true)
	 * 
	 * @param b set whether to include request attributes in the population process
	 */
	protected void setIncludeRequestAttributes(boolean b)
	{
		includeRequestAttributes = b;
	}

	/**
	 * Indicates whether the form validators should be executed when one of the 
	 * field validators failed. Default == true.
	 * 
	 * @return boolean Whether the form validators should be executed when one of the 
	 * 		field validators failed.
	 */
	protected boolean isDoFormValidationIfFieldValidationFailed()
	{
		return doFormValidationIfFieldValidationFailed;
	}

	/**
	 * Indicates whether the form validators should be executed when one of the 
	 * field validators failed. Default == true.
	 * 
	 * @param b whether the form validators should be executed when one of the 
	 * 		field validators failed.
	 */
	protected void setDoFormValidationIfFieldValidationFailed(boolean b)
	{
		doFormValidationIfFieldValidationFailed = b;
	}

	/**
	 * Whether the perform method of the control should be executed, even if the population/
	 * validation failed. Use this only in very special cases; extended usage will
	 * probably result in messy code. Default == false.
	 * 
	 * @return boolean whether the perform method of the control should be executed, even if the population/
	 * validation failed.
	 */
	protected boolean isDoPerformIfFieldValidationFailed()
	{
		return doPerformIfFieldValidationFailed;
	}

	/**
	 * Whether the perform method of the control should be executed, even if the population/
	 * validation failed. Use this only in very special cases; extended usage will
	 * probably result in messy code. Default == false.
	 * 
	 * @param b whether the perform method of the control should be executed, even if the population/
	 * validation failed.
	 */
	protected void setDoPerformIfFieldValidationFailed(boolean b)
	{
		doPerformIfFieldValidationFailed = b;
	}

}
