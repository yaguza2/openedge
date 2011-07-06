/*
 * $Id: CommandSingleView.java,v 1.4 2002/06/06 12:23:53 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/CommandSingleView.java,v $
 */

package org.infohazard.maverick.flow;

/**
 * Command implementation which only knows about a single view.
 */
class CommandSingleView extends CommandBase
{
	/**
	 */
	protected View singleView;

	/**
	 */
	public CommandSingleView(Controller ctl, View v)
	{
		super(ctl);

		if (v == null)
			throw new IllegalStateException("Cannot define a CommandSingleView without a view!");

		this.singleView = v;
	}

	/**
	 */
	protected View getView(String viewName)
	{
		return this.singleView;
	}
}