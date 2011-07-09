/*
 * $Id: ControllerSingleton.java,v 1.2 2004/06/27 17:41:31 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/ControllerSingleton.java,v $
 */

package org.infohazard.maverick.flow;

import org.jdom.Element;

/**
 * ControllerSingleton serves two purposes: First, the presence of this interface on a
 * Controller class indicates to the framework that the controller should be defined as a
 * singleton rather than instantiating a fresh instance for every request. Second, it
 * provides a method to initialize the singleton controller with the relevant XML data
 * from the configuration file.
 */
public interface ControllerSingleton extends Controller
{
	/**
	 * Guaranteed to be called once with the XML configuration of the controller from the
	 * master config file.
	 * 
	 * @param controllerNode
	 *            the xml node of this controller.
	 */
	public void init(Element controllerNode) throws ConfigException;
}
