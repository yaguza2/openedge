/*
 * $Id: Friends.java,v 1.1 2003/01/12 06:40:24 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp-fbu/javasrc/org/infohazard/friendbook/ctl/Friends.java,v $
 */

package org.infohazard.friendbook.ctl;

import org.infohazard.maverick.flow.ControllerContext;

import org.infohazard.friendbook.data.FriendBook;
import java.util.Collection;

/**
 * Show your friends.
 */
public class Friends extends Protected
{
	/**
	 */
	public static class Form extends ModelErrorMap
	{
		/**
		 */
		protected Collection friends;
		public Collection getFriends()				{ return this.friends; }
		public void setFriends(Collection value)	{ this.friends = value; }
	
		/**
		 */
		protected String myLogin;
		public String getMyLogin()				{ return this.myLogin; }
		public void setMyLogin(String value)	{ this.myLogin = value; }
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
		
		form.setMyLogin(this.currentLoginName(ctx));
		form.setFriends(FriendBook.getBook().findAll());

		return SUCCESS;
	}

}