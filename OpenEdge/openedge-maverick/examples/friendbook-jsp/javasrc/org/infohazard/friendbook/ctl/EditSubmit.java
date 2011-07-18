/*
 * $Id: EditSubmit.java,v 1.3 2004/06/07 20:45:36 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp/javasrc/org/infohazard/friendbook/ctl/EditSubmit.java,v $
 */

package org.infohazard.friendbook.ctl;

import java.util.LinkedList;

import org.infohazard.friendbook.data.Friend;
import org.infohazard.friendbook.data.FriendBook;

/**
 * The controller for the edit page submission.
 */
public class EditSubmit extends Edit
{
	/**
	 */
	protected void processParameters()
	{
		// We need to process the input parameters to extract the dynamic phone
		// and email lists.
		this.phoneList = new LinkedList();
		this.emailList = new LinkedList();

		// Iterate through the email list.  Index starts at 1.
		int i = 1;

		while (true)
		{
			String value = this.getCtx().getRequest().getParameter("email" + i);
			String del = this.getCtx().getRequest().getParameter("del_email" + i);

			// Once we get a null we have hit the max
			if (value == null)
				break;

			// Only add ones that are not marked for deletion and not empty
			if (del == null && !value.trim().equals(""))
				this.emailList.add(value);

			i++;
		}

		// Reset the counter to walk through the phone numbers
		i = 1;

		while (true)
		{
			String value = this.getCtx().getRequest().getParameter("phone" + i);
			String del = this.getCtx().getRequest().getParameter("del_phone" + i);

			// Once we get a null we have hit the max
			if (value == null)
				break;

			// Only add ones that are not marked for deletion and not empty
			if (del == null && !value.trim().equals(""))
				this.phoneList.add(value);

			i++;
		}
	}

	/**
	 */
	public void setFirstName(String value) { this.firstName = value; }
	public void setLastName(String value) { this.lastName = value; }
	public void setAddrLine1(String value) { this.addrLine1 = value; }
	public void setAddrLine2(String value) { this.addrLine2 = value; }
	public void setAddrCity(String value) { this.addrCity = value; }
	public void setAddrState(String value) { this.addrState = value; }

	/**
	 */
	protected String insidePerform() throws Exception
	{
		Friend me = FriendBook.getBook().findByLogin(this.currentLoginName());

		me.setFirstName(this.firstName);
		me.setLastName(this.lastName);

		me.getAddress().setAddressLine1(this.addrLine1);
		me.getAddress().setAddressLine2(this.addrLine2);
		me.getAddress().setCity(this.addrCity);
		me.getAddress().setState(this.addrState);

		this.processParameters();

		me.setPhoneList(this.phoneList);
		me.setEmailList(this.emailList);

		return SUCCESS;
	}
}
