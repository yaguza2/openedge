/*
 * $Id: ChangePasswordSubmit.java,v 1.3 2003/01/12 04:03:23 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp/javasrc/org/infohazard/friendbook/ctl/ChangePasswordSubmit.java,v $
 */

package org.infohazard.friendbook.ctl;

import org.infohazard.friendbook.data.*;

/**
 * The controller for the submission of the change password form.  Since
 * the error case simply sends back to the changePassword view, we extend
 * the initial ChangePassword controller to accomodate its form values.
 */
public class ChangePasswordSubmit extends ChangePassword
{
	public void setOldPassword(String value) { this.oldPassword = value; }
	public void setNewPassword(String value) { this.newPassword = value; }
	public void setNewPasswordAgain(String value) { this.newPasswordAgain = value; }

	/**
	 */
	public String insidePerform() throws Exception
	{
		// Validate the new password
		if (this.newPassword.length() < 3)
		{
			this.addError("newPassword", "Passwords must be at least 3 characters");
			this.newPassword = "";
			this.newPasswordAgain = "";
		}
		else if (!this.newPassword.equals(this.newPasswordAgain))
		{
			this.addError("newPassword", "Your passwords did not match");
			this.newPassword = "";
			this.newPasswordAgain = "";
		}

		// Validate the old password
		FriendBook book = FriendBook.getBook();
		Friend f = book.findByLogin(this.currentLoginName());

		if (!f.getPassword().equals(this.oldPassword))
		{
			this.addError("oldPassword", "The old password was incorrect");
			this.oldPassword = "";
		}

		// Execute if appropriate
		if (this.hasErrors())
		{
			return ERROR;
		}
		else
		{
			f.setPassword(this.newPassword);

			return SUCCESS;
		}
	}
}
