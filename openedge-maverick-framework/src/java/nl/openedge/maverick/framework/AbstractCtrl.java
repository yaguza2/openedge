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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.security.auth.Subject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import nl.openedge.access.AccessFilter;
import nl.openedge.access.UserPrincipal;
import nl.openedge.maverick.framework.util.UrlTool;
import nl.openedge.maverick.framework.validation.*;

import org.jdom.Element;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.infohazard.maverick.flow.ConfigException;
import org.infohazard.maverick.flow.ControllerContext;
import org.infohazard.maverick.flow.ControllerSingleton;

/**
 * baseclass for controls
 * @author Eelco Hillenius
 */
public abstract class AbstractCtrl implements ControllerSingleton 
{
	
	/** Common name for the typical "success" view */
	public static final String SUCCESS = "success";

	/** Common name for the typical "logon" view */
	public static final String LOGON = "logon";

	/** Common name for the typical "error" view */
	public static final String ERROR = "error";

	/** Common name for the "redirect" view */
	public static final String REDIRECT = "doredirect";
	
	/** log for this class */
	private static Log log = LogFactory.getLog(AbstractCtrl.class);
	
	/** if true, the no cache headers will be set */
	private boolean noCache = true;
	
	/** 
	 * sub classes can override this value. If it is true and a valid user
	 * was not found in the session, an error will be returned to the client
	 */
	protected boolean needsValidUser = false;
	
	/**
	 * Should the command NOT be executed if the form population fails?
	 * the default (false) has the effect that perform() will be executed
	 * even if the populate failed. Also, if this is false, no field
	 * overrides will be set for the fields that failed 
	 */
	protected boolean failOnPopulateError = false;
	
	/**
	 * If we get an empty string it should be translated to a null (true) or to
	 * an empty String false. This property is true by default
	 */
	protected boolean setNullForEmptyString = true;
	
	/**
	 * subclasses can register fieldValidators for custom validation on field level
	 */
	protected MultiMap fieldValidators = null;
	
	/**
	 * subclasses can register formValidators for custom validation on form level
	 * formValidators will be called AFTER field validators executed SUCCESSFULLY
	 */
	protected List formValidators = null;
	
	/**
	 * Optional objects that can be used to switch whether validation with
	 * custom fieldValidators should be performed in this request
	 * @author Eelco Hillenius
	 */
	protected List globalValidatorActivationRules = null;
	
	/**
	 * is called before any handling like form population etc.
	 * @param cctx maverick context
	 * @param formBean unpopulated formBean
	 * @throws ServletException
	 */
	public void doBefore(
		ControllerContext cctx,
		AbstractForm formBean) 
		throws ServletException
	{
		// noop
	}
	
	/**
	 * is called after all handling like form population etc. is done
	 * @param cctx maverick context
	 * @param formBean populated (if succesful) formBean
	 * @throws ServletException
	 */
	public void doAfter(
		ControllerContext cctx,
		AbstractForm formBean) 
		throws ServletException
	{
		// noop		
	}
	
	/**
	 * Executes this controller.  Override one of the other perform()
	 * methods to provide application logic.
	 *
	 * @see ControllerSingleton#perform
	 */
	public final String go(ControllerContext cctx) throws ServletException 
	{
		String viewName = SUCCESS;
		if(noCache)
		{
			setNoCache(cctx);
		}

		AbstractForm formBean = null;
		try 
		{	
			// let controller create form
			formBean = this.makeFormBean(cctx);
			
			// intercept before
			doBefore(cctx, formBean);

			if(needsValidUser)
			{
				// see if the user was saved in the session, and if so, store in form
				boolean foundUser = checkUser(cctx, formBean);
				cctx.setModel(formBean);

				// can we go on?
				if ((!foundUser) && needsValidUser)
				{
					// nope, we can't
					formBean.setError("global.message", 
						"user was not found in session");

					viewName = getErrorView(cctx, formBean);
					
					// intercept after
					doAfter(cctx, formBean);
		
					return viewName;
				}	
			}

			cctx.setModel(formBean);
			Locale locale = getLocaleForRequest(cctx, formBean);
			
			// populate form
			boolean populated = populateForm(cctx, formBean, locale);
			// go on?
			if(!populated && failOnPopulateError) // an error occured
			{
				// prepare for error command and execute it
				internalPerformError(cctx, formBean);
				
				viewName = getErrorView(cctx, formBean);
			}
			// else the form was populated succesfully or !failOnPopulateError
			else
			{

				if( formBean != null)
				{
					if( validateForm(cctx, formBean)) 
					{
						// passed validation, so execute 'normal' command
						
						if((formBean.getLastreq() != null) && (formBean.isRedirect())) 
						{
							this.perform(formBean, cctx);
							String lq = formBean.getLastreq();
							lq = UrlTool.replace(lq, "|amp|", "&"); 
							cctx.setModel(lq);
							
							viewName = REDIRECT;	
						} 
						else 
						{
		
							viewName = this.perform(formBean, cctx);
						}
					}
					else 
					{
						// did not pass validation, so prepare for error command 
						// and execute it
						internalPerformError(cctx, formBean);
		
						viewName = getErrorView(cctx, formBean);
					}
				}
				else
				{
					viewName = this.perform(formBean, cctx);
				}
			}
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			if(formBean != null) 
			{
				// save the exception so it can be displayed in the view
				if(e.getMessage() != null) 
				{
					formBean.setError(e, false);	
				} 
				else 
				{
					// as a fallback, save the stacktrace
					formBean.setError(e, true); 
				}
			}
			
			// prepare for error command and execute it
			internalPerformError(cctx, formBean);
			
			viewName = getErrorView(cctx, formBean);
		}
		
		// intercept after
		doAfter(cctx, formBean);
		
		return viewName;
	}
	
	/**
	 * get the prefered locale for this request
	 * @param cctx controller context
	 * @param form current form
	 * @return Locale the prefered locale
	 */
	protected Locale getLocaleForRequest(ControllerContext cctx, AbstractForm form)
	{
		Locale locale = cctx.getRequest().getLocale();
		UserPrincipal up = form.getUser();
		if(up != null)
		{
			if(up.getPreferedLocale() != null)
			{
				locale = up.getPreferedLocale();
			}
		}
		
		return locale;		
	}
	
	/**
	 * get error view. 'error' by default
	 * @param cctx controller context
	 * @param form current form
	 * @return String logical name of view
	 */
	protected String getErrorView(ControllerContext cctx, AbstractForm form)
	{
		return ERROR;
	}
	
	/**
	 * checks if a user is saved in the session and stores in formBean if found
	 * @param cctx Maverick context
	 * @param formBean bean to store user in
	 * @return boolean; true if a user was found, false otherwise
	 */
	protected boolean checkUser(ControllerContext cctx, AbstractForm formBean)
	{

		HttpSession session = cctx.getRequest().getSession();
		Subject subject = (Subject)session.getAttribute(
			AccessFilter.AUTHENTICATED_SUBJECT_KEY);
		
		if(subject == null)
		{
			return false;
		}
		
		UserPrincipal user = null;
		Set pset = subject.getPrincipals(UserPrincipal.class);	
		if(pset != null && (!pset.isEmpty()))
		{
			// just get first; usually there should be only one of
			// UserPrincipal type
			user = (UserPrincipal)pset.iterator().next();	
		}
		
		if (user != null)
		{
			formBean.setUser(user);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * default populate of form: BeanUtils way; set error if anything goes wrong
	 * @param cctx controller context
	 * @param formBean form current form
	 * @param properties map with name/ values
	 * @param locale the prefered locale
	 * @return true if populate did not have any troubles, false otherwise
	 */
	protected boolean populateWithErrorReport(
		ControllerContext cctx, 
		AbstractForm formBean, 
		Map properties,
		Locale locale)
	{
		
		// Do nothing unless both arguments have been specified
		if ((formBean == null) || (properties == null)) 
		{
			return true;
		}

		boolean succeeded = true;
		// Loop through the property name/value pairs to be set
		Iterator names = properties.keySet().iterator();
		while (names.hasNext()) 
		{
			boolean success = true;
			// Identify the property name and value(s) to be assigned
			String name = (String)names.next();
			if (name == null) continue;
			
			Object value = properties.get(name);
			String stringValue = null;
			
			if(value != null)
			{

				if(value instanceof String)
				{
					success = setSingleProperty(cctx, formBean, name, value);
				} 
				else if(value instanceof String[] && ((String[])value).length > 0)
				{
					Class type;
					PropertyDescriptor propertyDescriptor;
					try
					{
						propertyDescriptor =
							PropertyUtils.getPropertyDescriptor(formBean, name);
						type = propertyDescriptor.getPropertyType();
						
						if(( type != null) && type.isArray())
						{
							success = setArrayProperty(
								cctx, propertyDescriptor, 
								formBean, name, (String[])value);
		
						}
						else // the target property is not an array; let BeanUtils
							// handle the conversion
						{
							success = setSingleProperty(cctx, formBean, name, value);
						}
					}
					catch (Exception e)
					{
						log.error( "Property " + name + " not found"); 
					}
				}
				if(!success)
				{
					succeeded = false;
				}
			}
		}
		
		// do custom validation
		if(fieldValidators != null && (!fieldValidators.isEmpty()))
		{
			succeeded = doCustomValidation(cctx, formBean, properties, locale, succeeded);
		}
		
		return succeeded;
	}
	
	/* handle custom validation for all fields */
	private boolean doCustomValidation(
		ControllerContext cctx, 
		AbstractForm formBean, 
		Map properties,
		Locale locale,
		boolean succeeded)
	{

		boolean doCustomValidation = true;
		// see if there's any globally (form level) defined rules
		if(globalValidatorActivationRules != null && (!globalValidatorActivationRules.isEmpty()))
		{
			for(Iterator i = globalValidatorActivationRules.iterator(); i.hasNext(); )
			{
				ValidatorActivationRule rule = (ValidatorActivationRule)i.next();
				doCustomValidation = rule.allowValidation(cctx, formBean); // fire rule
				if(!doCustomValidation) break;
			}
		}
		
		if(doCustomValidation)
		{
			Iterator names = properties.keySet().iterator();
			while(names.hasNext())
			{
				String name = (String)names.next();
				if (name == null) continue;
				if(formBean.getError(name) == null) 
				{
					Collection propertyValidators = (Collection)fieldValidators.get(name);
					// these are the fieldValidators for one property
					if(propertyValidators != null)
					{
						try
						{
							succeeded = doCustomValidationForOneField(
								cctx, formBean, locale, succeeded, 
								name, propertyValidators);
						}
						catch (Exception e)
						{
							if(log.isDebugEnabled())
							{
								log.debug(e.getMessage());
							}
							// ignore
						}
					}	
				} // else an error allready occured; do not validate
			}
			// if we are still successful so far, check with the form level validators
			if(succeeded && (formValidators != null))
			{
				// check all registered until either all fired successfully or
				// one did not fire succesfully
				for(Iterator i = formValidators.iterator(); i.hasNext(); )
				{
					FormValidator fValidator = (FormValidator)i.next();
					if(!fValidator.isValid(cctx, formBean))
					{
						succeeded = false;
						String[] msg = fValidator.getErrorMessage(
							cctx, formBean, locale);
						formBean.setError(msg[0], msg[1]);
					}
				}
			}
		}
		return succeeded;
	}
	
	/* handle the custom validation for one field */
	private boolean doCustomValidationForOneField(
		ControllerContext cctx,
		AbstractForm formBean,
		Locale locale,
		boolean succeeded,
		String name,
		Collection propertyValidators)
		throws Exception
	{
		Object value = PropertyUtils.getProperty(formBean, name);
		for(Iterator j = propertyValidators.iterator(); j.hasNext(); )
		{
			FieldValidator validator = (FieldValidator)j.next();
			boolean validateField = true;
								
			if(validator instanceof ValidationRuleDependend)
			{
				ValidatorActivationRule rule = 
					((ValidationRuleDependend)validator)
						.getValidationActivationRule();
				if(rule != null)
				{
					validateField = rule.allowValidation(cctx, formBean);	
				}
			}
								
			if(validateField)
			{
				boolean success = validator.isValid(
					cctx, formBean, name, value);
				if(!success)
				{
					succeeded = false;
					try
					{
						String msg = validator.getErrorMessage(
							cctx, formBean, name, value, locale);
						formBean.setError(name, msg);
					}
					catch (Exception e)
					{
						if(log.isDebugEnabled())
						{
							e.printStackTrace();	
						}
						else
						{
							log.warn(e.getMessage());
						}
						formBean.setError(name, e.getMessage());
					}
					setOverrideField(cctx, formBean, name, value, null, validator);
					break;
				}	
			}
		}
		return succeeded;	
	}
	
	/**
	 * set an array property
	 * @param cctx controller context
	 * @param propertyDescriptor descriptor of property
	 * @param formBean current form
	 * @param name name of property
	 * @param values array of values to set
	 * @return true if successfull, false if not
	 */
	protected boolean setArrayProperty(
			ControllerContext cctx,
			PropertyDescriptor propertyDescriptor,
			AbstractForm formBean, 
			String name, 
			String[] values)
	{		
		boolean success = true;
		Class type = propertyDescriptor.getPropertyType();
		if(type.isArray())
		{
			Class componentType = type.getComponentType();
			Converter converter = ConvertUtils.lookup(componentType);
			Object array = Array.newInstance(componentType, values.length);
			
			int i = 0;
			for ( ; i < values.length; i++) 
			{
				try 
				{
					if(values[i] != null && (!values[i].trim().equals("")))
					{
						Object converted = converter.convert(
							componentType, values[i]);
						Array.set(array, i, converted);							
					}
					else
					{
						if(setNullForEmptyString)
						{
							Array.set(array, i, null);		
						}
						else
						{
							Array.set(array, i, values[i]);	
						}						
					}	
				} 
				catch (ConversionException e) 
				{
					setErrorForField(cctx, formBean, (name + '|' + i), values[i], e);
					setOverrideField(cctx, formBean, (name + '|' + i), values[i], e, null);
					success = false;
				}
			}
			
			try
			{
				PropertyUtils.setProperty(formBean, name, array);
			}
			catch (Exception e)
			{
				//this should not happen as we did extensive checking allready.
				// therefore print the stacktrace
				e.printStackTrace();
				setErrorForField(cctx, formBean, (name + '|' + i), values[i], e);
				success = false;
			}				
		}
		
		return success;
	}
	
	/**
	 * set a single property
	 * @param cctx controller context
	 * @param formBean form
	 * @param name name of property
	 * @param value value of property
	 * @return true if succeeded, false if not
	 */
	protected boolean setSingleProperty(
		ControllerContext cctx,	
		AbstractForm formBean, String name, Object value)
	{
		boolean success = true;
		String stringValue = null;
		try
		{
			// check as string first
			stringValue = ConvertUtils.convert(value);
			if(stringValue != null && (!stringValue.trim().equals("")))
			{
				Object o = PropertyUtils.getProperty(formBean, name);
				// Perform the assignment for this property
				BeanUtils.setProperty(formBean, name, value);
			}
			else
			{
				if(setNullForEmptyString)
				{
					BeanUtils.setProperty(formBean, name, null);	
				}
				else
				{
					BeanUtils.setProperty(formBean, name, stringValue);
				}
			}
		}
		catch (Exception e)
		{
			setErrorForField(cctx, formBean, name, stringValue, e);
			setOverrideField(cctx, formBean, name, stringValue, e, null);
			success = false;	
		}
		return success;
	}
	
	/**
	 * Called when populating the form failed.
	 * @param cctx
	 * @param formBean
	 */
	private void internalPerformError(
		ControllerContext cctx, 
		AbstractForm formBean)
	{
		if(formBean == null) return;
		// first, set overrides for the current request parameters
		formBean.setOverrideField(cctx.getRequest().getParameterMap());
		// set the current model
		cctx.setModel(formBean);
		// do further (possibly user specific) error handling and/ or
		// view preparing
		performError(cctx, formBean);	
	}
	
	/**
	 * prepare error model for view.
	 * This method will be called if the model failed to populate,
	 * or did not pass validation
	 * override this method to 'enrich' the error model
	 * @param cctx maverick context
	 * @param form form that failed to populate
	 */
	protected void performError(
		ControllerContext cctx, 
		AbstractForm form)
	{
		// nothing by default
	}
	
	/**
	 * Set error for field with name 'name'. As a default, this puts the
	 * localized message 'invalid.field.input' in the error map, and puts
	 * the value in the failed field map.
	 * 
	 * The message is formatted with objects triedValue, name and t, so you can
	 * use {0}, {1} and {2} resp. with your custom message.
	 * 
	 * If there is an entry in the default resource bundle that has form:
	 * 		formname.[name] (eg. formname.firstname and formname.lastname)
	 * 		the name parameter {1} will be replaced with this value
	 * 
	 * @param cctx controller context
	 * @param formBean form
	 * @param name name of field
	 * @param triedValue value that was tried for population
	 * @param t exception
	 */
	protected void setErrorForField(
		ControllerContext cctx, 
		AbstractForm formBean, 
		String name, 
		Object triedValue, 
		Throwable t) 
	{

		try
		{
			PropertyDescriptor descriptor = 
				PropertyUtils.getPropertyDescriptor(formBean, name);
			String key = "invalid.field.input"; 
			if (descriptor.getPropertyType().equals(Date.class))
			{
				key = "invalid.field.input.date";
			}
			else if(descriptor.getPropertyType().getName().equals("int") 
				|| descriptor.getPropertyType().getName().equals("java.lang.Integer"))
			{
				key = "invalid.field.input.integer";
			}
		
			String msg = null;
			String msgName = null;
			msgName = getLocalizedMessage("formname." + name);

			if(msgName != null)
			{
				msg = getLocalizedMessage(key, new Object[]{triedValue, msgName, t});	
			}
			else
			{
				msg = getLocalizedMessage(key, new Object[]{triedValue, name, t});
			}

			formBean.setError(name, msg);
		}		
		catch (Exception e)
		{
			log.error(e.getMessage());
		}
	}
	
	
	protected void setOverrideField(
		ControllerContext cctx, 
		AbstractForm formBean, 
		String name, 
		Object triedValue, 
		Throwable t,
		FieldValidator validator) 
	{
		if(validator != null)
		{
			Object value = validator.getOverrideValue(triedValue);
			formBean.setOverrideField(name, triedValue);
		}
		else
		{
			// if fail on populate, set the override field so that the input
			// value can be displayed
			if(failOnPopulateError)
			{
				formBean.setOverrideField(name, triedValue);	
			}	
		}
	}
	
	/**
	 * users can override this method to do custom populating. Call super first if you want the defaults to be set
	 * @param cctx controller context
	 * @param formBean bean to populate
	 * @param locale
	 * @throws Exception
	 * @return true if populate did not have any troubles, false otherwise
	 */
	protected boolean populateForm(
		ControllerContext cctx, 
		AbstractForm formBean, 
		Locale locale) 
		throws Exception 
	{
		// default behavoir
		boolean retval = true;
		retval = populateWithErrorReport(cctx, formBean, 
						cctx.getRequest().getParameterMap(), locale);
						
		if(retval == false) 
		{
			populateWithErrorReport(cctx, formBean, cctx.getControllerParams(), locale);
		} 
		else 
		{
			retval = populateWithErrorReport(
				cctx, formBean, cctx.getControllerParams(), locale);
		}
		return retval;
	}
	
	/**
	 * validate the form
	 * @param ctx current maverick context
	 * @param formBean form
	 * @return true if validation succeeded, false otherwise
	 * @deprecated use the classes from nl.openedge.maverick.framework.validation,
	 * 		in particular interface FieldValidator
	 */
	protected boolean validateForm(ControllerContext ctx, AbstractForm formBean) 
	{
		// do nothing by default
		return true;
	}
	
	/**
	 * This method can be overriden to perform application logic.
	 *
	 * Override this method if you want the model to be something
	 * other than the formBean itself.
	 *
	 * @param formBean will be a bean created by makeFormBean(),
	 * which has been populated with the http request parameters and
	 * possibly controller parameters.
	 */
	protected String perform(Object formBean, ControllerContext cctx) 
			throws Exception 
	{	
		return SUCCESS;
	}
												
	/**
	 * This method will be called to produce a simple bean whose properties
	 * will be populated with the http request parameters.  The parameters
	 * are useful for doing things like persisting beans across requests.
	 */
	protected abstract AbstractForm makeFormBean(ControllerContext cctx);
	
	/**
	 * @see ControllerSingleton@init
	 */
	public void init(Element controllerNode) throws ConfigException 
	{
		// initialise here...
	}

	/**
	 * should the command NOT be executed if the form population fails?
	 * @return if true, the command will not be executed
	 */
	protected boolean isFailOnPopulateError()
	{
		return failOnPopulateError;
	}

	/**
	 * should the command NOT be executed if the form population fails?
	 * @param failOnPopulateError if true, the command will not be executed
	 */
	protected void setFailOnPopulateError(boolean failOnPopulateError)
	{
		this.failOnPopulateError = failOnPopulateError;
	}

	/**
	 * @return boolean
	 */
	public boolean isNeedsValidUser()
	{
		return needsValidUser;
	}

	/**
	 * @param needsValidUser does the control need a valid user before
	 * execution is tried?
	 */
	public void setNeedsValidUser(boolean needsValidUser)
	{
		this.needsValidUser = needsValidUser;
	}
	
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
		catch (RuntimeException e)
		{
			if(log.isDebugEnabled())
			{
				log.warn(e.getMessage());
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
	public String getLocalizedMessage(
			String key, Object[] parameters)
	{	
		return getLocalizedMessage(key, null, parameters);
	}
	
	/**
	 * register a field validator for the given fieldName. 
	 * multiple fieldValidators for one key are allowed 
	 * @param fieldName name of field
	 * @param validator validator instance
	 */
	public void addValidator(String fieldName, FieldValidator validator)
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
	public void addValidator(FormValidator validator)
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
	public void removeValidators(String fieldName)
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
	public void removeValidator(String fieldName, FieldValidator validator)
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
	public void removeValidator(FormValidator validator)
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
	public void addGlobalValidatorActivationRule(ValidatorActivationRule rule)
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
	public void removeGlobalValidatorActivationRule(ValidatorActivationRule rule)
	{
		if(globalValidatorActivationRules != null)
		{
			globalValidatorActivationRules.remove(rule);
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
	
	/**
	 * get localized message for given key and locale
	 * and format it with the given parameters. 
	 * If locale is null, the default locale will be used
	 * @param key key of message
	 * @param locale locale for message
	 * @param parameters parameters for the message
	 * @return String localized message
	 */
	public String getLocalizedMessage(
			String key, Locale locale, Object[] parameters)
	{
		ResourceBundle res = getBundle(locale);
		String msg = res.getString(key);
		MessageFormat fmt = new MessageFormat(msg);
		String formattedMessage = 
			MessageFormat.format(msg, parameters);
		return formattedMessage;
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
	
	/**
	 * Returns <tt>text</tt> performing the following substring
	 * replacements:
	 *
	 *    & -> &amp;
	 *    < -> &lt;
	 *    > -> &gt;
	 *    " -> &#034;
	 *    ' -> &#039;
	 * @param text string or string[] to transform
	 * @return String String[] transformed string
	 */
	protected Object escapeCharacters(Object text)
	{
		if (text == null)
			return null;
		
		if(text instanceof String)
		{
			StringBuffer w = new StringBuffer();
			escapeCharacters(w, (String)text);
			return w.toString();		
		}
		else if(text instanceof String[])
		{
			String[] strings = (String[])text;
			StringBuffer w = new StringBuffer();
			int length = strings.length;
			if(length < 1)
			{
				return text;
			}
			for(int i = 0; i < length; i++)
			{
				w.delete(0, w.length());
				escapeCharacters(w, strings[i]);
				strings[i] = w.toString();	
			}
			return strings;				
		}
		else
		{
			return null;
		}
	}
	
	private void escapeCharacters(StringBuffer w, String text)
	{
		for (int i = 0; i < text.length(); i++)
		{
			char c = text.charAt(i);
			if (c == '&')
				w.append("&amp;");
			else if (c == '<')
				w.append("&lt;");
			else if (c == '>')
				w.append("&gt;");
			else if (c == '"')
				w.append("&#034;");
			else if (c == '\'')
				w.append("&#039;");
			else
				w.append(c);
		}	
	}
	
	/*
	 * set no cache headers
	 */
	private void setNoCache(ControllerContext cctx)
	{
		HttpServletResponse response = cctx.getResponse();
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 1);
	}

	/**
	 * @return boolean
	 */
	public boolean isNoCache()
	{
		return noCache;
	}

	/**
	 * @param noCache
	 */
	public void setNoCache(boolean noCache)
	{
		this.noCache = noCache;
	}

	/**
	 * @return boolean
	 */
	public boolean isSetNullForEmptyString()
	{
		return setNullForEmptyString;
	}

	/**
	 * @param b
	 */
	public void setSetNullForEmptyString(boolean b)
	{
		setNullForEmptyString = b;
	}

}
