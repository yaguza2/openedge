package nl.openedge.modules.impl.lt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.openedge.modules.AbstractComponentRepository;
import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.types.ComponentFactory;
import nl.openedge.modules.types.initcommands.InitCommand;

import org.jdom.Element;
import org.quartz.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loosely typed implementation of ComponentRepository. If this component repository is
 * used, components do not have to implement any interface at all, as all type information
 * and coupling to InitCommands will be read from the configuration file.
 * 
 * @author Eelco Hillenius
 */
public class LooselyTypedComponentRepository extends AbstractComponentRepository
{
	private static final long serialVersionUID = 1L;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public LooselyTypedComponentRepository()
	{
	}

	@Override
	protected void addComponent(String name, Class< ? > clazz, Element node) throws ConfigException
	{
		ComponentFactory factory = getComponentFactory(name, clazz, node);

		// store component
		getComponents().put(name, factory);

		// special case: see if this is a job
		if (Job.class.isAssignableFrom(clazz))
		{
			getJobs().put(name, factory);
		}

		// NOTE: in the 'loosely typed' case like this factory,
		// Jobs still have to implement the Job interface, as
		// this is a requirement of the Quartz framework

		log.info("addedd " + clazz.getName() + " with name: " + name);
	}

	@Override
	protected ComponentFactory getComponentFactory(String name, Class< ? > clazz, Element node)
			throws ConfigException
	{
		ComponentFactory factory = null;
		String typeName = node.getAttributeValue("type");

		factory = TypesRegistry.getComponentFactory(typeName);

		if (factory == null)
		{
			if (TypesRegistry.isUseDefaultFactory())
			{
				factory = TypesRegistry.getDefaultComponentFactory();

				log.warn(name + " is not of any known type... using " + factory
					+ " as component factory");
			}
			else
			{
				throw new ConfigException(name + " is not of any known type");
			}
		}

		factory.setName(name);
		factory.setComponentRepository(this);
		factory.setComponentNode(node);

		addInitCommands(factory, clazz, node);

		factory.setComponentClass(clazz);

		return factory;
	}

	@Override
	protected void addInitCommands(ComponentFactory factory, Class< ? > clazz, Element node)
			throws ConfigException
	{

		Element cmdNode = node.getChild("init-commands");
		if (cmdNode != null)
		{
			List<Object> commands = new ArrayList<Object>();
			List< ? > cmdList = cmdNode.getChildren("command");

			for (Iterator< ? > i = cmdList.iterator(); i.hasNext();)
			{
				Element cnode = (Element) i.next();
				String commandName = cnode.getTextNormalize();

				InitCommand initCommand = TypesRegistry.getInitCommand(commandName);

				// initialize the command
				initCommand.init(factory.getName(), node, this);
				// add command to the list
				commands.add(initCommand);

				InitCommand[] cmds = commands.toArray(new InitCommand[commands.size()]);

				if (cmds.length > 0)
				{
					factory.setInitCommands(cmds);
				}
			}
		}
	}

	@Override
	public List<Object> getComponentsByType(Class< ? > type, boolean exact)
	{
		List<Object> sublist = new ArrayList<Object>();

		if (type == null)
		{
			return sublist;
		}

		if (exact)
		{
			for (Iterator< ? > i = getComponents().values().iterator(); i.hasNext();)
			{

				ComponentFactory factory = (ComponentFactory) i.next();
				if (type.equals(factory.getComponentClass()))
				{
					sublist.add(getComponent(factory.getName()));
				}
			}
		}
		else
		{
			for (Iterator< ? > i = getComponents().values().iterator(); i.hasNext();)
			{

				ComponentFactory factory = (ComponentFactory) i.next();
				if (type.isAssignableFrom(factory.getComponentClass()))
				{
					sublist.add(getComponent(factory.getName()));
				}
			}
		}

		return sublist;
	}
}
