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
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletException;


import nl.openedge.maverick.framework.util.UrlTool;

import org.jdom.Element;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import org.infohazard.maverick.flow.ConfigException;
import org.infohazard.maverick.flow.ControllerContext;
import org.infohazard.maverick.flow.ControllerSingleton;

/**
 * baseclass for controlls
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

	/** session key for user */
	public static final String SESSION_KEY_USER = "_theUser";
	
	
	private static Logger log = Logger.getLogger(AbstractCtrl.class);
	
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
	 * Executes this controller.  Override one of the other perform()
	 * methods to provide application logic.
	 *
	 * @see ControllerSingleton#perform
	 */
	public final String go(ControllerContext cctx) throws ServletException 
	{
		
		AbstractForm formBean = null;
		try 
		{	
			// let controller create form
			formBean = this.makeFormBean(cctx);

			cctx.setModel(formBean);
			
			// populate form
			boolean populated = populateForm(cctx, formBean);
			// go on?
			if(!populated && failOnPopulateError) // an error occured
			{
				// prepare for error command and execute it
				internalPerformError(cctx, formBean);	
				
				return ERROR;
			}
			// else the form was populated succesfully

			if(validateForm(cctx, formBean)) 
			{
				// passed validation, so execute 'normal' command
				
				if((formBean.getLastreq() != null) && (formBean.isRedirect())) 
				{
					this.perform(formBean, cctx);
					String lq = formBean.getLastreq();
					lq = UrlTool.replace(lq, "|amp|", "&"); 
					cctx.setModel(lq);
					return REDIRECT;	
				} 
				else 
				{
					return this.perform(formBean, cctx);
				}
			} 
			else 
			{
				// did not pass validation, so prepare for error command 
				// and execute it
				internalPerformError(cctx, formBean);
				return ERROR;
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
			
			return ERROR;
		}
	}
	
	/**
	 * default populate of form: BeanUtils way; set error if anything goes wrong
	 * @param cctx controller context
	 * @param formBean form
	 * @param properties
	 * @return true if populate did not have any troubles, false otherwise
	 */
	protected boolean populateWithErrorReport(ControllerContext cctx, 
								AbstractForm formBean, Map properties)
		throws NoSuchMethodException, 
		InvocationTargetException,
		IllegalAccessException 
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
			if (name == null) 
			{
				continue;
			}
			Object value = properties.get(name);
			String stringValue = null;
			
			if(value != null)
			{

				if(value instanceof String)
				{
					success = setSingleProperty(cctx, formBean, name, value);
				} 
				else if(value instanceof String[])
				{
					Class type;
					PropertyDescriptor propertyDescriptor;
					try
					{
						propertyDescriptor =
							PropertyUtils.getPropertyDescriptor(formBean, name);
						type = propertyDescriptor.getPropertyType();
						
						if(type.isArray())
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
						break; 
						// Property does not exist in target object; ignore 
					}
				}
				if(!success)
				{
					succeeded = false;
				}
			}
		}
		return succeeded;
	}
	
	/**
	 * set an array property
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
					Object converted = converter.convert(
						componentType, (String)values[i]);

					Array.set(array, i, converted);		
				} 
				catch (ConversionException e) 
				{
					setErrorForField(cctx, formBean, (name + '|' + i), values[i], e);
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
				Object o = PropertyUtils.getProperty(formBean, name);
				// Perform the assignment for this property
				BeanUtils.setProperty(formBean, name, value);
			}
		}
		catch (Exception e)
		{
			setErrorForField(cctx, formBean, name, stringValue, e);
			success = false;	
		}
		return success;
	}
	
	/**
	 * Called when populating the form failed.
	 * @param cctx
	 * @param formBean
	 */
	private void internalPerformError(ControllerContext cctx, 
											AbstractForm formBean)
	{
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
	protected void performError(ControllerContext cctx, 
									AbstractForm form)
	{
		// nothing by default
	}
	
	/**
	 * Set error for field with name 'name'. As a default, this puts the
	 * localized message 'invalid.field.input' in the error map, and puts
	 * the value in the failed field map.
	 * 
	 * The message is formatted with objects name, triedValue and t, so you can
	 * use {0}, {1} and {2} resp. with your custom message
	 * 
	 * @param cctx controller context
	 * @param formBean form
	 * @param name name of field
	 * @param triedValue value that was tried for population
	 * @param t exception
	 */
	protected void setErrorForField(ControllerContext cctx, AbstractForm formBean, 
									String name, Object triedValue, Throwable t) 
	{

		String msg = getLocalizedMessage(
			"invalid.field.input", new Object[]{name, triedValue, t});

		formBean.setError(name, msg);
		
		// if fail on populate, set the override field so that the input
		// value can be displayed
		if(failOnPopulateError)
		{
			formBean.setOverrideField(name, triedValue);	
		}
	}
	
	/**
	 * users can override this method to do custom populating. Call super first if you want the defaults to be set
	 * @param cctx controller context
	 * @param formBean bean to populate
	 * @throws Exception
	 * @return true if populate did not have any troubles, false otherwise
	 */
	protected boolean populateForm(ControllerContext cctx, AbstractForm formBean) 
				throws Exception 
	{

		// default behavoir
		boolean retval = true;
		retval = populateWithErrorReport(cctx, formBean, 
						cctx.getRequest().getParameterMap());
						
		if(retval == false) 
		{
			populateWithErrorReport(cctx, formBean, cctx.getControllerParams());
		} 
		else 
		{
			retval = populateWithErrorReport(
				cctx, formBean, cctx.getControllerParams());
		}
		return retval;
	}
	
	/**
	 * validate the form
	 * @param ctx current maverick context
	 * @param formBean form
	 * @return true if validation succeeded, false otherwise
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
		return getBundle(locale).getString(key);
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

}
