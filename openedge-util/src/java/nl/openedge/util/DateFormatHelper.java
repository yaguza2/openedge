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

import java.io.*;
import java.util.*;
import java.text.*;

/**
 * @author	Eelco Hillenius, Sander Hofstee
 * @version	$Id$
 * 
 * Helper for using SimpleDateFormat. Use for example with Velocity templates
 * 
 */
public final class DateFormatHelper {

	private static HashMap formatters = new HashMap();
	private static SimpleDateFormat defaultFormatter = null;
	private static String defaultFormatterString = null;

	static {
		loadFormatters();	
	}

	/**
	 * constructor
	 */
	public DateFormatHelper() {}
	
	/*
	 * load formatters from config file
	 */
	private static void loadFormatters() {
		
		try {
			loadFromFile("/dateformathelper.cfg");
		}
        catch (Exception e) {
        	System.err.println("");
        	try {
        		loadFromFile("dateformathelper.default.cfg");
        	}
        	catch(Exception e2) {
        		e2.printStackTrace();
        	}
        } 
        	
	}
	
	/*
	 * load from configuration file
	 * ALLE formatters isLenient(false)
	 */
	private static void loadFromFile(String filename) throws IOException
	{
		if(filename == null) {
			throw new IOException("cannot load file 'null'");
		}
		InputStream is = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if(loader != null) { // try to load with context class loader
			is = loader.getResourceAsStream(filename);
		}
		if((loader == null) || (is == null)) { // first classloader fallthrough
			is = DateFormatHelper.class.getResourceAsStream(filename);
		}
		if(is == null) { // still null? try a hack
			if(!filename.startsWith("/")) {
				loadFromFile("/" + filename);
			} // else... give up
		}
		
		if(is == null) {
			throw new IOException("unable to load " + filename + " from classpath");
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
				
		String format = new String();
		SimpleDateFormat formatter;
		boolean defaultSet = false;
		while ((format = br.readLine()) != null) {
			if( (!format.startsWith("#")) &&
				(!format.trim().equals("")) ) {
				formatter = new SimpleDateFormat( format );
				formatter.setLenient(false);
				formatters.put( format, formatter );
				if( !defaultSet ) {
					defaultFormatter = formatter;
					defaultFormatterString = format;
					defaultSet = true;
				}
			}
		}
	}
	
	/**
	 * get all formatters
	 * @return all registerd formatters
	 */
	public static Map getFormatters() {
		return formatters;
	}
	
	/**
	 * formats a string according to the given format
	 * @param format		format like you would use with SimpleDateFormat
	 * @param date			the date to format
	 * @return formatted date
	 * @see SimpleDateFormat
	 */
	public static String format( String format, Date date ) {
		SimpleDateFormat formatter = (SimpleDateFormat)formatters.get( format );	
		if( formatter == null ) {
			formatter = new SimpleDateFormat( format );
			formatters.put( format, formatter );	
		}
		return formatter.format( date );
	}
	
	/**
	 * formats a string according to the given format
	 * @param format		format like you would use with SimpleDateFormat
	 * @param date			the date to format
	 * @return formatted date
	 * @see SimpleDateFormat
	 */
	public static String format( String format, long time ) {
		return format( format, new Date(time) );
	}
	
	/**
	 * formats a string according to the default format
	 * @param date			date to format
	 * @return formatted date
	 * @see SimpleDateFormat
	 */
	public static String format( Date date ) {
		return defaultFormatter.format( date );
	}
		
	/**
	 * formats a string according to the default format
	 * @param date			date to format
	 * @return formatted date
	 * @see SimpleDateFormat
	 */
	public static String format( long time ) {
		return defaultFormatter.format( new Date(time) );
	}
	
	/**
	 * parse date with fallback option
	 * @param stringDate		date as a string
	 * @param fallback			use all formatters before fail
	 * @return parsed date or null if input was null or empty string
	 */
	public static Date fallbackParse( String stringDate ) throws ParseException {
		
		if( stringDate == null || "".equals(stringDate.trim())) {
			return null;
		}

		Iterator i = formatters.values().iterator();
		DateFormat df = null;

		while( i.hasNext() ) {
			try {
				df = (DateFormat)i.next();
				return df.parse( stringDate );
			} catch( ParseException e ) {
				// do nothing... try next if available
			}	
		}
		throw new ParseException( stringDate + " is not a valid date", 0 );
	}
	
	/**
	 * parse date using default formatter
	 * @param stringDate		date as a string
	 * @return parsed date
	 */
	public static Date parse( String stringDate ) throws ParseException {
		return defaultFormatter.parse( stringDate );
	}
	
	/**
	 * parse date using default formatter
	 * @param stringDate		date as a string
	 * @param fallback			use fallback
	 * @deprecated	use fallbackParse
	 * @return parsed date
	 */
	public static Date parse( String stringDate, boolean fallback ) throws ParseException {
		return fallbackParse( stringDate );
	}
	
	/**
	 * parse date using the given format
	 * @param stringDate		date as a string
	 * @param format			format
	 * @see SimpleDateFormat
	 * @return parsed date
	 */
	public static Date parse( String stringDate, String format ) throws ParseException {
		DateFormat df = (DateFormat)formatters.get( format );
		if( df == null ) {
			df = new SimpleDateFormat( format );		
		}
		Date date = df.parse( stringDate );
		// no exception? keep dateformat
		formatters.put( format, df );
		return date;	
	}

	/**
	 * gets the default format string
	 * @return String
	 */
	public static String getDefaultFormatString()
	{
		return defaultFormatterString;
	}
		
	
}