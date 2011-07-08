/*
 * $Id: ViewFactory.java,v 1.4 2002/06/06 12:23:54 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/ViewFactory.java,v $
 */

package org.infohazard.maverick.flow;

import javax.servlet.ServletConfig;
import org.jdom.Element;


/**
 * This interface allows user-defined view factories to be added to
 * the system.
 */
public interface ViewFactory
{
	/**
	 * The factory will be initialized with the XML element from from the
	 * maverick configuration file.
	 */
	public void init(Element factoryNode, ServletConfig servletCfg) throws ConfigException;

	/**
	 * Creates a specific instance of the View from the XML element
	 * in the maverick configuration file.
	 */
	public View createView(Element viewNode) throws ConfigException;
}
