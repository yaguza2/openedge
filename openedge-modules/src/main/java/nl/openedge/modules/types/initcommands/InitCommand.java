package nl.openedge.modules.types.initcommands;

import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.config.ConfigException;

import org.jdom.Element;

/**
 * do some additional processing (like configurating) with types at initialization time.
 * An initCommand is executed exactely once for each instance.
 * 
 * @author Eelco Hillenius
 */
public interface InitCommand
{
	/**
	 * initialize the command.
	 * 
	 * @param componentName
	 *            name of component
	 * @param componentNode
	 *            XML configuration node of component
	 * @param componentRepository
	 *            instance of componentRepository
	 * @throws ConfigException
	 *             when an configuration error occurs
	 */
	void init(String componentName, Element componentNode, ComponentRepository componentRepository)
			throws ConfigException;

	/**
	 * execute the command.
	 * 
	 * @param componentInstance
	 *            instance of the component
	 * @throws InitCommandException
	 *             when initialization command failed
	 * @throws ConfigException
	 *             when an configuration error occurs
	 */
	void execute(Object componentInstance) throws InitCommandException, ConfigException;
}
