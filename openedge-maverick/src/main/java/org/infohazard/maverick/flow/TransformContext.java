/*
 * $Id: TransformContext.java,v 1.2 2003/02/19 22:50:49 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/TransformContext.java,v $
 */

package org.infohazard.maverick.flow;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TransformContext defines the methods and data available to a transform for a single
 * request.
 */
public interface TransformContext
{
	/**
	 */
	public HttpServletRequest getRequest();

	/**
	 */
	public ServletContext getServletContext();

	/**
	 * Obtain any params that were set.
	 */
	public Map getTransformParams();

	/**
	 * @return the next step in the transformation process
	 * 
	 *         Call this ONLY ONCE per step!
	 */
	public TransformStep getNextStep() throws ServletException;

	/**
	 * @return true if the transformation chain is going to be halted prematurely due to
	 *         user request.
	 */
	public boolean halting();

	/**
	 * Returns the *real* response object. Do not use this unless you know are the tail!
	 */
	public HttpServletResponse getRealResponse();
}
