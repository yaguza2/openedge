/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003, Promedico ICT B.V.
 * All rights reserved.
 */
package nl.openedge.util.velocity.tools;

import java.util.HashMap;
import java.util.Map;

/**
 * Tool that has methods for creating valid HTML. It replaces special special HTML and Javascript
 * characters in Java strings to valid HTML and Javascript characters.
 * 
 * @author shofstee
 */
public class HtmlTool
{

	/**
	 * The Map with HTML-encodings the key-characters in this map have to be replaced with the
	 * values to create a valid HTML text.
	 */
	private static Map htmlEncoding;

	/**
	 * The Map with Javascript-encodings the key-characters in this map have to be replaced with the
	 * values to create a valid Javascript text.
	 */
	private static Map javascriptEncoding;

	static
	{
		/*
		 * Vul de htmlEncoding map.
		 */
		htmlEncoding = new HashMap();
		htmlEncoding.put(new Character('<'), "&lt;");
		htmlEncoding.put(new Character('>'), "&gt;");
		htmlEncoding.put(new Character('&'), "&amp;");
		htmlEncoding.put(new Character('"'), "&#034;");
		htmlEncoding.put(new Character('\''), "&#039;");
		htmlEncoding.put(new Character('ë'), "&euml;");
		htmlEncoding.put(new Character('\n'), "<br>");

		javascriptEncoding = new HashMap();
		javascriptEncoding.put(new Character('\''), "\\'");
	}

	/**
	 * Replaces all characters in original with the value that it represents.
	 * 
	 * @param original
	 *            the Java string that has special characters.
	 * @return the HTML safe string with all special characters replaced.
	 */
	public static String parseText(String original)
	{
		if (original == null)
		{
			return original;
		}

		StringBuffer encodedStr = new StringBuffer(original.length());

		for (int i = 0; i < original.length(); i++)
		{
			Character current = new Character(original.charAt(i));

			if (htmlEncoding.containsKey(current))
			{
				encodedStr.append(htmlEncoding.get(current));
			}
			else
			{
				encodedStr.append(current);
			}
		}

		return encodedStr.toString();
	}

	/**
	 * Replaces all characters in original with the value that it represents.
	 * 
	 * @param original
	 *            the Java string that has special characters.
	 * @return the Javascript safe string with all special characters replaced.
	 */
	public static String parseJavascipt(String original)
	{
		if (original == null)
		{
			return original;
		}

		StringBuffer encodedStr = new StringBuffer(original.length());

		for (int i = 0; i < original.length(); i++)
		{
			Character current = new Character(original.charAt(i));

			if (javascriptEncoding.containsKey(current))
			{
				encodedStr.append(javascriptEncoding.get(current));
			}
			else
			{
				encodedStr.append(current);
			}
		}

		return encodedStr.toString();
	}
}