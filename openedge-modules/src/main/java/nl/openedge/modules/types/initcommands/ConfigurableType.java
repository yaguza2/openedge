package nl.openedge.modules.types.initcommands;

import nl.openedge.modules.config.ConfigException;

import org.jdom.Element;

/**
 * A class that is ConfigurableType can be initialized with an XML node of the main
 * configuration file.
 * 
 * @author E.F. Hillenius
 */
public interface ConfigurableType
{
	/**
	 * initialize with XML element.
	 * 
	 * @param configNode
	 *            configuration node
	 * @exception ConfigException
	 *                when the module could ne be
	 */
	void init(Element configNode) throws ConfigException;
}
