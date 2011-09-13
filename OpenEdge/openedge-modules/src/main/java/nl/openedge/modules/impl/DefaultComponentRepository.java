package nl.openedge.modules.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nl.openedge.modules.AbstractComponentRepository;
import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.types.ComponentFactory;
import nl.openedge.modules.types.initcommands.InitCommand;

import org.jdom.Element;
import org.quartz.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of ComponentRepository. This implementation looks for interfaces
 * that are implemented by the components for the coupling to the framework types and
 * InitCommands.
 * 
 * @author Eelco Hillenius
 */
public class DefaultComponentRepository extends AbstractComponentRepository
{
	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(DefaultComponentRepository.class);

	public DefaultComponentRepository()
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

		log.info("addedd " + clazz.getName() + " with name: " + name);
	}

	@Override
	protected ComponentFactory getComponentFactory(String name, Class< ? > clazz, Element node)
			throws ConfigException
	{
		ComponentFactory factory = null;

		Set< ? > baseTypes = TypesRegistry.getBaseTypes();
		if (baseTypes == null)
		{
			throw new ConfigException("there are no base types registered!");
		}

		boolean wasFoundOnce = false;
		for (Iterator< ? > j = baseTypes.iterator(); j.hasNext();)
		{
			Class< ? > baseType = (Class< ? >) j.next();
			if (baseType.isAssignableFrom(clazz))
			{
				if (wasFoundOnce) // more than one base type!
				{
					throw new ConfigException("component " + name
						+ " is of more than one registered base type!");
				}
				wasFoundOnce = true;

				factory = TypesRegistry.getComponentFactory(baseType);
			}
		}
		if (factory == null)
		{
			factory = TypesRegistry.getDefaultComponentFactory();

			log.warn(name + " is not of any known type... using " + factory
				+ " as component factory");
		}

		factory.setName(name);
		factory.setComponentRepository(this);
		factory.setComponentNode(node);

		addInitCommands(factory, clazz, node);

		factory.setComponentClass(clazz);

		return factory;
	}

	/**
	 * add initialization commands.
	 * 
	 * @param factory
	 *            factory
	 * @param node
	 *            config node
	 * @param clazz
	 *            component class
	 * @throws ConfigException
	 *             when an configuration error occurs
	 */
	@Override
	protected void addInitCommands(ComponentFactory factory, Class< ? > clazz, Element node)
			throws ConfigException
	{
		List<Class< ? >> initCommands = TypesRegistry.getInitCommandTypes();
		if (initCommands != null)
		{
			List<Object> commands = new ArrayList<Object>();
			for (Iterator<Class< ? >> j = initCommands.iterator(); j.hasNext();)
			{
				Class< ? > type = j.next();
				if (type.isAssignableFrom(clazz))
				{
					// get command for this class
					InitCommand initCommand = TypesRegistry.getInitCommand(type);
					// initialize the command
					initCommand.init(factory.getName(), node, this);
					// add command to the list
					commands.add(initCommand);
				}
			}

			InitCommand[] cmds = commands.toArray(new InitCommand[commands.size()]);

			if (cmds.length > 0)
			{
				factory.setInitCommands(cmds);
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
