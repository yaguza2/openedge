/*
 * $Id: BeanUtilsFieldPopulator.java,v 1.1 2004-04-04 18:28:59 eelco12 Exp $
 * $Revision: 1.1 $
 * $Date: 2004-04-04 18:28:59 $
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


import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.MappedPropertyDescriptor;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infohazard.maverick.flow.ControllerContext;

import nl.openedge.baritus.ConverterRegistry;
import nl.openedge.baritus.ExecutionParams;
import nl.openedge.baritus.FormBeanCtrlBase;
import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.LogConstants;
import nl.openedge.baritus.converters.ConversionException;
import nl.openedge.baritus.converters.Converter;

/**
 * Default populator for bean properties. Tries to set a property using introspection.
 * 
 * @author Eelco Hillenius
 * @author Sander Hofstee
 */
public final class BeanUtilsFieldPopulator extends AbstractFieldPopulator
{

	private static Log populationLog = LogFactory.getLog(LogConstants.POPULATION_LOG);

	/**
	 * construct with reference to the control
	 * @param ctrl
	 */
	public BeanUtilsFieldPopulator(FormBeanCtrlBase ctrl)
	{
		super(ctrl);
	}

	/**
	 * set a property on the given form
	 * @param cctx maverick context
	 * @param formBeanContext context with instance of the form bean to set the property on
	 * @param name name of the property
	 * @param value unconverted value to set
	 * @return boolean true if the property was set successfully, false otherwise
	 * @throws Exception
	 */
	public boolean setProperty(
		ControllerContext cctx,	
		FormBeanContext formBeanContext,
		String name,
		Object requestValue)
		throws Exception
	{

		boolean success = true;
		Locale locale = formBeanContext.getCurrentLocale();
		Object bean = formBeanContext.getBean();
		
		// resolve and get some more info we need for the target
		PropertyDescriptor propertyDescriptor = getPropertyDescriptor(bean, name);
		TargetPropertyMeta targetPropertyMeta = PropertyUtil.calculate(
			bean, name, propertyDescriptor);
			
		Object target = targetPropertyMeta.getTarget();
		Class targetType = getTargetType(propertyDescriptor);
		Converter converter = null;
		
		FormBeanCtrlBase ctrl = getCtrl();
		ExecutionParams executionParams = ctrl.getExecutionParams();
		
		if(targetType.isArray()) 
		{
			Class componentType = targetType.getComponentType();

			if(converter == null) converter = 
				ConverterRegistry.getInstance().lookup(componentType, locale);

			Method reader = propertyDescriptor.getReadMethod();
			Object[] originalArray = (Object[])reader.invoke(target, new Object[]{});

			if(targetPropertyMeta.getIndexesAndKeys().length < 1)
			{
				Object[] values = (Object[])requestValue;
				
				Object[] array = null;
				if((originalArray == null) || (originalArray.length < values.length)) 
					// overwrite completely with a new or larger array
				{
					array = (Object[])Array.newInstance(componentType, values.length);
					// set new array on target object
					//Method setter = propertyDescriptor.getWriteMethod();
					setTargetProperty(targetPropertyMeta, target, array);
				}
				else // use existing and overwrite first part or whole array if lengths are equal
				{
					array = originalArray;					
				}
			
				int index = 0;
				for ( ; index < values.length; index++) 
				{
					Object converted = null;
					try 
					{
					
						if( (values[index] instanceof String) &&
							(values[index] == null || 
							(((String)values[index]).trim().equals(""))) 
							&& executionParams.isSetNullForEmptyString())
						{
							converted = null;
						}
						else
						{
							
							if(populationLog.isDebugEnabled())
							{
								populationLog.debug("using converter " + converter + 
									" for indexed property " + name +
									" (type " + componentType + ")");
							}
							
							converted = converter.convert(componentType, values[index]);
						}
					
						Array.set(array, index, converted);
	
					} 
					catch (ConversionException e) 
					{
						String nameWithIndex = name + '[' + index + "]";
						ctrl.setConversionErrorForField(
							cctx, formBeanContext, targetType, nameWithIndex, values[index], e);
						ctrl.setOverrideField(cctx, formBeanContext, nameWithIndex, values[index], e, null);
						success = false;
					}
					catch (Exception e) 
					{
						populationLog.error(e.getMessage(), e);
						
						String nameWithIndex = name + '[' + index + "]";
						ctrl.setConversionErrorForField(
							cctx, formBeanContext, targetType, nameWithIndex, values[index], e);
						ctrl.setOverrideField(cctx, formBeanContext, nameWithIndex, values[index], e, null);
						success = false;
					}
				}				
			}
			else
			{
				int index = -1;
				if(requestValue instanceof String[]) // convert to plain string 
				{
					index = 0;
					requestValue = ((String[])requestValue)[0];
				}  
				// else keep original
				index = ((Integer)targetPropertyMeta.getIndexesAndKeys()[0]).intValue();
				Object converted = null;

				try
				{
					
					if( (requestValue instanceof String) &&
						(requestValue == null || (((String)requestValue).trim().equals(""))) 
						&& executionParams.isSetNullForEmptyString())
					{
						converted = null;
					}
					else
					{
						
						if(populationLog.isDebugEnabled())
						{
							populationLog.debug("using converter " + converter + 
								" for indexed property " + name +
								" (type " + targetType + ")");
						}
						
						converted = converter.convert(
							targetType, requestValue);
					}

					// replace value in array
					originalArray[index] = converted;
				}
				catch (ConversionException e)
				{
					ctrl.setConversionErrorForField(
						cctx, formBeanContext, targetType, name, requestValue, e);
						
					ctrl.setOverrideField(cctx, formBeanContext, name, requestValue, e, null);
					success = false;	
				}
				catch (Exception e)
				{
					populationLog.error(e.getMessage(), e);
					
					ctrl.setConversionErrorForField(
						cctx, formBeanContext, targetType, name, requestValue, e);
						
					ctrl.setOverrideField(cctx, formBeanContext, name, requestValue, e, null);
					success = false;	
				}			
			}
			
		}
		else // it's not an array
		{

			if(requestValue instanceof String[]) // convert to plain string 
			{
				requestValue = ((String[])requestValue)[0];
			} 
			// else keep original

			if(converter == null) converter = 
				ConverterRegistry.getInstance().lookup(targetType, locale);

			Object converted = null;
			try
			{
				if( (requestValue instanceof String) &&
					(requestValue == null || (((String)requestValue).trim().equals(""))) 
					&& executionParams.isSetNullForEmptyString())
				{
					converted = null;
				}
				else
				{
					if(populationLog.isDebugEnabled())
					{
						populationLog.debug("using converter " + converter + " for property " + name +
							" (type " + targetType + ")");
					}
					
					converted = converter.convert(
						targetType, requestValue);
				}
				
				setTargetProperty(targetPropertyMeta, target, converted);
			}
			catch (ConversionException e)
			{
				ctrl.setConversionErrorForField(
					cctx, formBeanContext, targetType, name, requestValue, e);
					
				ctrl.setOverrideField(cctx, formBeanContext, name, requestValue, e, null);
				success = false;	
			}
			catch (Exception e)
			{
				populationLog.error(e.getMessage(), e);
				
				ctrl.setConversionErrorForField(
					cctx, formBeanContext, targetType, name, requestValue, e);
					
				ctrl.setOverrideField(cctx, formBeanContext, name, requestValue, e, null);
				success = false;	
			}			
		}

		return success;

	}
	
	/*
	 * get the property descriptor
	 * @param bean the bean
	 * @param name property name
	 * @return PropertyDescriptor descriptor if found AND if has writeable method 
	 */
	private PropertyDescriptor getPropertyDescriptor(
		Object bean, 
		String name) 
		throws IllegalAccessException, 
		InvocationTargetException, 
		NoSuchMethodException
	{
		PropertyDescriptor propertyDescriptor = 
			PropertyUtils.getPropertyDescriptor(bean, name);

		if(propertyDescriptor == null) return null;

		if (propertyDescriptor instanceof MappedPropertyDescriptor) 
		{
			MappedPropertyDescriptor pd = (MappedPropertyDescriptor)propertyDescriptor;
			if(pd.getMappedWriteMethod() == null) propertyDescriptor = null;
		}
		else if (propertyDescriptor instanceof IndexedPropertyDescriptor) 
		{
			IndexedPropertyDescriptor pd = (IndexedPropertyDescriptor)propertyDescriptor;
			if(pd.getIndexedWriteMethod() == null) propertyDescriptor = null;
		}
		else 
		{
			if(propertyDescriptor.getWriteMethod() == null) propertyDescriptor = null;
		}
		
		return propertyDescriptor;
	}
	
	/**
	 * get the target type from this property descriptor
	 * @param propertyDescriptor
	 * @return Class the type of the target
	 */
	protected Class getTargetType(PropertyDescriptor propertyDescriptor)
	{
		Class targetType;
		if (propertyDescriptor instanceof MappedPropertyDescriptor) 
		{
			MappedPropertyDescriptor pd = (MappedPropertyDescriptor)propertyDescriptor;
			targetType = pd.getMappedPropertyType();
		}
		else if (propertyDescriptor instanceof IndexedPropertyDescriptor) 
		{
			IndexedPropertyDescriptor pd = (IndexedPropertyDescriptor)propertyDescriptor;
			targetType = pd.getIndexedPropertyType();
		}
		else 
		{
			targetType = propertyDescriptor.getPropertyType();
		}
		return targetType;
	}
	
	/**
	 * get the write method for this property
	 * @param propertyDescriptor property descriptor
	 * @return Method write method
	 */
	protected Method getWriteMethod(PropertyDescriptor propertyDescriptor)
	{
		Method method = null;
		
		if (propertyDescriptor instanceof MappedPropertyDescriptor) 
		{
			MappedPropertyDescriptor pd = (MappedPropertyDescriptor)propertyDescriptor;
			method = pd.getMappedWriteMethod();
		}
		else if (propertyDescriptor instanceof IndexedPropertyDescriptor) 
		{
			IndexedPropertyDescriptor pd = (IndexedPropertyDescriptor)propertyDescriptor;
			method = pd.getIndexedWriteMethod();
		}
		else 
		{
			method = propertyDescriptor.getWriteMethod();
		}
		
		return method;
	}
	
	/**
	 * set property on target
	 * 
	 * There is one special case, for multidimensional Maps and lists
	 * a method signature public void setProperty(Object[], Object) can
	 * be used to set the values.
	 * @param targetPropertyMeta
	 * @param targetObject object to set property on
	 * @param value value of property to set
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	protected void setTargetProperty(
		TargetPropertyMeta targetPropertyMeta,
		Object targetObject,
		Object value) 
		throws IllegalAccessException, 
		InvocationTargetException, 
		NoSuchMethodException
	{
		if (targetPropertyMeta.getIndexesAndKeys().length == 0)
		{
			PropertyUtils.setProperty(
				targetObject, targetPropertyMeta.getPropName(), value);
		}
		else if (targetPropertyMeta.getIndexesAndKeys().length == 1) 
		{
			Object indexOrKey = targetPropertyMeta.getIndexesAndKeys()[0]; 
			
			if (indexOrKey instanceof Integer)
			{
				int index = ((Integer)indexOrKey).intValue();
				PropertyUtils.setIndexedProperty(
					targetObject, targetPropertyMeta.getPropName(),
					index, value);
			}
			else
			{
				PropertyUtils.setMappedProperty(
					targetObject, targetPropertyMeta.getPropName(),
					(String)indexOrKey, value);
			}
		} 
		else 
		{
			String setterMethod = "set"
				+ StringUtils.capitalise(targetPropertyMeta.getPropertyDescriptor().getName());
			Class[] paramTypes = {Object[].class, Object.class};
			Method setter = targetObject.getClass().getMethod(setterMethod, paramTypes);
			
			Object[] params = {targetPropertyMeta.getIndexesAndKeys(), value};
			setter.invoke(targetObject, params);
		}
	}


}

/**
 * Utility class for property population.
 * 
 * @author Eelco Hillenius
 * @author Sander Hofstee
 */
final class PropertyUtil
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

/**
 * Utility class that describes a property.
 * 
 * @author (copied and slightely adjusted from the Apache Jakarta BeanUtils project)
 * @author Sander Hofstee
 */
final class TargetPropertyMeta
{

	private Object[] indexesAndKeys = null; // Indexed subscript value (if any)
	private String name;
	private String propName; // Simple name of target property
	private Object target; // target object
	private PropertyDescriptor propertyDescriptor = null; // property descriptor

	/**
	 * construct
	 * @param target target object
	 * @param name property name
	 * @param propName simple (end name) of the property
	 * @param key mapped key value (if any)
	 * @param indexesAndKeys indexed subscript value (if any)
	 * @param propertyDescriptor property descriptor of target property
	 */
	public TargetPropertyMeta(
		Object target, 
		String name, 
		String propName, 
		Object[] indexesAndKeys,
		PropertyDescriptor propertyDescriptor)
	{
		setTarget(target);
		setName(name);
		setPropName(propName);
		setIndexesAndKeys(indexesAndKeys);
		setPropertyDescriptor(propertyDescriptor);
	}

	/**
	 * get target
	 * @return Object
	 */
	public Object getTarget()
	{
		return target;
	}

	/**
	 * set target
	 * @param target
	 */
	public void setTarget(Object target)
	{
		this.target = target;
	}

	/**
	 * get indexesAndKeys
	 * @return int
	 */
	public Object[] getIndexesAndKeys()
	{
		return indexesAndKeys;
	}

	/**
	 * set indexesAndKeys
	 * @param indexesAndKeys
	 */
	public void setIndexesAndKeys(Object[] index)
	{
		this.indexesAndKeys = index;
	}

	/**
	 * get name
	 * @return String
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * set name
	 * @param name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * get property name
	 * @return String
	 */
	public String getPropName()
	{
		return propName;
	}

	/**
	 * set property name
	 * @param propName
	 */
	public void setPropName(String propName)
	{
		this.propName = propName;
	}
	
	/**
	 * set PropertyDescriptor
	 * @return PropertyDescriptor
	 */
	public PropertyDescriptor getPropertyDescriptor()
	{
		return propertyDescriptor;
	}

	/**
	 * get PropertyDescriptor
	 * @param descriptor
	 */
	public void setPropertyDescriptor(PropertyDescriptor descriptor)
	{
		propertyDescriptor = descriptor;
	}

}
