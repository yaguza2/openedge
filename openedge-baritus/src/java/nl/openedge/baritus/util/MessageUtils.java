/*
 * $Id: MessageUtils.java,v 1.2 2004-03-29 15:26:51 eelco12 Exp $
 * $Revision: 1.2 $
 * $Date: 2004-03-29 15:26:51 $
 *
 * ====================================================================
 * Copyright (c) 2003
 * All rights reserved.
 */
package nl.openedge.baritus.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class for handling localized messages.
 * 
 * @author Eelco Hillenius
 */
public final class MessageUtils
{

	private static Log log = LogFactory.getLog(MessageUtils.class);

	/**
	 * Get localized message for given key.
	 * @param key key of message
	 * @return String localized message
	 */
	public static String getLocalizedMessage(String key)
	{
		return getLocalizedMessage(key, Locale.getDefault());
	}

	/**
	 * Get localized message for given key and locale. 
	 * If locale is null, the default locale will be used.
	 * @param key key of message
	 * @param locale locale for message
	 * @return String localized message
	 */
	public static String getLocalizedMessage(String key, Locale locale)
	{
		String msg = null;
		try
		{
			msg = getBundle(locale).getString(key);
		}
		catch (Exception e)
		{
			if (log.isDebugEnabled())
			{
				log.error(e.getMessage(), e);
			}
		}
		return msg;
	}

	/**
	 * Get localized message for given key and locale
	 * and format it with the given parameters. 
	 * If locale is null, the default locale will be used.
	 * @param key key of message
	 * @param parameters parameters for the message
	 * @return String localized message
	 */
	public static String getLocalizedMessage(String key, Object[] parameters)
	{
		return getLocalizedMessage(key, null, parameters);
	}

	/**
	 * Get localized message for given key and locale
	 * and format it with the given parameters. 
	 * If locale is null, the default locale will be used.
	 * @param key key of message
	 * @param locale locale for message
	 * @param parameters parameters for the message
	 * @return String localized message
	 */
	public static String getLocalizedMessage(
		String key,
		Locale locale,
		Object[] parameters)
	{
		String msg = null;
		try
		{
			msg = getBundle(locale).getString(key);
			msg = MessageFormat.format(msg, parameters);
		}
		catch (Exception e)
		{
			if (log.isDebugEnabled())
			{
				log.error(e.getMessage(), e);
			}
		}
		return msg;
	}

	/* get resource bundle */
	private static ResourceBundle getBundle(Locale locale)
	{
		ResourceBundle res = null;
		if (locale != null)
		{
			res = ResourceBundle.getBundle("resources", locale);
		}
		else
		{
			res = ResourceBundle.getBundle("resources");
		}
		return res;
	}

}
