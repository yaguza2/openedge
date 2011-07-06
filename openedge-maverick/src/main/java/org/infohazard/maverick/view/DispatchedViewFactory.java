/*
 * $Id: DispatchedViewFactory.java,v 1.7 2004/06/07 20:38:17 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/view/DispatchedViewFactory.java,v $
 */

package org.infohazard.maverick.view;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infohazard.maverick.flow.ConfigException;
import org.infohazard.maverick.flow.TransformStep;
import org.infohazard.maverick.flow.View;
import org.infohazard.maverick.flow.ViewContext;
import org.infohazard.maverick.flow.ViewFactory;
import org.infohazard.maverick.util.XML;
import org.jdom.Element;


/**
 * <p>This factory creates Views which use the RequestDispatcher to obtain
 * content.  It is used by the DocumentViewFactory and not intended to be
 * designated as a normal Maverick view type.</p>
 */
public class DispatchedViewFactory implements ViewFactory
{
	/**
	 * Logger.
	 */
    private static Log log = LogFactory.getLog(DispatchedViewFactory.class);

    /**
     * Initialize.
     * @see org.infohazard.maverick.flow.ViewFactory#init(org.jdom.Element, javax.servlet.ServletConfig)
     */
	public void init(Element factoryNode, ServletConfig servletCfg) throws ConfigException
	{
	}

	/**
	 * @see org.infohazard.maverick.flow.ViewFactory#createView(org.jdom.Element)
	 */
	public View createView(Element viewNode) throws ConfigException
	{
		String path = XML.getValue(viewNode, "path");

		if (path == null)
			throw new ConfigException("View node must have a path:  " + XML.toString(viewNode));
			
		return new DispatchedView(path);
	}

	/**
	 * The view is rendered as a RequestDispatcher.forward() or include().
	 */
	protected static class DispatchedView implements View
	{
		/**
		 * Source url of the document.
		 */
		protected String path;
		
		/**
		 * @param path is the URL of the document to render.
		 */
		public DispatchedView(String path)
		{
			if (path.startsWith("/"))
				this.path = path;
			else
				this.path = "/" + path;
	
			log.debug("Creating DispatchedView with path:  " + path);
		}
	
		/**
		 * Renders the url associated with this view directly to the response.
		 *
		 * @see View#go
		 */
		public void go(ViewContext vctx) throws IOException, ServletException
		{
			// Put any params in the request attributes
			if (vctx.getViewParams() != null)
			{
				if (log.isDebugEnabled())
					log.debug("Setting " + vctx.getViewParams().size() + " params");
				
				Iterator entryIt = vctx.getViewParams().entrySet().iterator();
				while (entryIt.hasNext())
				{
					Map.Entry entry = (Map.Entry)entryIt.next();
					vctx.getRequest().setAttribute((String)entry.getKey(), entry.getValue());
				}
			}
			
			RequestDispatcher disp = vctx.getRequest().getRequestDispatcher(this.path);
			if (null == disp)
				throw new ServletException("RequestDispatcher could not be created for " + this.path);

			TransformStep next = vctx.getNextStep();

			if (log.isDebugEnabled())
				log.debug((next.isLast() ? "Forwarding to " : "Including ") + this.path);
				
			if (next.isLast())
				disp.forward(vctx.getRequest(), next.getResponse());
			else
				disp.include(vctx.getRequest(), next.getResponse());

			next.done();
		}
	}
}