/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003
 * All rights reserved.
 */
package nl.openedge.maverick.framework;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import nl.openedge.maverick.framework.converters.BaseLocaleConverter;
import nl.openedge.maverick.framework.converters.Formatter;
import nl.openedge.maverick.framework.converters.LocaleFormatter;
import nl.openedge.maverick.framework.converters.NoopConverter;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.locale.LocaleConverter;
import org.apache.commons.collections.FastHashMap;

/**
 * This serves as the alternative for ConvertUtils. We use this instead of ConvertUtils
 * for the following reasons:
 * <ul>
 * 	<li>to avoid conflicts with other uses of BeanUtils</li>
 * 	<li>to have a simple, flat registry with mixed usage of locales</li>
 * 	<li>to keep control of the registry within this framework</li>
 * 	<li>to have an extra option for the registration of 'just' formatters</li>
 * </ul>
 * @author Eelco Hillenius
 */
public final class ConverterRegistry
{

	/**
	 * The set of {@link Converter}s that can be used to convert Strings
	 * into objects of a specified Class, keyed by the destination Class.
	 */
	private FastHashMap converters = new FastHashMap();

	/**
	 * The set of {@link Converter}s that can be used to convert Strings
	 * into objects of a specified Class, keyed by the destination Class and locale.
	 */
	private FastHashMap localizedConverters = new FastHashMap();

	/**
	 * singleton instance
	 */
	private static ConverterRegistry _instance = null;
	
	/**
	 * converter for final fallthrough
	 */
	private static NoopConverter noopConverter = new NoopConverter();

	/*
	 * hidden constructor
	 */
	private ConverterRegistry()
	{
		converters.setFast(true);
		localizedConverters.setFast(true);
	}

	/**
	 * access to singleton
	 * @return ConverterRegistry singleton instance
	 */
	public static ConverterRegistry getInstance()
	{
		if (_instance == null)
		{
			_instance = new ConverterRegistry();
		}
		return _instance;
	}

	/**
	 * Register a custom {@link Converter} for the specified destination
	 * <code>Class</code>, replacing any previously registered Converter.
	 *
	 * @param converter Converter to be registered
	 * @param clazz Destination class for conversions performed by this Converter
	 */
	public void register(Converter converter, Class clazz)
	{
		converters.put(clazz, converter);
	}
	
	/**
	 * Register a custom {@link LocaleConverter} for the specified destination
	 * <code>Class</code>, replacing any previously registered Converter.
	 *
	 * @param converter LocaleConverter to be registered
	 * @param clazz Destination class for conversions performed by this Converter
	 */
	public void register(LocaleConverter converter, Class clazz)
	{
		localizedConverters.put(clazz, converter);
	}
	
	/**
	 * Register a custom {@link LocaleConverter} for the specified destination
	 * <code>Class</code>, replacing any previously registered Converter.
	 *
	 * @param converter LocaleConverter to be registered
	 * @param clazz Destination class for conversions performed by this Converter
	 * @param locale Locale class   
	 */
	public void register(LocaleConverter converter, Class clazz, Locale locale)
	{
		String lockey = getLocKey(clazz, locale);
		localizedConverters.put(lockey, converter);
	}
	
	/**
	 * register a global formatter with the given key
	 * @param formatter the formatter
	 * @param key the key to register the instance of Formatter with
	 */
	public void register(Formatter formatter, String key)
	{
		key = getLocKey(key);
		converters.put(key, formatter);	
	}

	/**
	 * register a global locale aware formatter with the given key and locale
	 * @param formatter the formatter
	 * @param key the key to register the instance of Formatter with
	 * @param locale the locale
	 */
	public void register(LocaleFormatter formatter, String key, Locale locale)
	{
		key = getLocKey(key, locale);
		localizedConverters.put(key, formatter);	
	}
	
	/**
	 * register a global locale aware formatter with the given key
	 * @param formatter the formatter
	 * @param key the key to register the instance of Formatter with
	 */
	public void register(LocaleFormatter formatter, String key)
	{
		key = getLocKey(key);
		localizedConverters.put(key, formatter);	
	}
	

	/**
	 * Remove any registered {@link Converter} for the specified destination
	 * <code>Class</code> and <code>Locale</code>.
	 *
	 * @param clazz Class for which to remove a registered Converter
	 */
	public void deregister(Class clazz, Locale locale)
	{
		String lockey = getLocKey(clazz, locale);
		localizedConverters.remove(lockey);
	}
	
	/**
	 * Remove any registered {@link Converter} for the specified destination
	 * <code>Class</code>.
	 *
	 * @param clazz Class for which to remove a registered Converter
	 */
	public void deregister(Class clazz)
	{
		converters.remove(clazz);
	}
	
	/**
	 * Remove any registered {@link Formatter} for the specified key
	 *
	 * @param key key for which to remove a registered Formatter
	 */
	public void deregister(String key)
	{
		key = getLocKey(key);
		converters.remove(key);
	}
	
	/**
	 * deregister a global formatter with the given key and locale
	 * @param key the key of the formatter
	 * @param locale the locale
	 */
	public void deregister(String key, Locale locale)
	{
		key = getLocKey(key, locale);
		localizedConverters.remove(key);	
	}
	
	/**
	 * lookup a globally registered formatter
	 * @param key key of formatter
	 * @return Formatter instance of Formatter that was registered with the
	 * 		specified key or null if not found
	 */
	public Formatter lookup(String key)
	{
		key = getLocKey(key);
		return (Formatter)converters.get(key);
	}

	/**
	 * Look up and return any registered {@link Converter} for the specified
	 * destination class and locale; if there is no registered Converter, return
	 * <code>null</code>.
	 * 
	 * Precedence: if a locale is given the first search is for a converter that was
	 * 		registered for the given type and locale. If it is not found, the second
	 * 		search is for any converter of the type LocaleConverter that was registered
	 * 		for the given type. If it is found, a new instance will be created for the
	 * 		given locale, the pattern will be copied if possible and the newly
	 * 		instantiated converter will be registered for the given type and locale
	 * 		(and thus will be found at the first search next time). If it is not found,
	 * 		the search is the same as when no locale was given (locale == null):
	 * 		the 'normal', not localized registry will be searched for an entry with
	 * 		the given type. If this is not found either, a lookup with ConvertUtils
	 * 		from Jakarta Commons BeanUtils will be done as a fallthrough. If still
	 * 		no Converter is found after this, an instance of NoopConverter is returned,
	 * 		so that clients allways get a valid converter.
	 * 
	 * @param clazz Class for which to return a registered Converter
	 * @param locale The Locale
	 */
	public Converter lookup(Class clazz, Locale locale) 
		throws NoSuchMethodException, 
		IllegalArgumentException, 
		InstantiationException, 
		IllegalAccessException, 
		InvocationTargetException
	{
		Converter converter = null;
		if(locale != null)
		{
			String lockey = getLocKey(clazz, locale);
		
			// first try registration for specific locale
			converter = (LocaleConverter)localizedConverters.get(lockey);
			if(converter == null) // not found, try generic localized registration
			{
				LocaleConverter _converter = (LocaleConverter)localizedConverters.get(clazz);
				// if found, instantiate a localized one and store for next use
				if(_converter != null)
				{
					Class cls = _converter.getClass();
					Class[] paramTypes = new Class[]{ Locale.class };
					Constructor constructor = cls.getConstructor(paramTypes);
					Object[] initArgs = new Object[]{ locale };
					// create new instance for this locale
					LocaleConverter _newConverter = 
						(LocaleConverter)constructor.newInstance(initArgs); 
					
					// try to copy the pattern
					if((_converter instanceof BaseLocaleConverter) &&
						(_newConverter instanceof BaseLocaleConverter) )
					{
						String pattern = ((BaseLocaleConverter)_converter).getPattern();
						((BaseLocaleConverter)_newConverter).setPattern(pattern);
					}
					// else: too bad, but it's probably not a problem

					// register the new instance for this locale
					localizedConverters.put(lockey, _newConverter);
					converter = _newConverter;
				}
			}			
		}
		// else // get without locale right away
		if(converter == null) // (still) not found, try generic non-localized registration
		{
			converter = (Converter)converters.get(clazz);
		}
		if(converter == null) 
			// still not found, finally try generic non-localized registration with ConvertUtils
		{
			converter = ConvertUtils.lookup(clazz);
		}
		
		if(converter == null) // STILL not found; return no-op 
		{
			converter = noopConverter;
		}
		
		return converter;
	}
	
	/**
	 * Look up and return any registered {@link Formatter} for the specified
	 * destination key and locale; if there is no registered Formatter, return
	 * <code>null</code>.
	 * 
	 * Precedence: if a locale is given the first search is for a formatter that was
	 * 		registered for the given type and locale. If it is not found, the second
	 * 		search is for any formatter of the type LocaleFormatter that was registered
	 * 		for the given key. If it is found, a new instance will be created for the
	 * 		given locale and the newly instantiated formatter will be registered for 
	 * 		the given key and locale
	 * 		(and thus will be found at the first search next time). If it is not found,
	 * 		the search is the same as when no locale was given (locale == null):
	 * 		the 'normal', not localized registry will be searched for an entry with
	 * 		the given key. If this is not found either, null will be returned.
	 * 
	 * @param clazz Class for which to return a registered Converter
	 * @param locale The Locale
	 */
	public Formatter lookup(String key, Locale locale) 
		throws NoSuchMethodException, 
		IllegalArgumentException, 
		InstantiationException, 
		IllegalAccessException, 
		InvocationTargetException
	{
		Formatter formatter = null;
		if(locale != null)
		{
			String lockey = getLocKey(key, locale);
		
			// first try registration for specific locale
			formatter = (LocaleFormatter)localizedConverters.get(lockey);
			if(formatter == null) // not found, try generic localized registration
			{
				String globLocKey = getLocKey(key);
				LocaleFormatter _formatter = (LocaleFormatter)localizedConverters.get(globLocKey);
				// if found, instantiate a localized one and store for next use
				if(_formatter != null)
				{
					Class cls = _formatter.getClass();
					LocaleFormatter _newFormatter = (LocaleFormatter)cls.newInstance();
					_newFormatter.setLocale(locale);

					// register the new instance for this locale
					localizedConverters.put(key, _newFormatter);
					formatter = _newFormatter;
				}
			}			
		}
		// else // get without locale right away
		if(formatter == null) // (still) not found, try generic non-localized registration
		{
			formatter = (Formatter)converters.get(getLocKey(key));
		}
		
		return formatter;
	}
	
	/*
	 * get key for localized converters
	 * @param clazz class
	 * @param locale locale
	 * @return String key
	 */
	private String getLocKey(Class clazz, Locale locale)
	{
		return clazz.getName() + "|" + 
			((locale.getCountry() != null) ? locale.getCountry() : "_") + "|" +
			((locale.getLanguage() != null) ? locale.getLanguage() : "_") + "|" +
			((locale.getVariant() != null) ? locale.getVariant() : "_");
	}
	
	/*
	 * get key for localized formatters
	 * @param key key
	 * @param locale locale
	 * @return String key
	 */
	private String getLocKey(String key, Locale locale)
	{
		return "_fmt" + key + "|" + 
			((locale.getCountry() != null) ? locale.getCountry() : "_") + "|" +
			((locale.getLanguage() != null) ? locale.getLanguage() : "_") + "|" +
			((locale.getVariant() != null) ? locale.getVariant() : "_");
	}
	
	/*
	 * get key for localized formatters
	 * @param key key
	 * @return String key
	 */
	private String getLocKey(String key)
	{
		return "_fmt" + key;
	}

}
