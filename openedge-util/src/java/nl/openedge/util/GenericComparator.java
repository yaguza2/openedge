/*
 * $Header$
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
import java.util.*;

/**
 * @author	Johan Compagner
 * @author	Eelco Hillenius
 */
public class GenericComparator implements Comparator {
	
	private ArrayList _alMethods;
	private int _iAscending;
	private static Object[] args = new Object[0];

	/**
	 * constructor
	 * @param cls
	 * @param sField
	 * @param bAscending
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 */
	public GenericComparator(Class cls, String sField, boolean bAscending) throws NoSuchFieldException 
	{
		fillMethods(sField, cls);
		if(bAscending) {
			_iAscending = 1;
		}
		else {
			_iAscending = -1;
		}
	}
	
	/**
	 * get method for property
	 * @param name		name property
	 * @param object	object to test on
	 * @return Method that can be called directely
	 */
	protected void fillMethods(String propertyName, Class cls)
						throws NoSuchFieldException
	{
		_alMethods = new ArrayList();
		StringTokenizer tk = new StringTokenizer(propertyName,"."); // split on '.'
		Class currentClass = cls;
		while(tk.hasMoreTokens()) {
			String currentProperty = tk.nextToken();
			Method m = findMethod(currentProperty, currentClass);
			currentClass = m.getReturnType();
			_alMethods.add(m);
		}
	}
	
	
	/**
	 * find method
	 * @param propertyName
	 * @param cls
	 * @return Method
	 * @throws NoSuchFieldException
	 */
	protected Method findMethod(String propertyName, Class cls)
				throws NoSuchFieldException {

		Method m = null;
		String methodName = "get" + propertyName.substring(0,1).toUpperCase() +
					propertyName.substring(1,propertyName.length());
		try {
			m = cls.getMethod(methodName, null);
		} catch(Exception e1) {
			// give the booleans a chance
			try {
				methodName = "is" + propertyName.substring(0,1).toUpperCase() +
							propertyName.substring(1,propertyName.length());
				m = cls.getMethod(methodName, null);
			} catch(Exception e2) {
				throw new NoSuchFieldException(cls.getName() + "->" + propertyName);
			}
		}
		return m;
	}

	/**
	 * get Value on Object
	 * @param o
	 * @return Object
	 * @throws NoSuchFieldException
	 */
	protected Object getPropertyValueOnObject(Object o) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException 
	{
		if(o == null) return o;
		Object currentObject = o;
		
		for (int i = 0; i < _alMethods.size(); i++)
		{
			Method m = (Method) _alMethods.get(i);
			currentObject = m.invoke(currentObject, args);
			if( currentObject == null)
				return null;
		}		
		return currentObject;	
	}
	
	/**
	 * @see Comparator#compare(Object, Object)
	 */
	public int compare(Object o1, Object o2) {
		
		try {
			
			Object o1Return = getPropertyValueOnObject(o1);
			Object o2Return = getPropertyValueOnObject(o2);
			// Both null then equal
			if(o1Return == null && o2Return == null) return 0;
			// o1 == null then o1 is greater
			if(o1Return == null) return 1*_iAscending;
			// o2 == null then o2 is greater
			if(o2Return == null) return -1*_iAscending;
			// else let them decide
			return ((Comparable)o1Return).compareTo(o2Return)*_iAscending;
		} catch(Exception e) {
			
			System.err.println("sorting failed:" + o1.getClass().getName() + 
								" compared to " + o2.getClass().getName());
			e.printStackTrace();
		}
		return 0;
	}
}
