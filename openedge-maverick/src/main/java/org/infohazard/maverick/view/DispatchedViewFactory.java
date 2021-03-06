/*
 * $Id: DispatchedViewFactory.java,v 1.7 2004/06/07 20:38:17 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/view/DispatchedViewFactory.java,v $
 */

package org.infohazard.maverick.view;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.infohazard.maverick.ViewDefinition;
import org.infohazard.maverick.flow.ConfigException;
import org.infohazard.maverick.flow.TransformStep;
import org.infohazard.maverick.flow.View;
import org.infohazard.maverick.flow.ViewContext;
import org.infohazard.maverick.flow.ViewFactory;
import org.infohazard.maverick.util.XML;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This factory creates Views which use the RequestDispatcher to obtain content. It is
 * used by the DocumentViewFactory and not intended to be designated as a normal Maverick
 * view type.
 * </p>
 */
public class DispatchedViewFactory implements ViewFactory
{
	/**
	 * Logger.
	 */
	private static Logger log = LoggerFactory.getLogger(DispatchedViewFactory.class);

	/**
	 * Initialize.
	 * 
	 * @see org.infohazard.maverick.flow.ViewFactory#init(org.jdom.Element,
	 *      javax.servlet.ServletConfig)
	 */
	@Override
	public void init(Element factoryNode, ServletConfig servletCfg)
	{
	}

	/**
	 * @see org.infohazard.maverick.flow.ViewFactory#createView(org.jdom.Element)
	 */
	@Override
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
		 * @param path
		 *            is the URL of the document to render.
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
		@Override
		public void go(ViewContext vctx) throws IOException, ServletException
		{
			// Put any params in the request attributes
			if (vctx.getViewParams() != null)
			{
				if (log.isDebugEnabled())
					log.debug("Setting " + vctx.getViewParams().size() + " params");

				Iterator<Entry<String, Object>> entryIt =
					vctx.getViewParams().entrySet().iterator();
				while (entryIt.hasNext())
				{
					Entry<String, Object> entry = entryIt.next();
					vctx.getRequest().setAttribute(entry.getKey(), entry.getValue());
				}
			}

			RequestDispatcher disp = vctx.getRequest().getRequestDispatcher(this.path);
			if (null == disp)
				throw new ServletException("RequestDispatcher could not be created for "
					+ this.path);

			TransformStep next = vctx.getNextStep();

			if (log.isDebugEnabled())
				log.debug((next.isLast() ? "Forwarding to " : "Including ") + this.path);

			if (next.isLast())
				disp.forward(vctx.getRequest(), next.getResponse());
			else
				disp.include(vctx.getRequest(), next.getResponse());

			next.done();
		}

		@Override
		public ViewDefinition getViewDefinition()
		{
			return null;
		}
	}
}
