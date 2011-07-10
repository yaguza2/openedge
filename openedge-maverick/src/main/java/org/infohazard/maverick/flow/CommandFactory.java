/*
 * $Id: CommandFactory.java,v 1.7 2004/06/27 17:42:14 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/CommandFactory.java,v $
 */
package org.infohazard.maverick.flow;

import java.util.List;
import java.util.Map;

import org.jdom.Element;

/**
 * Factory for creating Command objects based on some preloaded context (such as global
 * views) and an XML command node.
 * 
 * created January 28, 2002
 * 
 * @author Jeff Schnitzer
 * @version $Revision: 1.7 $ $Date: 2004/06/27 17:42:14 $
 */
class CommandFactory
{
	/** tag for views. */
	protected final static String TAG_VIEW = "view";

	/** tag for the controller. */
	protected final static String TAG_CONTROLLER = "controller";

	/** view registry. */
	protected ViewRegistry viewRegistry;

	/** the controller factory. */
	protected ControllerFactory controllerFactory = new DefaultControllerFactory();

	public CommandFactory(ViewRegistry viewReg)
	{
		this.viewRegistry = viewReg;
	}

	/**
	 * Creates a command from the commandNode.
	 * 
	 * @param commandNode
	 * @return
	 * @exception ConfigException
	 */
	public Command createCommand(Element commandNode) throws ConfigException
	{
		Controller ctl =
			this.controllerFactory.createController(commandNode.getChild(TAG_CONTROLLER));

		@SuppressWarnings("unchecked")
		List<Element> viewNodes = commandNode.getChildren(TAG_VIEW);
		Map<String, View> viewsMap = this.viewRegistry.createViewsMap(viewNodes);
		if (viewsMap.size() > 1)
			return new CommandMultipleViews(ctl, viewsMap);
		else
		{
			// Optimize with a Command that doesn't do a map lookup.
			// This is also how nameless views are resolved.
			View theView = viewsMap.values().iterator().next();
			return new CommandSingleView(ctl, theView);
		}
	}

	/**
	 * Get view registry.
	 * 
	 * @return ViewRegistry Returns the viewRegistry.
	 */
	public ViewRegistry getViewRegistry()
	{
		return viewRegistry;
	}

	/**
	 * Set view registry.
	 * 
	 * @param viewReg
	 *            viewRegistry to set.
	 */
	public void setViewRegistry(ViewRegistry viewReg)
	{
		this.viewRegistry = viewReg;
	}

	/**
	 * Get controllerFactory.
	 * 
	 * @return ControllerFactory Returns the controllerFactory.
	 */
	public ControllerFactory getControllerFactory()
	{
		return controllerFactory;
	}

	/**
	 * Set controllerFactory.
	 * 
	 * @param controllerFactory
	 *            controllerFactory to set.
	 */
	public void setControllerFactory(ControllerFactory controllerFactory)
	{
		this.controllerFactory = controllerFactory;
	}
}
