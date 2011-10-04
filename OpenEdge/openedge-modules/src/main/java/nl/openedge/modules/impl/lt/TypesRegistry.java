package nl.openedge.modules.impl.lt;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.types.ComponentFactory;
import nl.openedge.modules.types.RegistryException;
import nl.openedge.modules.types.base.JobTypeFactory;
import nl.openedge.modules.types.base.SingletonTypeFactory;
import nl.openedge.modules.types.base.ThreadSingletonTypeFactory;
import nl.openedge.modules.types.base.ThrowAwayTypeFactory;
import nl.openedge.modules.types.initcommands.*;

/**
 * Registry for types and init commands that can be used with the loosely typed
 * implementation of the component repository. Couples on strings.
 * 
 * @author Eelco Hillenius
 */
public final class TypesRegistry
{
	/** default number of component factories. */
	private static final int DFT_TYPE_SIZE = 5;

	/** default number of command types. */
	private static final int DFT_CMD_SIZE = 9;

	/** default class. */
	private static Class< ? > defaultComponentFactoryClass = SingletonTypeFactory.class;

	/**
	 * If true, the default factory will be used for types that are not of any know type.
	 * Otherwise (if false), an exception will be thrown when a component is not of any
	 * known type.
	 */
	private static boolean useDefaultFactory = true;

	/**
	 * map of component factories.
	 */
	private static Map<String, Class< ? >> componentFactories = new HashMap<String, Class< ? >>(
		DFT_TYPE_SIZE);

	/**
	 * map of init commands.
	 */
	private static Map<Serializable, Class< ? >> initCommandClasses =
		new HashMap<Serializable, Class< ? >>(DFT_CMD_SIZE);

	// initialize defaults
	static
	{
		// set default factories

		componentFactories.put("singleton", SingletonTypeFactory.class);

		componentFactories.put("threadSingleton", ThreadSingletonTypeFactory.class);

		componentFactories.put("throwaway", ThrowAwayTypeFactory.class);

		componentFactories.put("job", JobTypeFactory.class);

		initCommandClasses.put("bean", BeanTypeInitCommand.class);

		initCommandClasses.put("dependent", DependentTypeInitCommand.class);

		initCommandClasses.put("servletContextAware", ServletContextAwareTypeInitCommand.class);

		initCommandClasses.put("configurable", ConfigurableTypeInitCommand.class);

		initCommandClasses.put("chainedEventCaster", ChainedEventCasterInitCommand.class);

		initCommandClasses.put("componentFactoryObserver",
			ComponentFactoryObserverInitCommand.class);

		initCommandClasses.put("chainedEventObserver", ChainedEventObserverInitCommand.class);

		initCommandClasses.put("schedulerObserver", SchedulerObserverInitCommand.class);

		initCommandClasses.put("componentObserver", ComponentObserverInitCommand.class);
	}

	/**
	 * get the default component factory that is to be used when components are not of any
	 * known type and use default == true.
	 * 
	 * @return ComponentFactory
	 * @throws RegistryException
	 *             when the registry is faulty
	 */
	public static ComponentFactory getDefaultComponentFactory()
	{
		ComponentFactory factory = null;
		try
		{
			factory = (ComponentFactory) defaultComponentFactoryClass.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new RegistryException(e);
		}
		catch (IllegalAccessException e)
		{
			throw new RegistryException(e);
		}

		return factory;
	}

	/**
	 * set the default adapter factory class that is to be used when components are not of
	 * any known type and use default == true.
	 * 
	 * @param factoryClass
	 *            the default component factory
	 */
	public static void setDefaultComponentFactory(Class< ? > factoryClass)
	{
		defaultComponentFactoryClass = factoryClass;
	}

	/**
	 * get the component factory for the given type name.
	 * 
	 * @param typeName
	 *            name of the type to get the factory for
	 * @return ComponentFactory
	 * @throws RegistryException
	 *             when the registry is faulty
	 */
	public static ComponentFactory getComponentFactory(String typeName)
	{
		if (typeName == null)
		{
			return null;
		}

		ComponentFactory factory = null;
		try
		{
			Class< ? > factoryClass = componentFactories.get(typeName);
			factory = (ComponentFactory) factoryClass.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new RegistryException(e);
		}
		catch (IllegalAccessException e)
		{
			throw new RegistryException(e);
		}
		return factory;
	}

	/**
	 * register an component factory class for the given type name.
	 * 
	 * @param typeName
	 *            the name of the type
	 * @param componentFactoryClass
	 *            the class of the component factory
	 */
	public static void registerComponentType(String typeName, Class< ? > componentFactoryClass)
	{
		componentFactories.put(typeName, componentFactoryClass);
	}

	/**
	 * de-register an component type with the given name.
	 * 
	 * @param typeName
	 *            the name of the type
	 */
	public static void deRegisterComponentType(String typeName)
	{
		componentFactories.remove(typeName);
	}

	/**
	 * get the init command with the given name.
	 * 
	 * @param commandName
	 *            the name of the command
	 * @return InitCommand the command
	 * @throws ConfigException
	 *             when an configuration error occurs if no command was found or
	 *             instantiation failed
	 */
	public static InitCommand getInitCommand(String commandName) throws ConfigException
	{

		Class< ? > c = initCommandClasses.get(commandName);
		if (c != null)
		{
			try
			{
				return (InitCommand) c.newInstance();
			}
			catch (InstantiationException e)
			{
				throw new ConfigException(e);
			}
			catch (IllegalAccessException e)
			{
				throw new ConfigException(e);
			}
		}
		else
		{
			throw new ConfigException("init command with name " + commandName + " not found");
		}
	}

	/**
	 * register an init command for the given name.
	 * 
	 * @param commandName
	 *            the name of the command
	 * @param initCommandClass
	 *            class of the command
	 */
	public static void registerInitCommand(Class< ? > commandName, Class< ? > initCommandClass)
	{
		initCommandClasses.put(commandName, initCommandClass);
	}

	/**
	 * de-register an init command for the given name.
	 * 
	 * @param commandName
	 *            the name of the command
	 */
	public static void deRegisterInitCommand(Class< ? > commandName)
	{
		initCommandClasses.remove(commandName);
	}

	/**
	 * Whether to use the default factory.
	 * 
	 * @return whether to use the default factory
	 */
	public static boolean isUseDefaultFactory()
	{
		return useDefaultFactory;
	}

	/**
	 * Set whether to use the default factory.
	 * 
	 * @param b
	 *            whether to use the default factory
	 */
	public static void setUseDefaultFactory(boolean b)
	{
		useDefaultFactory = b;
	}
}