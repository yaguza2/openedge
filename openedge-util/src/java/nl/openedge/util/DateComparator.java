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

import java.util.Comparator;
import java.util.Date;

/**
 * Compares java Dates on a date level, without time.
 * The comparator checks year, month, day and returns
 * -1 or 0 or 1.
 * 
 * @author shofstee
 */
public class DateComparator implements Comparator
{
	
	/**
	 * Checks year then month then day of month.
	 * @return -1 or 0 or 1
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 * @throws IllegalArgumentException when o1 or o2 is null
	 * @throws ClassCastException when o1 or o2 not instance of java.sql.Date
	 */
	public int compare(Object o1, Object o2)
	{
		int result = 0;
		Date date1 = null;
		Date date2 = null;
		
		if (o1 == null || o2 == null)
		{
			throw new IllegalArgumentException("Cannot compare with null.");
		}
		if (!(o1 instanceof Date || o2 instanceof Date))
		{
			handleClassCastException(o1, o2);
		}
		
		date1 = (Date) o1;
		date2 = (Date) o2;
		
		int yearResult = date2.getYear() - date1.getYear();
		int monthResult = date2.getMonth() - date1.getMonth();
		int dayResult = date2.getDate() - date1.getDate();
		
		if (yearResult != 0)
		{
			result = yearResult > 0? 1: -1;
		}
		else if (monthResult != 0)
		{
			result = monthResult > 0? 1: -1;
		}
		else if (dayResult != 0) 
		{
			result = dayResult > 0? 1: -1;
		}
		
		return result;
	}

	/**
	 * Throws a ClassCastException with the parameter that is not instanceof Date
	 * checks o1 then o2.
	 * @param o1
	 * @param o2
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
