/*
 * $Id: OgnlConverterWrapper.java,v 1.1 2004-04-04 18:26:59 eelco12 Exp $
 * $Revision: 1.1 $
 * $Date: 2004-04-04 18:26:59 $
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
package nl.openedge.baritus.population;

import java.lang.reflect.Member;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.openedge.baritus.ConverterRegistry;
import nl.openedge.baritus.ExecutionParams;
import nl.openedge.baritus.LogConstants;
import nl.openedge.baritus.converters.ConversionException;
import nl.openedge.baritus.converters.Converter;

import ognl.DefaultTypeConverter;
import ognl.OgnlOps;

/**
 * @author hillenius
 */
public class OgnlConverterWrapper extends DefaultTypeConverter
{
	
	private static Log populationLog = LogFactory.getLog(LogConstants.POPULATION_LOG);

	/**
	 * convert the provided value to provided type using provided context
	 * @param context
	 * @param value
	 * @param toType
	 * @return Converted value
	 * @see ognl.DefaultTypeConverter#convertValue(java.util.Map, java.lang.Object, java.lang.Class)
	 */
	public Object convertValue(Map context, Object value, Class toType)
	{

		if(value == null) return null;

		Object converted = null;
		
		Locale locale = (Locale)context.get(OgnlFieldPopulator.CTX_KEY_CURRENT_LOCALE);
		ExecutionParams executionParams = (ExecutionParams)
			context.get(OgnlFieldPopulator.CTX_KEY_CURRENT_EXEC_PARAMS);
		
		if((!toType.isArray()) && value instanceof String[] && ((String[])value).length == 1)
		{
			value = ((String[])value)[0];	
		}
		
		if( (value instanceof String) && ((String)value).trim().equals("") 
			&& executionParams.isSetNullForEmptyString())
		{
			if(populationLog.isDebugEnabled())
			{
				String name = (String)context.get(
					OgnlFieldPopulator.CTX_KEY_CURRENT_FIELD_NAME);
				populationLog.debug("empty input for property " + name + " converted to null");
			}
			return null;
		}
		
		context.put(OgnlFieldPopulator.CTX_KEY_CURRENT_TRIED_VALUE, value);
		
		try
		{
			ConverterRegistry reg = ConverterRegistry.getInstance();
			Converter converter = reg.lookup(toType, locale);
			
			if(converter != null)
			{
				context.put(OgnlFieldPopulator.CTX_KEY_CURRENT_CONVERTER, converter);
				
				if(populationLog.isDebugEnabled())
				{
					String name = (String)context.get(
						OgnlFieldPopulator.CTX_KEY_CURRENT_FIELD_NAME);
					populationLog.debug("using converter " 
						+ converter + " for property " + name +
						" (type " + toType + ")");
				}
				
				converted = converter.convert(toType, value);
				
				if(!toType.isArray())
				{
					if(value instanceof String[] && ((String[])value).length == 1) 
					{
						value = ((String[])value)[0];
					}					
				}
				converted = converter.convert(toType, value);	
			}
			else
			{
				converted = OgnlOps.convertValue(value, toType);
			}
		}
		catch(ConversionException e)
		{
			context.put(OgnlFieldPopulator.CTX_KEY_CURRENT_TARGET_TYPE, toType);
			throw e;
		}
		catch(Exception e)
		{
			context.put(OgnlFieldPopulator.CTX_KEY_CURRENT_TARGET_TYPE, toType);
			throw new ConversionException(e);
		}
		
		return converted;
	}

	public Object convertValue(
		Map context,
		Object target,
		Member member,
		String propertyName,
		Object value,
		Class toType)
	{
		return convertValue(context, value, toType);
	}

}