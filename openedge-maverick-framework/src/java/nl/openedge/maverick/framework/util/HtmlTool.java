/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003, Promedico ICT B.V.
 * All rights reserved.
 */
package nl.openedge.maverick.framework.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shofstee
 *
 * Tool that has methods for creating valid HTML.
 */
public class HtmlTool
{

	/**
	 * De Map met encodings bevat de characters waarvoor een andere
	 * string ingevuld moet worden.
	 */
	private static Map encoding;

	static
	{
		/*
		 * Vul de encoding map.
		 */
		encoding = new HashMap();
		encoding.put(new Character('<'), "&lt;");
		encoding.put(new Character('>'), "&gt;");
		encoding.put(new Character('&'), "&amp;");
		encoding.put(new Character('"'), "&#034;");
		encoding.put(new Character('\''), "&#039;");
	}

	/**
	 * Vervangt alle characters in original die ook voorkomen in encoding
	 * met de daaraan gekoppelde waarde.
	 * @param original de string met speciale characters
	 * @return string waarin de speciale characters vervangen zijn door 
	 * 			HTML-vriendelijke characters.
	 */
	public static String parseText(String original)
	{
		StringBuffer encodedStr = new StringBuffer(original.length());		

		for (int i = 0; i < original.length(); i++)
		{
			Character current = new Character(original.charAt(i));

			if (encoding.containsKey(current))
			{
				encodedStr.append(encoding.get(current));
			}
			else
			{
				encodedStr.append(current);
			}
		}

		return encodedStr.toString();
	}
}
