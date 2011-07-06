/*
 * $Id: ViewWithParams.java,v 1.2 2003/02/19 22:50:49 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/ViewWithParams.java,v $
 */

package org.infohazard.maverick.flow;

import java.util.*;
import java.io.IOException;
import javax.servlet.ServletException;

/**
 * ViewWithTransforms is a decorator that sets params when
 * rendering a view.
 */
class ViewWithParams implements View
{
	/**
	 */
	protected View decorated;
	protected Map params;
	
	/**
	 */
	public ViewWithParams(View decorate, Map params)
	{
		if (params == null)
			throw new IllegalArgumentException("Don't use this decorator without params");
			
		this.decorated = decorate;
		this.params = params;
	}
	
	/**
	 */
	public void go(ViewContext vctx) throws IOException, ServletException
	{
		((MaverickContext)vctx).putAllViewParams(this.params);
		
		this.decorated.go(vctx);
	}
}