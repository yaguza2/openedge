package nl.openedge.access;

import org.jdom.Element;

/**
 * @author Hillenius
 * $Id$
 */
public interface Configurable {

	/**
	 * initialise with xml element
	 * @param configNode
	 * @exception ConfigException
	 */
	public void init(Element configNode) throws ConfigException;

}
