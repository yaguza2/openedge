/*
 * $Id: Protected.java,v 1.2 2004/06/07 20:45:36 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp/javasrc/org/infohazard/friendbook/ctl/Protected.java,v $
 */

package org.infohazard.friendbook.ctl;


/**
 * Protected commands should use this controller or controllers
 * derived from it.  Might result in a "loginRequired" view,
 * otherwise will result in "success".
 */
public class Protected extends Gatekeeper
{
	/**
	 */
	protected static final String LOGIN_REQUIRED_VIEW_NAME = "loginRequired";

	/**
	 */
	protected String dest;
	public String getDest()				{ return this.dest; }
	public void setDest(String value)	{ this.dest = value; }
	
	/**
	 */
	protected final String outsidePerform() throws Exception
	{
		this.dest = this.getCtx().getRequest().getRequestURI();
		
		if (this.getCtx().getRequest().getQueryString() != null)
			this.dest += "?" + this.getCtx().getRequest().getQueryString();

		return LOGIN_REQUIRED_VIEW_NAME;
	}
	
	/**
	 * This method should be overriden to perform application logic
	 * which requires authentication.
	 */
	protected String insidePerform() throws Exception
	{
		return SUCCESS;
	}
}
