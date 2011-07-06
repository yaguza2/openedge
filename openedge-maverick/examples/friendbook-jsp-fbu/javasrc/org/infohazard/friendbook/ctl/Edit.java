/*
 * $Id: Edit.java,v 1.1 2003/01/12 06:40:24 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp-fbu/javasrc/org/infohazard/friendbook/ctl/Edit.java,v $
 */

package org.infohazard.friendbook.ctl;

import org.infohazard.maverick.flow.ControllerContext;

import org.infohazard.friendbook.data.*;
import java.util.List;

/**
 * The controller for the login submission.
 */
public class Edit extends Protected
{
	/**
	 */
	public static class Form extends ModelErrorMap
	{
		/**
		 */
		protected String firstName;
		public String getFirstName()			{ return this.firstName; }
		public void setFirstName(String value)	{ this.firstName = value; }
		
		/**
		 */
		protected String lastName;
		public String getLastName()				{ return this.lastName; }
		public void setLastName(String value)	{ this.lastName = value; }
		
		/**
		 */
		protected String addrLine1;
		public String getAddrLine1()			{ return this.addrLine1; }
		public void setAddrLine1(String value)	{ this.addrLine1 = value; }

		/**
		 */
		protected String addrLine2;
		public String getAddrLine2()			{ return this.addrLine2; }
		public void setAddrLine2(String value)	{ this.addrLine2 = value; }
		
		/**
		 */
		protected String addrCity;
		public String getAddrCity()				{ return this.addrCity; }
		public void setAddrCity(String value)	{ this.addrCity = value; }
		
		/**
		 */
		protected String addrState;
		public String getAddrState()			{ return this.addrState; }
		public void setAddrState(String value)	{ this.addrState = value; }
	
		/**
		 * phones and emails are handled a little differently.
		 */
		protected List phoneList;
		public List getPhoneList()				{ return this.phoneList; }
		public void setPhoneList(List value)	{ this.phoneList = value; }

		/**
		 * phones and emails are handled a little differently.
		 */
		protected List emailList;
		public List getEmailList()				{ return this.emailList; }
		public void setEmailList(List value)	{ this.emailList = value; }
	
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
		
		Friend me = FriendBook.getBook().findByLogin(this.currentLoginName(ctx));

		form.setFirstName(me.getFirstName());
		form.setLastName(me.getLastName());
		form.setAddrLine1(me.getAddress().getAddressLine1());
		form.setAddrLine2(me.getAddress().getAddressLine2());
		form.setAddrCity(me.getAddress().getCity());
		form.setAddrState(me.getAddress().getState());

		form.setPhoneList(me.getPhoneList());
		form.setEmailList(me.getEmailList());

		return SUCCESS;
	}
}