/*
 * $Id: FormBeanCtrlBase.java,v 1.6 2004-04-05 09:56:22 eelco12 Exp $
 * $Revision: 1.6 $
 * $Date: 2004-04-05 09:56:22 $
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

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nl.openedge.baritus.interceptors.Interceptor;
import nl.openedge.baritus.interceptors.flow.FlowInterceptorContext;
import nl.openedge.baritus.population.FieldPopulator;
import nl.openedge.baritus.util.MessageUtils;
import nl.openedge.baritus.util.MultiHashMap;
import nl.openedge.baritus.util.ValueUtils;
import nl.openedge.baritus.validation.FieldValidator;
import nl.openedge.baritus.validation.FormValidator;
import nl.openedge.baritus.validation.ValidationActivationRule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infohazard.maverick.flow.Controller;
import org.infohazard.maverick.flow.ControllerContext;

/**
 * FormBeanBase is the class that does the real work within Baritus.
 * Usually, you should extend the singleton implementation FormBeanCtrl.
 * However, if you want to have behaviour like Maverick's ThrowawayFormBeanUser
 * (a new instance of the controller is created on each request), you
 * can extend from this class directly. Note that as method init(Node) is
 * specific for the ControllerSingleton that is implemented in FormBeanCtrl,
 * you do not have this method at your disposal here, hence you should do 
 * initialisation in your constructor instead.
 * 
 * @author Eelco Hillenius
 */
public abstract class FormBeanCtrlBase implements Controller
{
	
	//-------------------------- constants ---------------------------------------/
	
	/** Common name for the typical "success" view */
	public static final String SUCCESS = "success";

	/** Common name for the typical "logon" view */
	public static final String LOGON = "logon";

	/** Common name for the typical "error" view */
	public static final String ERROR = "error";

	/** Common name for the "redirect" view */
	public static final String REDIRECT = "doredirect";
	
	/** session key for the current locale */
	public static final String SESSION_KEY_CURRENT_LOCALE = "_currentLocale";
	
	/*
	 * Key for request attribute that is used to store the formbean context.
	 * This key is used to be able to reuse the formbean context with
	 * multiple commands within the same request without having to figure out
	 * what the last Maverick beanName was (it is possible to override the name
	 * of the bean - 'model' by default - that is stored in the request by Maverick
	 * for each view). Not intended for use outside this class.
	 */
	private static final String REQUEST_ATTRIBUTE_FORMBEANCONTEXT = "__formBeanContext";
	
	//--------------------- logs --------------------------------------------------/
	
	/* log for this class */
	private static Log log = LogFactory.getLog(FormBeanCtrlBase.class);
	
	/* population log */
	private static Log populationLog = LogFactory.getLog(LogConstants.POPULATION_LOG);


	//------------------------ registries ----------------------------------------/
	
	// registry for populators by field name and the default populator
	private PopulatorRegistry populatorRegistry = new PopulatorRegistry(this);

	// registry for form validators, field validators and rules that apply to them
	private ValidatorRegistry validatorRegistry = new ValidatorRegistry();
	
	// default delegate for validation. Users of this framework can add
	// validator delegates if they want, e.g. ones that are based on
	// commons validator or formproc. This instance of ValidatorDelegate however,
	// will allways be executed (first).
	private ValidatorDelegate defaultValidatorDelegate
		= new DefaultValidatorDelegate(validatorRegistry, this);
		
	// validator delegates
	private List validatorDelegates = null;
	
	// registry for form interceptors
	private InterceptorRegistry interceptorRegistry = new InterceptorRegistry();
	
	//------------------------- paramters that influence the execution -----------/
	private ExecutionParams executionParams = new ExecutionParams();
	
	//------------------------- delegates ----------------------------------------/
	
	private InterceptorDelegate intercDlg = new InterceptorDelegate(interceptorRegistry);
	
	
	//------------------------- methods ------------------------------------------/

	
	/**
	 * Executes this controller. You should verride the perform method 
	 * to provide application logic. This method handles all callbacks and 
	 * interceptors. See the manual for the path(s) of execution.
	 *
	 * @param cctx maverick controller context
	 * @return String name of the view
	 * @throws ServletException
	 *
	 * @see org.infohazard.maverick.flow.ControllerSingleton#go(org.infohazard.maverick.flow.ControllerContext)
	 */
	public final String go(ControllerContext cctx) throws ServletException 
	{
		
		String viewName = SUCCESS; // default view
		
		Object bean = null;
		Locale locale = null;
		boolean populated = true;
		
		ExecutionParams execParams = getExecutionParams();
		if(execParams.isNoCache())
		{
			doSetNoCache(cctx); // set no cache headers
		}

		// create flowInterceptor objects
		FlowInterceptorContext flowInterceptorContext = new FlowInterceptorContext();
		flowInterceptorContext.setCctx(cctx);
		String flowInterceptView = null; // view possibly assigned by an interceptor
		
		// get the form bean context and set it in the flow interceptor context
		FormBeanContext formBeanContext = getFormBeanContext(cctx, execParams);
		// set the current controller to be able to use methods like getPropertyNameKey etc
		formBeanContext.setController(this);
		cctx.setModel(formBeanContext); // set context as model
		flowInterceptorContext.setFormBeanContext(formBeanContext);
		
		locale = getLocaleForRequest(cctx, formBeanContext); // get the locale
		formBeanContext.setCurrentLocale(locale); // and set in context
		
		// flow intercept before make form bean
		intercDlg.doInterceptBeforeMakeFormBean(cctx, formBeanContext);
		flowInterceptView = intercDlg.doFlowInterceptBeforeMakeFormBean(flowInterceptorContext);
		if(flowInterceptView != null)
		{
			return flowInterceptView;
		}
		
		try 
		{	
			// let controller create form
			bean = getFormBean(formBeanContext, cctx);
			formBeanContext.setBean(bean);
			
			// intercept before population
			intercDlg.doInterceptBeforePopulation(cctx, formBeanContext);
			flowInterceptView = intercDlg.doFlowInterceptBeforePopulation(flowInterceptorContext);
			if(flowInterceptView != null)
			{
				return flowInterceptView;
			}
			
			// populate if property of formBeanContext populateAndValidate is true 
			// (as it is by default)
			if(formBeanContext.isPopulateAndValidate())
			{
				populated = populateFormBean(cctx, formBeanContext, locale, execParams);	
			}
			
		} 
		catch(Exception e)
		{
			// as we should normally not get here, log stacktrace
			log.error("Unexpected exception occured during form population.", e);
				
			internalPerformError(cctx, execParams, formBeanContext, e);
			viewName = getErrorView(cctx, formBeanContext);
			
			// intercept population error
			intercDlg.doInterceptPopulationError(cctx, formBeanContext);	
			flowInterceptView = intercDlg.doFlowInterceptPopulationError(flowInterceptorContext);
			if(flowInterceptView != null)
			{
				return flowInterceptView;
			}		
		}
		
		try
		{	
			// was the bean population successful or should we execute perform
			// regardless of the population/ validation outcome?
			if(populated || execParams.isDoPerformIfPopulationFailed()) 
			{
				// flow intercept after population
				intercDlg.doInterceptAfterPopulation(cctx, formBeanContext);
				flowInterceptView = intercDlg.doFlowInterceptAfterPopulation(flowInterceptorContext);
				if(flowInterceptView != null)
				{
					return flowInterceptView;
				}
				
				// execute command en get the view
				viewName = this.perform(formBeanContext, cctx);	

			}
			else // bean population was not successful 
				 // this is the normal place of handling a failed (by either populators
				 // or validators) population attempt.
			{	
				internalPerformError(cctx, execParams, formBeanContext, null);
				viewName = getErrorView(cctx, formBeanContext);
				
				// intercept on population error
				intercDlg.doInterceptPopulationError(cctx, formBeanContext);
				flowInterceptView = intercDlg.doFlowInterceptPopulationError(flowInterceptorContext);
				if(flowInterceptView != null)
				{
					return flowInterceptView;
				}
			}
		} 
		catch (Exception e) 
		{
			log.error(e.getMessage(), e);
			
			// prepare for error command and execute it
			internalPerformError(cctx, execParams, formBeanContext, e);

			// flow intercept on perform error
			intercDlg.doInterceptPerformException(cctx, formBeanContext);
			flowInterceptorContext.setException(e);
			flowInterceptView = intercDlg.doFlowInterceptPerformException(flowInterceptorContext);
			if(flowInterceptView != null)
			{
				return flowInterceptView;
			}
			
			viewName = getErrorView(cctx, formBeanContext);
		}

		// intercept after perform
		intercDlg.doInterceptAfterPerform(cctx, formBeanContext);
		flowInterceptView = intercDlg.doFlowInterceptAfterPerform(flowInterceptorContext);
		if(flowInterceptView != null)
		{
			return flowInterceptView;
		}
		
		return viewName;
	}
	
	
	/* get the formBeanContext */
	private FormBeanContext getFormBeanContext(
		ControllerContext cctx,
		ExecutionParams _execParams)
	{
		FormBeanContext formBeanContext = null;
		if(_execParams.isReuseFormBeanContext()) 
			// if true, see if an instance was save earlier request
		{
			formBeanContext = (FormBeanContext)
				cctx.getRequest().getAttribute(REQUEST_ATTRIBUTE_FORMBEANCONTEXT);
			if(formBeanContext == null) 
			{
				formBeanContext = new FormBeanContext();
				cctx.getRequest().setAttribute(
					REQUEST_ATTRIBUTE_FORMBEANCONTEXT, formBeanContext);
			}
		}
		else
		{
			formBeanContext = new FormBeanContext();	
		}
		return formBeanContext;
	}
	
	/* get the form bean */
	private Object getFormBean(FormBeanContext formBeanContext, ControllerContext cctx)
	{
		return this.makeFormBean(formBeanContext, cctx);
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
		Locale locale,
		ExecutionParams _execParams) 
		throws Exception 
	{

		boolean popSuccessCtrlParams = true;
		boolean popSuccessSessionAttribs = true;
		boolean popSuccessRequestParams = true;
		boolean popSuccessRequestAttribs = true;
		
		Map allParameters = new HashMap(); // for use with validators later on
		
		// The order in which parameters/ attributes are used for population:
		//	1. controller parameters (if includeControllerParameters == true)
		//	2. session attributes (if includeSessionAttributes == true)
		//	3. request parameters
		//	4. request attributes (if includeRequestAttributes == true)
		
		StringBuffer traceMsg = null;
		Object bean = formBeanContext.getBean();
		if(populationLog.isDebugEnabled())
		{
			traceMsg = new StringBuffer();
			traceMsg.append("trace ctrl ")
				.append(this).append("; populate of bean ")
				.append(bean).append(" with parameters:");
		}
		
		// controller parameters
		if(_execParams.isIncludeControllerParameters() &&
			(cctx.getControllerParams() != null))
		{
			Map parameters = new HashMap(cctx.getControllerParams());

			allParameters.putAll(parameters);
			traceParameters(parameters, traceMsg, "maverick controller params");
						
			popSuccessCtrlParams = populateWithErrorReport(
				cctx, formBeanContext, parameters, locale);
		}		

		// session attributes
		if(_execParams.isIncludeSessionAttributes())
		{
			Map parameters = new HashMap();
			HttpSession httpSession = cctx.getRequest().getSession();
			Enumeration enum = httpSession.getAttributeNames();
			while(enum.hasMoreElements())
			{
				String attrName = (String)enum.nextElement();
				parameters.put(attrName, httpSession.getAttribute(attrName));	
			}
			
			allParameters.putAll(parameters);
			traceParameters(parameters, traceMsg, "session attributes");
			
			popSuccessSessionAttribs = populateWithErrorReport(
				cctx, formBeanContext, parameters, locale);
		}

		// request parameters
		Map reqParameters = new HashMap();

		reqParameters.putAll(cctx.getRequest().getParameterMap());
		
		allParameters.putAll(reqParameters);
		traceParameters(reqParameters, traceMsg, "request parameters");
		
		popSuccessRequestParams = populateWithErrorReport(
			cctx, formBeanContext, reqParameters, locale);

		// request attributes
		if(_execParams.isIncludeRequestAttributes())
		{
			Map parameters = new HashMap();
			HttpServletRequest request = cctx.getRequest();
			Enumeration enum = request.getAttributeNames();
			while(enum.hasMoreElements())
			{
				String attrName = (String)enum.nextElement();
				if(!REQUEST_ATTRIBUTE_FORMBEANCONTEXT.equals(attrName))
				{
					// extra check for Model
					Object param = request.getAttribute(attrName);
					if(! (param instanceof FormBeanContext) )
					{
						parameters.put(attrName, param);	
					}	
				}	
			}
			
			allParameters.putAll(parameters);
			traceParameters(parameters, traceMsg, "request attributes");
			
			popSuccessRequestAttribs = populateWithErrorReport(
				cctx, formBeanContext, parameters, locale);
		}
		
		if(populationLog.isDebugEnabled())
		{
			if(allParameters.isEmpty()) traceMsg.append("\n\tno parameters found");
			populationLog.debug(traceMsg);
		}
		
		// we consider population successfull if population of all parameters succeeded
		boolean succeeded = (popSuccessCtrlParams && popSuccessSessionAttribs && 
			popSuccessRequestParams && popSuccessRequestAttribs);
		
		// do custom validation
		succeeded = defaultValidatorDelegate.doValidation(
			cctx, formBeanContext, _execParams, allParameters, locale, succeeded);
			
		List additionalValidators = getValidatorDelegates();
		if(additionalValidators != null) // if there are any delegates registered
		{
			for(Iterator i = additionalValidators.iterator(); i.hasNext(); )
			{ // loop through them
				boolean _succeeded = succeeded; // set to last known val
				
				ValidatorDelegate valDel = (ValidatorDelegate)i.next();
				_succeeded = valDel.doValidation(
					cctx, formBeanContext, executionParams,
					allParameters, locale, _succeeded);
				
				if(!_succeeded) succeeded = false;
			}
		}
		
		return succeeded;
	}
	
	/* extra debug info */
	private final void traceParameters(
		Map parameters, 
		StringBuffer msg,
		String parameterSet)
	{
		if(populationLog.isDebugEnabled() 
			&& (parameters != null) && (!parameters.isEmpty()))
		{
			msg.append("\n\t").append(parameterSet).append(": ");
			for(Iterator i = parameters.keySet().iterator(); i.hasNext(); )
			{
				Object key = i.next();
				Object value = parameters.get(key);
				msg.append(key + " = " + ValueUtils.convertToString(value));
				if(i.hasNext()) msg.append(", ");
			}	
		}
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
		
		// Do nothing unless both arguments have been specified or parameters is empty
		if ((formBeanContext == null) || (parameters == null) || (parameters.isEmpty())) 
		{
			return true;
		}

		boolean succeeded = true;
		
		// try regex population
		succeeded = regexPopulateWithErrorReport(cctx, formBeanContext, parameters, locale);
		
		// populate remainder
		succeeded = fieldPopulateWithErrorReport(cctx, formBeanContext, parameters, locale, succeeded);
		
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
		
		Map regexFieldPopulators = populatorRegistry.getRegexFieldPopulators();
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
					
					// See if we have a custom populator registered for the given field
					if(populatorRegistry.getFieldPopulator(name) != null)
					{
						continue; // do not match on regexp
					}
					
					Object value = parameters.get(name);

					try
					{						
						Matcher matcher = pattern.matcher(name);
						if(matcher.matches())
						{
							FieldPopulator fieldPopulator = (FieldPopulator)
								regexFieldPopulators.get(pattern);
							
							keysToBeRemoved.add(name);
	
							boolean success;
							try
							{
								// execute population on form
								success = fieldPopulator.setProperty(
									cctx, formBeanContext, name, value);
							}
							catch (Exception e)
							{
								populationLog.error(e);
								if(populationLog.isDebugEnabled())
								{
									populationLog.error(e.getMessage(), e);
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
							log.error(e.getMessage(), e); // print stacktrace
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
				// See if we have a custom populator registered for the given field
				FieldPopulator fieldPopulator = 
					populatorRegistry.getFieldPopulator(name);
			
				if(fieldPopulator == null) // if no custom populator was found, we use the default
				{
					fieldPopulator = populatorRegistry.getDefaultFieldPopulator();
				}
			
				// execute population on form
				success = fieldPopulator.setProperty(
					cctx, formBeanContext, name, value);

			}
			catch (Exception e)
			{
				populationLog.error(e);
				if(populationLog.isDebugEnabled())
				{
					populationLog.error(e.getMessage(), e);
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
	 * Called when populating the form failed.
	 * @param cctx maverick context
	 * @param params execution params
	 * @param formBeanContext context with form bean
	 */
	private void internalPerformError(
		ControllerContext cctx, 
		ExecutionParams execParams,
		FormBeanContext formBeanContext,
		Throwable e)
		throws ServletException
	{
		if(formBeanContext == null) return;
	
		if(e != null) 
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

		// set overrides for the current request parameters if params allow
		if(execParams.isSaveReqParamsAsOverrideFieldsOnError())
		{
			formBeanContext.setOverrideField(cctx.getRequest().getParameterMap());	 
		}	
		
		if(populationLog.isDebugEnabled()) traceErrors(formBeanContext);
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
				populationLog.debug("\t " + key + " == " + ValueUtils.convertToString(value));
			}
			populationLog.debug("----------------------------------------------------------");	
		}
	}
	
	/**
	 * Set error for field with name 'name' in case of a conversion error. 
	 * uses getConversionErrorLabelKey to get the specific label.
	 * NOTE: this will be used in case of conversion errors ONLY. If a validator
	 * causes an error (after normal conversion) the error message of the validator
	 * will be used, not this method.
	 * 
	 * The message is formatted with objects triedValue, name (by calling getPropertyNameKey)
	 * and t, so you can use {0}, {1} and {2} resp. with your custom message.
	 * 
	 * If there is an entry in the default resource bundle that has form:
	 * 		formname.[name] (eg. formname.firstname and formname.lastname)
	 * 		the name parameter {1} will be replaced with this value.
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
			msgName = MessageUtils.getLocalizedMessage(getPropertyNameKey(name));

			String desiredPattern = null;
			if(t instanceof nl.openedge.baritus.converters.ConversionException)
			{
				nl.openedge.baritus.converters.ConversionException ex = 
					(nl.openedge.baritus.converters.ConversionException)t;
				desiredPattern = ex.getDesiredPattern();
			}

			if(msgName != null)
			{
				msg = MessageUtils.getLocalizedMessage(
					key, new Object[]{triedValue, msgName, t, desiredPattern});	
			}
			else
			{
				msg = MessageUtils.getLocalizedMessage(
					key, new Object[]{triedValue, name, t, desiredPattern});
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
	 * Get the message bundle key for the given property name.
	 * 
	 * @param name property name
	 * @return String the message bundle key of the property, defaults to "formname." + name
	 */
	protected String getPropertyNameKey(String name)
	{
		return "formname." + name;
	}
	
	/**
	 * Get the message bundle key for a conversion error for the given type 
	 * and field with the given name.
	 * 
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
	 * Set the override value for the provdied field name. 
	 * This method will be called if a property could not be
	 * set on the form or did not pass validation. by registering the 'original' value
	 * (possibly modified by overrides of either this method or the 'getOverrideValue'
	 * of the validator that was the cause of the validation failure) end users can have
	 * their 'wrong' input value shown.
	 * 
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
	 * This method must be overriden to perform application logic.
	 *
	 * @param formBeanContext context with the populated bean returned by makeFormBean().
	 * @param cctx maverick controller context.
	 * 
	 * @return String view to display
	 * @throws Exception As a last fallthrough, exceptions are handled by the framework.
	 *  It is advisable however, to keep control of the error reporting, and let this
	 *  method do the exception handling
	 */
	protected abstract String perform(
		FormBeanContext formBeanContext, 
		ControllerContext cctx) 
		throws Exception;
												
	/**
	 * This method will be called to produce a bean whose properties
	 * will be populated with the http currentRequest parameters. The resulting object
	 * will be placed in the formBeanContext after this call.
	 * 
	 * @param formBeanContext the form bean context. If this is the first control within
	 * 	a request, the formBeanContext will be empty. If this is a not the first 
	 *  control that is called within a request (i.e. more controls are linked together),
	 *  and execution parameters property reuseFormBeanContext is true (which is the
	 *  default), the formBeanContext may allready contain error registrations, and
	 *  contains the formBean that was used in the control before this one.
	 * @param cctx controller context with references to request, response etc.
	 * 
	 * @return Object instance of bean that should be populated. Right after the call
	 *  to makeFormBean, the instance will be set in the formBeanContext as property 'bean'
	 * 
	 */
	protected abstract Object makeFormBean(
		FormBeanContext formBeanContext, 
		ControllerContext cctx);
		
	//**************************** validators ********************************************/
	
	/**
	 * Add a validator delegate.
	 * ValidatorDelegates can do validation on input and populated form beans.
	 * Besides the allways used DefaultValidatorDelegate, users of Baritus
	 * can register additional delegates, for instance to be able to plug in
	 * validator mechanisms like FormProc or Commons Validator.
	 * 
	 * @param validatorDelegate
	 */
	protected void addValidatorDelegate(ValidatorDelegate validatorDelegate)
	{
		if(validatorDelegates == null)
		{
			validatorDelegates = new ArrayList(1);
		}
		validatorDelegates.add(validatorDelegate);
	}
	
	/**
	 * Remove a validator delegate.
	 * 
	 * @param validatorDelegate
	 */
	protected void removeValidatorDelegate(ValidatorDelegate validatorDelegate)
	{
		if(validatorDelegates != null)
		{
			validatorDelegates.remove(validatorDelegate);
		}
	}
	
	/**
	 * Get the list of registered validator delegates.
	 * 
	 * @return the list of registered validator delegates, possibly null.
	 */
	protected List getValidatorDelegates()
	{
		return validatorDelegates;	
	}
	
	/**
	 * Register a field validator for the given fieldName. 
	 * multiple fieldValidators for one key are allowed. 
	 * 
	 * @param fieldName name of field
	 * @param validator validator instance
	 */
	protected void addValidator(String fieldName, FieldValidator validator)
	{
		validatorRegistry.addValidator(fieldName, validator);
	}
	
	/**
	 * Register a form validator.
	 * form validators will be called after the field level validators executed
	 * successfully, and thus can be used to check consistency etc.
	 * 
	 * @param validator the form level validator
	 */
	protected void addValidator(FormValidator validator)
	{
		validatorRegistry.addValidator(validator);
	}
	
	/**
	 * De-register the fieldValidators that were registered with the given fieldName.
	 * 
	 * @param fieldName name of field
	 */
	protected void removeValidators(String fieldName)
	{
		validatorRegistry.removeValidators(fieldName);
	}
	
	/**
	 * De-register the given validator that was registered with the given fieldName.
	 * 
	 * @param fieldName name of field
	 * @param validator the validator to remove for the given field
	 */
	protected void removeValidator(String fieldName, FieldValidator validator)
	{
		validatorRegistry.removeValidator(fieldName, validator);
	}
	
	/**
	 * De-register the given form level validator.
	 * 
	 * @param validator form validator
	 */
	protected void removeValidator(FormValidator validator)
	{
		validatorRegistry.removeValidator(validator);
	}
	
	/**
	 * Register the rule for the whole form.
	 * 
	 * @param rule activation rule
	 */
	protected void addValidationActivationRule(ValidationActivationRule rule)
	{
		validatorRegistry.addValidationActivationRule(rule);
	}
	
	/**
	 * De-register the given rule for the whole form.
	 * 
	 * @param rule global rule to remove
	 */
	protected void removeValidationActivationRule(ValidationActivationRule rule)
	{
		validatorRegistry.removeValidationActivationRule(rule);
	}
	
	/**
	 * Get the fieldValidators that were registered with the given fieldName.
	 * 
	 * @param fieldName name of the field
	 * @return MultiMap the fieldValidators that were registered with the given fieldName
	 */
	protected MultiHashMap getValidators(String fieldName)
	{
		return validatorRegistry.getValidators(fieldName);
	}
	
	
	//**************************** populators ********************************************/
	
	/**
	 * Register a field populator for the given fieldName. 
	 * Field populators override the default population of a property on the current form.
	 * 
	 * @param fieldName name of field
	 * @param populator populator instance
	 */
	protected void addPopulator(String fieldName, FieldPopulator populator)
	{
		populatorRegistry.addPopulator(fieldName, populator);
	}

	
	/**
	 * De-register the field populator that was registered with the given fieldName.
	 * 
	 * @param fieldName name of field
	 */
	protected void removePopulator(String fieldName)
	{
		populatorRegistry.removePopulator(fieldName);
	}
	
	/**
	 * Register a custom populator that overrides the default population
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
		populatorRegistry.addPopulator(pattern, populator);
	}

	
	/**
	 * Register a custom populator that overrides the default population
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
		populatorRegistry.removePopulator(pattern);
	}
	
	/**
	 * set the default field populator
	 * @param populator the default field populator
	 */
	protected void setDefaultPopulator(FieldPopulator populator)
	{
		populatorRegistry.setDefaultFieldPopulator(populator);
	}
	
	/**
	 * get the default field populator
	 * @return FieldPopulator the default field populator
	 */
	protected FieldPopulator getDefaultPopulator()
	{
		return populatorRegistry.getDefaultFieldPopulator();
	}
	
	//**************************** interceptors *******************************************/
	
	/**
	 * Add an interceptor to the current list of interceptors.
	 * 
	 * @param interceptor the interceptor to add to the current list of interceptors
	 */
	protected void addInterceptor(Interceptor interceptor)
	{
		interceptorRegistry.addInterceptor(interceptor);
	}
	
	/**
	 * Add an interceptor to the current list of interceptors at the specified position.
	 * 
	 * @param index index position where to insert the interceptor
	 * @param interceptor the interceptor to add to the current list of interceptors
	 */
	protected void addInterceptor(int index, Interceptor interceptor)
	{
		interceptorRegistry.addInterceptor(index, interceptor);
	}

	
	/**
	 * Remove an interceptor from the current list of interceptors.
	 * 
	 * @param interceptor the interceptor to remove from the current list of interceptors
	 */
	protected void removeInterceptor(Interceptor interceptor)
	{
		interceptorRegistry.removeInterceptor(interceptor);
	}
	
	// ************************ misc utility- and property methods *********************/
	
	/**
	 * Check if the value is null or empty.
	 * 
	 * @param value object to check on
	 * @return true if value is not null AND not empty 
	 * 	(e.g. in case of a String or Collection)
	 */	
	protected boolean isNullOrEmpty(Object value)
	{
		return ValueUtils.isNullOrEmpty(value);
	}
	
	/**
	 * Get localized message for given key.
	 * 
	 * @param key key of message
	 * @return String localized message
	 */
	public static String getLocalizedMessage(String key)
	{
		return getLocalizedMessage(key, Locale.getDefault());
	}

	/**
	 * Get localized message for given key and locale. 
	 * If locale is null, the default locale will be used.
	 * 
	 * @param key key of message
	 * @param locale locale for message
	 * @return String localized message
	 */
	protected static String getLocalizedMessage(String key, Locale locale)
	{
		return MessageUtils.getLocalizedMessage(key, locale);
	}

	/**
	 * Get localized message for given key and locale
	 * and format it with the given parameters. 
	 * If locale is null, the default locale will be used.
	 * 
	 * @param key key of message
	 * @param parameters parameters for the message
	 * @return String localized message
	 */
	protected static String getLocalizedMessage(String key, Object[] parameters)
	{
		return MessageUtils.getLocalizedMessage(key, parameters);
	}

	/**
	 * Get localized message for given key and locale
	 * and format it with the given parameters. 
	 * If locale is null, the default locale will be used.
	 * 
	 * @param key key of message
	 * @param locale locale for message
	 * @param parameters parameters for the message
	 * @return String localized message
	 */
	protected static String getLocalizedMessage(
		String key,
		Locale locale,
		Object[] parameters)
	{
		return MessageUtils.getLocalizedMessage(key, locale, parameters);
	}
	
	/**
	 * Get the prefered locale for the current request.
	 * IF a user is set in the form, the preferedLocale will be checked for this user.
	 * IF a locale is found as an attribute in the session with key 
	 * 		SESSION_KEY_CURRENT_LOCALE, the previous found locale(s) 
	 * 		will be replaced with this value.
	 * 
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
		return ERROR;
	}
	
	/**
	 * Set http response headers that indicate that this page should not be cached.
	 * @param cctx controller context
	 */
	protected void doSetNoCache(ControllerContext cctx)
	{
		HttpServletResponse response = cctx.getResponse();
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 1);
	}

	/**
	 * Get the execution params that are used to influence the execution
	 * of the formBeanCtrl (like population, validation, etc).
	 * 
	 * @return ExecutionParams the execution params that are used to influence the execution
	 * of the formBeanCtrl (like population, validation, etc).
	 */
	public ExecutionParams getExecutionParams()
	{
		return executionParams;
	}

	/**
	 * Set the execution params that are used to influence the execution
	 * of the formBeanCtrl (like population, validation, etc).
	 * 
	 * @param params the execution params that are used to influence the execution
	 * of the formBeanCtrl (like population, validation, etc).
	 */
	public void setExecutionParams(ExecutionParams params)
	{
		executionParams = params;
	}

}
