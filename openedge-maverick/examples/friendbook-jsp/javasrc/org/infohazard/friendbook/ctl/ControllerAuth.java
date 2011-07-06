/*
 * $Id: ControllerAuth.java,v 1.4 2004/06/07 20:45:36 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp/javasrc/org/infohazard/friendbook/ctl/ControllerAuth.java,v $
 */

package org.infohazard.friendbook.ctl;

import org.infohazard.friendbook.data.Friend;
import org.infohazard.friendbook.data.FriendBook;

/**
 * Controller support class which provides basic authentication services
 * to subclasses.  All this could be folded into ControllerProtected
 * except that the SignupSubmit controller needs to be able to login()
 * from an (obviously) unprotected page.
 */
public class ControllerAuth extends ControllerErrorable
{
	/**
	 * The name of the session attribute which stores the login name.
	 */
	protected static final String LOGIN_ATTRNAME = "loginName";

	/**
	 * @return the login of the user currently logged in, or null if no
	 *	user is logged in.
	 */
	protected String currentLoginName()
	{
		return (String)this.getCtx().getRequest().getSession().getAttribute(LOGIN_ATTRNAME);
	}

	/**
	 * Try to log in with the specified credentials.
	 * @return true if it worked, false if the credentials are bad.
	 */
	protected boolean login(String login, String password)
	{
		FriendBook book = FriendBook.getBook();

		Friend f = book.findByLogin(login);

		if (f == null || !f.getPassword().equals(password))
		{
			return false;
		}
		else
		{
			this.getCtx().getRequest().getSession().setAttribute(LOGIN_ATTRNAME, login);
			return true;
		}
	}

	/**
	 */
	protected void logout()
	{
		this.getCtx().getRequest().getSession().removeAttribute(LOGIN_ATTRNAME);
	}
	
	/**
	 */
	protected boolean isLoggedIn()
	{
		return this.getCtx().getRequest().getSession().getAttribute(LOGIN_ATTRNAME) != null;
	}
}
