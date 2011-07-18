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
 * Comparator with static helper methods for comparing and constructing dates where only
 * the year/month/day fields are relevant (the time fields are actually reset).
 * 
 * @author Eelco Hillenius
 */
public final class YearMonthDayHelper implements Comparator<Date>
{
	/**
	 * We use this property to be able to fix 'today' on a date for testing.
	 */
	private static Date today = null;

	/**
	 * Compare date1 with date2 and return -1, 0 (zero) or 1 if date1 is before, the same,
	 * or after date2.
	 * 
	 * @param date1
	 *            first date
	 * @param date2
	 *            second date
	 * @return int -1, 0 (zero) or 1 if date1 is before, the same, or after date2
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Date date1, Date date2)
	{
		return internalCompare(date1, date2);
	}

	/**
	 * Compare date1 with date2 and return -1, 0 (zero) or 1 if date1 is before, the same,
	 * or after date2.
	 * 
	 * @param date1
	 *            first date
	 * @param date2
	 *            second date
	 * @return int -1, 0 (zero) or 1 if date1 is before, the same, or after date2
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	protected static int internalCompare(Date date1, Date date2)
	{
		if (date1 == null || date2 == null)
			throw new IllegalArgumentException("ongeldige argumenten: " + date1 + ", " + date2);

		return stripTime(date1).compareTo(stripTime(date2));
	}

	private static Date stripTime(Date dateWithTime)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateWithTime);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * Construct a date object based on the given parameters.
	 * 
	 * @param year
	 *            the year
	 * @param month
	 *            month (january = 1, february = 2; note that this differs from Calendar
	 *            that counts months from 0 (zero) to 11).
	 * @param dayInMonth
	 *            day in month
	 * @return Date date object based on the given parameters
	 */
	public static Date getDate(int year, int month, int dayInMonth)
	{
		Calendar calendar = getCalendar(year, month, dayInMonth);
		return calendar.getTime();
	}

	/**
	 * Construct a calendar object based on the given parameters.
	 * 
	 * @param year
	 *            the year
	 * @param month
	 *            month (january = 1, february = 2; note that this differs from Calendar
	 *            that counts months from 0 (zero) to 11).
	 * @param dayInMonth
	 *            day in month
	 * @return Calendar calendar object based on the given parameters
	 */
	public static Calendar getCalendar(int year, int month, int dayInMonth)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, getCalendarMonth(month));
		calendar.set(Calendar.DAY_OF_MONTH, dayInMonth);
		return calendar;
	}

	/**
	 * Whether date1 is after date2 disregarding the time fields.
	 * 
	 * @param date1
	 *            first date
	 * @param date2
	 *            second date
	 * @return boolean Whether date1 is after date2 disregarding the time fields
	 */
	public static boolean after(Date date1, Date date2)
	{
		return (internalCompare(date1, date2) > 0);
	}

	/**
	 * Whether date1 is after or on date2 disregarding the time fields.
	 * 
	 * @param date1
	 *            first date
	 * @param date2
	 *            second date
	 * @return boolean Whether date1 is after or on date2 disregarding the time fields
	 */
	public static boolean afterOrSame(Date date1, Date date2)
	{
		return (internalCompare(date1, date2) >= 0);
	}

	/**
	 * Whether date1 is before date2 disregarding the time fields.
	 * 
	 * @param date1
	 *            first date
	 * @param date2
	 *            second date
	 * @return boolean Whether date1 is before date2 disregarding the time fields
	 */
	public static boolean before(Date date1, Date date2)
	{
		return (internalCompare(date1, date2) < 0);
	}

	/**
	 * Whether date1 is before or on date2 disregarding the time fields.
	 * 
	 * @param datum1
	 *            first date
	 * @param datum2
	 *            second date
	 * @return boolean Whether date1 is before or on date2 disregarding the time fields
	 */
	public static boolean beforeOrSame(Date datum1, Date datum2)
	{
		return (internalCompare(datum1, datum2) <= 0);
	}

	/**
	 * Whether date1 is on (the same as) date2 disregarding the time fields.
	 * 
	 * @param datum1
	 *            eerste datum
	 * @param datum2
	 *            tweede datum
	 * @return boolean Whether date1 is on (the same as) date2 disregarding the time
	 *         fields
	 */
	public static boolean same(Date datum1, Date datum2)
	{
		return (internalCompare(datum1, datum2) == 0);
	}

	/**
	 * The Calendar component counts months from 0 (zero) till 11, but we want months from
	 * 1 till 12, give the Calendar month from our month.
	 * 
	 * @param yearMonthDayMonth
	 *            our month (1 - 12)
	 * @return int converted month (yearMonthDayMonth - 1)
	 */
	public static int getCalendarMonth(int yearMonthDayMonth)
	{
		return yearMonthDayMonth - 1;
	}

	/**
	 * The Calendar component counts months from 0 (zero) till 11, but we want months from
	 * 1 till 12, give the our month from the calendar month.
	 * 
	 * @param calendarMonth
	 *            calendar month (0 - 11)
	 * @return int converted month (calendarMonth + 1)
	 */
	public static int getYearMonthDayMonth(int calendarMonth)
	{
		return calendarMonth + 1;
	}

	/**
	 * Give todays' date with the time fields cleared. If static variable 'today' is set,
	 * that will be returned - use this only for test purposes!
	 * 
	 * @return Date todays' date with the time fields cleared
	 */
	public static Date getToday()
	{
		if (today == null)
		{
			Calendar calendar = Calendar.getInstance();
			int jaar = calendar.get(Calendar.YEAR);
			int maand = calendar.get(Calendar.MONTH);
			int dag = calendar.get(Calendar.DAY_OF_MONTH);
			calendar.clear(); // clear all fields
			calendar.set(jaar, maand, dag); // and set fields we are interested in
			return calendar.getTime();
		}
		else
		{
			return today; // return test property
		}
	}

	/**
	 * Set dummy today; we use this property to be able to fix 'today' on a date for
	 * testing.
	 * 
	 * @param dummyToday
	 *            dummy today
	 */
	public static void setToday(Date dummyToday)
	{
		today = dummyToday;
	}
}
