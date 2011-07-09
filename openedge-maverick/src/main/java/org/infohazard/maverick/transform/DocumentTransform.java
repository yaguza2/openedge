/*
 * $Id: DocumentTransform.java,v 1.7 2004/06/07 20:38:11 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/transform/DocumentTransform.java,v $
 */

package org.infohazard.maverick.transform;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import org.infohazard.maverick.flow.Transform;
import org.infohazard.maverick.flow.TransformContext;
import org.infohazard.maverick.flow.TransformStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Transform wraps the input one or more times by putting the output from the
 * previous step in the request attributes.
 */
class DocumentTransform implements Transform
{
	/**
	 * Transform path.
	 */
	protected String path;

	/**
	 * The name.
	 */
	protected String wrappedName;

	/**
	 * Logger.
	 */
	private static Logger log = LoggerFactory.getLogger(DocumentTransform.class);

	/**
	 * Construct with path and name.
	 * 
	 * @param path
	 *            path
	 * @param wrappedName
	 *            name
	 */
	public DocumentTransform(String path, String wrappedName)
	{
		this.path = path;
		this.wrappedName = wrappedName;

		log.debug("Created DocumentTransform with wrapped name \"" + wrappedName + "\" and path "
			+ path);
	}

	/**
	 * @see org.infohazard.maverick.flow.Transform#createStep(org.infohazard.maverick.flow.TransformContext)
	 */
	public TransformStep createStep(TransformContext tctx) throws ServletException
	{
		return new Step(tctx);
	}

	/**
	 * Transform step.
	 */
	protected class Step extends StringTransformStep
	{
		/**
		 * Construct.
		 * 
		 * @param tctx
		 *            transform context
		 * @throws ServletException
		 */
		public Step(TransformContext tctx) throws ServletException
		{
			super(tctx);
		}

		/**
		 * @see org.infohazard.maverick.flow.TransformStep#go(java.lang.String)
		 */
		public void go(String input) throws IOException, ServletException
		{
			if (log.isDebugEnabled())
				log.debug("Wrapping text with length " + input.length());

			// Populate params, if applicable
			if (this.getTransformCtx().getTransformParams() != null)
			{
				Iterator entriesIt =
					this.getTransformCtx().getTransformParams().entrySet().iterator();
				while (entriesIt.hasNext())
				{
					Map.Entry entry = (Map.Entry) entriesIt.next();
					this.getTransformCtx().getRequest()
						.setAttribute((String) entry.getKey(), entry.getValue());
				}
			}

			// Set up request attribute with input
			this.getTransformCtx().getRequest().setAttribute(wrappedName, input);

			RequestDispatcher disp = this.getTransformCtx().getRequest().getRequestDispatcher(path);
			if (null == disp)
				throw new ServletException("RequestDispatcher could not be created for " + path);

			if (log.isDebugEnabled())
				log.debug("Transforming (" + (this.getNext().isLast() ? "forward" : "include")
					+ ") with document:  " + path);

			if (this.getNext().isLast())
				disp.forward(this.getTransformCtx().getRequest(), this.getNext().getResponse());
			else
				disp.include(this.getTransformCtx().getRequest(), this.getNext().getResponse());

			this.getNext().done();
		}
	}
}
