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

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.beanutils.MappedPropertyDescriptor;
import org.apache.commons.beanutils.PropertyUtils;

import nl.openedge.maverick.framework.AbstractCtrl;

/**
 * @author Eelco Hillenius
 */
public abstract class AbstractFieldPopulator
{
	
	/** reference to the controller */
	private AbstractCtrl ctrl = null;

	/**
	 * default constructor
	 */
	public AbstractFieldPopulator(AbstractCtrl ctrl)
	{
		setCtrl(ctrl);
	}
	
	/**
	 * get reference to the controller
	 * @return reference to the controller
	 */
	public AbstractCtrl getCtrl()
	{
		return ctrl;
	}

	/**
	 * set reference to the controller
	 * @param ctrl reference to the controller
	 */
	public void setCtrl(AbstractCtrl ctrl)
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
		if (targetPropertyMeta.getIndex() >= 0) 
		{
			PropertyUtils.setIndexedProperty(
				targetObject, targetPropertyMeta.getPropName(),
				targetPropertyMeta.getIndex(), value);
		} 
		else if (targetPropertyMeta.getKey() != null) 
		{
			PropertyUtils.setMappedProperty(
				targetObject, targetPropertyMeta.getPropName(),
				targetPropertyMeta.getKey(), value);
		} 
		else 
		{
			PropertyUtils.setProperty(
				targetObject, targetPropertyMeta.getPropName(), value);
		}
	}

}
