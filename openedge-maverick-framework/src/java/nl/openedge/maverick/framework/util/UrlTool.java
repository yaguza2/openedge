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
package nl.openedge.maverick.framework.util;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Eelco Hillenius
 */
public class UrlTool
{

	/**
	 * Strips a parameter from url
	 */
	public static String stripParameter(String url, String parameter)
	{

		StringBuffer u = new StringBuffer(url);
		int start;
		int temp;
		while ((start = u.indexOf(parameter)) != -1)
		{
			// skip amps
			//System.out.println(u.substring(start));
			while ((temp = u.indexOf("&amp", start + 1)) != -1)
			{
				start = temp;
			}
			int end = u.indexOf("&", start + 1);
			if (end == -1)
				end = u.length();
			if (u.substring(start - 5).startsWith("|amp|"))
				start -= 5;
			u.delete(start, end);
		}
		//System.out.println(u);
		return u.toString();
	}

	/**
	 * get lastrequest as string; strip given parameter 
	 * 	(that is used to store last currentRequest in url)
	 * @param currentRequest
	 * @param parameterToStrip
	 * @return String
	 */
	public static String getLastRequest(HttpServletRequest request, 
		String parameterToStrip)
	{

		String lq = request.getRequestURL() + "?" + request.getQueryString();
		lq = replace(lq, '&', "|amp|");
		lq = stripParameter(lq, parameterToStrip);
		return lq;
	}
	
	/**
	 * Returns <tt>text</tt> performing the following substring
	 * replacements (to facilitate output to XML/HTML pages):
	 *
	 *    & -> &amp;
	 *    < -> &lt;
	 *    > -> &gt;
	 *    " -> &#034;
	 *    ' -> &#039;
	 * @param text string to transform
	 * @return String transformed string
	 */
	public static String escapeXml(String text)
	{

		if (text == null)
			return null;
		StringBuffer w = new StringBuffer();
		for (int i = 0; i < text.length(); i++)
		{
			char c = text.charAt(i);
			if (c == '&')
				w.append("&amp;");
			else if (c == '<')
				w.append("&lt;");
			else if (c == '>')
				w.append("&gt;");
			else if (c == '"')
				w.append("&#034;");
			else if (c == '\'')
				w.append("&#039;");
			else
				w.append(c);
		}
		return w.toString();
	}

	/**
	 * @param text string to transform
	 * @param scanchar char to scan for
	 * @param with replacement string to replace scanchar
	 * @return String transformed string
	 */
	public static String replace(String text, char scanchar, String replacement)
	{

		if (text == null || replacement == null)
			return null;
		StringBuffer w = new StringBuffer();
		for (int i = 0; i < text.length(); i++)
		{
			char c = text.charAt(i);
			if (c == scanchar)
				w.append(replacement);
			else
				w.append(c);
		}
		return w.toString();
	}

	/**
	 * @param text string to transform
	 * @param scanstring string to scan for
	 * @param with replacement string to replace scanstring
	 * @return String transformed string
	 */
	public static String replace(String text, String scanstring, String replacement)
	{

		if (text == null || scanstring == null || replacement == null)
			return null;
		StringBuffer w = new StringBuffer(text);
		int start;
		while ((start = w.indexOf(scanstring)) != -1)
		{
			int end = start + scanstring.length();
			if (end == -1)
				end = w.length();
			w.replace(start, end, replacement);
		}
		return w.toString();
	}

}
