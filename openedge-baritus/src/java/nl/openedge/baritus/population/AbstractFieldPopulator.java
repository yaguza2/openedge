/*
 * $Id: AbstractFieldPopulator.java,v 1.4 2004-04-02 09:50:22 eelco12 Exp $
 * $Revision: 1.4 $
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

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.beanutils.MappedPropertyDescriptor;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import nl.openedge.baritus.FormBeanCtrlBase;

/**
 * Convenience class for populators.
 * 
 * @author Eelco Hillenius
 * @author Sander Hofstee
 */
public abstract class AbstractFieldPopulator implements FieldPopulator
{
	
	/** reference to the controller */
	private FormBeanCtrlBase ctrl = null;

	/**
	 * default constructor
	 */
	public AbstractFieldPopulator()
	{
		// noop
	}

	/**
	 * constructor with control
	 */
	public AbstractFieldPopulator(FormBeanCtrlBase ctrl)
	{
		setCtrl(ctrl);
	}
	
	/**
	 * get reference to the controller
	 * @return reference to the controller
	 */
	public FormBeanCtrlBase getCtrl()
	{
		return ctrl;
	}

	/**
	 * set reference to the controller
	 * @param ctrl reference to the controller
	 */
	public void setCtrl(FormBeanCtrlBase ctrl)
	{
		this.ctrl = ctrl;
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
