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
package nl.openedge.util.velocity.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eelco Hillenius Select an array element This is deliberately NOT supported in Velocity,
 *         but I want it anyway ;-)
 */
public class ArrayTool
{

	/**
	 * Get the element at the given index
	 * 
	 * @param array
	 *            Object array
	 * @param index
	 *            index of element
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
	 * 
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
	 * 
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
	 * 
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
	 * 
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

	/**
	 * the length of the input array.
	 * 
	 * @param array
	 * @return the length of the input array. if array == null return -1.
	 */
	public static int size(Object[] array)
	{
		if (array == null)
		{
			return -1;
		}
		return array.length;
	}

	/**
	 * Uses elementAt to return the last element in array.
	 * 
	 * @param array
	 * @return
	 */
	public static Object getLast(Object[] array)
	{
		return elementAt(array, array.length - 1);
	}

	/**
	 * pretty print the contents of an array
	 * 
	 * @param array
	 *            array
	 * @return String
	 */
	public static String printArray(Object[] array)
	{
		if (array == null)
			return "null";
		StringBuffer b = new StringBuffer("{");
		int length = array.length;
		for (int i = 0; i < length; i++)
		{
			b.append(array[i]);
			if ((i + 1) < length)
				b.append(", ");
		}
		b.append("}");
		return b.toString();
	}

	/**
	 * pretty print the contents of a Collection
	 * 
	 * @param c
	 *            collection
	 * @return String
	 */
	public static String printCollection(Collection c)
	{
		if (c == null)
			return "null";
		StringBuffer b = new StringBuffer("{");
		for (Iterator i = c.iterator(); i.hasNext();)
		{
			b.append(i.next());
			if (i.hasNext())
				b.append(", ");
		}
		b.append("}");
		return b.toString();
	}
}