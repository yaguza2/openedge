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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Sander Hofstee
 * @author Eelco Hillenius Helper for parsing dates in multiple formats, currently for non
 *         localized. Loads formats defined in 'dateformathelper.cfg' in classpath root, or from
 *         'dateformathelper.default.cfg' in this package as a fallthrough.
 */
public final class DateFormatHelper
{
	/** List of formatters. */
	private static LinkedHashMap formatters = new LinkedHashMap();

	/** The default formatter. */
	private static SimpleDateFormat defaultFormatter = null;

	/** string for default formatter. */
	private static String defaultFormatterString = null;

	/** test for unwanted characters. */
	private static boolean checkForCharacters = true;

	/** test for unwanted length of dd, MM, yyyy. */
	private static boolean checkForSize = true;

	/** Log. */
	private static Log log = LogFactory.getLog(DateFormatHelper.class);

	/** Special var to check years. */
	private static final int MAX_YEARS = 9999;

	static
	{
		loadFormatters();
	}

	/**
	 * load formatters from config file.
	 */
	private static void loadFormatters()
	{
		try
		{
			loadFromFile("/dateformathelper.cfg");
		}
		catch (IOException e)
		{
			log.warn("unable to load /dateformathelper.cfg from the classpath root \n("
					+ e.getMessage() + "); using defaults");
			try
			{
				loadFromFile("dateformathelper.default.cfg");
			}
			catch (IOException e2)
			{
				log.error(e2.getMessage(), e);
			}
		}

	}

	/**
	 * load from configuration file ALLE formatters isLenient(false).
	 * 
	 * @param filename
	 *            file to load
	 * @throws IOException
	 *             when the file cannot be loaded
	 */
	private static void loadFromFile(String filename) throws IOException
	{
		if (filename == null)
		{
			throw new IOException("cannot load file 'null'");
		}
		InputStream is = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader != null)
		{ // try to load with context class loader
			is = loader.getResourceAsStream(filename);
		}
		if ((loader == null) || (is == null))
		{ // first classloader fallthrough
			is = DateFormatHelper.class.getResourceAsStream(filename);
		}
		if (is == null)
		{ // still null? try a hack
			if (!filename.startsWith("/"))
			{
				loadFromFile("/" + filename);
			} // else... give up
		}

		if (is == null)
		{
			throw new IOException("unable to load " + filename + " from classpath");
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String format = new String();
		SimpleDateFormat formatter;
		boolean defaultSet = false;
		while ((format = br.readLine()) != null)
		{
			if ((!format.startsWith("#")) && (!format.trim().equals("")))
			{
				formatter = createSimpleDateFormat(format);
				formatters.put(format, formatter);
				if (!defaultSet)
				{
					defaultFormatter = formatter;
					defaultFormatterString = format;
					defaultSet = true;
				}
			}
		}
	}

	/**
	 * Get all formatters.
	 * 
	 * @return Map all registerd formatters
	 */
	public static Map getFormatters()
	{
		return formatters;
	}

	/**
	 * Formats a string according to the given format.
	 * 
	 * @param format
	 *            format like you would use with SimpleDateFormat
	 * @param date
	 *            the date to format
	 * @return String formatted date
	 * @see SimpleDateFormat
	 */
	public static String format(String format, Date date)
	{
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(date);
	}

	/**
	 * Formats a string according to the given format.
	 * 
	 * @param format
	 *            format like you would use with SimpleDateFormat
	 * @param time
	 *            the long date to format
	 * @return String formatted date
	 * @see SimpleDateFormat
	 */
	public static String format(String format, long time)
	{
		return format(format, new Date(time));
	}

	/**
	 * Formats a string according to the default format.
	 * 
	 * @param date
	 *            date to format
	 * @return String formatted date
	 * @see SimpleDateFormat
	 */
	public static String format(Date date)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(defaultFormatter.toPattern());
		return sdf.format(date);
	}

	/**
	 * Formats a string according to the default format.
	 * 
	 * @param time
	 *            the long date to format
	 * @return String formatted date
	 * @see SimpleDateFormat
	 */
	public static String format(long time)
	{
		return format(new Date(time));
	}

	/**
	 * Parses a string for date information. Multiple patterns are tried. Stops matching after the
	 * first match.
	 * 
	 * @param stringDate
	 *            date as a string
	 * @return Date parsed date or null if input was null or empty string
	 * @throws ParseException
	 *             when the stringdate could not be parsed
	 */
	public static Date fallbackParse(String stringDate) throws ParseException
	{
		if (stringDate == null || "".equals(stringDate.trim()))
		{
			return null;
		}

		Iterator i = formatters.values().iterator();
		SimpleDateFormat sdf = null;
		Date date = null;

		while (i.hasNext() && date == null)
		{
			try
			{
				sdf = (SimpleDateFormat) i.next();
				if (stringDate.length() == sdf.toPattern().length())
				{
					date = parse(stringDate, sdf);
				}
			}
			catch (CheckException e)
			{
				// parsing succeeded, but the extra check failed... fail
				throw new ParseException(e.getMessage(), 0);
			}
			catch (ParseException e)
			{
				// do nothing... try next if available
				if (log.isDebugEnabled())
				{
					log.debug("parsing "
							+ stringDate + " failed for " + sdf.toLocalizedPattern() + " ("
							+ e.getMessage() + ")");
				}
			}
		}
		if (date == null)
		{
			throw new ParseException(stringDate + " is not a valid date", 0);
		}
		else
		{
			return date;
		}
	}

	/**
	 * Parse date using default formatter.
	 * 
	 * @param stringDate
	 *            date as a string
	 * @return Date parsed date
	 * @throws ParseException
	 *             when the stringdate could not be parsed
	 * @throws CheckException
	 *             when the check failed
	 */
	public static Date parse(String stringDate) throws CheckException, ParseException
	{
		return parse(stringDate, defaultFormatter);
	}

	/**
	 * Parse date using default formatter.
	 * 
	 * @param stringDate
	 *            date as a string
	 * @param fallback
	 *            use fallback
	 * @deprecated use fallbackParse
	 * @return Date parsed date
	 * @throws ParseException
	 *             when the stringdate could not be parsed
	 */
	public static Date parse(String stringDate, boolean fallback) throws ParseException
	{
		return fallbackParse(stringDate);
	}

	/**
	 * Parse date using the given format.
	 * 
	 * @param stringDate
	 *            date as a string
	 * @param format
	 *            format
	 * @see java.text.SimpleDateFormat
	 * @return Date parsed date
	 * @throws ParseException
	 *             when the stringdate could not be parsed
	 * @throws CheckException
	 *             when the check failed
	 */
	public static Date parse(String stringDate, String format) throws CheckException,
			ParseException
	{
		DateFormat df = (DateFormat) formatters.get(format);
		if (df == null)
		{
			df = createSimpleDateFormat(format);
		}
		Date date = parse(stringDate, df);
		// no exception? keep dateformat
		formatters.put(format, df);
		return date;
	}

	/**
	 * Parse given stringDate with DateFormat df.
	 * 
	 * @param stringDate
	 *            string rep of date
	 * @param df
	 *            date formatter
	 * @return Date parsed date
	 * @throws ParseException
	 *             when the stringdate could not be parsed
	 * @throws CheckException
	 *             when the check failed
	 */
	protected static synchronized Date parse(String stringDate, DateFormat df)
			throws CheckException, ParseException
	{
		Date date = df.parse(stringDate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		if (checkForCharacters)
		{
			int length = stringDate.length();
			for (int j = 0; j < length; j++)
			{
				char c = stringDate.charAt(j);
				if ((c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z'))
				{
					throw new CheckException(stringDate + " is not a valid date");
				}
			}
		}

		if (checkForSize)
		{
			int year = cal.get(Calendar.YEAR);
			if (year > MAX_YEARS)
			{
				throw new CheckException(stringDate + " has an invalid year, use YY or YYYY");
			}
		}

		return date;
	}

	/**
	 * Create new instance of SimpleDateFormat.
	 * 
	 * @param pattern
	 *            pattern to use for creation
	 * @return SimpleDateFormat date format
	 */
	protected static SimpleDateFormat createSimpleDateFormat(String pattern)
	{
		SimpleDateFormat df = new SimpleDateFormat(pattern.trim());
		df.setLenient(false); // keep interpretation strict
		return df;
	}

	/**
	 * Gets the default format string.
	 * 
	 * @return String default format string
	 */
	public static String getDefaultFormatString()
	{
		return defaultFormatterString;
	}

	/**
	 * Whether to check for unwanted characters.
	 * 
	 * @return boolean whether to check for unwanted characters
	 */
	public static boolean isCheckForCharacters()
	{
		return checkForCharacters;
	}

	/**
	 * Set whether to check for chars.
	 * 
	 * @param b
	 *            whether to check for chars
	 */
	public static void setCheckForCharacters(boolean b)
	{
		checkForCharacters = b;
	}

	/**
	 * Get whether to check for size.
	 * 
	 * @return boolean check for unwanted size?
	 */
	public static boolean isCheckForSize()
	{
		return checkForSize;
	}

	/**
	 * Set whether to check for size.
	 * 
	 * @param b
	 *            whether to check for size
	 */
	public static void setCheckForSize(boolean b)
	{
		checkForSize = b;
	}

}

/**
 * Special internal exception for checks.
 */

class CheckException extends Exception
{
	/**
	 * Construct.
	 * 
	 * @param message
	 *            the message
	 */
	public CheckException(String message)
	{
		super(message);
	}
}