/*
 * $Id: XSLTransform.java,v 1.20 2004/06/07 20:38:11 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/transform/XSLTransform.java,v $
 */
package org.infohazard.maverick.transform;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.infohazard.maverick.flow.ConfigException;
import org.infohazard.maverick.flow.Transform;
import org.infohazard.maverick.flow.TransformContext;
import org.infohazard.maverick.flow.TransformStep;
import org.xml.sax.ContentHandler;

/**
 * This Transform runs the input through a series of XSLT transformations.
 *
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 * @version $Revision: 1.20 $ $Date: 2004/06/07 20:38:11 $
 */
class XSLTransform implements Transform
{
	/** Enumeration for the caching options */
	public static final int CACHE_PRELOAD = 1;
	/** Enumeration for the caching options */
	public static final int CACHE_LAZY = 2;
	/** Enumeration for the caching options */
	public static final int CACHE_DISABLED = 3;

	/** Logger. */
	private static Logger log = LoggerFactory.getLogger(XSLTransform.class);

	/** Mime type used if we stop before last transform. */
	protected final static String UNFINISHED_CONTENTTYPE = "text/xml";

	/** Path to the template */
	protected String path;

	/** If template file is monitored */
	protected boolean isMonitoredFile = false;

	/** Monitored template File */
	protected File monitoredFile = null;

	/** Monitored template File last modified time */
	protected long monitoredFileLastModified;

	/** Cached compiled templates */
	protected Templates compiled;

	/** The content-type to set if this is the last transform and it completes normally. */
	protected String finalContentType;

	/** If not null, set this as the uri resolver for xslt transformations */
	protected URIResolver uriResolver;
	
	/** The caching style to use */
	protected int cachingStyle;
	
	/**
	 * Expects that you will call addTemplate() to add the individual transformations.
	 *
	 * @param path The context-relative path to the template
	 * @param finishedContentType If this is the last transform and the chain
	 *  completes successfully, this will be the content type set on the response.
	 * @param webAppContext Allows xsl files to be loaded as resources.
	 * @param uriRes Set a custom URI Resolver for transformation.  Can be null.
	 * @param templateCachingStyle Must be one of the CACHE_ constants.
	 * @exception ConfigException Thrown if there is a problem with the configuration.
	 */
	public XSLTransform(String path,
	                    boolean isMonitored,
						int templateCachingStyle,
						ServletContext webAppContext,
						String finishedContentType,
						URIResolver uriRes) throws ConfigException
	{
		log.debug("Creating xslt transform:  " + path);

		this.path = path;
		this.isMonitoredFile = isMonitored;
		this.finalContentType = finishedContentType;
		this.uriResolver = uriRes;
		this.cachingStyle = templateCachingStyle;
		
		if (cachingStyle == CACHE_PRELOAD)
			this.compiled = loadTemplate(this.path, webAppContext);
	}

	/**
	 * @see org.infohazard.maverick.flow.Transform#createStep(org.infohazard.maverick.flow.TransformContext)
	 */
	public TransformStep createStep(TransformContext tctx) throws ServletException
	{
		return new Step(tctx);
	}

	/**
	 * @param path Path, starting with /, relative to context root, of xsl
	 * @return The jaxp object for the specified path.
	 * @exception ConfigException
	 */
	protected Templates loadTemplate(String path, ServletContext servletCtx) throws ConfigException
	{

		boolean pathIsFileUrl = false;
		boolean pathIsOtherUrl = false;
		java.net.URL resURL = null;

		if (this.isMonitoredFile && this.monitoredFile != null)
		{
			// This happens if the file is monitored and it has changed on the filesystem
			try
			{
				resURL = this.monitoredFile.toURL();
				this.monitoredFileLastModified = this.monitoredFile.lastModified();
			}
			catch (MalformedURLException me)
			{
				log.error("Eror parsing monitored template URL " + path + ":  " + me.toString());
				throw new ConfigException(me);
			}
			catch (SecurityException se)
			{
				log.error("Unable to access monitored template " + path + ":  " + se.toString());
				throw new ConfigException(se);
			}
		}
		else
		{
			// Check to see if the path is a URL and what type, otherwise make sure
			// it's a proper servlet resource path
			if (path.toLowerCase().startsWith("file:"))
				pathIsFileUrl = true;
			else if (path.toLowerCase().startsWith("http:") ||
					 path.toLowerCase().startsWith("https:") ||
					 path.toLowerCase().startsWith("ftp:"))
				pathIsOtherUrl = true;
			else if (!path.startsWith("/"))
				path = "/" + path;

			try
			{
				if (pathIsFileUrl && this.isMonitoredFile)
				{
					resURL = new java.net.URL(path);
					this.monitoredFile = new File(resURL.getFile());

					if (this.monitoredFile == null || !this.monitoredFile.canRead())
					{
						this.monitoredFile = null;
						log.error("Resource not found or unable to read file:  " + path);
						throw new ConfigException("Resource not found or unable to read file:  " + path);
					}

					this.monitoredFileLastModified = this.monitoredFile.lastModified();
				}
				else if (pathIsFileUrl || pathIsOtherUrl)
					resURL = new java.net.URL(path);
				else
					resURL = servletCtx.getResource(path);

				if (resURL == null)
				{
					log.error("Resource not found:  " + path);
					throw new ConfigException("Resource not found:  " + path);
				}
			}
			catch (MalformedURLException me)
			{
				log.error("Eror parsing template URL " + path + ":  " + me.toString());
				throw new ConfigException(me);
			}
		}

		try
		{
			TransformerFactory tFactory = TransformerFactory.newInstance();
			if (this.uriResolver != null)
				tFactory.setURIResolver(this.uriResolver);

			return tFactory.newTemplates(new StreamSource(resURL.openStream(), resURL.toString()));
		}
		catch (TransformerException ex)
		{
			log.error("Error loading template " + path + ":  " + ex.toString());
			throw new ConfigException(ex);
		}
		catch (IOException ex)
		{
			log.error("Eror loading template " + path + ":  " + ex.toString());
			throw new ConfigException(ex);
		}

	}

	/**
	 * Set parameters on a transformer
	 *
	 * @param t
	 * @param params
	 */
	protected void populateParams(Transformer t, Map params)
	{
		Iterator entriesIt = params.entrySet().iterator();
		while (entriesIt.hasNext())
		{
			Map.Entry entry = (Map.Entry)entriesIt.next();
			t.setParameter((String)entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Step.
	 */
	protected class Step extends XMLTransformStep
	{
		/** */
		protected boolean handlerUsed = false;
		
		/** */
		public Step(TransformContext tctx) throws ServletException
		{
			super(tctx);
		}
	
		/**
		 * Set the final content type for this sucka
		 */
		protected void assignContentType(TransformContext tctx) throws ServletException
		{
			if (this.getNext().isLast() && !tctx.halting())
				this.getNext().setContentType(finalContentType);
			else
				this.getNext().setContentType(UNFINISHED_CONTENTTYPE);
		}
		
		/**
		 * Funnels stored output to go(Reader), or just passes on the
		 * done() message if TransformerHandler was used (yeay SAX!)
		 */
		public void done() throws IOException, ServletException
		{
			log.debug("Done being written to");
			
			if (this.fakeResponse == null)
			{
				if (this.handlerUsed)
					this.getNext().done();
				else
					throw new IllegalStateException("done() called illegally");
			}
			else
			{
				this.go(this.fakeResponse.getOutputAsReader());
				this.fakeResponse = null;
			}
		}
	
		/** */
		public ContentHandler getSAXHandler() throws IOException, ServletException
		{
			this.handlerUsed = true;
			
			// Set the content-type for the output
			assignContentType(this.getTransformCtx());
			
			TransformerHandler tHandler;
			try
			{
				SAXTransformerFactory saxTFact = (SAXTransformerFactory)TransformerFactory.newInstance();
				tHandler = saxTFact.newTransformerHandler(this.getCompiled());
			}
			catch (TransformerConfigurationException ex)
			{
				throw new ServletException(ex);
			}
			
			// Populate any params which might have been set
			if (this.getTransformCtx().getTransformParams() != null)
				populateParams(tHandler.getTransformer(), this.getTransformCtx().getTransformParams());
			
			if (this.getNext().isLast())
				tHandler.setResult(new StreamResult(this.getNext().getResponse().getOutputStream()));
			else
				tHandler.setResult(new SAXResult(this.getNext().getSAXHandler()));
				
			return tHandler;
		}

		/** */
		public void go(Source input) throws IOException, ServletException
		{
			// Set the content-type for the output
			assignContentType(this.getTransformCtx());
			
			Transformer trans;
			try
			{
				trans = this.getCompiled().newTransformer();
			}
			catch (TransformerConfigurationException ex)
			{
				throw new ServletException(ex);
			}
			
			// Populate any params which might have been set
			if (this.getTransformCtx().getTransformParams() != null)
				populateParams(trans, this.getTransformCtx().getTransformParams());
			
			Result res;
			if (this.getNext().isLast())
				res = new StreamResult(this.getNext().getResponse().getOutputStream());
			else
				res = new SAXResult(this.getNext().getSAXHandler());

			try
			{
				trans.transform(input, res);
			}
			catch (TransformerException ex)
			{
				throw new ServletException(ex);
			}

			this.getNext().done();
		}

		/**
		 * @return The compiled value
		 * @exception ConfigException
		 */
		public Templates getCompiled() throws ConfigException
		{

			try
			{
				if (isMonitoredFile && monitoredFile != null)
				{
					long lastModified = monitoredFile.lastModified();

					if (lastModified <= 0L)
					{
						log.error("Unable to access monitored template " + path);

						if (compiled != null)
							return compiled;
						else
							throw new IllegalStateException("Unable to access monitored template " + path);
					}
					else if (monitoredFileLastModified < lastModified)
					{
						compiled = loadTemplate(path, this.getTransformCtx().getServletContext());
						return compiled;
					}
				}

				switch (cachingStyle)
				{
					case CACHE_PRELOAD:
					{
						return compiled;
					}
					case CACHE_LAZY:
					{
						if (compiled == null)
							compiled = loadTemplate(path, this.getTransformCtx().getServletContext());

						return compiled;
					}
					case CACHE_DISABLED:
					{
						return loadTemplate(path, this.getTransformCtx().getServletContext());
					}
					default:
					{
						throw new IllegalStateException("Unknown caching style");
					}
				}

			}
			catch (SecurityException se)
			{
				log.error("Unable to access monitored template " + path, se);

				if (compiled != null)
					return compiled;
				else
					throw new ConfigException(se);
			}

		}
	}

}

