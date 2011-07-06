/*
 * $Id: Logout.java,v 1.3 2003/01/12 04:03:23 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp/javasrc/org/infohazard/friendbook/ctl/Logout.java,v $
 */

package org.infohazard.friendbook.ctl;

/**
 * The controller to log somoene out
 */
public class Logout extends Protected
{
	/**
	 */
	protected String insidePerform() throws Exception
	{
		this.logout();

		return SUCCESS;
	}
}