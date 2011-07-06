/*
 * $Id: XSLTransformFactory.java,v 1.12 2004/06/27 17:41:31 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/transform/XSLTransformFactory.java,v $
 */
package org.infohazard.maverick.transform;

import javax.servlet.*;
import javax.xml.transform.URIResolver;

import org.infohazard.maverick.flow.*;
import org.infohazard.maverick.util.XML;
import org.jdom.Element;

/**
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 * @version $Revision: 1.12 $ $Date: 2004/06/27 17:41:31 $
 *
 * <p>Factory for creating transformation pipelines using XSLT.</p>
 *
 * <p>This factory has certain options which can be defined when
 * declaring the factory.  The defaults are shown here:</p>
 *
 * <pre>
 * &lt;transform-factory type="xslt" provider="org.infohazard.maverick.transform.XSLTransformFactory"&gt;
 *   &lt;default-output-type value="text/html"/&gt;
 *   &lt;template-caching value="preload"/&gt;
 *   &lt;uri-resolver value=""/&gt;
 * &lt;/transform-factory&gt;
 * </pre>
 *
 * <ul>
 *   <li>
 *     <b>default-output-type</b> - Unless an individual transform
 *     explicitly specifies an output-type, this will be the http
 *     content-type for a successfully completing transformation.
 *   </li>
 *   <li>
 *     <b>template-caching</b> - Must be one of three values:  <b>preload</b>,
 *     which compiles and caches all XSLT templates when the web application
 *     starts, <b>lazy</b>, which caches compiled XSLT templates but does
 *     not load them until they are requested, and <b>disabled</b>, which
 *     causes templates to be (re)loaded for every request.  The default is
 *     preload.
 *   </li>
 *   <li>
 *     <b>uri-resolver</b> - The classname of a custom URI Resolver to set on
 *     all transformations.
 *   </li>
 * </ul>
 *
 * <p>The options for an individual transform are like this:</p>
 *
 * <pre>
 * &lt;transform type="xslt" path="blah.xsl" output-type="text/plain"/&gt;
 * </pre>
 */
public class XSLTransformFactory implements TransformFactory
{
	/** For the factory configuration */
	protected final static String ATTR_DEFAULT_FINAL_CONTENT_TYPE = "default-output-type";
	/** */
	protected final static String ATTR_TEMPLATE_CACHING = "template-caching";
	/** */
	protected final static String VAL_TEMPLATE_CACHING_LAZY = "lazy";
	/** */
	protected final static String VAL_TEMPLATE_CACHING_PRELOAD = "preload";
	/** */
	protected final static String VAL_TEMPLATE_CACHING_DISABLED = "disabled";
	/** */
	protected final static String ATTR_URI_RESOLVER = "uri-resolver";

	/** For individual transform nodes */
	protected final static String ATTR_PATH = "path";
	/** */
	protected final static String ATTR_MONITOR = "monitor";
	/** */
	protected final static String ATTR_FINAL_CONTENT_TYPE = "output-type";

	/**
	 * If the init param for default content type is not specified, this
	 * becomes the actual default final content type.  The default default :-)
	 */
	protected final static String DEFAULT_DEFAULT_FINAL_CONTENT_TYPE = "text/html";

	/**
	 * Unless specified in individual transform nodes, this is the content type
	 * for a successfully completed transformation.
	 */
	protected String defaultFinalContentType = DEFAULT_DEFAULT_FINAL_CONTENT_TYPE;

	/** Should templates be preloaded and cached */
	protected int templateCachingStyle = XSLTransform.CACHE_PRELOAD;

	/** Allow the user to override the URI resolver. */
	protected URIResolver uriResolver = null;

	/** We need this to be able to look up real pathnames. */
	protected ServletContext servletCtx;

	/**
	 * @param factoryNode The XML from which to draw configuration information.
	 * @param servletCfg Information about the container.
	 * @exception ConfigException Thrown if configuration is bad.
	 */
	public void init(Element factoryNode, ServletConfig servletCfg) throws ConfigException
	{
		this.servletCtx = servletCfg.getServletContext();

		if (factoryNode != null)
		{
			// Figure out the default content type for successful transforms
			String outputTypeStr = XML.getValue(factoryNode, ATTR_DEFAULT_FINAL_CONTENT_TYPE);
			if (outputTypeStr != null)
				this.defaultFinalContentType = outputTypeStr;

			// Should we lazy-load the templates
			String templateLoadingStr = XML.getValue(factoryNode, ATTR_TEMPLATE_CACHING);
			if (templateLoadingStr != null)
			{
				templateLoadingStr = templateLoadingStr.toLowerCase();

				if (VAL_TEMPLATE_CACHING_PRELOAD.equals(templateLoadingStr))
				{
					this.templateCachingStyle = XSLTransform.CACHE_PRELOAD;
				}
				else if (VAL_TEMPLATE_CACHING_LAZY.equals(templateLoadingStr))
				{
					this.templateCachingStyle = XSLTransform.CACHE_LAZY;
				}
				else if (VAL_TEMPLATE_CACHING_DISABLED.equals(templateLoadingStr))
				{
					this.templateCachingStyle = XSLTransform.CACHE_DISABLED;
				}
			}

			// Override the uri resolver?
			String uriResolverStr = XML.getValue(factoryNode, ATTR_URI_RESOLVER);
			if (uriResolverStr != null)
			{
				try
				{
					ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
					if (classLoader == null)
					{
						classLoader = DefaultControllerFactory.class.getClassLoader();
					}
					Class resolverClass = classLoader.loadClass(uriResolverStr);
					this.uriResolver = (URIResolver)resolverClass.newInstance();
				}
				catch (ClassNotFoundException ex)
				{
					throw new ConfigException(ex);
				}
				catch (InstantiationException ex)
				{
					throw new ConfigException(ex);
				}
				catch (IllegalAccessException ex)
				{
					throw new ConfigException(ex);
				}
			}
		}
	}

	/**
	 * @param transformNode
	 * @return
	 * @exception ConfigException
	 */
	public Transform createTransform(Element transformNode) throws ConfigException
	{
		String outputType = XML.getValue(transformNode, ATTR_FINAL_CONTENT_TYPE);
		if (outputType == null)
			outputType = this.defaultFinalContentType;

		boolean isMonitored = false;
		String monitor = XML.getValue(transformNode, ATTR_MONITOR);
		if (monitor != null && "true".equals(monitor.toLowerCase()))
			isMonitored = true;

		String path = XML.getValue(transformNode, ATTR_PATH);
		if (path == null)
			throw new ConfigException("XSLT transform node must have a \""
										+ ATTR_PATH + "\" attribute:  "
										+ XML.toString(transformNode));

		return new XSLTransform(
						path,
						isMonitored,
						this.templateCachingStyle,
						this.servletCtx,
						outputType,
						this.uriResolver);
	}
}

