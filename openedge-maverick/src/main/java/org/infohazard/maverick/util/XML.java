/*
 * $Id: XML.java,v 1.9 2005/10/03 21:45:58 prophecyslides Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/util/XML.java,v $
 */
package org.infohazard.maverick.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * created January 27, 2002
 * 
 * @author Jeff Schnitzer
 * @version $Revision: 1.9 $ $Date: 2005/10/03 21:45:58 $
 */
public class XML
{
	/** The attribute which represents the "value" of an element. */
	public final static String ATTR_VALUE = "value";

	/** The tag for a parameter node */
	public final static String TAG_PARAM = "param";

	/** The attribute for a parameter name */
	public final static String ATTR_PARAM_NAME = "name";

	/** */
	protected static XMLOutputter outputter = new XMLOutputter(Format.getCompactFormat());

	/**
	 * <p>
	 * Extracts the named value from the element, by checking (in order):
	 * </p>
	 * <ol>
	 * <li>
	 * A system property whose name is constructed like this: "maverick." + node.getName()
	 * + "." + name</li>
	 * <li>An attribute with the specified name.</li>
	 * <li>The "value" attribute of a subelement with the specified name.</li>
	 * </ol>
	 * 
	 * @param node
	 * @param name
	 * @return null if a value with that name is not defined.
	 */
	public static String getValue(Element node, String name)
	{
		String result = System.getProperty("maverick." + node.getName() + "." + name);
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
	 * Converts the specified node (and subnodes) to a nice, pretty, html escaped XML
	 * string.
	 * 
	 * @param node
	 * @return
	 */
	public static String toString(Element node)
	{
		return escape(outputter.outputString(node));
	}

	/**
	 * Escapes any html characters in the input string.
	 * 
	 * @param in
	 * @return
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
	 * Extracts a set of param child nodes from the specified node. Expects that param
	 * nodes will look like: &lt;param name="theName" value="theValue"/&gt;
	 * 
	 * @param node
	 *            Parent element.
	 * @return null if no parameters are found.
	 */
	public static Map<String, Object> getParams(Element node)
	{
		Map<String, Object> params = null;

		@SuppressWarnings("unchecked")
		Iterator<Element> it = node.getChildren(TAG_PARAM).iterator();
		while (it.hasNext())
		{
			Element paramNode = it.next();

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
				params = new HashMap<String, Object>();

			params.put(name, value);
		}

		return params;
	}
}
