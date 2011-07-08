/*
 * $Id: ModelErrorMap.java,v 1.1 2003/01/12 06:40:24 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp-fbu/javasrc/org/infohazard/friendbook/ctl/ModelErrorMap.java,v $
 */

package org.infohazard.friendbook.ctl;

import java.util.*;

/**
 * Provides some basic facilities for form error handling.
 */
public class ModelErrorMap
{
	/**
	 */
	protected Map errors;

	/**
	 */
	public boolean hasErrors()
	{
		return (this.errors != null);
	}

	/**
	 * @return a map of String field name to String message, or
	 *	null if no errors have been reported.
	 */
	public Map getErrors()
	{
		return this.errors;
	}

	/**
	 */
	protected void setError(String field, String message)
	{
		if (this.errors == null)
			this.errors = new HashMap();

		this.errors.put(field, message);
	}
}
