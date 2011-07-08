/*
 * $Id: Protected.java,v 1.2 2004/06/07 20:43:57 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp-fbu/javasrc/org/infohazard/friendbook/ctl/Protected.java,v $
 */

package org.infohazard.friendbook.ctl;

import org.infohazard.maverick.flow.ControllerContext;

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
	protected String outsidePerform(Object formBean, ControllerContext ctx) throws Exception
	{
		LoginSubmit.Form form = new LoginSubmit.Form();
		
		String dest = ctx.getRequest().getRequestURI();
		
		if (ctx.getRequest().getQueryString() != null)
			dest += "?" + ctx.getRequest().getQueryString();

		form.setDest(dest);

		ctx.setModel(form);
		
		return LOGIN_REQUIRED_VIEW_NAME;
	}
	
	/**
	 * This method should be overriden to perform application logic
	 * which requires authentication.
	 */
	protected String insidePerform(Object formBean, ControllerContext ctx) throws Exception
	{
		return SUCCESS;
	}
}
