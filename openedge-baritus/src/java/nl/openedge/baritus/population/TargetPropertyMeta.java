/*
 * $Id: TargetPropertyMeta.java,v 1.2 2004-04-02 09:50:22 eelco12 Exp $
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

import java.beans.PropertyDescriptor;

/**
 * Utility class that describes a property.
 * 
 * @author (copied and slightely adjusted from the Apache Jakarta BeanUtils project)
 * @author Sander Hofstee
 */
public final class TargetPropertyMeta
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