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

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import nl.openedge.maverick.framework.converters.Formatter;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AbstractForm is a base class to be used with AbstractCtrl.
 * Using this form, you (or better AbstractCtrl) can save errors,
 * save override fields and save the current user.
 * 
 * Override fields are filled with the string values from the HttpServletRequest
 * if conversion the the target type (e.g. an integer) in the form failed.
 * For example: if you have a form with property 'myinteger' and send the currentRequest
 * parameter 'myinteger=foo', 'foo' will be saved as an override field with key
 * 'myinteger'. On top of this, an error will be registered (stored in map errors)
 * for this conversion failure, default with key 'myinteger'.
 * 
 * @author Eelco Hillenius
 */
public abstract class AbstractForm
{

	/** last currentRequest */
	private String lastreq;

	/** should we redirect? */
	private boolean redirect = false;

	/** validated user principal */
	private Principal user = null;

	/** the current locale */
	private Locale currentLocale = null;

	/** errors */
	private Map errors = null;

	/** overriden values as strings */
	private Map overrideFields = null;

	/** log for this class */
	private static Log log = LogFactory.getLog(AbstractCtrl.class);

	/** error key for stacktrace if any */
	public final static String ERROR_KEY_STACKTRACE = "stacktrace";

	/** error key for stacktrace if any */
	public final static String ERROR_KEY_EXCEPTION = "exception";

	/**
	 * construct empty
	 */
	public AbstractForm()
	{
		// do nothing	
	}

	//	----------------------- PROPERTY METHODS -----------------------------//

	/**
	 * @return String
	 */
	public String getLastreq()
	{
		return lastreq;
	}

	/**
	 * Sets the lastreq.
	 * @param lastreq The lastreq to set
	 */
	public void setLastreq(String lastreq)
	{
		this.lastreq = lastreq;
	}

	/**
	 * @return boolean
	 */
	public boolean isRedirect()
	{
		return redirect;
	}

	/**
	 * Sets the redirect.
	 * @param redirect The redirect to set
	 */
	public void setRedirect(boolean redirectHint)
	{
		this.redirect = redirectHint;
	}

	/**
	 * get user
	 * @return UserPrincipal
	 */
	public Principal getUser()
	{
		return user;
	}

	/**
	 * set user
	 * @param user 
	 */
	public void setUser(Principal user)
	{
		this.user = user;
	}

	/**
	 * get the current locale
	 * @return Locale current locale
	 */
	public Locale getCurrentLocale()
	{
		return currentLocale;
	}

	/**
	 * set the current locale
	 * @param locale current locale
	 */
	public void setCurrentLocale(Locale locale)
	{
		currentLocale = locale;
	}

	//	----------------------- ERROR/ OVERRIDE METHODS -----------------------------//

	/**
	 * @return Map
	 */
	public Map getErrors()
	{
		return errors;
	}

	/**
	 * get error for field
	 * @param field
	 * @return String
	 */
	public String getError(String field)
	{
		return (errors != null) ? (String)errors.get(field) : null;
	}

	/**
	 * Sets the errors.
	 * @param errors The errors to set
	 */
	public void setErrors(Map errors)
	{
		this.errors = errors;
	}

	/**
	 * either add this exception with the given key, or add the stacktrace
	 * of this exception with the given key
	 * @param key key to store error under
	 * @param t exception
	 * @param asStackTrace if true, the stacktrace is added; otherwise the exception
	 *  is added
	 */
	public void setError(String key, Throwable t, boolean asStackTrace)
	{
		String value = null;
		if (asStackTrace)
		{
			value = getErrorMessage(t);
		}
		else
		{
			value = t.getMessage();
		}
		setError(key, value);
	}

	/**
	 * add exception and its stacktrace
	 * @param exceptionKey key to use for exception
	 * @param stackTraceKey key to use for stacktrace
	 * @param t exception
	 */
	public void setError(
		String exceptionKey,
		String stackTraceKey,
		Throwable t)
	{
		String stackTrace = getErrorMessage(t);
		String errorMessage = t.getMessage();
		setError(stackTraceKey, stackTrace);
		setError(exceptionKey, errorMessage);
	}

	/**
	 * adds an exception with key 'exception' and adds the stacktrace
	 * of this exception with key 'stacktrace'
	 * @param t exception
	 */
	public void setError(Throwable t)
	{
		setError(ERROR_KEY_EXCEPTION, ERROR_KEY_STACKTRACE, t);
	}

	/**
	 * adds an exception with key 'exception' and adds either the stacktrace
	 * of this exception with key 'stacktrace' if asStackTrace is true, or add the
	 * exception message with key 'exception' if asStackTrace is false
	 * @param t exception
	 * @param asStackTrace if true, the stacktrace is added; otherwise the exception
	 */
	public void setError(Throwable t, boolean asStackTrace)
	{
		setError(ERROR_KEY_EXCEPTION, t, asStackTrace);
	}

	/** add or overwrite an error */
	public void setError(String key, String value)
	{
		if (errors == null)
			errors = new HashMap();
		errors.put(key, value);
	}

	/** add or overwrite an error */
	public void removeError(String key)
	{
		if (errors != null)
		{
			errors.remove(key);
		}
	}

	/*
	 * get errormessage; try stacktrace
	 */
	private String getErrorMessage(Throwable t)
	{
		String msg;
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			PrintWriter pw = new PrintWriter(bos);
			t.printStackTrace(pw);
			pw.flush();
			pw.close();
			bos.flush();
			bos.close();
			msg = bos.toString();
		}
		catch (Exception e)
		{
			msg = t.getMessage();
		}
		return msg;
	}

	/**
	 * get map of failed field values
	 * @return Map
	 */
	public Map getOverrideFields()
	{
		return overrideFields;
	}

	/**
	 * set value of field that overrides. WILL overwrite allready registered override
	 * @param name name of the field/ property
	 * @param value the string value (from HTML field)
	 */
	public void setOverrideField(String name, Object value)
	{
		if (overrideFields == null)
		{
			overrideFields = new HashMap();
		}
		overrideFields.put(name, value);
	}

	/**
	 * get string value of field that overrides
	 * E.g. we got in a formbean property 'myDate' of type date. 
	 * If form submit gives 'blah', this cannot be parsed as a date.
	 * Now, if setFailedField('myDate', 'blah') is called, 
	 * the view can show the 'wrong' value transparently, as an 
	 * Velocity EventCardridge will override any property with a 
	 * failed field if set.
	 * @param name
	 * @return String
	 */
	public String getOverrideField(String name)
	{
		return (overrideFields != null)
			? (String)overrideFields.get(name)
			: null;
	}

	/**
	 * set values of fields that overrides. WILL NOT overwrite allready registered overrides
	 * @param name name of the field/ property
	 * @param value the string value (from HTML field)
	 */
	public void setOverrideField(Map fields)
	{
		if (fields != null)
		{
			if (overrideFields == null)
			{
				overrideFields = new HashMap();
				overrideFields.putAll(fields);
			}
			else
			{
				for (Iterator i = fields.keySet().iterator(); i.hasNext();)
				{
					String key = (String)i.next();
					if (!overrideFields.containsKey(key))
					{
						overrideFields.put(key, fields.get(key));
					}
				}
			}
		}
	}

	// ----------------------- DISPLAY/ OUTPUT METHODS ---------------------//

	/**
	* get the display string of the property with the given name without using a pattern
	* 
	* If an object was found for the given property name, it will be formatted with the 
	* formatter found as follows:
	* 	1. look in the ConverterRegistry if a formatter was stored with the fieldname
	* 			and optionally locale as key.
	* 	2. if not found, look in the ConverterRegistry if a Converter was stored for the 
	* 			type of the property that implements Formatter (as well as Converter). 
	* If a formatter was found, it will be used for formatting the property 
	* (using the format(property, pattern) method). If not, ConvertUtils of the 
	* BeanUtils package is used to get the string representation of the property.
	* 
	* Override this in your form if you want to customize
	* 
	* @param name name of the property
	* @return String the display string
	*/
	public String displayProperty(String name)
	{
		return displayProperty(name, null);
	}

	/**
	* Get the display string of the property with the given name, optionally 
	* using the given pattern.
	* 
	* If an object was found for the given property name, it will be formatted with the 
	* formatter found as follows:
	* 	1. look in the ConverterRegistry if a formatter was stored with the fieldname
	* 			and optionally locale as key.
	* 	2. if not found, look in the ConverterRegistry if a formatter was stored with
	* 			the pattern and optionally the locale as key.
	* 	3. if not found, look in the ConverterRegistry if a Converter was stored for the 
	* 			type of the property that implements Formatter (as well as Converter). 
	* If a formatter was found, it will be used for formatting the property 
	* (using the format(property, pattern) method). If not, ConvertUtils of the 
	* BeanUtils package is used to get the string representation of the property.
	* 
	* Override this in your form if you want to customize
	* 
	* @param name name of the property
	* @pattern optional pattern to use for formatting
	* @return String
	*/
	public String displayProperty(String name, String pattern)
	{
		if (name == null)
			return null;

		String displayString = null;
		
		// first, check if there is a registration in the override fields
		Map _overrideFields = getOverrideFields();
		boolean wasOverriden = false;
		if (_overrideFields != null)
		{
			Object storedRawValue = _overrideFields.get(name);
			String storedValue = null;
			// first, try default
			if (storedRawValue != null) // an entry is found
			{
				wasOverriden = true; // set flag
				if (storedRawValue instanceof String) // no conversion
				{
					storedValue = (String)storedRawValue;
				}
				else // this is probably a String[]; convert to a simple String
				{
					storedValue = ConvertUtils.convert(storedRawValue);
				}

				if (storedValue != null)
				{
					displayString = storedValue;
				}
			}
		}
		
		if(!wasOverriden) // no override value found?
		{
			try
			{
				Object _value = PropertyUtils.getProperty(this, name); // get the property value
				if (_value != null)
				{
					// format string
					displayString = format(name, _value, pattern);
				}
			}
			catch (Exception e)
			{
				if(log.isDebugEnabled())
				{
					log.error(e);
					e.printStackTrace();	
				}
				return null;
			}	
		}

		return displayString;
	}

	/**
	 * get the formatter for the given fieldname/ class/ locale
	 * 
	 * 	1. look in the ConverterRegistry if a formatter was stored with the fieldname
	 * 			and optionally locale as key.
	 * 	2. if not found, look in the ConverterRegistry if a formatter was stored with
	 * 			the pattern and optionally the locale as key.
	 * 	3. if not found, look in the ConverterRegistry if a Converter was stored for the 
	 * 			type of the property that implements Formatter (as well as Converter). 
	 * 
	 * @param fieldname name of field
	 * @param pattern pattern: might be used as a key to store a Formatter
	 * @param clazz class of property
	 * @param locale locale to get Formatter for
	 * @return Formatter instance of Formatter if found, null otherwise
	 */
	public Formatter getFormatter(String fieldname, String pattern, Class clazz, Locale locale)
	{
		Formatter formatter = null;
		ConverterRegistry reg = ConverterRegistry.getInstance();

		try
		{
			// first look up on fieldname
			if(fieldname != null)
			{
				formatter = reg.lookup(fieldname);
			}
			
			if(formatter == null && (pattern != null)) // not found, try pattern
			{
				formatter = reg.lookup(pattern);
			}

			if (formatter == null && (clazz != null)) // not found, try converter
			{
				Converter converter = reg.lookup(clazz, getCurrentLocale());
				if ((converter != null) && (converter instanceof Formatter))
				{
					formatter = (Formatter)converter;
				} // else will return null
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			// ignore
		}

		return formatter;
	}

	// ----------------------- UTILITY METHODS -----------------------------//

	/**
	 * check if the value is null or empty
	 * @param value object to check on
	 * @return true if value is not null AND not empty (e.g. 
	 * in case of a String or Collection)
	 */
	public boolean isNullOrEmpty(Object value)
	{
		if (value instanceof String)
		{
			return (value == null || (((String)value).trim().equals("")));
		}
		else if (value instanceof Collection)
		{
			return (value == null || (((Collection)value).isEmpty()));
		}
		else
		{
			return (value == null);
		}
	}
	
	/**
	 * Format the given value, independent of the current form.
	 * @param value value to format
	 * @return String formatted value
	 */
	public String format(Object value)
	{
		return format(null, value, null);
	}
	
	/**
	 * Format the given value, independent of the current form.
	 * @param value value to format
	 * @param patttern pattern for format
	 * @return String formatted value
	 */
	public String format(Object value, String pattern)
	{
		return format(null, value, pattern);
	}
	
	/**
	 * Format the given value, independent of the current form.
	 * @param fieldname fieldname that can be used to get a formatter. This will not be used
	 * 		to get the property value.
	 * @param value value to format
	 * @return String formatted value
	 */
	public String format(String fieldname, Object value)
	{
		return format(fieldname, value, null);
	}
	
	/**
	 * Format the given value, independent of the current form.
	 * @param fieldname fieldname that can be used to get a formatter. This will not be used
	 * 		to get the property value.
	 * @param value value to format
	 * @param pattern pattern for format
	 * @return String formatted value
	 */
	public String format(String fieldname, Object value, String pattern)
	{
		if(value == null) return null;
		
		String formatted = null;
		Formatter formatter =
			getFormatter(fieldname, pattern, value.getClass(), getCurrentLocale());
		
		if (formatter != null)
		{
			formatted = formatter.format(value, null);
		}
		else
		{
			formatted = ConvertUtils.convert(value);
		}
		return formatted;
	}

}
