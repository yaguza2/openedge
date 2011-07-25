/*
 * $Id: ChangePassword.java,v 1.2 2003/01/12 04:03:23 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp/javasrc/org/infohazard/friendbook/ctl/ChangePassword.java,v $
 */

package org.infohazard.friendbook.ctl;

/**
 * The initial controller for the change password form.
 */
public class ChangePassword extends Protected
{
	protected String oldPassword = "";
	protected String newPassword = "";
	protected String newPasswordAgain = "";

	public String getOldPassword() { return this.oldPassword; }
	public String getNewPassword() { return this.newPassword; }
	public String getNewPasswordAgain() { return this.newPasswordAgain; }
}
