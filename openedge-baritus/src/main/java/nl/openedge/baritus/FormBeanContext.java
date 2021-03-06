package nl.openedge.baritus;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import nl.openedge.baritus.converters.Converter;
import nl.openedge.baritus.converters.Formatter;
import nl.openedge.baritus.util.ValueUtils;
import ognl.Ognl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FormBeanContext wraps the form bean, errors the current locale and overrideFields.
 * Furthermore, it acts as a decorator for a HashMap where you can optionally store
 * attributes you do not want to include as properties in your form bean.
 * 
 * @author Eelco Hillenius
 * @author Maurice Marrink
 */
public final class FormBeanContext implements Map<String, Object>
{

	/* the current locale */
	private Locale currentLocale = null;

	/*
	 * The form bean. This is the bean that will be populated, and that is returned by
	 * makeFormbean
	 */
	private Object bean = null;

	/* errors */
	private Map<String, String> errors = null;

	/* overriden values as strings */
	private Map<String, Object> overrideFields = new HashMap<String, Object>();

	/* the current controller */
	private FormBeanCtrlBase controller = null;

	/*
	 * if, for some reason, you want to store extra attributes in this context instead in
	 * the formBean, you can do this here (e.g. messages)
	 */
	private Map<String, Object> attributes = null;

	/* log for this class */
	private static Logger log = LoggerFactory.getLogger(FormBeanCtrlBase.class);

	/* format log */
	private static Logger formattingLog = LoggerFactory.getLogger(LogConstants.FORMATTING_LOG);

	/** error key for stacktrace if any */
	public final static String ERROR_KEY_STACKTRACE = "stacktrace";

	/** error key for stacktrace if any */
	public final static String ERROR_KEY_EXCEPTION = "exception";

	// ----------------------- PROPERTY METHODS -----------------------------//

	/**
	 * Get the current locale.
	 * 
	 * @return Locale current locale
	 */
	public Locale getCurrentLocale()
	{
		return currentLocale;
	}

	/**
	 * Set the current locale.
	 * 
	 * @param locale
	 *            current locale
	 */
	public void setCurrentLocale(Locale locale)
	{
		currentLocale = locale;
	}

	/**
	 * Get the form bean. The current control provided a bean to populate with method
	 * makeFormBean. That bean is saved (before population) in the formBeanContext.
	 * 
	 * @return Object the bean that will be populated, and that is returned by
	 *         makeFormbean
	 */
	public Object getBean()
	{
		return bean;
	}

	/**
	 * Set the form bean. This is the bean that will be populated, and that is returned by
	 * make formbean.
	 * 
	 * @param bean
	 *            the bean that will be populated, and that is returned by makeFormbean
	 */
	public void setBean(Object bean)
	{
		this.bean = bean;
	}

	// ----------------------- ERROR/ OVERRIDE METHODS -----------------------------//

	/**
	 * Get the map with errors. Returns null if no errors are registered.
	 * 
	 * @return Map map with errors or null if no errors are registered.
	 */
	public Map<String, String> getErrors()
	{
		return errors;
	}

	/**
	 * Get the registered error for one field. Returns null if no error is registered for
	 * the given field name.
	 * 
	 * @param field
	 *            name of the field to lookup the error for.
	 * @return String the error that was registered for the provided field name, or null
	 *         if no error was registered for the provided field name.
	 */
	public String getError(String field)
	{
		return (errors != null) ? errors.get(field) : null;
	}

	/**
	 * Set the map of errors.
	 * 
	 * @param errors
	 *            The map of errors to set
	 */
	public void setErrors(Map<String, String> errors)
	{
		this.errors = errors;
	}

	/**
	 * Either add this exception with the given key, or add the stacktrace of this
	 * exception with the given key. Any value that was registered with the same key prior
	 * to this is overwritten.
	 * 
	 * @param key
	 *            key to store error under
	 * @param t
	 *            exception
	 * @param asStackTrace
	 *            if true, the stacktrace is added; otherwise the exception is added
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
	 * Add exception and its stacktrace. Any value that was registered with the same key
	 * prior to this is overwritten.
	 * 
	 * @param exceptionKey
	 *            key to use for exception
	 * @param stackTraceKey
	 *            key to use for stacktrace
	 * @param t
	 *            exception
	 */
	public void setError(String exceptionKey, String stackTraceKey, Throwable t)
	{
		String stackTrace = getErrorMessage(t);
		String errorMessage = t.getMessage();
		setError(stackTraceKey, stackTrace);
		setError(exceptionKey, errorMessage);
	}

	/**
	 * Adds an exception with key 'exception' and adds the stacktrace of this exception
	 * with key 'stacktrace'. Any value that was registered with the same keys prior to
	 * this are overwritten.
	 * 
	 * @param t
	 *            exception
	 */
	public void setError(Throwable t)
	{
		setError(ERROR_KEY_EXCEPTION, ERROR_KEY_STACKTRACE, t);
	}

	/**
	 * Adds an exception with key 'exception' and adds either the stacktrace of this
	 * exception with key 'stacktrace' if asStackTrace is true, or add the exception
	 * message with key 'exception' if asStackTrace is false. Any value that was
	 * registered with the same keys prior to this are overwritten.
	 * 
	 * @param t
	 *            exception
	 * @param asStackTrace
	 *            if true, the stacktrace is added; otherwise the exception
	 */
	public void setError(Throwable t, boolean asStackTrace)
	{
		setError(ERROR_KEY_EXCEPTION, t, asStackTrace);
	}

	/**
	 * Register (or overwrite) an error with the provided key and value.
	 * 
	 * @param key
	 *            the key that the error should be registered with.
	 * @param value
	 *            the value (message) of the error.
	 */
	public void setError(String key, String value)
	{
		if (errors == null)
			errors = new HashMap<String, String>();

		errors.put(key, value);
	}

	/**
	 * De-register (remove) an error that was registered with the provided key.
	 * 
	 * @param key
	 *            the key that the error was registered with.
	 */
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
			pw.print("<pre>");
			t.printStackTrace(pw);
			pw.print("</pre>");
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
	 * Get the map of failed field values.
	 * 
	 * @return Map map with override fields
	 */
	public Map<String, Object> getOverrideFields()
	{
		return overrideFields;
	}

	/**
	 * Set value of field that overrides. WILL overwrite any allready registered override
	 * 
	 * @param name
	 *            name of the field/ property
	 * @param value
	 *            usually the original input value
	 */
	public void setOverrideField(String name, Object value)
	{
		overrideFields.put(name, value);
	}

	/**
	 * Get the value of the field that was overridden. E.g. we got in a formbean property
	 * 'myDate' of type date. If form submit gives 'blah', this cannot be parsed as a
	 * date. Now, if setFailedField('myDate', 'blah') is called, the view can show the
	 * 'wrong' value transparently, as an Velocity EventCardridge will override any
	 * property with a failed field if set.
	 * 
	 * @param name
	 *            name of the field/ property
	 * @return Object usually the original input value
	 */
	public Object getOverrideField(String name)
	{
		return overrideFields.get(name);
	}

	/**
	 * Set values of fields that have overrides. This WILL NOT overwrite allready
	 * registered overrides.
	 * 
	 * @param fields
	 *            map of fields to override the current values.
	 */
	public void setOverrideFields(Map<String, String[]> fields)
	{
		if (fields != null)
		{
			for (Map.Entry<String, String[]> entry : fields.entrySet())
			{
				String key = entry.getKey();
				if (!overrideFields.containsKey(key))
				{
					overrideFields.put(key, entry.getValue());
				}
			}
		}
	}

	/**
	 * Whether any errors were registered during population/ validation.
	 * 
	 * @return boolean are there any errors registered for this formBean? True if so,
	 *         false otherwise.
	 */
	public boolean hasErrors()
	{
		return (errors != null && (!errors.isEmpty()));
	}

	// ----------------------- DISPLAY/ OUTPUT METHODS ---------------------//

	/**
	 * Get the display string of the property with the given name without using a pattern.
	 * If an object was found for the given property name, it will be formatted with the
	 * formatter found as follows: 1. look in the ConverterRegistry if a formatter was
	 * stored with the fieldname and optionally locale as key. 2. if not found, look in
	 * the ConverterRegistry if a Converter was stored for the type of the property that
	 * implements Formatter (as well as Converter). If a formatter was found, it will be
	 * used for formatting the property (using the format(property, pattern) method). If
	 * not, ConvertUtils of the BeanUtils package is used to get the string representation
	 * of the property.
	 * 
	 * @param name
	 *            name of the property
	 * @return String the formatted value of the property of the current bean instance or
	 *         the registered override value if any was registered.
	 */
	public String displayProperty(String name)
	{
		return displayProperty(name, null);
	}

	/**
	 * Get the display string of the property with the given name, optionally using the
	 * given pattern. If an object was found for the given property name, it will be
	 * formatted with the formatter found as follows: 1. look in the ConverterRegistry if
	 * a formatter was stored with the fieldname and optionally locale as key. 2. if not
	 * found, look in the ConverterRegistry if a formatter was stored with the pattern and
	 * optionally the locale as key. 3. if not found, look in the ConverterRegistry if a
	 * Converter was stored for the type of the property that implements Formatter (as
	 * well as Converter). If a formatter was found, it will be used for formatting the
	 * property (using the format(property, pattern) method). If not, ConvertUtils of the
	 * BeanUtils package is used to get the string representation of the property.
	 * 
	 * @param name
	 *            name of the property
	 * @param pattern
	 *            optional pattern to use for formatting
	 * @return String the formatted value (using the pattern) of the property of the
	 *         current bean instance or the registered override value if any was
	 *         registered.
	 */
	public String displayProperty(String name, String pattern)
	{
		if (name == null)
			return null;

		String displayString = null;

		// first, check if there is a registration in the override fields
		Map<String, Object> _overrideFields = getOverrideFields();
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
					storedValue = (String) storedRawValue;
				}
				else if (storedRawValue instanceof String[])
				// this is a String[]; convert to a simple String
				{
					storedValue = ValueUtils.convertToString(storedRawValue);
				}
				else
				// another type, try to format
				{
					try
					{
						storedValue = format(name, storedRawValue, pattern);
					}
					catch (Exception e)
					{
						storedValue = ValueUtils.convertToString(storedRawValue);
					}
				}
				if (storedValue != null)
				{
					displayString = storedValue;
				}
			}

			if (formattingLog.isDebugEnabled())
			{
				formattingLog.debug("using override for property " + name + ": " + displayString);
			}
		}

		if (!wasOverriden) // no override value found?
		{
			try
			{
				Object _value = Ognl.getValue(name, bean); // get the property value
				if (_value != null)
				{
					// format string
					displayString = format(name, _value, pattern);
				}
			}
			catch (Exception e)
			{
				if (log.isDebugEnabled())
				{
					log.error(e.getMessage(), e);
				}
				return null;
			}
		}

		if (displayString != null && displayString.endsWith("\\"))
			displayString = displayString + " ";

		return displayString;
	}

	/**
	 * Get the formatter for the given fieldname/ class/ locale. 1. look in the
	 * ConverterRegistry if a formatter was stored with the formatterName and optionally
	 * locale as key. 2. if not found, look in the ConverterRegistry if a formatter was
	 * stored with the pattern and optionally the locale as key. 3. if not found, look in
	 * the ConverterRegistry if a Converter was stored for the type of the property that
	 * implements Formatter (as well as Converter).
	 * 
	 * @param formatterName
	 *            name of formatter
	 * @param pattern
	 *            pattern: might be used as a key to store a Formatter
	 * @param clazz
	 *            class of property
	 * @param locale
	 *            locale to get Formatter for
	 * @return Formatter instance of Formatter if found, null otherwise
	 */
	public Formatter getFormatter(String formatterName, String pattern,
			Class< ? extends Object> clazz, Locale locale)
	{
		Formatter formatter = null;
		ConverterRegistry reg = ConverterRegistry.getInstance();

		try
		{
			// first look up on fieldname
			if (formatterName != null)
			{
				formatter = reg.lookup(formatterName);
			}

			if (formatter == null && (pattern != null)) // not found, try pattern
			{
				formatter = reg.lookup(pattern);
			}

			if (formatter == null && (clazz != null)) // not found, try converter
			{
				Converter converter = reg.lookup(clazz, getCurrentLocale());
				if ((converter != null) && (converter instanceof Formatter))
				{
					formatter = (Formatter) converter;
				} // else will return null
			}
		}
		catch (Exception e)
		{
			if (log.isDebugEnabled())
				log.error(e.getMessage(), e);
			else
				log.error(e.getMessage());
		}

		return formatter;
	}

	// ----------------------- UTILITY METHODS -----------------------------//

	/**
	 * Format the given value, independent of the current form, using the class of the
	 * value to lookup a formatter.
	 * 
	 * @param value
	 *            value to format
	 * @return String formatted value
	 */
	public String format(Object value)
	{
		return format(null, value, null);
	}

	/**
	 * Format the given value, independent of the current form, using the class of the
	 * value to lookup a formatter using the provided pattern.
	 * 
	 * @param value
	 *            value to format
	 * @param pattern
	 *            pattern for format
	 * @return String formatted value
	 */
	public String format(Object value, String pattern)
	{
		return format(null, value, pattern);
	}

	/**
	 * Format the given value, independent of the current form, using the provided name of
	 * the formatter to lookup the formatter or - if no formatter was found - using the
	 * class of the value to lookup the formatter.
	 * 
	 * @param formatterName
	 *            name of formatter.
	 * @param value
	 *            value to format
	 * @return String formatted value
	 */
	public String format(String formatterName, Object value)
	{
		return format(formatterName, value, null);
	}

	/**
	 * Format the given value, independent of the current form using: 1. look in the
	 * ConverterRegistry if a formatter was stored with the formatterName and optionally
	 * locale as key; 2. if not found, look in the ConverterRegistry if a formatter was
	 * stored with the pattern and optionally the locale as key; 3. if not found, look in
	 * the ConverterRegistry if a Converter was stored for the type of the property that
	 * implements Formatter (as well as Converter);
	 * 
	 * @param formatterName
	 *            name of formatter.
	 * @param value
	 *            value to format
	 * @param pattern
	 *            pattern for format
	 * @return String formatted value
	 */
	public String format(String formatterName, Object value, String pattern)
	{
		if (value == null)
			return null;

		String formatted = null;
		Formatter formatter =
			getFormatter(formatterName, pattern, value.getClass(), getCurrentLocale());

		if (formatter != null)
		{
			if (formattingLog.isDebugEnabled())
			{
				formattingLog.debug("using formatter " + formatter + " for property "
					+ formatterName);
			}
			formatted = formatter.format(value, pattern);
		}
		else
		{
			if (formattingLog.isDebugEnabled())
			{
				formattingLog.debug("using default convertUtils formatter for property "
					+ formatterName);
			}
			formatted = ValueUtils.convertToString(value);
		}
		return formatted;
	}

	// ------------- ATTRIBUTES, the decorator methods for a HashMap -------------//

	/**
	 * Returns the value to which the attributes map maps the specified key. Returns null
	 * if the map contains no mapping for this key.
	 * 
	 * @param key
	 *            key whose associated value is to be returned
	 * @return Object the value to which the attributes map maps the specified key, or
	 *         null if the map contains no mapping for this key.
	 * @see java.util.Map#get(java.lang.Object)
	 */
	@Override
	public Object get(Object key)
	{
		return (attributes != null) ? attributes.get(key) : null;
	}

	/**
	 * Associates the specified value with the specified key in the attribute map. If the
	 * attribute map previously contained a mapping for this key, the old value is
	 * replaced by the specified value.
	 * 
	 * @param key
	 *            key with which the specified value is to be associated.
	 * @param value
	 *            value to be associated with the specified key.
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Object put(String key, Object value)
	{
		if (attributes == null)
			attributes = new HashMap<String, Object>();
		return attributes.put(key, value);
	}

	/**
	 * get the attribute values
	 * 
	 * @return Collection the attribute values or null if no attributes were set
	 * @see java.util.Map#values()
	 */
	@Override
	public Collection<Object> values()
	{
		return (attributes != null) ? attributes.values() : null;
	}

	/**
	 * get the key set of the attributes
	 * 
	 * @return Set the key set of the attributes or null if no attributes were set
	 * @see java.util.Map#keySet()
	 */
	@Override
	public Set<String> keySet()
	{
		return (attributes != null) ? attributes.keySet() : null;
	}

	/**
	 * clear the attributes
	 * 
	 * @see java.util.Map#clear()
	 */
	@Override
	public void clear()
	{
		this.attributes = null;
	}

	/**
	 * get the number of attributes
	 * 
	 * @return int the number of attributes
	 * @see java.util.Map#size()
	 */
	@Override
	public int size()
	{
		return (attributes != null) ? attributes.size() : 0;
	}

	/**
	 * put the provided map in the attribute map
	 * 
	 * @param t
	 *            map to put in attributes
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	@Override
	public void putAll(Map< ? extends String, ? extends Object> t)
	{
		if (attributes == null)
			attributes = new HashMap<String, Object>();
		attributes.putAll(t);
	}

	/**
	 * get the entries of the attributes
	 * 
	 * @return Set the entries of the attributes or null if no attributes were set
	 * @see java.util.Map#entrySet()
	 */
	@Override
	public Set<Entry<String, Object>> entrySet()
	{
		return (attributes != null) ? attributes.entrySet() : null;
	}

	/**
	 * is the provided key the key of an attribute
	 * 
	 * @param key
	 *            the key to look for
	 * @return boolean is the provided key the key of an attribute
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(Object key)
	{
		return (attributes != null) ? attributes.containsKey(key) : false;
	}

	/**
	 * are there any attributes
	 * 
	 * @return boolean are there any attributes
	 * @see java.util.Map#isEmpty()
	 */
	@Override
	public boolean isEmpty()
	{
		return (attributes != null) ? attributes.isEmpty() : true;
	}

	/**
	 * remove an attribute
	 * 
	 * @param key
	 *            key of the attribute to remove
	 * @return Object the object that was stored under the given key, if any
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	@Override
	public Object remove(Object key)
	{
		return (attributes != null) ? attributes.remove(key) : null;
	}

	/**
	 * is the provided value stored as an attribute
	 * 
	 * @param value
	 *            the value to look for
	 * @return boolean is the provided value stored as an attribute
	 * @see java.util.Map#containsValue(java.lang.Object value)
	 */
	@Override
	public boolean containsValue(Object value)
	{
		return (attributes != null) ? attributes.containsValue(value) : false;
	}

	/**
	 * get current controller
	 * 
	 * @return FormBeanCtrlBase
	 */
	public FormBeanCtrlBase getController()
	{
		return controller;
	}

	/**
	 * set current controller
	 * 
	 * @param controller
	 *            current controller
	 */
	public void setController(FormBeanCtrlBase controller)
	{
		this.controller = controller;
	}

	/**
	 * String rep.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuffer b = new StringBuffer(super.toString());
		b.append(" { bean=").append(bean).append(", ctrl=").append(controller);
		if (attributes != null && (!attributes.isEmpty()))
		{
			b.append(", attributes{");
			for (Iterator<String> i = attributes.keySet().iterator(); i.hasNext();)
			{
				String key = i.next();
				b.append(key).append("=").append(attributes.get(key));
				if (i.hasNext())
				{
					b.append(",");
				}
			}
			b.append("}");
		}
		b.append(", errors{");
		if (errors != null)
		{
			for (Iterator<String> i = errors.keySet().iterator(); i.hasNext();)
			{
				String errorKey = i.next();
				b.append(errorKey).append("=").append(getError(errorKey)).append("(override=")
					.append(getOverrideField(errorKey)).append(")");
				if (i.hasNext())
				{
					b.append(",");
				}
			}
		}
		b.append("}}");
		return b.toString();
	}

}
