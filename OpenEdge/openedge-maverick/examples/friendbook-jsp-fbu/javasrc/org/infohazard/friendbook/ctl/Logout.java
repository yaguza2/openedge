/*
 * $Id: Logout.java,v 1.1 2003/01/12 06:40:24 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp-fbu/javasrc/org/infohazard/friendbook/ctl/Logout.java,v $
 */

package org.infohazard.friendbook.ctl;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * The controller to log somoene out
 */
public class Logout extends Protected
{
	/**
	 */
	protected String insidePerform(Object formBean, ControllerContext ctx) throws Exception
	{
		this.logout(ctx);

		return SUCCESS;
	}
}
