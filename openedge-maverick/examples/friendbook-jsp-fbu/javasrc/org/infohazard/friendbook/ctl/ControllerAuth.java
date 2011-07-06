/*
 * $Id: ControllerAuth.java,v 1.2 2004/06/07 20:43:57 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp-fbu/javasrc/org/infohazard/friendbook/ctl/ControllerAuth.java,v $
 */

package org.infohazard.friendbook.ctl;

import org.infohazard.friendbook.data.Friend;
import org.infohazard.friendbook.data.FriendBook;
import org.infohazard.maverick.flow.ControllerContext;


/**
 * Controller support class which provides basic authentication services
 * to subclasses.
 */
abstract public class ControllerAuth extends org.infohazard.maverick.ctl.FormBeanUser
{
	/**
	 * The name of the session attribute which stores the login name.
	 */
	protected static final String LOGIN_ATTRNAME = "loginName";

	/**
	 * @return the login of the user currently logged in, or null if no
	 *	user is logged in.
	 */
	protected String currentLoginName(ControllerContext ctx)
	{
		return (String)ctx.getRequest().getSession().getAttribute(LOGIN_ATTRNAME);
	}

	/**
	 * Try to log in with the specified credentials.
	 * @return true if it worked, false if the credentials are bad.
	 */
	protected boolean login(String login, String password, ControllerContext ctx)
	{
		FriendBook book = FriendBook.getBook();

		Friend f = book.findByLogin(login);

		if (f == null || !f.getPassword().equals(password))
		{
			return false;
		}
		else
		{
			ctx.getRequest().getSession().setAttribute(LOGIN_ATTRNAME, login);
			return true;
		}
	}

	/**
	 */
	protected void logout(ControllerContext ctx)
	{
		ctx.getRequest().getSession().removeAttribute(LOGIN_ATTRNAME);
	}
	
	/**
	 */
	protected boolean isLoggedIn(ControllerContext ctx)
	{
		return ctx.getRequest().getSession().getAttribute(LOGIN_ATTRNAME) != null;
	}
}
