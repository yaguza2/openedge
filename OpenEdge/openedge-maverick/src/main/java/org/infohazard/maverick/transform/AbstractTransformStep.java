/*
 * $Id: AbstractTransformStep.java,v 1.4 2004/06/07 20:37:59 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/transform/AbstractTransformStep.java,v $
 */

package org.infohazard.maverick.transform;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.infohazard.maverick.flow.TransformContext;
import org.infohazard.maverick.flow.TransformStep;
import org.infohazard.maverick.util.FakeHttpServletResponse;

/**
 * Helper class for all transform steps.  When extending this class,
 * remember that you are providing the TransformStep interface, so
 * the getResponse(), etc methods are for a *client* to call.  You
 * should be sending your output to the methods on getNext(), which
 * is safe to call multiple times.
 *
 * Remember, the goal is to take data written to you and send it on
 * to the next step!
 */
public abstract class AbstractTransformStep implements TransformStep
{
	/**
	 * Logger
	 */
    private static Logger log = LoggerFactory.getLogger(AbstractTransformStep.class);

	/**
	 */
	private TransformContext transformCtx;
	
	/**
	 */
	private TransformStep next;
	
	/**
	 */
	protected FakeHttpServletResponse fakeResponse;
	
	/**
	 */
	public AbstractTransformStep(TransformContext tctx) throws ServletException
	{
		this.transformCtx = tctx;
	}
	
	/**
	 * Always return false from pluggable transform steps.
	 */
	public boolean isLast()
	{
		return false;
	}

	/**
	 * By default do nothing.
	 */	
	public void setContentType(String contentType)
	{
	}
	
	/**
	 */
	protected TransformContext getTransformCtx()
	{
		return this.transformCtx;
	}
	
	/**
	 * Allow us to lazily create the next step, much more convenient.
	 */
	protected TransformStep getNext() throws ServletException
	{
		if (this.next == null)
			this.next = this.getTransformCtx().getNextStep();
			
		return this.next;
	}
	
	/**
	 */
	public HttpServletResponse getResponse() throws IOException, ServletException
	{
		log.debug("Getting fake response");
		
		// Should be ok to wrap the real mccoy here.
		if (this.fakeResponse == null)
			this.fakeResponse = new FakeHttpServletResponse(this.getTransformCtx().getRealResponse());

		return this.fakeResponse;
	}

	/**
	 */
	public Writer getWriter() throws IOException, ServletException
	{
		return this.getResponse().getWriter();
	}
}
