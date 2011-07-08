/*
 * $Id: Gatekeeper.java,v 1.2 2004/06/07 20:45:36 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp/javasrc/org/infohazard/friendbook/ctl/Gatekeeper.java,v $
 */

package org.infohazard.friendbook.ctl;


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
	protected String outsidePerform() throws Exception { return OUTSIDE_VIEW_NAME; }
	protected String insidePerform() throws Exception { return INSIDE_VIEW_NAME; }

	/**
	 * Our protection logic.
	 */
	public final String perform() throws Exception
	{
		if (this.isLoggedIn())
		{
			return this.insidePerform();
		}
		else
		{
			return this.outsidePerform();
		}
	}

}
