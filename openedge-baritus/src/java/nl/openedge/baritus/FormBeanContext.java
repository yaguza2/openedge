/*
 * $Id: FormBeanContext.java,v 1.1.1.1 2004-02-24 20:33:53 eelco12 Exp $
 * $Revision: 1.1.1.1 $
 * $Date: 2004-02-24 20:33:53 $
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

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import nl.openedge.baritus.converters.Formatter;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * FormBeanContext wraps the form bean, errors the current locale and overrideFields.
 * Furthermore, it acts as a decorator for a HashMap where you can optionally store 
 * attributes you do not want to include as properties in your form bean. 
 *  
 * @author Eelco Hillenius
 */
public final class FormBeanContext
{

	/** the current locale */
	private Locale currentLocale = null;
	
	/** 
	 * The form bean. This is the bean that will be populated, 
	 * and that is returned by makeFormbean 
	 */
	private Object bean = null;

	/** errors */
	private Map errors = null;

	/** overriden values as strings */
	private Map overrideFields = null;
	
	/** 
	 * if, for some reason, you want to store extra attributes in this context
	 * instead in the formBean, you can do this here (e.g. messages)
	 */
	private Map attributes = null;

	/** log for this class */
	private static Log log = LogFactory.getLog(FormBeanCtrl.class);
	
	/** format log */
	private static Log formattingLog = LogFactory.getLog(LogConstants.FORMATTING_LOG);

	/** error key for stacktrace if any */
	public final static String ERROR_KEY_STACKTRACE = "stacktrace";

	/** error key for stacktrace if any */
	public final static String ERROR_KEY_EXCEPTION = "exception";

	/**
	 * construct empty
	 */
	public FormBeanContext()
	{
		// do nothing	
	}

	//	----------------------- PROPERTY METHODS -----------------------------//

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
	
	/**
	 * Get the form bean. This is the bean that will be populated, 
	 * and that is returned by make formbean
	 * 
	 * @return Object the bean that will be populated, and that is returned by makeFormbean
	 */
	public Object getBean()
	{
		return bean;
	}

	/** 
	 * Set the form bean. This is the bean that will be populated, 
	 * and that is returned by make formbean
	 * 
	 * @param bean the bean that will be populated, and that is returned by makeFormbean
	 */
	public void setBean(Object bean)
	{
		this.bean = bean;
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
		if (errors == null) errors = new HashMap();
		
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
	
	/**
	 * are there any errors registered for this formBean?
	 * @return boolean are there any errors registered for this formBean? True if so, false otherwise.
	 */
	public boolean hasErrors()
	{
		return (errors != null && (!errors.isEmpty()));
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
				else if(storedRawValue instanceof String[]) 
					// this is a  String[]; convert to a simple String
				{
					storedValue = ConvertUtils.convert(storedRawValue);
				}
				else // another type, try to format 
				{
					try
					{
						storedValue = format(name, storedRawValue, pattern);
					}
					catch(Exception e)
					{
						storedValue = ConvertUtils.convert(storedRawValue);
					}
				}
				if (storedValue != null)
				{
					displayString = storedValue;
				}
			}
			
			if(formattingLog.isDebugEnabled())
			{
				formattingLog.debug(
					"using override for property " + name + ": " + displayString);
			}
		}
		
		if(!wasOverriden) // no override value found?
		{
			try
			{
				Object _value = PropertyUtils.getProperty(bean, name); // get the property value
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
					log.error(e.getMessage(), e);	
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
			log.error(e);
			if(log.isDebugEnabled())
			{
				log.error(e.getMessage(), e);
			}
			// ignore
		}

		return formatter;
	}

	// ----------------------- UTILITY METHODS -----------------------------//
	
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
			if(formattingLog.isDebugEnabled())
			{
				formattingLog.debug(
					"using formatter " + formatter +
					" for property " + fieldname);
			}
			formatted = formatter.format(value, pattern);
		}
		else
		{
			if(formattingLog.isDebugEnabled())
			{
				formattingLog.debug(
					"using default convertUtils formatter for property " + fieldname);
			}
			formatted = ConvertUtils.convert(value);
		}
		return formatted;
	}
	
	// ------------- ATTRIBUTES, the decorator methods for a HashMap -------------//

	/**
	 * Returns the value to which the attributes map maps the specified key. 
	 * Returns null if the map contains no mapping for this key.
	 * @param key key whose associated value is to be returned
	 * @return Object the value to which the attributes map maps the specified key, 
	 * or null if the map contains no mapping for this key.
	 * @see java.util.HashMap#get(java.lang.Object)
	 */
	public Object get(Object key)
	{
		return (attributes != null) ? attributes.get(key) : null;
	}
	
	/** 
	 * Associates the specified value with the specified key in the attribute map. 
	 * If the attribute map previously contained a mapping for this key, the old value 
	 * is replaced by the specified value.
	 * @param key key with which the specified value is to be associated.
	 * @param value value to be associated with the specified key.
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	public void put(Object key, Object value)
	{
		if(attributes == null) attributes = new HashMap();
		attributes.put(key, value);
	}

	/**
	 * get the attribute values
	 * @return Collection the attribute values or null if no attributes were set
	 * @see java.util.HashMap#values(Object)
	 */
	public Collection values()
	{
		return (attributes != null) ? attributes.values() : null;
	}

	/**
	 * get the key set of the attributes
	 * @return Set the key set of the attributes or null if no attributes were set
	 * @see java.util.HashMap#keySet()
	 */
	public Set keySet()
	{
		return (attributes != null) ? attributes.keySet() : null;
	}

	/**
	 * clear the attributes
	 * @see java.util.HashMap#clear()
	 */
	public void clear()
	{
		this.attributes = null;
	}

	/**
	 * get the number of attributes
	 * @return int the number of attributes
	 * @see java.util.HashMap#size()
	 */
	public int size()
	{
		return (attributes != null) ? attributes.size() : 0;
	}

	/**
	 * put the provided map in the attribute map 
	 * @param t map to put in attributes
	 * @see java.util.HashMap#putAll(java.util.Map)
	 */
	public void putAll(Map t)
	{
		if(attributes == null) attributes = new HashMap();
		attributes.putAll(t);
	}

	/**
	 * get the entries of the attributes
	 * @return Set the entries of the attributes or null if no attributes were set
	 * @see java.util.HashMap#entrySet()
	 */
	public Set entrySet()
	{
		return (attributes != null) ? attributes.entrySet() : null;
	}

	/**
	 * is the provided key the key of an attribute
	 * @param key the key to look for
	 * @return boolean is the provided key the key of an attribute
	 * @see java.util.HashMap#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key)
	{
		return (attributes != null) ? attributes.containsKey(key) : false;
	}

	/**
	 * are there any attributes
	 * @return boolean are there any attributes
	 * @see java.util.HashMap#isEmpty()
	 */
	public boolean isEmpty()
	{
		return (attributes != null) ? attributes.isEmpty() : true;
	}

	/**
	 * remove an attribute
	 * @param key key of the attribute to remove
	 * @return Object the object that was stored under the given key, if any
	 * @see java.util.HashMap#remove(java.lang.Object)
	 */
	public Object remove(Object key)
	{
		return (attributes != null) ? attributes.remove(key) : null;
	}

	/**
	 * is the provided value stored as an attribute
	 * @param value the value to look for
	 * @return boolean is the provided value stored as an attribute
	 * @see java.util.HashMap#(java.lang.Object value)
	 */
	public boolean containsValue(Object value)
	{
		return (attributes != null) ? attributes.containsValue(value) : false;
	}

}
