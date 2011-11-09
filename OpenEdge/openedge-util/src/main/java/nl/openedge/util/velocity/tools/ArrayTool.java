package nl.openedge.util.velocity.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eelco Hillenius Select an array element This is deliberately NOT supported in
 *         Velocity, but I want it anyway ;-)
 */
public class ArrayTool
{

	/**
	 * Get the element at the given index.
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
	 * wrap as list.
	 * 
	 * @param o1
	 *            object to wrap
	 * @return object wrapped in a list
	 */
	public static List asList(Object o1)
	{
		List l = new ArrayList(1);
		l.add(o1);
		return l;
	}

	/**
	 * wrap as list.
	 * 
	 * @param o1
	 *            object to wrap
	 * @param o2
	 *            object to wrap
	 * @return objects wrapped in a list
	 */
	public static List asList(Object o1, Object o2)
	{
		List l = new ArrayList(1);
		l.add(o1);
		l.add(o2);
		return l;
	}

	/**
	 * wrap as list.
	 * 
	 * @param o1
	 *            object to wrap
	 * @param o2
	 *            object to wrap
	 * @param o3
	 *            object to wrap
	 * @return objects wrapped in a list
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
	 * wrap as list.
	 * 
	 * @param o1
	 *            object to wrap
	 * @param o2
	 *            object to wrap
	 * @param o3
	 *            object to wrap
	 * @param o4
	 *            object to wrap
	 * @return objects wrapped in a list
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
	 *            array to get the size of
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
	 *            the array to get the last element from
	 * @return the last object in the array or null if the array is null or has no
	 *         elements
	 */
	public static Object getLast(Object[] array)
	{
		if (array == null || array.length == 0)
		{
			return null;
		}
		else
		{
			return elementAt(array, array.length - 1);
		}
	}

	/**
	 * pretty print the contents of an array.
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
	 * pretty print the contents of a Collection.
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
