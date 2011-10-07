/*
 * $Id: ControllerFactory.java,v 1.11 2004/06/27 17:42:14 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/ControllerFactory.java,v $
 */

package org.infohazard.maverick.flow;

import javax.servlet.ServletConfig;

import org.jdom.Element;

/**
 * Interface for controller factories.
 * 
 * @author Eelco Hillenius
 */
public interface ControllerFactory
{
	/**
	 * The factory is initialized with the XML element from the configuration file.
	 * Individual ControllerFactory implementations are free to interpret their XML nodes
	 * as they like.
	 * 
	 * @param factoryNode
	 *            The XML element (and child nodes) configured in the Maverick
	 *            configuration file.
	 * @param servletCfg
	 *            So that the factory can get information from the container.
	 * @exception ConfigException
	 *                If the configuration was invalid.
	 */
	public void init(Element factoryNode, ServletConfig servletCfg) throws ConfigException;

	/**
	 * Creates a controller.
	 * 
	 * @param controllerNode
	 *            xml node of the controller
	 * @return Controller a controller
	 * @throws ConfigException
	 */
	public Controller createController(Element controllerNode) throws ConfigException;
}
