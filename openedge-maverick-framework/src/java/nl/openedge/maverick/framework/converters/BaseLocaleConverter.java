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

package nl.openedge.maverick.framework.converters;

import org.apache.commons.beanutils.locale.LocaleConverter;

import java.text.ParseException;
import java.util.Locale;

/**
 * base class for localized converters
 * @author Eelco Hillenius
 */
public abstract class BaseLocaleConverter implements LocaleConverter, Formatter
{

	// ----------------------------------------------------- Instance Variables

	/** The default value specified to our Constructor, if any. */
	private Object defaultValue = null;

	/** The locale specified to our Constructor, by default - system locale. */
	protected Locale locale = Locale.getDefault();

	/** The default pattern specified to our Constructor, if any. */
	protected String pattern = null;

	/** The flag indicating whether the given pattern string is localized or not. */
	protected boolean locPattern = false;

	// ----------------------------------------------------------- Constructors

	/**
	 * Create a {@link LocaleConverter} that will throw a {@link ConversionException}
	 * if a conversion error occurs.
	 * An unlocalized pattern is used for the convertion.
	 *
	 * @param locale        The locale
	 * @param pattern       The convertion pattern
	 */
	protected BaseLocaleConverter(Locale locale, String pattern)
	{
		this(locale, pattern, false);
	}


	/**
	 * Create a {@link LocaleConverter} that will throw a {@link ConversionException} 
	 * if an conversion error occurs.
	 *
	 * @param locale        The locale
	 * @param pattern       The convertion pattern
	 * @param locPattern    Indicate whether the pattern is localized or not
	 */
	protected BaseLocaleConverter(
		Locale locale,
		String pattern,
		boolean locPattern)
	{

		if (locale != null)
		{
			this.locale = locale;
		}

		this.pattern = pattern;
		this.locPattern = locPattern;
	}

	// --------------------------------------------------------- Methods

	/**
	 * Convert the specified locale-sensitive input object into an output object of the
	 * specified type.
	 *
	 * @param value The input object to be converted
	 * @param pattern The pattern is used for the convertion
	 *
	 * @exception ConversionException if conversion cannot be performed
	 *  successfully
	 */

	abstract protected Object parse(Object value, String pattern)
		throws ParseException;

	/**
	 * Convert the specified locale-sensitive input object into an output object.
	 * The default pattern is used for the convertion.
	 *
	 * @param value The input object to be converted
	 *
	 * @exception ConversionException if conversion cannot be performed
	 *  successfully
	 */
	public Object convert(Object value)
	
	{
		return convert(value, null);
	}

	/**
	 * Convert the specified locale-sensitive input object into an output object.
	 *
	 * @param value The input object to be converted
	 * @param pattern The pattern is used for the convertion
	 *
	 * @exception ConversionException if conversion cannot be performed
	 *  successfully
	 */
	public Object convert(Object value, String pattern)
	{
		return convert(null, value, pattern);
	}

	/**
	 * Convert the specified locale-sensitive input object into an output object of the
	 * specified type. The default pattern is used for the convertion.
	 *
	 * @param type Data type to which this value should be converted
	 * @param value The input object to be converted
	 *
	 * @exception ConversionException if conversion cannot be performed
	 *  successfully
	 */
	public Object convert(Class type, Object value)
	{
		return convert(type, value, null);
	}

//	/**
//	 * Convert the specified locale-sensitive input object into an output object of the
//	 * specified type.
//	 *
//	 * @param type Data type to which this value should be converted
//	 * @param value The input object to be converted
//	 * @param pattern The pattern is used for the convertion
//	 *
//	 * @exception ConversionException if conversion cannot be performed successfully
//	 */
//	public Object convert(Class type, Object value, String pattern)
//	{
//		if (value == null)
//		{
//			return null;
//		}
//
//		try
//		{
//			if (pattern != null)
//			{
//				return parse(value, pattern);
//			}
//			else
//			{
//				return parse(value, this.pattern);
//			}
//		}
//		catch (Exception e)
//		{
//			throw new ConversionException(e);
//		}
//	}

	/**
	 * get the locale
	 * @return Locale
	 */
	public Locale getLocale()
	{
		return locale;
	}

	/**
	 * get the pattern
	 * @return String
	 */
	public String getPattern()
	{
		return pattern;
	}

	/**
	 * set the locale
	 * @param locale
	 */
	public void setLocale(Locale locale)
	{
		this.locale = locale;
	}

	/**
	 * set the pattern
	 * @param string
	 */
	public void setPattern(String string)
	{
		pattern = string;
	}

}
