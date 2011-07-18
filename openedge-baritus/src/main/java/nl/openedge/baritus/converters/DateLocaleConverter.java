/*
 * $Id: DateLocaleConverter.java,v 1.3 2004-08-08 11:46:29 eelco12 Exp $
 * $Revision: 1.3 $
 * $Date: 2004-08-08 11:46:29 $
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

package nl.openedge.baritus.converters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * <p>
 * Standard {@link LocaleConverter} implementation that converts an incoming
 * locale-sensitive String into a <code>java.util.Date</code> object, optionally using a
 * default value or throwing a {@link ConversionException} if a conversion error occurs.
 * </p>
 * 
 * @author Yauheny Mikulski
 * @author Michael Szlapa
 */
public class DateLocaleConverter extends BaseLocaleConverter
{
	private boolean lenient = false;

	private int dateStyle = DateFormat.SHORT;

	/**
	 * Create a {@link LocaleConverter} that will throw a {@link ConversionException} if a
	 * conversion error occurs. The locale is the default locale for this instance of the
	 * Java Virtual Machine and an unlocalized pattern is used for the conversion.
	 * 
	 */
	public DateLocaleConverter()
	{
		this(Locale.getDefault());
	}

	/**
	 * Create a {@link LocaleConverter} that will throw a {@link ConversionException} if a
	 * conversion error occurs. An unlocalized pattern is used for the conversion.
	 * 
	 * @param locale
	 *            The locale
	 */
	public DateLocaleConverter(Locale locale)
	{
		this(locale, null);
	}

	/**
	 * Create a {@link LocaleConverter} that will throw a {@link ConversionException} if a
	 * conversion error occurs. An unlocalized pattern is used for the conversion.
	 * 
	 * @param locale
	 *            The locale
	 * @param pattern
	 *            The convertion pattern
	 */
	public DateLocaleConverter(Locale locale, String pattern)
	{
		super(locale, pattern, false);
	}

	/**
	 * Returns whether date formatting is lenient.
	 * 
	 * @return true if the <code>DateFormat</code> used for formatting is lenient
	 * @see java.text.DateFormat#isLenient
	 */
	public boolean isLenient()
	{
		return lenient;
	}

	/**
	 * Specify whether or not date-time parsing should be lenient.
	 * 
	 * @param lenient
	 *            true if the <code>DateFormat</code> used for formatting should be
	 *            lenient
	 * @see java.text.DateFormat#setLenient
	 */
	public void setLenient(boolean lenient)
	{
		this.lenient = lenient;
	}

	/**
	 * get date style
	 * 
	 * @return int date style as a constant from DateFormat
	 */
	public int getDateStyle()
	{
		return dateStyle;
	}

	/**
	 * set date style
	 * 
	 * @param dateStyle
	 */
	public void setDateStyle(int dateStyle)
	{
		this.dateStyle = dateStyle;
	}

	/**
	 * Convert the specified locale-sensitive input object into an output object of the
	 * specified type.
	 * 
	 * @param value
	 *            The input object to be converted
	 * @param pattern
	 *            The pattern is used for the conversion
	 * 
	 * @exception ConversionException
	 *                if conversion cannot be performed successfully
	 */
	@Override
	protected Object parse(Object value, String pattern) throws ConversionException
	{
		DateFormat formatter = getFormat(pattern, locale);

		try
		{
			return formatter.parse((String) value);
		}
		catch (ParseException e)
		{
			String dpat = null;
			if (pattern != null)
			{
				dpat = pattern;
			}
			else if (formatter instanceof SimpleDateFormat)
			{
				dpat = ((SimpleDateFormat) formatter).toLocalizedPattern();
			}

			throw new ConversionException(e, dpat);
		}
	}

	/**
	 * format value with pattern or using the default pattern
	 * 
	 * @see nl.openedge.baritus.converters.Formatter#format(java.lang.Object,
	 *      java.lang.String)
	 */
	@Override
	public String format(Object value, String pattern) throws IllegalArgumentException
	{
		DateFormat format = getFormat(pattern, locale);
		Date date = null;
		if (value instanceof Date)
		{
			date = (Date) value;
		}
		else
		{
			date = (Date) convert(Date.class, value);
		}
		return format.format(date);
	}

	/**
	 * Get date format
	 */
	private DateFormat getFormat(String pattern, Locale locale)
	{
		DateFormat format = null;
		if (pattern == null)
		{
			format = DateFormat.getDateInstance(dateStyle, locale);
			format.setLenient(lenient);
		}
		else
		{
			SimpleDateFormat _format = new SimpleDateFormat(pattern, locale);
			_format.setLenient(lenient);
			if (locPattern)
			{
				_format.applyLocalizedPattern(pattern);
			}
			else
			{
				_format.applyPattern(pattern);
			}
			format = _format;
		}
		return format;
	}

	/**
	 * Convert the specified locale-sensitive input object into an output object of the
	 * specified type.
	 * 
	 * @param type
	 *            Data type to which this value should be converted
	 * @param value
	 *            The input object to be converted
	 * @param pattern
	 *            The pattern is used for the conversion
	 * 
	 * @exception ConversionException
	 *                if conversion cannot be performed successfully
	 */
	@Override
	public Object convert(Class< ? > type, Object value, String pattern)
	{
		if (value == null)
		{
			return null;
		}

		return parse(value, pattern);
	}
}
