/*
 * $Id: SignupSubmit.java,v 1.3 2002/06/06 12:23:53 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp/javasrc/org/infohazard/friendbook/ctl/SignupSubmit.java,v $
 */

package org.infohazard.friendbook.ctl;

import org.infohazard.friendbook.data.*;

/**
 * The controller for the submission of the signup form.  Since the error
 * case simply sends back to the signup view, we provide input values to
 * the form as well.
 */
public class SignupSubmit extends ControllerAuth
{
	protected String loginName = "";
	protected String password = "";
	protected String passwordAgain = "";

	public String getLoginName() { return this.loginName; }
	public void setLoginName(String value) { this.loginName = value; }

	public String getPassword() { return this.password; }
	public void setPassword(String value) { this.password = value; }

	public String getPasswordAgain() { return this.passwordAgain; }
	public void setPasswordAgain(String value) { this.passwordAgain = value; }

	/**
	 */
	public String perform() throws Exception
	{
		// Validate the password
		if (this.password.length() < 3)
		{
			this.addError("password", "Passwords must be at least 3 characters");
			this.password = "";
			this.passwordAgain = "";
		}
		else if (!this.password.equals(this.passwordAgain))
		{
			this.addError("password", "Your passwords did not match");
			this.password = "";
			this.passwordAgain = "";
		}

		// Validate the loginName
		FriendBook book = FriendBook.getBook();

		if (this.loginName.length() == 0)
		{
			this.addError("loginName", "You must choose a login name!");
		}
		else if (book.findByLogin(this.loginName) != null)
		{
			this.addError("loginName", "Someone already has that name");
			//this.loginName = "";
		}

		// Execute if appropriate
		if (this.hasErrors())
			return ERROR;
		else
		{
			Friend f = new Friend();
			f.setLogin(this.loginName);
			f.setPassword(this.password);

			book.addFriend(f);

			this.login(this.loginName, this.password);

			return SUCCESS;
		}
	}
}