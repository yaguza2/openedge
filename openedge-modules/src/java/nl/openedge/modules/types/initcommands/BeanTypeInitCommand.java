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
package nl.openedge.modules.types.initcommands;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.jdom.Element;

import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.config.ConfigException;

/**
 * Command that populates instances using BeanUtils
 * @author Eelco Hillenius
 */
public final class BeanTypeInitCommand implements InitCommand
{
	
	private Map properties = null;

	/**
	 * initialize
	 * @see nl.openedge.components.types.decorators.InitCommand#init(java.lang.String, org.jdom.Element, nl.openedge.components.ComponentRepository)
	 */
	public void init(
		String componentName, 
		Element componentNode,
		ComponentRepository componentRepository)
		throws ConfigException
	{
		this.properties = new HashMap();
		List pList = componentNode.getChildren("property");
		if (pList != null)
		{
			for (Iterator j = pList.iterator(); j.hasNext();)
			{

				Element pElement = (Element)j.next();
				properties.put(pElement.getAttributeValue("name"), 
							pElement.getAttributeValue("value"));
			}
				
		}
	}

	/**
	 * populate the component instance
	 * @see nl.openedge.components.types.decorators.InitCommand#execute(java.lang.Object)
	 */
	public void execute(Object componentInstance) 
		throws InitCommandException, ConfigException
	{

		if(properties != null)
		{
			boolean success = true;
			// try to set its properties
			try
			{
				success = populate(componentInstance, this.properties);
			}
			catch (Exception e)
			{
				throw new ConfigException(e);
			}
			if(!success)
			{
				throw new ConfigException(
					"there were errors during population of " + componentInstance);
			}
		}
	}
	
	/**
	 * default populate of form: BeanUtils way; set error if anything goes wrong
	 * @param componentInstance
	 * @param properties
	 * @return true if populate did not have any troubles, false otherwise
	 */
	protected boolean populate(Object componentInstance, Map properties)
		throws NoSuchMethodException, 
		InvocationTargetException,
		IllegalAccessException 
	{
		
		// Do nothing unless both arguments have been specified
		if ((componentInstance == null) || (properties == null)) 
		{
			return true;
		}

		boolean succeeded = true;
		// Loop through the property name/value pairs to be set
		Iterator names = properties.keySet().iterator();
		while (names.hasNext()) 
		{
			boolean success = true;
			// Identify the property name and value(s) to be assigned
			String name = (String)names.next();
			if (name == null) 
			{
				continue;
			}
			Object value = properties.get(name);
			String stringValue = null;
			
			if(value != null)
			{

				if(value instanceof String)
				{
					success = setSingleProperty(componentInstance, name, value);
				} 
				else if(value instanceof String[])
				{
					Class type;
					PropertyDescriptor propertyDescriptor;
					try
					{
						propertyDescriptor =
							PropertyUtils.getPropertyDescriptor(componentInstance, name);
						type = propertyDescriptor.getPropertyType();
						
						if(type.isArray())
						{
							success = setArrayProperty(
								propertyDescriptor, 
								componentInstance, name, (String[])value);
		
						}
						else // the target property is not an array; let BeanUtils
							// handle the conversion
						{
							success = setSingleProperty(componentInstance, name, value);
						}
					}
					catch (Exception e)
					{
						// Property does not exist in target object; ignore 
					}
				}
				if(!success)
				{
					succeeded = false;
				}
			}
		}
		return succeeded;
	}
	
	/**
	 * set an array property
	 */
	private boolean setArrayProperty(
			PropertyDescriptor propertyDescriptor,
			Object componentInstance,
			String name, 
			String[] values)
	{		
		boolean success = true;
		Class type = propertyDescriptor.getPropertyType();
		if(type.isArray())
		{
			Class componentType = type.getComponentType();
			Converter converter = ConvertUtils.lookup(componentType);
			Object array = Array.newInstance(componentType, values.length);
			
			int i = 0;
			for ( ; i < values.length; i++) 
			{
				try 
				{
					Object converted = converter.convert(
						componentType, (String)values[i]);

					Array.set(array, i, converted);		
				} 
				catch (ConversionException e) 
				{
					e.printStackTrace();
					success = false;
				}
			}
			
			try
			{
				PropertyUtils.setProperty(componentInstance, name, array);
			}
			catch (Exception e)
			{
				//this should not happen as we did extensive checking allready.
				// therefore print the stacktrace
				e.printStackTrace();
				success = false;
			}				
		}
		
		return success;
	}
	
	/**
	 * set a single property
	 */
	private boolean setSingleProperty(
		Object componentInstance, String name, Object value)
	{
		boolean success = true;
		String stringValue = null;
		try
		{
			// check as string first
			stringValue = ConvertUtils.convert(value);
			if(stringValue != null && (!stringValue.trim().equals("")))
			{
				Object o = PropertyUtils.getProperty(componentInstance, name);
				// Perform the assignment for this property
				BeanUtils.setProperty(componentInstance, name, value);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			success = false;	
		}
		return success;
	}

}
