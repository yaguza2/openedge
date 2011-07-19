/*
 * $Id: DocumentView.java,v 1.5 2004/08/07 07:35:42 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/view/DocumentView.java,v $
 */

package org.infohazard.maverick.view;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.ServletException;

import org.infohazard.maverick.flow.View;
import org.infohazard.maverick.flow.ViewContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * DocumentView is the base class for the default Maverick "document" view type.
 * </p>
 * <p>
 * A DocumentView is a {@link View} that sets up a "model" object in request or session
 * scope and forwards to another resource to render the final response. The "model" object
 * exposes dynamic data so that it can be rendered as part of the response.
 * </p>
 * <p>
 * This class is used by the Maverick {@link DocumentViewFactory}. The DocumentViewFactory
 * defines the abstract SetAttribute method for each instance according to the parameters
 * passed through the View XML element.
 * </p>
 * <p>
 * By default, a DocumentView will be associated with a {@link DispatchedViewFactory
 * DispatchedView} that uses the {@link javax.servlet.RequestDispatcher RequestDispatcher}
 * to forward control to another resource (e.g. service or servlet that renders a server
 * page).
 * </p>
 */
public abstract class DocumentView implements View
{
	private static Logger log = LoggerFactory.getLogger(DocumentView.class);

	/**
	 * <p>
	 * The name of the bean in the appropriate scope attributes.
	 * </p>
	 */
	protected String beanName;

	/**
	 * <p>
	 * Convenience constructor to pass the "model" object name and the aggregated
	 * {@link View}.
	 * </p>
	 * 
	 * @param beanName
	 *            The name of the bean in the appropriate scope attributes.
	 */
	public DocumentView(String beanName)
	{
		this.beanName = beanName;
	}

	/**
	 * <p>
	 * Entry method that initiates the View rendering process. Here, the DocumentView sets
	 * the the "model" object to the appropriate scope and invokes the forward View to
	 * complete the response.
	 * </p>
	 * 
	 * @param vctx
	 *            is placed in an attribute collection.
	 * @throws IOException
	 * @throws ServletException
	 * @see View#go
	 */
	@Override
	public void go(ViewContext vctx) throws IOException, ServletException
	{
		// Should we put the null in the collection?
		if (vctx.getModel() != null)
			this.setAttribute(vctx);

		// Put any params in the request attributes
		if (vctx.getViewParams() != null)
		{
			if (log.isDebugEnabled())
				log.debug("Setting " + vctx.getViewParams().size() + " params");

			Iterator<Entry<String, Object>> entryIt = vctx.getViewParams().entrySet().iterator();
			while (entryIt.hasNext())
			{
				Entry<String, Object> entry = entryIt.next();
				vctx.getRequest().setAttribute(entry.getKey(), entry.getValue());
			}
		}
		vctx.getNextStep().go("");
	}

	/**
	 * <p>
	 * Template method that can be used to place the "model" object in whatever scope is
	 * appropriate for this View element instance.
	 * </p>
	 * <p>
	 * The {@link DocumentViewFactory} provides an implementation of this method
	 * appropriate to the View parameters passed from the Maverick configuration document.
	 * </p>
	 * 
	 * @param vctx
	 *            provides access to request and session scope collections, if needed.
	 */
	protected abstract void setAttribute(ViewContext vctx);
}
