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

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

/**
 * Compares java Dates on a date level, without time. The comparator checks year, month,
 * day and returns -1 or 0 or 1. This component is not Thread safe.
 * 
 * @author shofstee
 */
public class DateComparator implements Comparator
{

	// as we use instance variables, this component is not Thread safe.
	/** Calendar working object 1. */
	private Calendar cal1 = Calendar.getInstance();

	/** Calendar working object 2. */
	private Calendar cal2 = Calendar.getInstance();

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Object o1, Object o2)
	{
		int result = 0;

		if (o1 == null || o2 == null)
		{
			throw new IllegalArgumentException("Cannot compare with null.");
		}
		if (!(o1 instanceof Date && o2 instanceof Date))
		{
			handleClassCastException(o1, o2);
		}

		cal1.setTime((Date) o1);
		cal2.setTime((Date) o2);

		int yearResult = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR);
		int monthResult = cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH);
		int dayResult = cal2.get(Calendar.DATE) - cal1.get(Calendar.DATE);

		result = compareFields(yearResult, monthResult, dayResult);

		return result;
	}

	/**
	 * Compare the fields.
	 * 
	 * @param yearResult
	 *            the year result
	 * @param monthResult
	 *            the month result
	 * @param dayResult
	 *            the day result
	 * @return int result
	 */
	private int compareFields(int yearResult, int monthResult, int dayResult)
	{
		int result = 0;
		if (yearResult != 0)
		{
			if (yearResult > 0)
			{
				result = 1;
			}
			else
			{
				result = -1;
			}
		}
		else if (monthResult != 0)
		{
			if (monthResult > 0)
			{
				result = 1;
			}
			else
			{
				result = -1;
			}
		}
		else if (dayResult != 0)
		{
			if (dayResult > 0)
			{
				result = 1;
			}
			else
			{
				result = -1;
			}
		}
		return result;
	}

	/**
	 * Throws a ClassCastException with the parameter that is not instanceof Date checks
	 * o1 then o2.
	 * 
	 * @param o1
	 *            first object
	 * @param o2
	 *            second object
	 */
	private void handleClassCastException(Object o1, Object o2)
	{
		if (!(o1 instanceof Date))
		{
			throw new ClassCastException("Cannot convert object " + o1 + " to Date");
		}
		else
		{
			throw new ClassCastException("Cannot convert object " + o2 + " to Date");
		}

	}

}
