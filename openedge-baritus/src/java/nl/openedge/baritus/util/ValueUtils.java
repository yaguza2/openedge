/*
 * $Id: ValueUtils.java,v 1.3 2004-05-23 10:26:57 eelco12 Exp $
 * $Revision: 1.3 $
 * $Date: 2004-05-23 10:26:57 $
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
package nl.openedge.baritus.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.openedge.baritus.ConverterRegistry;
import nl.openedge.baritus.converters.ConversionException;
import nl.openedge.baritus.converters.Converter;

/**
 * Misc utility methods for handling values
 * 
 * @author Eelco Hillenius
 */
public final class ValueUtils
{
    
    /* logger */
    private static Log log = LogFactory.getLog(ValueUtils.class);

	/**
	 * check if the value is null or empty
	 * @param value object to check on
	 * @return true if value is not null AND not empty (e.g. 
	 * in case of a String or Collection)
	 */
	public static boolean isNullOrEmpty(Object value)
	{
		if (value instanceof String)
		{
			return (value == null || (((String)value).trim().equals("")));
		}
		if (value instanceof Object[])
		{
			if (value == null)
			{
				return true;
			}
			else if (((Object[])value).length == 0)
			{
				return true;
			}
			else if (((Object[])value).length == 1)
			{
				return isNullOrEmpty(((Object[])value)[0]);
			}
			else
			{
				return false;
			}
		}
		else if (value instanceof Collection)
		{
			return (value == null || (((Collection)value).isEmpty()));
		}
		else if (value instanceof Map)
		{
			return (value == null || (((Map)value).isEmpty()));
		}
		else
		{
			return (value == null);
		}
	}

	/**
	 * Convert the specified value into a String.  If the specified value
	 * is an array, the first element (converted to a String) will be
	 * returned.  The registered {@link Converter} for the
	 * <code>java.lang.String</code> class will be used, which allows
	 * applications to customize Object->String conversions (the default
	 * implementation simply uses toString()).
	 *
	 * @param value Value to be converted (may be null)
	 */
	public static String convertToString(Object value)
	{

		if (value == null)
		{
			return ((String) null);
		}
		else if (value.getClass().isArray())
		{
			if (Array.getLength(value) < 1)
			{
				return (null);
			}
			value = Array.get(value, 0);
			if (value == null)
			{
				return ((String) null);
			}
			else
			{
				try
				{
					Converter converter = ConverterRegistry.getInstance().lookup(String.class);
					Object converted = converter.convert(String.class, value);
					return (converted instanceof String) ? 
						(String)converted : String.valueOf(converted);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					throw new ConversionException(e);
				}
			}
		}
		else
		{
			try
			{
				Converter converter = ConverterRegistry.getInstance().lookup(String.class);
				Object converted = converter.convert(String.class, value);
				return (converted instanceof String) ? 
					(String)converted : String.valueOf(converted);
			}
			catch (Exception e)
			{
				log.error(e.getMessage(), e);
				throw new ConversionException(e);
			}
		}

	}

}
