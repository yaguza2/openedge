/*
 * $Id: PropertyUtil.java,v 1.2 2004-04-02 09:50:22 eelco12 Exp $
 * $Revision: 1.2 $
 * $Date: 2004-04-02 09:50:22 $
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


import org.apache.commons.beanutils.*;
import org.apache.commons.lang.StringUtils;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for property population.
 * 
 * @author Eelco Hillenius
 * @author Sander Hofstee
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
		Object[] index = getIndexesAndKeys(propName);
		
		return new TargetPropertyMeta(target, name, propName, index, propertyDescriptor);
	}
	
	/**
	 * Retrieves all the indexes and keys from the propertyName.
	 * 
	 * @param propName name of property
	 * @return Object[] all the indexes and keys from the propertyName
	 */
	private static Object[] getIndexesAndKeys(String propName)
	{
		List result = new ArrayList(1);
		String[] lookForStart = {Character.toString(PropertyUtils.INDEXED_DELIM)
			, Character.toString(PropertyUtils.MAPPED_DELIM)};
		String[] lookForEnd = {Character.toString(PropertyUtils.INDEXED_DELIM2)
			, Character.toString(PropertyUtils.MAPPED_DELIM2)};
		
		int startIndex = StringUtils.indexOfAny(propName, lookForStart);
		int endIndex = StringUtils.indexOfAny(propName, lookForEnd);
		
		while (startIndex >= 0 && endIndex > startIndex)
		{
			String indexOrKey = propName.substring(startIndex + 1, endIndex);
			
			/*
			 * It's an indexed property
			 */
			if (propName.charAt(startIndex) == PropertyUtils.INDEXED_DELIM)
			{
				Integer index = new Integer(indexOrKey);
				result.add(index);
			}
			/*
			 * It's a mapped property
			 */
			else
			{
				result.add(indexOrKey);
			}
			propName = propName.substring(endIndex +1);
			startIndex = StringUtils.indexOfAny(propName, lookForStart);
			endIndex = StringUtils.indexOfAny(propName, lookForEnd);
		}
	
		return result.toArray();
	}
}
