/*
 * $Id: Friend.java,v 1.2 2002/06/06 12:23:53 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp/javasrc/org/infohazard/friendbook/data/Friend.java,v $
 */

package org.infohazard.friendbook.data;

import java.util.*;

/**
 * Data for a friend.
 */
public class Friend
{
	protected String login = "";
	protected String password = "";

	protected String firstName = "";
	protected String lastName = "";

	protected Address address = new Address();

	protected List phoneList = new LinkedList();
	protected List emailList = new LinkedList();

	/**
	 */
	public String getLogin() { return this.login; }
	public void setLogin(String login) { this.login = login; }

	/**
	 */
	public String getPassword() { return this.password; }
	public void setPassword(String password) { this.password = password; }

	/**
	 */
	public String getFirstName() { return this.firstName; }
	public void setFirstName(String first) { this.firstName = first; }

	/**
	 */
	public String getLastName() { return this.lastName; }
	public void setLastName(String last) { this.lastName = last; }

	/**
	 */
	public Address getAddress()	{ return this.address; }
	public void setAddress(Address addr) { this.address = addr; }

	/**
	 */
	public List getPhoneList() { return this.phoneList; }
	public void setPhoneList(List list) { this.phoneList = list; }

	/**
	 */
	public List getEmailList() { return this.emailList; }
	public void setEmailList(List list) { this.emailList = list; }
	
	/**
	 */
	public String toString()
	{
		return "login=" + this.login + ", password=" + this.password
				+ ", firstName=" + this.firstName + ", lastName=" + this.lastName;
	}
}
