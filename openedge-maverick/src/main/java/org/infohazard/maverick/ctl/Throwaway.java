/*
 * $Id: Throwaway.java,v 1.7 2003/10/27 11:00:39 thusted Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/ctl/Throwaway.java,v $
 */

package org.infohazard.maverick.ctl;

import org.infohazard.maverick.flow.Controller;
import org.infohazard.maverick.flow.ControllerContext;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Note:  While not formally deprecated, use of this class is
 * discouraged.  You should use Throwaway2 instead.
 *
 * Throwaway is a base class for simple controllers which implements
 * the single-use controller pattern (a fresh controller instance is
 * created to service each request).  No population of properties is
 * performed by this class.
 */
public abstract class Throwaway implements Controller
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

			String result = this.rawPerform();
			
			this.controllerCtx.setModel(this.model());
			
			return result;
		}
		catch (Exception ex)
		{
			throw new ServletException(ex);
		}
	}

	/**
	 * This is the method you should override to implement application logic.
	 */
	protected abstract String rawPerform() throws Exception;

	/**
	 * This is the method you should override to return the data model after
	 * rawPerform() is executed.
	 */
	public abstract Object model();
	
	/**
	 * @return the ControllerContext
	 */
	protected ControllerContext getCtx()
	{
		return this.controllerCtx;
	}

	/**
	 * @return the servlet request object
	 */
	protected HttpServletRequest getRequest()
	{
		return this.controllerCtx.getRequest();
	}

	/**
	 * @return the servlet response object
	 */
	protected HttpServletResponse getResponse()
	{
		return this.controllerCtx.getResponse();
	}

	/**
	 * @return the servlet session
	 */
	protected HttpSession getSession()
	{
		return this.getRequest().getSession();
	}

	/**
	 * @return the servlet configuration object
	 */
	protected ServletConfig getServletConfig()
	{
		return this.controllerCtx.getServletConfig();
	}

	/**
	 * @return the webapp context object
	 */
	protected ServletContext getServletContext()
	{
		return this.getServletConfig().getServletContext();
	}
}