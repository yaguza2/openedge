/*
 * $Id: DefaultFieldPopulator.java,v 1.2 2004-03-04 08:21:39 eelco12 Exp $
 * $Revision: 1.2 $
 * $Date: 2004-03-04 08:21:39 $
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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Locale;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infohazard.maverick.flow.ControllerContext;

import nl.openedge.baritus.*;
import nl.openedge.baritus.FormBeanCtrlBase;
import nl.openedge.baritus.FormBeanContext;

/**
 * Default populator for bean properties. Tries to set a property using introspection
 * @author Eelco Hillenius
 */
public final class DefaultFieldPopulator extends AbstractFieldPopulator
{

	private static Log populationLog = LogFactory.getLog(LogConstants.POPULATION_LOG);

	/**
	 * construct with reference to the control
	 * @param ctrl
	 */
	public DefaultFieldPopulator(FormBeanCtrlBase ctrl)
	{
		super(ctrl);
	}

	/**
	 * set a property on the given form
	 * @param cctx maverick context
	 * @param formBeanContext context with instance of the form bean to set the property on
	 * @param name name of the property
	 * @param value the value from the request. This is either a String or a String array (String[])
	 * @param targetPropertyMeta an extra wrapper for the target
	 * @param locale
	 * @return boolean true if the property was set successfully, false otherwise
	 * @throws Exception
	 */
	public boolean setProperty(
		ControllerContext cctx,	
		FormBeanContext formBeanContext,
		String name,
		Object requestValue,
		TargetPropertyMeta targetPropertyMeta,
		Locale locale)
		throws Exception
	{
		Object bean = formBeanContext.getBean();
		Object newValue = null;
		boolean success = true;
		Object target = targetPropertyMeta.getTarget();
		PropertyDescriptor propertyDescriptor = targetPropertyMeta.getPropertyDescriptor();
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
			
			if(targetPropertyMeta.getIndex() < 0)
				// target is an array and name of property is not indexed 
				// (a property is indexed in form: myproperty[1]; getIndex() would be 1 in 
				//	this case and the actual property to navigate is an array element 
				//	instead of the whole array).
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
			else // the target is one specific element in the array
			{
				if(requestValue instanceof String[]) // convert to plain string 
				{
					requestValue = ((String[])requestValue)[0];
				}  
				// else keep original
				int index = targetPropertyMeta.getIndex();
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

}
