/*
 * $Id: ShuntFactory.java,v 1.3 2002/02/07 22:49:56 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/ShuntFactory.java,v $
 */
package org.infohazard.maverick.flow;

import javax.servlet.ServletConfig;

import org.jdom.Element;

/**
 * Pluggable modules which build Shunts must implement this interface and then be defined
 * in the &lt;modules&gt; section of the Maverick config file. One factory is created to
 * build all Shunts.
 * 
 * @author Jeff Schnitzer
 * @version $Revision: 1.3 $ $Date: 2002/02/07 22:49:56 $
 */
public interface ShuntFactory
{
	/**
	 * The factory is initialized with the XML element from the configuration file.
	 * Individual ShuntFactory implementations are free to interpret their XML nodes as
	 * they like.
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
	 * Creates an empty Shunt which will be populated.
	 * 
	 * @return A new, empty shunt.
	 * @exception ConfigException
	 *                If something is wrong with the configuration.
	 */
	public Shunt createShunt() throws ConfigException;
}
