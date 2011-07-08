/*
 * $Id: ChangePasswordSubmit.java,v 1.1 2003/01/12 06:40:24 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp-fbu/javasrc/org/infohazard/friendbook/ctl/ChangePasswordSubmit.java,v $
 */

package org.infohazard.friendbook.ctl;

import org.infohazard.maverick.flow.ControllerContext;

import org.infohazard.friendbook.data.*;

/**
 * The controller for the submission of the change password form.
 */
public class ChangePasswordSubmit extends Protected
{
	/**
	 */
	public static class Form extends ModelErrorMap
	{
		/**
		 */
		protected String oldPassword = "";
		public String getOldPassword() { return this.oldPassword; }
		public void setOldPassword(String value) { this.oldPassword = value; }
		
		/**
		 */
		protected String newPassword = "";
		public String getNewPassword() { return this.newPassword; }
		public void setNewPassword(String value) { this.newPassword = value; }
		
		/**
		 */
		protected String newPasswordAgain = "";
		public void setNewPasswordAgain(String value) { this.newPasswordAgain = value; }	
		public String getNewPasswordAgain() { return this.newPasswordAgain; }
	}
	
	/**
	 */
	protected Object makeFormBean(ControllerContext cctx)
	{
		return new Form();
	}

	/**
	 */
	protected String insidePerform(Object formBean, ControllerContext ctx) throws Exception
	{
		Form form = (Form)formBean;
		
		// Validate the new password
		if (form.getNewPassword().length() < 3)
		{
			form.setError("newPassword", "Passwords must be at least 3 characters");
			form.setNewPassword("");
			form.setNewPasswordAgain("");
		}
		else if (!form.getNewPassword().equals(form.getNewPasswordAgain()))
		{
			form.setError("newPassword", "Your passwords did not match");
			form.setNewPassword("");
			form.setNewPasswordAgain("");
		}

		// Validate the old password
		FriendBook book = FriendBook.getBook();
		Friend f = book.findByLogin(this.currentLoginName(ctx));

		if (!f.getPassword().equals(form.getOldPassword()))
		{
			form.setError("oldPassword", "The old password was incorrect");
			form.setOldPassword("");
		}

		// Execute if appropriate
		if (form.hasErrors())
		{
			return ERROR;
		}
		else
		{
			f.setPassword(form.getNewPassword());

			return SUCCESS;
		}
	}
}
