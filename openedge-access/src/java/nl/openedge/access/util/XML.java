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
package nl.openedge.access.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.jdom.*;
import org.jdom.output.XMLOutputter;

/**
 * Helper for reading elements from the configuration document.
 * 
 * Used the copy/ paste pattern on this one. Got it from the 
 * <a href="http://mav.sourceforge.org/">Maverick framework</a> 
 * 
 * @author Jeff Schnitzer
 */
public class XML
{
	/** The attribute which represents the "value" of an element. */
	public final static String ATTR_VALUE = "value";
	
	/** The tag for a parameter node */
	public final static String TAG_PARAM = "param";
	
	/** The attribute for a parameter name */
	public final static String ATTR_PARAM_NAME = "name";

	/** output helper */
	protected static XMLOutputter outputter = new XMLOutputter();

	static
	{
		outputter.setTextNormalize(true);
	}

	/**
	 * <p>Extracts the named value from the element, by checking (in order):</p>
	 * <ol>
	 *   <li>
	 *     A system property whose name is constructed like this:
	 *     "oeaccess." + node.getName() + "." + name
	 *   </li>
	 *   <li>An attribute with the specified name.</li>
	 *   <li>The "value" attribute of a subelement with the specified name.</li>
	 * </ol>
	 *
	 * @param node
	 * @param name
	 * @return null if a value with that name is not defined.
	 */
	public static String getValue(Element node, String name)
	{
		String result = System.getProperty("oeaccess." + node.getName() + "." + name);
		if (result != null)
			return result;
		
		result = node.getAttributeValue(name);
		if (result != null)
			return result;

		Element subnode = node.getChild(name);
		if (subnode == null)
			return null;

		return subnode.getAttributeValue(ATTR_VALUE);
	}

	/**
	 * Converts the specified node (and subnodes) to a nice, pretty,
	 * html escaped XML string.
	 *
	 * @param node
	 * @return string repr
	 */
	public static String toString(Element node)
	{
		return escape(outputter.outputString(node));
	}

	/**
	 * Escapes any html characters in the input string.
	 *
	 * @param in
	 * @return escaped string
	 */
	public static String escape(String in)
	{
		StringBuffer out = new StringBuffer();

		for (int i = 0; i < in.length(); i++)
		{
			char c = in.charAt(i);

			switch (c)
			{
				case '<':
					out.append("&lt;");
					break;
				case '>':
					out.append("&gt;");
					break;
				case '&':
					out.append("&amp;");
					break;
				case '"':
					out.append("&quot;");
					break;
				default:
					out.append(c);
					break;
			}
		}

		return out.toString();
	}

	/**
	 * Extracts a set of param child nodes from the specified node.  Expects
	 * that param nodes will look like:
	 * &lt;param name="theName" value="theValue"/&gt;
	 *
	 * @param node Parent element.
	 * @return null if no parameters are found.
	 */
	public static Map getParams(Element node)
	{
		Map params = null;

		Iterator it = node.getChildren(TAG_PARAM).iterator();
		while (it.hasNext())
		{
			Element paramNode = (Element)it.next();

			String name = paramNode.getAttributeValue(ATTR_PARAM_NAME);

			Object value = paramNode.getAttributeValue(ATTR_VALUE);
			if (value == null)
			{
				if (paramNode.getChildren().size() > 0)
					value = paramNode.getChildren();
				else
				    value = paramNode.getTextTrim();
			}

			if (params == null)
				params = new HashMap();
				
			params.put(name, value);
		}

		return params;
	}
}

