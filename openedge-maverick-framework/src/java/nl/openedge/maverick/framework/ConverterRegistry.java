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
	 * 		from Jakarta Commons BeanUtils will be done as a fallthrough.
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
		
		return converter;
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

}
