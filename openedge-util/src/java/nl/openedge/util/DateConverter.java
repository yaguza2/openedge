/*
 * $Header$
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
package nl.openedge.util;

import java.text.ParseException;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

/**
 * @author Klaasjan Brand
 * @author Eelco Hillenius
 */
public final class DateConverter implements Converter
{

	/*
	 * @see Converter#convert(Class, Object)
	 */
	public Object convert(Class type, Object value)
	{

		if (value == null)
		{
			return null;
		}
		java.util.Date stdDate = null;
		if (value instanceof String)
		{

			String sDate = String.valueOf(value);
			if ("".equals(sDate.trim()))
			{
				return null;
			}
			try
			{
				stdDate = DateFormatHelper.fallbackParse(sDate);
			}
			catch (ParseException e)
			{
				throw new ConversionException(
					"Can't convert " + sDate + " to a date");
			}
		}
		else if (value instanceof java.util.Date)
		{
			return value;
		}
		else if (value instanceof java.sql.Date)
		{
			stdDate = new java.util.Date(((java.sql.Date)value).getTime());
		}
		else if (value instanceof Long)
		{
			stdDate = new java.util.Date(((Long) (value)).longValue());
		}
		else if (value instanceof java.sql.Time)
		{

		}

		if (type == java.util.Date.class)
		{
			return stdDate;
		}
		else if (type == java.sql.Date.class)
		{
			return new java.sql.Date(stdDate.getTime());
		}
		else if (type == java.sql.Time.class)
		{
			return new java.sql.Time(stdDate.getTime());
		}
		else
		{
			throw new ConversionException(type + " is an unsupported type");
		}
	}

}
