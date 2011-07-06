/*
 * $Id: LoginSubmit.java,v 1.3 2004/06/07 20:43:59 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp-fbu/javasrc/org/infohazard/friendbook/ctl/LoginSubmit.java,v $
 */
package org.infohazard.friendbook.ctl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infohazard.maverick.flow.ControllerContext;

/**
 * @author Jeff Schnitzer
 * @version $Revision: 1.3 $ $Date: 2004/06/07 20:43:59 $
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
	public static class Form
	{
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
	}
	
	/**
	 */
	protected Object makeFormBean(ControllerContext cctx)
	{
		return new Form();
	}

	/**
	 */
	protected String perform(Object formBean, ControllerContext ctx) throws Exception
	{
		Form form = (Form)formBean;
		
		if (!this.login(form.getName(), form.getPassword(), ctx))
		{
			return ERROR;
		}
		else	// they are now logged in...
		{
			// Target of redirect
			if (form.getDest() == null || form.getDest().trim().length() == 0)
				ctx.setModel(DEFAULT_DEST);
			else
				ctx.setModel(form.getDest());
			
			return SUCCESS;
		}
	}
}

