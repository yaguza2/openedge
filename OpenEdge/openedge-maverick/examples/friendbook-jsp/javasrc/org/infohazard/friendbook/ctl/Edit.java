/*
 * $Id: Edit.java,v 1.2 2003/01/12 04:03:23 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp/javasrc/org/infohazard/friendbook/ctl/Edit.java,v $
 */

package org.infohazard.friendbook.ctl;

import org.infohazard.friendbook.data.*;
import java.util.List;

/**
 * The controller for the login submission.
 */
public class Edit extends Protected
{
	/**
	 */
	protected String firstName;
	protected String lastName;
	protected String addrLine1;
	protected String addrLine2;
	protected String addrCity;
	protected String addrState;

	/**
	 * phones and emails are handled a little differently.
	 */
	protected List phoneList;
	protected List emailList;

	/**
	 */
	public String getFirstName() { return this.firstName; }
	public String getLastName() { return this.lastName; }
	public String getAddrLine1() { return this.addrLine1; }
	public String getAddrLine2() { return this.addrLine2; }
	public String getAddrCity() { return this.addrCity; }
	public String getAddrState() { return this.addrState; }

	/**
	 */
	public List getPhoneList() { return this.phoneList; }
	public List getEmailList() { return this.emailList; }

	/**
	 */
	protected String insidePerform() throws Exception
	{
		Friend me = FriendBook.getBook().findByLogin(this.currentLoginName());

		this.firstName = me.getFirstName();
		this.lastName = me.getLastName();
		this.addrLine1 = me.getAddress().getAddressLine1();
		this.addrLine2 = me.getAddress().getAddressLine2();
		this.addrCity = me.getAddress().getCity();
		this.addrState = me.getAddress().getState();

		this.phoneList = me.getPhoneList();
		this.emailList = me.getEmailList();

		return SUCCESS;
	}
}
