/*
 * $Id: TransformFactory.java,v 1.2 2002/06/06 12:23:54 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/TransformFactory.java,v $
 */

package org.infohazard.maverick.flow;

import javax.servlet.ServletConfig;
import org.jdom.Element;


/**
 */
public interface TransformFactory
{
	/**
	 * Factories will be initialized with the XML from the configuration file
	 * so that they can check for any options defined in child nodes,
	 * attributes, etc.
	 */
	public void init(Element factoryNode, ServletConfig servletCfg) throws ConfigException;

	/**
	 * Creates a transform from the element (and any children, if appropriate).
	 */
	public Transform createTransform(Element transformNode) throws ConfigException;
}