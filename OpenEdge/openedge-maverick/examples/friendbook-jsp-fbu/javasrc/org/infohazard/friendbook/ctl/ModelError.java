/*
 * $Id: ModelError.java,v 1.1 2003/01/12 06:40:24 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp-fbu/javasrc/org/infohazard/friendbook/ctl/ModelError.java,v $
 */

package org.infohazard.friendbook.ctl;

/**
 * Provides some basic facilities for form error handling.
 */
public class ModelError
{
	/**
	 */
	protected String error;
	public String getError()			{ return this.error; }
	public void setError(String value)	{ this.error = value; }
}
