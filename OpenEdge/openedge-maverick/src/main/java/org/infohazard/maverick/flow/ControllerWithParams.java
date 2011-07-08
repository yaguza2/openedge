/*
 * $Id: ControllerWithParams.java,v 1.3 2004/06/27 17:41:31 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/ControllerWithParams.java,v $
 */

package org.infohazard.maverick.flow;

import java.util.Map;
import javax.servlet.ServletException;

/**
 * This is a Decorator pattern, adding params defined for a controller.
 */
public class ControllerWithParams implements Controller
{
	/**
	 * the decorated controller.
	 */
	protected Controller decorated;
	/**
	 * the controller parameters.
	 */
	protected Map params;
	
	/**
	 * Create a decorator for the given controller with the given parameters.
	 * @param decorate the controller to decorate
	 * @param params the parameters
	 */
	public ControllerWithParams(Controller decorate, Map params)
	{
		if (params == null)
			throw new IllegalArgumentException("Don't use this decorator without params");
			
		this.decorated = decorate;
		this.params = params;
	}

	/**
	 * Puts all controller parameters and then defers execution to the command
	 * method of the controller.
	 * @see org.infohazard.maverick.flow.Controller#go(org.infohazard.maverick.flow.ControllerContext)
	 */
	public String go(ControllerContext cctx) throws ServletException
	{
		((MaverickContext)cctx).putAllControllerParams(this.params);
		
		return this.decorated.go(cctx);
	}
    /**
     * Get decorated.
     * @return Controller Returns the decorated.
     */
    public Controller getDecorated()
    {
        return decorated;
    }
}
