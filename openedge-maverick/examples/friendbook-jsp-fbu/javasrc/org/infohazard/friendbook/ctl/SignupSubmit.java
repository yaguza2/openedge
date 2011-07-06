/*
 * $Id: SignupSubmit.java,v 1.1 2003/01/12 06:40:24 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp-fbu/javasrc/org/infohazard/friendbook/ctl/SignupSubmit.java,v $
 */

package org.infohazard.friendbook.ctl;

import org.infohazard.maverick.flow.ControllerContext;

import org.infohazard.friendbook.data.*;

/**
 * The controller for the submission of the signup form.  Since the error
 * case simply sends back to the signup view, we provide input values to
 * the form as well.
 */
public class SignupSubmit extends ControllerAuth
{
	/**
	 */
	public static class Form extends ModelErrorMap
	{
		/**
		 */
		protected String loginName = "";
		public String getLoginName() { return this.loginName; }
		public void setLoginName(String value) { this.loginName = value; }
	
		/**
		 */
		protected String password = "";
		public String getPassword() { return this.password; }
		public void setPassword(String value) { this.password = value; }
	
		/**
		 */
		protected String passwordAgain = "";
		public String getPasswordAgain() { return this.passwordAgain; }
		public void setPasswordAgain(String value) { this.passwordAgain = value; }
	}
	
	/**
	 */
	protected Object makeFormBean(ControllerContext cctx)
	{
		return new Form();
	}


	/**
	 */
	protected String perform(Object formBean, ControllerContext ctx) throws Exception
	{
		Form form = (Form)formBean;
		
		// Validate the password
		if (form.getPassword().length() < 3)
		{
			form.setError("password", "Passwords must be at least 3 characters");
			form.setPassword("");
			form.setPasswordAgain("");
		}
		else if (!form.getPassword().equals(form.getPasswordAgain()))
		{
			form.setError("password", "Your passwords did not match");
			form.setPassword("");
			form.setPasswordAgain("");
		}

		// Validate the loginName
		FriendBook book = FriendBook.getBook();

		if (form.getLoginName().length() == 0)
		{
			form.setError("loginName", "You must choose a login name!");
		}
		else if (book.findByLogin(form.getLoginName()) != null)
		{
			form.setError("loginName", "Someone already has that name");
		}

		// Execute if appropriate
		if (form.hasErrors())
			return ERROR;
		else
		{
			Friend f = new Friend();
			f.setLogin(form.getLoginName());
			f.setPassword(form.getPassword());

			book.addFriend(f);

			this.login(form.getLoginName(), form.getPassword(), ctx);

			return SUCCESS;
		}
	}
}