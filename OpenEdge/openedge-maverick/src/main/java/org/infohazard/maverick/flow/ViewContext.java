/*
 * $Id: ViewContext.java,v 1.2 2003/02/19 22:50:49 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/ViewContext.java,v $
 */

package org.infohazard.maverick.flow;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ViewContext defines the methods and data available to a view for a single request.
 */
public interface ViewContext
{
	/**
	 * Obtain the model which is to be rendered.
	 */
	public Object getModel();

	/**
	 * Obtain any params that were set.
	 */
	public Map<String, Object> getViewParams();

	/**
	 * This is where output should be sent. If it returns null, there are no transforms,
	 * and you should use the real response.
	 */
	public TransformStep getNextStep() throws ServletException;

	/**
	 */
	public HttpServletRequest getRequest();

	/**
	 */
	public ServletContext getServletContext();

	/**
	 * Returns the *real* response object. Do not use this unless you know are the tail!
	 */
	public HttpServletResponse getRealResponse();
}
