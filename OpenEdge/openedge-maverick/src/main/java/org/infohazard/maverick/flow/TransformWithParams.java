/*
 * $Id: TransformWithParams.java,v 1.2 2003/02/19 22:50:49 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/TransformWithParams.java,v $
 */

package org.infohazard.maverick.flow;

import java.util.Map;
import javax.servlet.ServletException;

/**
 * This is a Decorator pattern, adding params defined for a transform.
 */
class TransformWithParams implements Transform
{
	/**
	 */
	protected Transform decorated;
	protected Map params;
	
	/**
	 */
	public TransformWithParams(Transform simpler, Map params)
	{
		if (params == null)
			throw new IllegalArgumentException("Don't use this decorator without params");
			
		this.decorated = simpler;
		this.params = params;
	}
	 
	/**
	 * Sets some params before creating the step
	 */
	public TransformStep createStep(TransformContext tctx) throws ServletException
	{
		// Get access to the internal API.
		((MaverickContext)tctx).putAllTransformParams(this.params);
		
		return this.decorated.createStep(tctx);
	}
}
