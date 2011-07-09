/*
 * $Id: AbstractControllerFactory.java,v 1.1 2004/06/27 17:42:14 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/AbstractControllerFactory.java,v $
 */

package org.infohazard.maverick.flow;

import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.infohazard.maverick.util.XML;
import org.jdom.Element;

/**
 * Base class for controller factories. Creates (what appears to be) a singleton
 * controller object appropriate for the type of controller specified in the XML,
 * including single-use controllers. If no controller is specified, a special do-nothing
 * controller is returned. If you want to use a custom controller factory, it is advisable
 * to extend from this class and override either the interface method (createController)
 * or override one or more of the template methods (getControllerClass,
 * getControllerInstance and initializeController).
 * 
 * @author Jeff Schnitzer
 * @author Eelco Hillenius
 */
public abstract class AbstractControllerFactory implements ControllerFactory
{
	/**
	 * xml attribute of controller class.
	 */
	protected static final String ATTR_CONTROLLER_CLASS = "class";

	/**
	 * Dummy controller class.
	 */
	static class NullController implements ControllerSingleton
	{
		/** initialize. */
		@Override
		public void init(Element controllerNode)
		{
		}

		/** cmd method impl. */
		@Override
		public String go(ControllerContext cctx) throws ServletException
		{
			return "NO CONTROLLER DEFINED";
		}
	}

	/**
	 * dummy controller.
	 */
	protected static final Controller nullController = new NullController();

	/**
	 * Initialize: this method does nothing; override to customize.
	 * 
	 * @see org.infohazard.maverick.flow.ControllerFactory#init(org.jdom.Element,
	 *      javax.servlet.ServletConfig)
	 */
	@Override
	public void init(Element factoryNode, ServletConfig servletCfg) throws ConfigException
	{
		// no nada
	}

	/**
	 * Creates (what appears to be) a singleton controller object appropriate for the type
	 * of controller specified in the XML, including single-use controllers. If no
	 * controller is specified, a special do-nothing controller is returned.
	 * 
	 * @see org.infohazard.maverick.flow.ControllerFactory#createController(org.jdom.Element)
	 */
	@Override
	public Controller createController(Element controllerNode) throws ConfigException
	{
		if (controllerNode == null) // if no controller is defined, return the dummy impl
		{
			return nullController;
		}

		// get the class of the controller node
		Class controllerClass = getControllerClass(controllerNode);
		// create the proper instance based on the class
		Controller controller = getControllerInstance(controllerNode, controllerClass);
		// initialize the controller based on the implementation
		initializeController(controllerNode, controller);

		return controller;
	}

	/**
	 * Get the controller class from the configuration.
	 * 
	 * @param controllerNode
	 *            the xml node of the controller
	 * @return Class the class from the configuration or null if the controller node was
	 *         null
	 * @throws ConfigException
	 */
	protected Class getControllerClass(Element controllerNode) throws ConfigException
	{
		if (controllerNode == null)
			return null;

		String className = controllerNode.getAttributeValue(ATTR_CONTROLLER_CLASS);
		if (className == null || className.trim().equals(""))
		{
			throw new ConfigException("Controller element must have " + ATTR_CONTROLLER_CLASS
				+ " attribute:  " + XML.toString(controllerNode));
		}

		Class controllerClass;
		try
		{
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			if (classLoader == null)
			{
				classLoader = AbstractControllerFactory.class.getClassLoader();
			}
			controllerClass = classLoader.loadClass(className);
		}
		catch (ClassNotFoundException ex)
		{
			throw new ConfigException(ex);
		}
		return controllerClass;
	}

	/**
	 * Create a controller instance (or a decorator) based on the controller class and the
	 * controller node.
	 * 
	 * @param controllerNode
	 *            xml node of the controller
	 * @param controllerClass
	 *            the class of the controller
	 * @return Controller a controller or a controller decorator
	 * @throws ConfigException
	 */
	protected Controller getControllerInstance(Element controllerNode, Class controllerClass)
			throws ConfigException
	{
		Controller controller = null;
		try
		{
			if (ControllerSingleton.class.isAssignableFrom(controllerClass))
			{
				controller = createSingletonController(controllerNode, controllerClass);
			}
			else
			{
				controller = createThrowawayController(controllerNode, controllerClass);
			}
		}
		catch (InstantiationException ex)
		{
			throw new ConfigException(ex);
		}
		catch (IllegalAccessException ex)
		{
			throw new ConfigException(ex);
		}

		// decorate the controller if needed
		controller = decorateController(controllerNode, controller);

		return controller;
	}

	/**
	 * If needed, decorate the controller.
	 * 
	 * @param controllerNode
	 *            xml node of controller
	 * @param controller
	 *            the controller instance.
	 * @return Controller the original controller or a decorator
	 * @throws ConfigException
	 */
	protected Controller decorateController(Element controllerNode, Controller controller)
			throws ConfigException
	{
		Controller decorated = controller;
		Map params = XML.getParams(controllerNode);
		if (params != null) // if we have params, create a decorator for the controller
		{
			decorated = new ControllerWithParams(controller, params);
		}
		return decorated;
	}

	/**
	 * Create a singleton controller instance.
	 * 
	 * @param controllerNode
	 *            controller node
	 * @param controllerClass
	 *            controller class
	 * @return Controller instance
	 * @throws ConfigException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	protected Controller createSingletonController(Element controllerNode, Class controllerClass)
			throws ConfigException, InstantiationException, IllegalAccessException
	{
		return (ControllerSingleton) controllerClass.newInstance();
	}

	/**
	 * Create a throwaway controller; this implementation creates an instance of
	 * ThrowawayControllerAdapter.
	 * 
	 * @param controllerNode
	 *            controller node
	 * @param controllerClass
	 *            controller class
	 * @return Controller instance
	 * @throws ConfigException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	protected Controller createThrowawayController(Element controllerNode, Class controllerClass)
			throws ConfigException, InstantiationException, IllegalAccessException
	{
		return new ThrowawayControllerAdapter(controllerClass);
	}

	/**
	 * initialize the controller if it needs to be done. This implementation just looks if
	 * the given controller (or embedded controller in case the given controller is a
	 * decorator) is of type ControllerSingleton and, if it is, it's init method is
	 * called.
	 * 
	 * @param controllerNode
	 *            the xml node of the controller
	 * @param controller
	 *            the controller instance
	 * @throws ConfigException
	 */
	protected void initializeController(Element controllerNode, Controller controller)
			throws ConfigException
	{
		controller = getControllerUndecorated(controller);
		if (controller instanceof ControllerSingleton)
		{
			((ControllerSingleton) controller).init(controllerNode);
		}
		// else do nothing
	}

	/**
	 * Get the 'real' controller instance unwrapped.
	 * 
	 * @param controller
	 *            the controller or wrapper.
	 * @return Controller the 'real' controller instance unwrapped.
	 */
	protected Controller getControllerUndecorated(Controller controller)
	{
		Controller concreteController = controller;
		if (controller instanceof ControllerWithParams)
		{
			concreteController = ((ControllerWithParams) controller).getDecorated();
		}
		return concreteController;
	}
}
