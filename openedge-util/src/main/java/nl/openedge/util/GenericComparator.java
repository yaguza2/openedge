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
package nl.openedge.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Compares using introspection.
 * 
 * @author Johan Compagner
 * @author Eelco Hillenius
 */
public class GenericComparator<T> implements Comparator<T>
{
	/** Log. */
	private static Log log = LogFactory.getLog(GenericComparator.class);

	/** Arguments. */
	private Object[] args = new Object[0];

	/** all methods. */
	private ArrayList<Method> internalAllMethods;

	/** whether to sort ascending (true) or descending (false). */
	private int internalIsAscending;

	/**
	 * constructor.
	 * 
	 * @param cls
	 *            class
	 * @param sField
	 *            the field name
	 * @param bAscending
	 *            whether this is ascending
	 * @throws NoSuchFieldException
	 *             when field could not be found
	 */
	public GenericComparator(Class<T> cls, String sField, boolean bAscending)
			throws NoSuchFieldException
	{
		fillMethods(sField, cls);
		if (bAscending)
		{
			internalIsAscending = 1;
		}
		else
		{
			internalIsAscending = -1;
		}
	}

	/**
	 * get method for property.
	 * 
	 * @param propertyName
	 *            name property
	 * @param cls
	 *            class of object to test on
	 * @throws NoSuchFieldException
	 *             when field could not be found
	 */
	protected void fillMethods(String propertyName, Class< ? > cls) throws NoSuchFieldException
	{
		internalAllMethods = new ArrayList<Method>();
		StringTokenizer tk = new StringTokenizer(propertyName, ".");
		// split on '.'
		Class< ? > currentClass = cls;
		while (tk.hasMoreTokens())
		{
			String currentProperty = tk.nextToken();
			Method m = findMethod(currentProperty, currentClass);
			currentClass = m.getReturnType();
			internalAllMethods.add(m);
		}
	}

	/**
	 * find method.
	 * 
	 * @param propertyName
	 *            property name
	 * @param cls
	 *            class
	 * @return Method the method
	 * @throws NoSuchFieldException
	 *             when field could not be found
	 */
	protected Method findMethod(String propertyName, Class< ? > cls) throws NoSuchFieldException
	{
		Method m = null;
		String methodName =
			"get" + propertyName.substring(0, 1).toUpperCase()
				+ propertyName.substring(1, propertyName.length());
		try
		{
			m = cls.getMethod(methodName);
		}
		catch (NoSuchMethodException e1)
		{
			// give the booleans a chance
			try
			{
				methodName =
					"is" + propertyName.substring(0, 1).toUpperCase()
						+ propertyName.substring(1, propertyName.length());
				m = cls.getMethod(methodName);
			}
			catch (NoSuchMethodException e2)
			{
				throw new NoSuchFieldException(cls.getName() + "->" + propertyName);
			}
		}
		return m;
	}

	/**
	 * get Value on Object.
	 * 
	 * @param object
	 *            the object
	 * @return Object
	 * @exception IllegalAccessException
	 *                if this <code>Method</code> object enforces Java language access
	 *                control and the underlying method is inaccessible.
	 * @exception IllegalArgumentException
	 *                if the method is an instance method and the specified object
	 *                argument is not an instance of the class or interface declaring the
	 *                underlying method (or of a subclass or implementor thereof); if the
	 *                number of actual and formal parameters differ; if an unwrapping
	 *                conversion for primitive arguments fails; or if, after possible
	 *                unwrapping, a parameter value cannot be converted to the
	 *                corresponding formal parameter type by a method invocation
	 *                conversion.
	 * @exception InvocationTargetException
	 *                if the underlying method throws an exception.
	 * @exception NoSuchFieldException
	 *                if the field could not be found
	 */
	protected Object getPropertyValueOnObject(Object object) throws NoSuchFieldException,
			IllegalAccessException, InvocationTargetException
	{
		if (object == null)
		{
			return null;
		}
		Object currentObject = object;

		for (int i = 0; i < internalAllMethods.size(); i++)
		{
			Method m = internalAllMethods.get(i);
			currentObject = m.invoke(currentObject, args);
			if (currentObject == null)
				return null;
		}
		return currentObject;
	}

	/**
	 * @see Comparator#compare(Object, Object)
	 */
	@Override
	public int compare(Object o1, Object o2)
	{
		try
		{
			Object o1Return = getPropertyValueOnObject(o1);
			Object o2Return = getPropertyValueOnObject(o2);
			// Both null then equal
			if (o1Return == null && o2Return == null)
				return 0;
			// o1 == null then o1 is greater
			if (o1Return == null)
				return 1 * internalIsAscending;
			// o2 == null then o2 is greater
			if (o2Return == null)
				return -1 * internalIsAscending;
			// else let them decide
			return ((Comparable) o1Return).compareTo(o2Return) * internalIsAscending;
		}
		catch (NoSuchFieldException e)
		{
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		catch (IllegalAccessException e)
		{
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		catch (InvocationTargetException e)
		{
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
}