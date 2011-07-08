/*
 * $Id: Friends.java,v 1.3 2003/01/12 04:03:23 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp/javasrc/org/infohazard/friendbook/ctl/Friends.java,v $
 */

package org.infohazard.friendbook.ctl;

import org.infohazard.friendbook.data.FriendBook;
import java.util.Collection;

/**
 * The controller for the login submission.
 */
public class Friends extends Protected
{
	/**
	 */
	protected Collection friends;

	/**
	 */
	public Collection getFriends()
	{
		return this.friends;
	}

	/**
	 */
	public String getMyLogin()
	{
		return this.currentLoginName();
	}

	/**
	 */
	protected String insidePerform() throws Exception
	{
		this.friends = FriendBook.getBook().findAll();

		return SUCCESS;
	}

}
