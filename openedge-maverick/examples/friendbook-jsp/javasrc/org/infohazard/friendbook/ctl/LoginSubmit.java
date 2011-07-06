/*
 * $Id: LoginSubmit.java,v 1.3 2004/06/07 20:45:36 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp/javasrc/org/infohazard/friendbook/ctl/LoginSubmit.java,v $
 */
package org.infohazard.friendbook.ctl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author Jeff Schnitzer
 * @version $Revision: 1.3 $ $Date: 2004/06/07 20:45:36 $
 */
public class LoginSubmit extends ControllerAuth
{
	/**
	 */
	private static Log logger = LogFactory.getLog(LoginSubmit.class);

	/**
	 * If no dest is specified, this is where we go on successful login
	 */
	public static final String DEFAULT_DEST = "friends.m";
	
	/**
	 */
	protected String name;
	public String getName()				{ return this.name; }
	public void setName(String value)	{ this.name = value; }

	/**
	 */
	protected String password;
	public String getPassword()				{ return this.password; }
	public void setPassword(String value)	{ this.password = value; }

	/**
	 */
	protected String dest;
	public String getDest()				{ return this.dest; }
	public void setDest(String value)	{ this.dest = value; }
	
	/**
	 */
	protected String perform() throws Exception
	{
		if (!this.login(this.name, this.password))
		{
			return ERROR;
		}
		else	// they are now logged in...
		{
			// Target of redirect
			if (this.dest == null || this.dest.trim().length() == 0)
				this.getCtx().setModel(DEFAULT_DEST);
			else
				this.getCtx().setModel(this.dest);
			
			return SUCCESS;
		}
	}
}

