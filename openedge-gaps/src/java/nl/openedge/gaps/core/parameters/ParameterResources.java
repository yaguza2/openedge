/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Hulp klasse voor het ophalen van teksten.
 * @see java.util.ResourceBundle
 */
public final class ParameterResources
{

	/** Resource bundle. */
	private static final ResourceBundle RESOURCES;

	static
	{
		Package pck = ParameterResources.class.getPackage();
		RESOURCES = ResourceBundle.getBundle(pck.getName() + ".RESOURCES");
	}

	/** Log. */
	private static Log log = LogFactory.getLog(ParameterResources.class);

	/**
	 * Verborgen utility constructor.
	 */
	private ParameterResources()
	{
		//
	}

	/**
	 * Geeft tekst uit RESOURCES.properties voor de gegeven key.
	 * @param key key waarbij de tekst wordt opgezocht
	 * @return tekst bij gegeven key of '!' + key + '!' indien niet gevonden
	 * @see java.util.ResourceBundle
	 */
	public static String getText(String key)
	{
		String text = null;
		try
		{
			text = RESOURCES.getString(key);
		}
		catch (MissingResourceException e)
		{
			log.warn(e.getMessage());
		}
		if (text == null)
		{
			text = '!' + key + '!';
		}
		return text;
	}

	/**
	 * Geeft tekst uit RESOURCES.properties voor de gegeven key nadat de gevonden tekst is
	 * geformatteerd mbv de gegeven parameters (params).
	 * @param key key waarbij de tekst wordt opgezocht
	 * @param params parameters die in de tekst kunnen worden gebruikt
	 * @return tekst bij gegeven key
	 * @see java.text.MessageFormat
	 * @see java.util.ResourceBundle
	 */
	public static String getText(String key, Object[] params)
	{
		String txt = getText(key);
		txt = MessageFormat.format(txt, params);
		return txt;
	}

}