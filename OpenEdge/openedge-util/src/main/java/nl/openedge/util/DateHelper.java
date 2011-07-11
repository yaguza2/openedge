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
import java.util.Date;

/**
 * Utility class that makes working with Dates a bit easier.
 * 
 * @author Eelco Hillenius
 */
public final class DateHelper
{

	/**
	 * Hidden constructor.
	 */
	private DateHelper()
	{
		// nothing here
	}

	/**
	 * Add years (possibly negative) to given date and return result.
	 * 
	 * @param date
	 *            date to work on
	 * @param years
	 *            function arg
	 * @param lenient
	 *            whether or not date/time interpretation is to be lenient
	 * @return Date new date
	 */
	public static Date addYears(Date date, int years, boolean lenient)
	{
		Calendar cal = getCalendar(date, lenient);
		cal.add(Calendar.YEAR, years);
		return cal.getTime();
	}

	/**
	 * Add months (possibly negative) to given date and return result.
	 * 
	 * @param date
	 *            date to work on
	 * @param months
	 *            function arg
	 * @param lenient
	 *            whether or not date/time interpretation is to be lenient
	 * @return Date new date
	 */
	public static Date addMonths(Date date, int months, boolean lenient)
	{
		Calendar cal = getCalendar(date, lenient);
		cal.add(Calendar.MONTH, months);
		return cal.getTime();
	}

	/**
	 * Add days (possibly negative) to given date and return result.
	 * 
	 * @param date
	 *            date to work on
	 * @param days
	 *            function arg
	 * @param lenient
	 *            whether or not date/time interpretation is to be lenient
	 * @return Date new date
	 */
	public static Date addDaysInMonth(Date date, int days, boolean lenient)
	{
		Calendar cal = getCalendar(date, lenient);
		cal.add(Calendar.DAY_OF_MONTH, days);
		return cal.getTime();
	}

	/**
	 * Add hours (possibly negative) to given date and return result.
	 * 
	 * @param date
	 *            date to work on
	 * @param hours
	 *            function arg
	 * @param lenient
	 *            whether or not date/time interpretation is to be lenient
	 * @return Date new date
	 */
	public static Date addHours(Date date, int hours, boolean lenient)
	{
		Calendar cal = getCalendar(date, lenient);
		cal.add(Calendar.HOUR_OF_DAY, hours);
		return cal.getTime();
	}

	/**
	 * Add minutes (possibly negative) to given date and return result.
	 * 
	 * @param date
	 *            date to work on
	 * @param minutes
	 *            function arg
	 * @param lenient
	 *            whether or not date/time interpretation is to be lenient
	 * @return Date new date
	 */
	public static Date addMinutes(Date date, int minutes, boolean lenient)
	{
		Calendar cal = getCalendar(date, lenient);
		cal.add(Calendar.MINUTE, minutes);
		return cal.getTime();
	}

	/**
	 * Add seconds (possibly negative) to given date and return result.
	 * 
	 * @param date
	 *            date to work on
	 * @param seconds
	 *            function arg
	 * @param lenient
	 *            whether or not date/time interpretation is to be lenient
	 * @return Date new date
	 */
	public static Date addSeconds(Date date, int seconds, boolean lenient)
	{
		Calendar cal = getCalendar(date, lenient);
		cal.add(Calendar.SECOND, seconds);
		return cal.getTime();
	}

	/**
	 * Add miliseconds (possibly negative) to given date and return result.
	 * 
	 * @param date
	 *            date to work on
	 * @param miliseconds
	 *            function arg
	 * @param lenient
	 *            whether or not date/time interpretation is to be lenient
	 * @return Date new date
	 */
	public static Date addMiliseconds(Date date, int miliseconds, boolean lenient)
	{
		Calendar cal = getCalendar(date, lenient);
		cal.add(Calendar.MILLISECOND, miliseconds);
		return cal.getTime();
	}

	/**
	 * Get year field of date.
	 * 
	 * @param date
	 *            date to work with
	 * @return int result
	 */
	public static int getYear(Date date)
	{
		Calendar cal = getCalendar(date, false);
		return cal.get(Calendar.YEAR);
	}

	/**
	 * Get month field of date.
	 * 
	 * @param date
	 *            date to work with
	 * @return int result
	 */
	public static int getMonth(Date date)
	{
		Calendar cal = getCalendar(date, false);
		return cal.get(Calendar.MONTH);
	}

	/**
	 * Get year field of date.
	 * 
	 * @param date
	 *            date to work with
	 * @return int result
	 */
	public static int getDayOfMonth(Date date)
	{
		Calendar cal = getCalendar(date, false);
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Get hour field of date.
	 * 
	 * @param date
	 *            date to work with
	 * @return int result
	 */
	public static int getHourOfDay(Date date)
	{
		Calendar cal = getCalendar(date, false);
		return cal.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * Get minute field of date.
	 * 
	 * @param date
	 *            date to work with
	 * @return int result
	 */
	public static int getMinute(Date date)
	{
		Calendar cal = getCalendar(date, false);
		return cal.get(Calendar.MINUTE);
	}

	/**
	 * Get second field of date.
	 * 
	 * @param date
	 *            date to work with
	 * @return int result
	 */
	public static int getSecond(Date date)
	{
		Calendar cal = getCalendar(date, false);
		return cal.get(Calendar.SECOND);
	}

	/**
	 * Get milisecond field of date.
	 * 
	 * @param date
	 *            date to work with
	 * @return int result
	 */
	public static int getMilisecond(Date date)
	{
		Calendar cal = getCalendar(date, false);
		return cal.get(Calendar.MILLISECOND);
	}

	/**
	 * Get Calendar.
	 * 
	 * @param date
	 *            date to set in calendar.
	 * @param lenient
	 *            whether or not date/time interpretation is to be lenient
	 * @return Calendar with date.
	 */
	private static Calendar getCalendar(Date date, boolean lenient)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.setLenient(lenient);
		return cal;
	}
}
