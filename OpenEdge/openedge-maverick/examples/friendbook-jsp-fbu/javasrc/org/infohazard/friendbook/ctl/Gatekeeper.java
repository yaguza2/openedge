/*
 * $Id: Gatekeeper.java,v 1.2 2004/06/07 20:43:59 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp-fbu/javasrc/org/infohazard/friendbook/ctl/Gatekeeper.java,v $
 */

package org.infohazard.friendbook.ctl;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * This Controller provides different behavior depending on whether
 * the user is "inside" (authenticated) or "outside" (unauthenticated).
 */
public class Gatekeeper extends ControllerAuth
{
	/**
	 */
	protected static final String INSIDE_VIEW_NAME = "inside";
	protected static final String OUTSIDE_VIEW_NAME = "outside";
	
	/**
	 * One of these methods will be called depending on if the user has
	 * been logged in or not.
	 */
	protected String outsidePerform(Object formBean, ControllerContext ctx) throws Exception { return OUTSIDE_VIEW_NAME; }
	protected String insidePerform(Object formBean, ControllerContext ctx) throws Exception { return INSIDE_VIEW_NAME; }

	/**
	 * We must implement this abstract method, but by default it does nothing.
	 */
	protected Object makeFormBean(ControllerContext cctx)
	{
		return null;
	}

	/**
	 * Our protection logic.
	 */
	protected final String perform(Object formBean, ControllerContext ctx) throws Exception
	{
		if (this.isLoggedIn(ctx))
		{
			return this.insidePerform(formBean, ctx);
		}
		else
		{
			return this.outsidePerform(formBean, ctx);
		}
	}

}
