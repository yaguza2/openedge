/*
 * $Id: EditSubmit.java,v 1.2 2004/06/07 20:43:57 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp-fbu/javasrc/org/infohazard/friendbook/ctl/EditSubmit.java,v $
 */

package org.infohazard.friendbook.ctl;

import java.util.LinkedList;
import java.util.List;

import org.infohazard.friendbook.data.Friend;
import org.infohazard.friendbook.data.FriendBook;
import org.infohazard.maverick.flow.ControllerContext;

/**
 * The controller for the edit page submission.
 */
public class EditSubmit extends Protected
{
	/**
	 */
	protected Object makeFormBean(ControllerContext cctx)
	{
		return new Edit.Form();
	}

	/**
	 */
	protected void processParameters(Edit.Form form, ControllerContext ctx)
	{
		// We need to process the input parameters to extract the dynamic phone
		// and email lists.
		List phoneList = new LinkedList();
		form.setPhoneList(phoneList);
		
		List emailList = new LinkedList();
		form.setEmailList(emailList);

		// Iterate through the email list.  Index starts at 1.
		int i = 1;

		while (true)
		{
			String value = ctx.getRequest().getParameter("email" + i);
			String del = ctx.getRequest().getParameter("del_email" + i);

			// Once we get a null we have hit the max
			if (value == null)
				break;

			// Only add ones that are not marked for deletion and not empty
			if (del == null && !value.trim().equals(""))
				emailList.add(value);

			i++;
		}

		// Reset the counter to walk through the phone numbers
		i = 1;

		while (true)
		{
			String value = ctx.getRequest().getParameter("phone" + i);
			String del = ctx.getRequest().getParameter("del_phone" + i);

			// Once we get a null we have hit the max
			if (value == null)
				break;

			// Only add ones that are not marked for deletion and not empty
			if (del == null && !value.trim().equals(""))
				phoneList.add(value);

			i++;
		}
	}

	/**
	 */
	protected String insidePerform(Object formBean, ControllerContext ctx) throws Exception
	{
		Edit.Form form = (Edit.Form)formBean;
		
		Friend me = FriendBook.getBook().findByLogin(this.currentLoginName(ctx));

		me.setFirstName(form.getFirstName());
		me.setLastName(form.getLastName());

		me.getAddress().setAddressLine1(form.getAddrLine1());
		me.getAddress().setAddressLine2(form.getAddrLine2());
		me.getAddress().setCity(form.getAddrCity());
		me.getAddress().setState(form.getAddrState());

		this.processParameters(form, ctx);

		me.setPhoneList(form.getPhoneList());
		me.setEmailList(form.getEmailList());

		return SUCCESS;
	}
}
