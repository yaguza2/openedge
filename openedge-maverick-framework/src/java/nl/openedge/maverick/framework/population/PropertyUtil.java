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
package nl.openedge.maverick.framework.population;


import org.apache.commons.beanutils.*;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

/**
 * Utility class for property population
 * @author Eelco Hillenius
 */
public final class PropertyUtil
{

	/**
	 * Calculate the property type.
	 *
	 * @param target The bean
	 * @param name The property name
	 * @param propName The Simple name of target property
	 *
	 * @exception IllegalAccessException if the caller does not have
	 *  access to the property accessor method
	 * @exception InvocationTargetException if the property accessor method
	 *  throws an exception
	 */
	public static Class definePropertyType(
		Object target,
		String name,
		String propName)
		throws IllegalAccessException, InvocationTargetException
	{

		Class type = null; // Java type of target property

		if (target instanceof DynaBean)
		{
			DynaClass dynaClass = ((DynaBean) target).getDynaClass();
			DynaProperty dynaProperty = dynaClass.getDynaProperty(propName);
			if (dynaProperty == null)
			{
				return null; // Skip this property setter
			}
			type = dynaProperty.getType();
		}
		else
		{
			PropertyDescriptor descriptor = null;
			try
			{
				descriptor = PropertyUtils.getPropertyDescriptor(target, name);
				if (descriptor == null)
				{
					return null; // Skip this property setter
				}
			}
			catch (NoSuchMethodException e)
			{
				return null; // Skip this property setter
			}
			if (descriptor instanceof MappedPropertyDescriptor)
			{
				type =
					((MappedPropertyDescriptor) descriptor)
						.getMappedPropertyType();
			}
			else if (descriptor instanceof IndexedPropertyDescriptor)
			{
				type =
					((IndexedPropertyDescriptor) descriptor)
						.getIndexedPropertyType();
			}
			else
			{
				type = descriptor.getPropertyType();
			}
		}
		return type;
	}

	/**
	 * Invoke the setter method.
	 *
	 * @param target The bean
	 * @param propName The Simple name of target property
	 * @param key The Mapped key value (if any)
	 * @param index The indexed subscript value (if any)
	 * @param newValue The value to be set
	 *
	 * @exception IllegalAccessException if the caller does not have
	 *  access to the property accessor method
	 * @exception InvocationTargetException if the property accessor method
	 *  throws an exception
	 */
	public  static void invokeSetter(
		Object target,
		String propName,
		String key,
		int index,
		Object newValue)
		throws IllegalAccessException, InvocationTargetException
	{

		try
		{
			if (index >= 0)
			{
				PropertyUtils.setIndexedProperty(
					target, propName, index, newValue);
			}
			else if (key != null)
			{
				PropertyUtils.setMappedProperty(
					target, propName, key, newValue);
			}
			else
			{
				PropertyUtils.setProperty(target, propName, newValue);
			}
		}
		catch (NoSuchMethodException e)
		{
			throw new InvocationTargetException(e, "Cannot set " + propName);
		}
	}

	/**
	 * Resolve any nested expression to get the actual target bean.
	 *
	 * @param bean The bean
	 * @param name The property name
	 * @param propertyDescriptor The property descriptor
	 *
	 * @exception IllegalAccessException if the caller does not have
	 *  access to the property accessor method
	 * @exception InvocationTargetException if the property accessor method
	 *  throws an exception
	 */
	public  static TargetPropertyMeta calculate(
		Object bean, 
		String name, 
		PropertyDescriptor propertyDescriptor)
		throws IllegalAccessException, 
		InvocationTargetException
	{

		String propName = null; // Simple name of target property
		int index = -1; // Indexed subscript value (if any)
		String key = null; // Mapped key value (if any)

		Object target = bean;
		int delim = name.lastIndexOf(PropertyUtils.NESTED_DELIM);
		if (delim >= 0)
		{
			try
			{
				target = PropertyUtils.getProperty(bean, name.substring(0, delim));
			}
			catch (NoSuchMethodException e)
			{
				return null; // Skip this property setter
			}
			name = name.substring(delim + 1);
		}

		// Calculate the property name, index, and key values
		propName = name;
		int i = propName.indexOf(PropertyUtils.INDEXED_DELIM);
		if (i >= 0)
		{
			int k = propName.indexOf(PropertyUtils.INDEXED_DELIM2);
			try
			{
				index = Integer.parseInt(propName.substring(i + 1, k));
			}
			catch (NumberFormatException e)
			{
				;
			}
			propName = propName.substring(0, i);
		}
		int j = propName.indexOf(PropertyUtils.MAPPED_DELIM);
		if (j >= 0)
		{
			int k = propName.indexOf(PropertyUtils.MAPPED_DELIM2);
			try
			{
				key = propName.substring(j + 1, k);
			}
			catch (IndexOutOfBoundsException e)
			{
				;
			}
			propName = propName.substring(0, j);
		}
		
		return new TargetPropertyMeta(target, name, propName, key, index, propertyDescriptor);
	}
}
