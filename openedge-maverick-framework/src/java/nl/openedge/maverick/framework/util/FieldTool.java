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
package nl.openedge.maverick.framework.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.openedge.maverick.framework.AbstractForm;

/**
 * @author Eelco Hillenius
 */
public class FieldTool
{
	
	/* prefix for form, default = "model" */
	private String modelPrefix = "model.";
	
	private static Log log = LogFactory.getLog(FieldTool.class);

	/**
	 * print error message
	 * @param model current model
	 * @param field field name
	 * @return String or null if no error was found
	 */
	public String error(Object model, String field) 
	{	
		if(model instanceof AbstractForm) 
		{
			AbstractForm form = (AbstractForm)model;
			String stringVal = form.getError(field);
			if(stringVal != null) 
			{
				return stringVal;		
			} 
			else 
			{
				return null;
			}
		}
		
		// fallback
		return null;	
	}

	/**
	 * get value from form as a String
	 * @param bean bean with field
	 * @param name name of field
	 * @return String
	 */	
	public String getFormValueAsString(Object bean, String name)
	{
		if( (bean == null) || (name == null)) return null;
		String value = null;
		AbstractForm model = null;
		if(bean instanceof AbstractForm)
		{
			model = (AbstractForm)model;
		}
		try
		{
			value = BeanUtils.getProperty(bean, name);
		}
		catch (Exception e)
		{
			if(log.isDebugEnabled())
			{
				log.error(
					"error while handling property " + name + " with object " + bean );
				log.error(e.getMessage(), e);			
			}

			return null;
		}
		if(model != null)
		{
			Map overrideFields = model.getOverrideFields();
			if( overrideFields != null ) 
			{
		
				Object storedRawValue = overrideFields.get(name);
				String storedValue = null;
				// first, try default
				if(storedRawValue != null)
				{
					storedValue = ConvertUtils.convert(storedRawValue);
					if(storedValue != null) 
					{
						value = storedValue;
					}	
				}
				else // try without model prefix (if it has one)
				{
					if(name.startsWith(modelPrefix))
					{
						name = name.substring(modelPrefix.length());
					
						storedRawValue = overrideFields.get(name);
						if(storedRawValue != null)
						{
							storedValue = ConvertUtils.convert(storedRawValue);
							if(storedValue != null) 
							{
								value = storedValue;
							}	
						}
					}
				}
			}	
		}
		return value;
	}
	
	/**
	 * get value from form as a String formatted as pattern
	 * @param bean bean with field
	 * @param name name of field
	 * @param pattern for format
	 * @return String
	 */	
	public String getFormattedFormValueAsString(Object bean, String name, String pattern)
	{
		if((bean == null) || (name == null) || (pattern == null)) return null;
		Object value = null;
		String converted = null;
		AbstractForm model = null;
		if(bean instanceof AbstractForm)
		{
			model = (AbstractForm)model;
		}
		try
		{
			value = PropertyUtils.getProperty(bean, name);
		}
		catch (Exception e)
		{
			if(log.isDebugEnabled())
			{
				log.error(
					"error while handling property " + name + " with object " + bean );
				log.error(e.getMessage(), e);			
			}

			return null;
		}
		if(model != null)
		{
			Map overrideFields = model.getOverrideFields();
			if( overrideFields != null ) 
			{
		
				Object storedRawValue = overrideFields.get(name);
				// first, try default
				if(storedRawValue == null)
				{
					if(name.startsWith(modelPrefix))
					{
						name = name.substring(modelPrefix.length());
						storedRawValue = overrideFields.get(name);
					}
				}
				if(storedRawValue != null)
				{
					value = storedRawValue;
				}
			}	
		}
		if(value != null)
		{
			if( (value instanceof java.util.Date) || (value instanceof java.sql.Date) )
			{
				SimpleDateFormat df = new SimpleDateFormat(pattern);
				converted = df.format((java.util.Date)value);
			}
			else if(value instanceof Number)
			{
				DecimalFormat df = new DecimalFormat(pattern);
				converted = df.format(value);
			}
			else
			{
				converted = ConvertUtils.convert(value);
				System.err.println("unable to apply pattern to field " + name + " of object " +
					bean + ": type " + value.getClass().getName() + 
					" cannot be used with a pattern. default conversion: " +
					value + "->" + converted);
			}	
		}
		return converted;
	}

	/**
	 * @return String
	 */
	public String getModelPrefix()
	{
		return modelPrefix;
	}

	/**
	 * @param string
	 */
	public void setModelPrefix(String string)
	{
		modelPrefix = string;
	}

}
