/*
 * $Id: Controller.java,v 1.4 2003/10/27 11:00:42 thusted Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/Controller.java,v $
 */

package org.infohazard.maverick.flow;

import javax.servlet.*;

/**
 * Controller is the interface that all command controllers must implement.
 * A controller which implements this interface (and not ControllerSingleton)
 * will have a new instance instantiated for every request.
 */
public interface Controller
{
	/**
	 * @return which view to switch to
	 */
	public String go(ControllerContext cctx) throws ServletException;
}
