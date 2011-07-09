/*
 * $Id: CommandBase.java,v 1.11 2004/06/07 20:38:42 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/CommandBase.java,v $
 */

package org.infohazard.maverick.flow;

import java.io.IOException;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Base class for implementing a Maverick {@link Command}. Implementors must define
 * {@link #getView}.
 * </p>
 * <p>
 * CommandBase obtains a {@link View} name by calling its Controller's
 * {@link Controller#go go} method, and then invokes the View's {@link View#go go} method.
 * <p>
 * <p>
 * The {@link ModelLifetime} interface is honored. After invoking View.go, if the model
 * object is an instance of ModelLifetime, its {@link ModelLifetime#discard discard}
 * method is called.
 * 
 */
abstract class CommandBase implements Command
{
	/**
	 * <p>
	 * CommandBase logger.
	 * </p>
	 */
	private static Logger log = LoggerFactory.getLogger(CommandBase.class);

	/**
	 * <p>
	 * Reference to our {@link Controller}.
	 * </p>
	 */
	protected Controller controller;

	/**
	 * <p>
	 * Set reference to out {@link Controller}.
	 * </p>
	 */
	public CommandBase(Controller ctl)
	{
		this.controller = ctl;
	}

	/**
	 *
	 */
	@Override
	public void go(MaverickContext mctx) throws IOException, ServletException
	{
		Object model = null;

		try
		{
			// There must be a controller class to distinguish between views
			String viewName = this.controller.go(mctx);

			// Hold on to the model now because chained commands might replace it
			model = mctx.getModel();

			if (log.isDebugEnabled())
				log.debug("Switching to view:  " + viewName);

			View target = this.getView(viewName);
			if (null == target)
				throw new ServletException("Controller specified view \"" + viewName
					+ "\", but no view with that name is defined.");

			target.go(mctx);
		}
		finally
		{
			// Allow the model to manage resources
			// instanceof returns false if object is null
			if (model instanceof ModelLifetime)
				((ModelLifetime) model).discard();
		}
	}

	/**
	 */
	protected abstract View getView(String name);
}
