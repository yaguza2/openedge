/*
 * $Id: ControllerContext.java,v 1.3 2003/02/19 22:50:48 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/ControllerContext.java,v $
 */

package org.infohazard.maverick.flow;

import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ControllerContext defines the methods and data available to a controller for a single
 * request.
 */
public interface ControllerContext
{
	/**
	 * Basic data
	 */
	public HttpServletRequest getRequest();

	/**
	 * Basic data
	 */
	public HttpServletResponse getResponse();

	/**
	 * Basic data
	 */
	public ServletConfig getServletConfig();

	/**
	 * Basic data
	 */
	public ServletContext getServletContext();

	/**
	 * Set the model to be rendered.
	 */
	public void setModel(Object mod);

	/**
	 * @return the model which was set.
	 */
	public Object getModel();

	/**
	 * Sets a parameter to the controller (not view or transforms).
	 */
	public void setControllerParam(String name, Object value);

	/**
	 * Sets a parameter to the view (not controller or transforms).
	 */
	public void setViewParam(String name, Object value);

	/**
	 * Sets a parameter to the transforms (not controller or view).
	 */
	public void setTransformParam(String name, Object value);

	/**
	 * @return any params set on the controller node, or null if none.
	 */
	public Map<String, Object> getControllerParams();
}
