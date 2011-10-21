package nl.openedge.util.velocity.tools;

import javax.servlet.http.HttpServletRequest;

/**
 * Helper class for Url related stuff.
 */
public class UrlTool
{
	/** custom amp sign length. */
	private static final int CUSTAMPLENGTH = 5;

	/**
	 * Strips a parameter from url.
	 * 
	 * @param url
	 *            url to strip parameter from
	 * @param parameter
	 *            parameter to strip
	 * @return stripped url
	 */
	public static String stripParameter(String url, String parameter)
	{
		StringBuffer u = new StringBuffer(url);
		int start;
		int temp;
		while ((start = u.indexOf(parameter)) != -1)
		{
			// skip amps
			while ((temp = u.indexOf("&amp", start + 1)) != -1)
			{
				start = temp;
			}
			int end = u.indexOf("&", start + 1);
			if (end == -1)
				end = u.length();
			if (u.substring(start - CUSTAMPLENGTH).startsWith("|amp|"))
				start -= CUSTAMPLENGTH;
			u.delete(start, end);
		}
		return u.toString();
	}

	/**
	 * get lastrequest as string; strip given parameter (that is used to store last
	 * currentRequest in url).
	 * 
	 * @param request
	 *            the request
	 * @param parameterToStrip
	 *            the parameter to strip
	 * @return String stripped request url
	 */
	public static String getLastRequest(HttpServletRequest request, String parameterToStrip)
	{

		String lq = request.getRequestURL() + "?" + request.getQueryString();
		lq = replace(lq, '&', "|amp|");
		lq = stripParameter(lq, parameterToStrip);
		return lq;
	}

	/**
	 * Returns <tt>text</tt> performing the following substring replacements (to
	 * facilitate output to XML/HTML pages): & -> &amp; < -> &lt; > -> &gt; " -> &#034; '
	 * -> &#039;.
	 * 
	 * @param text
	 *            string to transform
	 * @return String transformed string
	 */
	public static String escapeXml(String text)
	{

		if (text == null)
		{
			return null;
		}
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
	 * Replace all occurences of scanchar withing the given text with the given
	 * replacement.
	 * 
	 * @param text
	 *            string to transform
	 * @param scanchar
	 *            char to scan for
	 * @param replacement
	 *            the replacement for scanchar
	 * @return String transformed string
	 */
	public static String replace(String text, char scanchar, String replacement)
	{

		if (text == null || replacement == null)
		{
			return null;
		}
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
	 * Replace all occurences of scanstring withing the given text with the given
	 * replacement.
	 * 
	 * @param text
	 *            string to transform
	 * @param scanstring
	 *            string to scan for
	 * @param replacement
	 *            the replacement for scanchar
	 * @return String transformed string
	 */
	public static String replace(String text, String scanstring, String replacement)
	{

		if (text == null || scanstring == null || replacement == null)
		{
			return null;
		}
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
