/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003
 * All rights reserved.
 */
package nl.openedge.maverick.framework.converters;

import java.util.Locale;

/**
 * a Formatter that uses a locale.
 * @author Eelco Hillenius
 */
public interface LocaleFormatter extends Formatter
{
	/**
	 * set the locale for this instance
	 * @param locale the locale for this instance
	 */
	public void setLocale(Locale locale);
	
	/**
	 * get the locale for this instance
	 * @param locale the locale for this instance
	 * @return Locale the locale for this instance
	 */
	public Locale getLocale();
}
