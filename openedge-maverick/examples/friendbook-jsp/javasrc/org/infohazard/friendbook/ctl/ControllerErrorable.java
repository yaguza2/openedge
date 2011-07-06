/*
 * $Id: ControllerErrorable.java,v 1.2 2002/06/09 22:09:40 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp/javasrc/org/infohazard/friendbook/ctl/ControllerErrorable.java,v $
 */

package org.infohazard.friendbook.ctl;

import org.infohazard.maverick.ctl.ThrowawayBean2;
import java.util.*;

/**
 * Controller support class which provides some basic facilities for
 * form error handling.
 */
public class ControllerErrorable extends ThrowawayBean2
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
	 * @return a map of String field name to String message, which
	 *  will be empty if no errors have been reported.
	 */
	public Map getErrors()
	{
		if (this.errors == null)
			return Collections.EMPTY_MAP;
		else
			return this.errors;
	}

	/**
	 */
	protected void addError(String field, String message)
	{
		if (this.errors == null)
			this.errors = new HashMap();

		this.errors.put(field, message);
	}
}