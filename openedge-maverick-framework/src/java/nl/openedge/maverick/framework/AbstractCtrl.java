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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.security.auth.Subject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
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
	
	/** special performance log */
	private static Log performanceLog = 
		LogFactory.getLog(LogConstants.PERFORMANCE_LOG);
	
	/** if true, the no cache headers will be set */
	private boolean noCache = true;
	
	/** 
	 * sub classes can override this value. If it is true and a valid user
	 * was not found in the session, an error will be returned to the client
	 */
	private boolean needsValidUser = false;
	
	/**
	 * If we get an empty string, should it be translated to a null (true) or should
	 * the empty String be kept (false). This property is true by default
	 */
	private boolean setNullForEmptyString = true;
	
	/**
	 * Indicates whether we should use the request attributes for the population process
	 * as well. This property is false by default.
	 * This can be very handy when linking action together, as usually, the request paramters
	 * are read only. 
	 * NOTE: request attributes OVERRIDE request parameters
	 */	
	private boolean includeRequestAttributes = false;
	
	/**
	 * subclasses can register fieldValidators for custom validation on field level
	 */
	protected MultiMap fieldValidators = null;
	
	/**
	 * subclasses can register converters for custom conversion from string (request parameter)
	 * to other types
	 * registering a converter for a field will override the default (BeanUtils) conversion
	 * 
	 */
	protected Map fieldConverters = null;
	
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
	private List globalValidatorActivationRules = null;
	
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
		long tsBegin = System.currentTimeMillis();
		
		String viewName = SUCCESS;
		if(isNoCache())
		{
			setNoCache(cctx);
		}

		AbstractForm formBean = null;
		try 
		{	
			// let controller create form
			long tsBeginMakeFormBean = System.currentTimeMillis();

			formBean = this.makeFormBean(cctx);
			
			if(performanceLog.isDebugEnabled())
			{
				long tsEndMakeFormBean = System.currentTimeMillis();
				performanceLog.debug("execution of " + this + ".makeFormBean: " +
					(tsEndMakeFormBean - tsBeginMakeFormBean) + " milis");
			}
			
			long tsBeginBefore = System.currentTimeMillis();
			
			// intercept before
			doBefore(cctx, formBean);
			
			if(performanceLog.isDebugEnabled())
			{
				long tsEndBefore = System.currentTimeMillis();
				performanceLog.debug("execution of " + this + ".doBefore: " +
					(tsEndBefore - tsBeginBefore) + " milis");
			}

			if(isNeedsValidUser())
			{
				// see if the user was saved in the session, and if so, store in form
				boolean foundUser = checkUser(cctx, formBean);
				cctx.setModel(formBean);

				// can we go on?
				if ((!foundUser) && isNeedsValidUser())
				{
					// nope, we can't
					formBean.setError("global.message", 
						"user was not found in session");

					viewName = getErrorView(cctx, formBean);
					
					long tsBeginAfter = System.currentTimeMillis();

					// intercept after
					doAfter(cctx, formBean);

					if(performanceLog.isDebugEnabled())
					{
						long tsEndAfter = System.currentTimeMillis();
						performanceLog.debug("execution of " + this + ".doAfter: " +
							(tsEndAfter - tsBeginAfter) + " milis");
					}
		
					return viewName;
				}	
			}

			cctx.setModel(formBean);
			Locale locale = getLocaleForRequest(cctx, formBean);
			
			// populate form
			boolean populated = populateForm(cctx, formBean, locale);
			// go on?
			if(!populated) // an error occured
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
							long tsBeginPerform = System.currentTimeMillis();
							
							this.perform(formBean, cctx);
							
							if(performanceLog.isDebugEnabled())
							{
								long tsEndPerform = System.currentTimeMillis();
								performanceLog.debug("execution of " + this + ".perform: " +
									(tsEndPerform - tsBeginPerform) + " milis");
							}
							
							String lq = formBean.getLastreq();
							lq = UrlTool.replace(lq, "|amp|", "&"); 
							cctx.setModel(lq);
							
							viewName = REDIRECT;	
						} 
						else 
						{
							long tsBeginPerform = System.currentTimeMillis();
							
							viewName = this.perform(formBean, cctx);
							
							if(performanceLog.isDebugEnabled())
							{
								long tsEndPerform = System.currentTimeMillis();
								performanceLog.debug("execution of " + this + ".perform: " +
									(tsEndPerform - tsBeginPerform) + " milis");
							}		
							
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
					long tsBeginPerform = System.currentTimeMillis();
							
					viewName = this.perform(formBean, cctx);
							
					if(performanceLog.isDebugEnabled())
					{
						long tsEndPerform = System.currentTimeMillis();
						performanceLog.debug("execution of " + this + ".perform: " +
							(tsEndPerform - tsBeginPerform) + " milis");
					}	
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
		
		long tsBeginAfter = System.currentTimeMillis();

		// intercept after
		doAfter(cctx, formBean);

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
	
	/*
	 * default populate of form: BeanUtils way; set error if anything goes wrong
	 * @param cctx controller context
	 * @param formBean form current form
	 * @param properties map with name/ values
	 * @param locale the prefered locale
	 * @return true if populate did not have any troubles, false otherwise
	 */
	private boolean populateWithErrorReport(
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
		while(names.hasNext()) 
		{
			boolean success = true;
			// Identify the property name and value(s) to be assigned
			String name = (String)names.next();
			if (name == null) continue;
			
			Object value = properties.get(name);
			
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
						if(log.isDebugEnabled())
						{
							log.debug(name + 
								" is a request parameter, but is not a property of form " + 
								formBean);	
						} 
					}
				}
				if(!success)
				{
					succeeded = false;
				}
			}
		}
		
		// do custom validation
		if( (fieldValidators != null && (!fieldValidators.isEmpty())) ||
			(formValidators != null && (!formValidators.isEmpty())))
		{
			succeeded = doCustomValidation(cctx, formBean, properties, locale, succeeded);
		}
		
		return succeeded;
	}
	
	/*
	 * set an array property
	 * @param cctx controller context
	 * @param propertyDescriptor descriptor of property
	 * @param formBean current form
	 * @param name name of property
	 * @param values array of values to set
	 * @return true if successfull, false if not
	 */
	private boolean setArrayProperty(
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
						if(isSetNullForEmptyString())
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
					setConversionErrorForField(cctx, formBean, (name + '|' + i), values[i], e);
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
				setConversionErrorForField(cctx, formBean, (name + '|' + i), values[i], e);
				success = false;
			}				
		}
		
		return success;
	}
	
	/*
	 * set a single property
	 * @param cctx controller context
	 * @param formBean form
	 * @param name name of property
	 * @param value value of property
	 * @return true if succeeded, false if not
	 */
	private boolean setSingleProperty(
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
				// Perform the assignment for this property
				BeanUtils.setProperty(formBean, name, value);
			}
			else
			{
				if(isSetNullForEmptyString())
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
			setConversionErrorForField(cctx, formBean, name, stringValue, e);
			setOverrideField(cctx, formBean, name, stringValue, e, null);
			success = false;	
		}
		return success;
	}
	
	/* handle custom validation for all fields */
	private boolean doCustomValidation(
		ControllerContext cctx, 
		AbstractForm formBean, 
		Map properties,
		Locale locale,
		boolean succeeded)
	{
		
		long tsBegin = System.currentTimeMillis();

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
			// if fieldValidators were registered
			if(fieldValidators != null && (!fieldValidators.isEmpty()))
			{
				Iterator names = properties.keySet().iterator(); // loop through the properties
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
			}
			// if we are still successful so far, check with the form level validators
			if(succeeded && (formValidators != null))
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
							if(!fRule.allowValidation(cctx, formBean))
							{
								fireValidator = false;
							}	
						}
					}
					if(fireValidator)
					{
						if(!fValidator.isValid(cctx, formBean))
						{
							succeeded = false;
							String[] msg = fValidator.getErrorMessage(
								cctx, formBean, locale);
							formBean.setError(msg[0], msg[1]);
						}	
					}
					// else ignore
				}
			}
		}
		
		if(performanceLog.isDebugEnabled())
		{
			long tsEnd = System.currentTimeMillis();
			performanceLog.debug("execution of " + this + ".doCustomValidation: " +
				(tsEnd - tsBegin) + " milis");
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
						String msgName = getLocalizedMessage(getPropertyNameKey(name));
						String msg = validator.getErrorMessage(
							cctx, formBean, (msgName != null) ? msgName : name, value, locale);
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
							log.warn(e.getMessage(), e);
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
	 * Called when populating the form failed.
	 * @param cctx
	 * @param formBean
	 */
	private void internalPerformError(
		ControllerContext cctx, 
		AbstractForm formBean)
	{
		if(formBean == null) return;
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
	 * @param formBean form
	 * @param name name of field
	 * @param triedValue value that was tried for population
	 * @param t exception
	 */
	protected void setConversionErrorForField(
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
			String key = getConversionErrorLabelKey(
				descriptor.getPropertyType(), name, triedValue);
		
			String msg = null;
			String msgName = null;
			msgName = getLocalizedMessage(getPropertyNameKey(name));

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
			formBean.setError(name, e.getMessage());
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
	 * @param formBean form
	 * @param name name of the field
	 * @param triedValue the user input value/ request parameter
	 * @param t exception if known (may be null)
	 * @param validator the validator that was the cause of the validation failure, if one
	 * 	(is null if this was a conversion error)
	 */
	private void setOverrideField(
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
			formBean.setOverrideField(name, value);
		}
		else
		{
			formBean.setOverrideField(name, triedValue);
		}
	}
	
	/*
	 * users can override this method to do custom populating. Call super first if you want the defaults to be set
	 * @param cctx controller context
	 * @param formBean bean to populate
	 * @param locale
	 * @throws Exception
	 * @return true if populate did not have any troubles, false otherwise
	 */
	private boolean populateForm(
		ControllerContext cctx, 
		AbstractForm formBean, 
		Locale locale) 
		throws Exception 
	{
		long tsBegin = System.currentTimeMillis();
		// default behavoir
		boolean retval = true;
		Map parameters = new HashMap();
		parameters.putAll(cctx.getRequest().getParameterMap());
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
		retval = populateWithErrorReport(cctx, formBean, parameters, locale);
						
		if(retval == false) 
		{
			populateWithErrorReport(cctx, formBean, cctx.getControllerParams(), locale);
		} 
		else 
		{
			retval = populateWithErrorReport(
				cctx, formBean, cctx.getControllerParams(), locale);
		}

		if(performanceLog.isDebugEnabled())
		{
			long tsEnd = System.currentTimeMillis();
			performanceLog.debug("execution of " + this + ".populateForm: " +
				(tsEnd - tsBegin) + " milis");
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
	 * sub classes can override this value. If it is true and a valid user
	 * was not found in the session, an error will be returned to the client
	 * @return boolean
	 */
	protected boolean isNeedsValidUser()
	{
		return needsValidUser;
	}

	/**
	 * sub classes can override this value. If it is true and a valid user
	 * was not found in the session, an error will be returned to the client
	 * @param needsValidUser does the control need a valid user before
	 * execution is tried?
	 */
	protected void setNeedsValidUser(boolean needsValidUser)
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
	protected boolean isSetNullForEmptyString()
	{
		return setNullForEmptyString;
	}

	/**
	 * If we get an empty string it should be translated to a null (true) or to
	 * an empty String false. This property is true by default
	 * @param b
	 */
	protected void setSetNullForEmptyString(boolean b)
	{
		setNullForEmptyString = b;
	}

	/**
	 * Indicates whether we should use the request attributes for the population process
	 * as well. This property is false by default.
	 * This can be very handy when linking action together, as usually, the request paramters
	 * are read only. 
	 * NOTE: request attributes OVERRIDE request parameters
	 * @return boolean
	 */
	protected boolean isIncludeRequestAttributes()
	{
		return includeRequestAttributes;
	}

	/**
	 * Indicates whether we should use the request attributes for the population process
	 * as well. This property is false by default.
	 * This can be very handy when linking action together, as usually, the request paramters
	 * are read only. 
	 * NOTE: request attributes OVERRIDE request parameters
	 * @param b
	 */
	protected void setIncludeRequestAttributes(boolean b)
	{
		includeRequestAttributes = b;
	}

}
