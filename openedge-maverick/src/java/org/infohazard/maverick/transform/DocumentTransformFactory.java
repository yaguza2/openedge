/*
 * $Id: DocumentTransformFactory.java,v 1.4 2003/10/27 11:00:49 thusted Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/transform/DocumentTransformFactory.java,v $
 */

package org.infohazard.maverick.transform;

import org.infohazard.maverick.flow.*;
import org.infohazard.maverick.util.XML;
import javax.servlet.*;
import org.jdom.Element;


/**
 * <p>Factory for creating transformation pipelines based on executing
 * successive documents which are aware of servlet attribute collections.
 * The output of the preceeding step is
 * stored as a String in the request attributes, available to be
 * included anywhere in the successive document.</p>
 *
 * <p>This factory has certain options which can be defined when
 * declaring the factory.  The defaults are shown here:</p>
 *
 * <pre>
 * &lt;transform-factory type="document" provider="org.infohazard.maverick.transform.DocumentTransformFactory"&gt;
 *   &lt;default-bean-name value="wrapped"/&gt;
 * &lt;/transform-factory&gt;
 * </pre>
 *
 * <ul>
 *   <li>
 *     <b>default-bean-name</b> - If no "bean" attribute is specified
 *     for individual transformations, this is the name which will be
 *     used for placing the wrapped content in the request attributes.
 *   </li>
 * </ul>
 *
 * <p>The options for an individual transform are like this:</p>
 *
 * <pre>
 * &lt;transform type="document" path="blah.jsp" bean="wrapped"/&gt;
 * </pre>
 */
public class DocumentTransformFactory implements TransformFactory
{
	/**
	 * If not specified on the factory, the default name for wrapped beans.
	 */
	protected static final String DEFAULT_DEFAULT_WRAPPED_NAME = "wrapped";

	/**
	 * For the factory configuration
	 */
	protected static final String ATTR_DEFAULT_WRAPPED_NAME = "default-bean-name";


	/**
	 * For transform nodes
	 */
	protected static final String ATTR_PATH = "path";
	protected static final String ATTR_BEAN = "bean";


	/**
	 * Unless overriden on path nodes, the bean name to use for wrapping
	 * content from previous stages.
	 */
	protected String defaultWrappedName = DEFAULT_DEFAULT_WRAPPED_NAME;

	/**
	 */
	public void init(Element factoryNode, ServletConfig servletCfg) throws ConfigException
	{
		if (factoryNode != null)
		{
			// Figure out the default content type for successful transforms
			String wrappedNameStr = XML.getValue(factoryNode, ATTR_DEFAULT_WRAPPED_NAME);
			if (wrappedNameStr != null)
				this.defaultWrappedName = wrappedNameStr;
		}
	}

	/**
	 */
	public Transform createTransform(Element transformNode) throws ConfigException
	{
		String path = XML.getValue(transformNode, ATTR_PATH);
		if (path == null)
			throw new ConfigException("Document transform node must have a \""
										+ ATTR_PATH + "\" attribute:  "
										+ XML.toString(transformNode));
		
		String wrappedName = XML.getValue(transformNode, ATTR_BEAN);
		if (wrappedName == null)
			wrappedName = this.defaultWrappedName;

		return new DocumentTransform(path, wrappedName);
	}
}