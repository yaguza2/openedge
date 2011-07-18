package nl.openedge.modules.types;

import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.types.initcommands.InitCommand;

import org.jdom.Element;

/**
 * A component factory knows how to construct components of a certain type.
 * 
 * @author Eelco Hillenius
 */
public interface ComponentFactory
{
	String getName();

	void setName(String name);

	Class< ? > getComponentClass();

	void setComponentClass(Class< ? > componentClass) throws ConfigException;

	ComponentRepository getComponentRepository();

	void setComponentRepository(ComponentRepository factory);

	InitCommand[] getInitCommands();

	void setInitCommands(InitCommand[] commands);

	Object getComponent();

	void setComponentNode(Element componentNode) throws ConfigException;
}
