/*
 * $Id: ByteLocaleConverter.java,v 1.2 2004-04-09 18:44:53 eelco12 Exp $
 * $Revision: 1.2 $
 * $Date: 2004-04-09 18:44:53 $
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

import java.text.ParseException;
import java.util.Locale;

/**
 * localized byte converter
 * @author Eelco Hillenius
 */
public class ByteLocaleConverter extends DecimalLocaleConverter
{

	// ----------------------------------------------------------- Constructors

	/**
	 * Create a {@link LocaleConverter} that will throw a {@link ConversionException} if a
	 * conversion error occurs. The locale is the default locale for this instance of the
	 * Java Virtual Machine and an unlocalized pattern is used for the convertion.
	 */
	public ByteLocaleConverter()
	{
		this(Locale.getDefault());
	}

	/**
	 * Create a {@link LocaleConverter} that will throw a {@link ConversionException} if a
	 * conversion error occurs. An unlocalized pattern is used for the convertion.
	 * @param locale The locale
	 */
	public ByteLocaleConverter(Locale locale)
	{
		this(locale, null);
	}

	/**
	 * Create a {@link LocaleConverter} that will throw a {@link ConversionException} if a
	 * conversion error occurs. An unlocalized pattern is used for the convertion.
	 * @param locale The locale
	 * @param pattern The convertion pattern
	 */
	public ByteLocaleConverter(Locale locale, String pattern)
	{

		this(locale, pattern, false);
	}

	/**
	 * Create a {@link LocaleConverter} that will throw a {@link ConversionException} if a
	 * conversion error occurs.
	 * @param locale The locale
	 * @param pattern The convertion pattern
	 * @param locPattern Indicate whether the pattern is localized or not
	 */
	public ByteLocaleConverter(Locale locale, String pattern, boolean locPattern)
	{

		super(locale, pattern, locPattern);
	}

	/**
	 * Convert the specified locale-sensitive input object into an output object of the
	 * specified type. This method will return values of type Byte.
	 * @param value The input object to be converted
	 * @param pattern The pattern is used for the convertion
	 * @exception ConversionException if conversion cannot be performed successfully
	 */
	protected Object parse(Object value, String pattern) throws ParseException
	{
		final Number parsed = (Number) super.parse(value, pattern);
		if (parsed.longValue() != parsed.byteValue())
		{
			throw new ConversionException("Supplied number is not of type Byte: "
					+ parsed.longValue());
		}
		// now return property Byte
		return Byte.valueOf(parsed.byteValue());
	}

	/**
	 * Convert the specified locale-sensitive input object into an output object of the
	 * specified type.
	 * @param type Data type to which this value should be converted
	 * @param value The input object to be converted
	 * @param pattern The pattern is used for the convertion
	 * @exception ConversionException if conversion cannot be performed successfully
	 */
	public Object convert(Class type, Object value, String pattern)
	{
		if (value == null)
		{
			return null;
		}

		Number temp = getNumber(value, pattern);

		return (temp instanceof Byte) ? (Byte) temp : Byte.valueOf(temp.byteValue());
	}
}
