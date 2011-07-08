/*
 * $Id: Throwaway2.java,v 1.3 2003/10/27 11:00:39 thusted Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/ctl/Throwaway2.java,v $
 */

package org.infohazard.maverick.ctl;

import org.infohazard.maverick.flow.Controller;
import org.infohazard.maverick.flow.ControllerContext;
import javax.servlet.*;

/**
 * Throwaway2 is a base class for simple controllers which implements
 * the single-use controller pattern (a fresh controller instance is
 * created to service each request).  No population of properties is
 * performed by this class.
 */
public abstract class Throwaway2 implements Controller
{
	/**
	 * Common name for the typical "success" view.
	 */
	public static final String SUCCESS = "success";

	/**
	 * Common name for the typical "error" view.
	 */
	public static final String ERROR = "error";

	/**
	 */
	private ControllerContext controllerCtx;
	
	/**
	 * Sets up the servlet parameters and calls through to the
	 * parameterless rawPerform() method.  Does not result in
	 * bean population.
	 *
	 * @see Controller#go
	 */
	public final String go(ControllerContext cctx) throws ServletException
	{
		try
		{
			this.controllerCtx = cctx;

			return this.go();
		}
		catch (Exception ex)
		{
			throw new ServletException(ex);
		}
	}

	/**
	 * This is the method you should override to implement application logic.
	 */
	protected abstract String go() throws Exception;

	/**
	 * Obtain the controller context for this request.
	 */
	protected ControllerContext getCtx()
	{
		return this.controllerCtx;
	}
}
