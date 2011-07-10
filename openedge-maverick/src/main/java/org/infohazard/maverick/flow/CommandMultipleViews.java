/*
 * $Id: CommandMultipleViews.java,v 1.2 2002/06/06 12:23:53 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/CommandMultipleViews.java,v $
 */

package org.infohazard.maverick.flow;

import java.util.Map;

/**
 * Command implementation which allows one of several views to be determined by the
 * controller result.
 */
class CommandMultipleViews extends CommandBase
{
	/**
	 * Stores mapping of String view name to View object
	 */
	protected Map<String, View> views;

	public CommandMultipleViews(Controller ctl, Map<String, View> views)
	{
		super(ctl);

		this.views = views;
	}

	@Override
	protected View getView(String name)
	{
		return this.views.get(name);
	}
}
