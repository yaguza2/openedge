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
package nl.openedge.maverick.framework.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eelco Hillenius
 * Select an array element
 * This is deliberately NOT supported in Velocity, but I want it anyway ;-)
 */
public class ArrayTool
{

	/**
	 * Get the element at the given index
	 * @param array Object array
	 * @param index index of element
	 * @return Object at position index or null
	 */
	public static Object elementAt(Object[] array, int index)
	{

		if (array == null)
			return null;
		int length = array.length;
		if (index < 0 || index > (length - 1))
			return null;
		return array[index];
	}

	/**
	 * wrap as list
	 * @param o1
	 * @return
	 */
	public static List asList(Object o1)
	{
		List l = new ArrayList(1);
		l.add(o1);
		return l;
	}

	/**
	 * wrap as list
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static List asList(Object o1, Object o2)
	{
		List l = new ArrayList(1);
		l.add(o1);
		l.add(o2);
		return l;
	}

	/**
	 * wrap as list
	 * @param o1
	 * @param o2
	 * @param o3
	 * @return
	 */
	public static List asList(Object o1, Object o2, Object o3)
	{
		List l = new ArrayList(1);
		l.add(o1);
		l.add(o2);
		l.add(o3);
		return l;
	}

	/**
	 * wrap as list
	 * @param o1
	 * @param o2
	 * @param o3
	 * @param o4
	 * @return
	 */
	public static List asList(Object o1, Object o2, Object o3, Object o4)
	{
		List l = new ArrayList(1);
		l.add(o1);
		l.add(o2);
		l.add(o3);
		l.add(o4);
		return l;
	}

}