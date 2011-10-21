package nl.openedge.modules.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.observers.ChainedEventCaster;
import nl.openedge.modules.observers.ComponentRepositoryObserver;
import nl.openedge.modules.types.ComponentFactory;
import nl.openedge.modules.types.RegistryException;
import nl.openedge.modules.types.base.JobTypeFactory;
import nl.openedge.modules.types.base.SingletonType;
import nl.openedge.modules.types.base.SingletonTypeFactory;
import nl.openedge.modules.types.base.ThreadSingletonType;
import nl.openedge.modules.types.base.ThreadSingletonTypeFactory;
import nl.openedge.modules.types.base.ThrowAwayType;
import nl.openedge.modules.types.base.ThrowAwayTypeFactory;
import nl.openedge.modules.types.initcommands.*;

import org.quartz.Job;

/**
 * Registry for types and init commands. Couples on classes.
 * 
 * @author Eelco Hillenius
 */
public final class TypesRegistry
{
	/**
	 * Map of component factories. Keyed on types, the values are instances of
	 * BuilderFactory.
	 */
	private static Map<Class< ? >, Class< ? >> componentFactories =
		new HashMap<Class< ? >, Class< ? >>();

	/**
	 * List of command types (Class). These types can do additional processing (like
	 * configurating) with types at initialization time NOTE: order can matter here! E.g.
	 * its best to first add BeanType, and then ConfigurableType as usually it's handy to
	 * have a component populated first and then do some more initialization with it.
	 */
	private static List<Class< ? >> initCommandTypes = new ArrayList<Class< ? >>();

	/**
	 * Map of init commands. Keyed on types, the values are classes of commands for the
	 * types.
	 */
	private static Map<Class< ? >, Class< ? >> initCommandClasses =
		new HashMap<Class< ? >, Class< ? >>();

	/**
	 * the default component factory class will be used when the component is not of a
	 * type registered as a base type in this registry.
	 */
	private static Class< ? > defaultComponentFactoryClass = SingletonTypeFactory.class;

	static
	{

		// the component factories
		componentFactories.put(SingletonType.class, SingletonTypeFactory.class);

		componentFactories.put(ThreadSingletonType.class, ThreadSingletonTypeFactory.class);

		componentFactories.put(ThrowAwayType.class, ThrowAwayTypeFactory.class);

		componentFactories.put(Job.class, JobTypeFactory.class);

		// add the default enhancer types
		// we use this to have ordering in the commands
		initCommandTypes.add(BeanType.class);
		initCommandTypes.add(DependentType.class);
		initCommandTypes.add(ServletContextAwareType.class);
		initCommandTypes.add(ConfigurableType.class);
		initCommandTypes.add(ChainedEventCaster.class);
		initCommandTypes.add(ComponentRepositoryObserver.class);

		// and the commands for them
		initCommandClasses.put(BeanType.class, BeanTypeInitCommand.class);

		initCommandClasses.put(DependentType.class, DependentTypeInitCommand.class);

		initCommandClasses.put(ServletContextAwareType.class,
			ServletContextAwareTypeInitCommand.class);

		initCommandClasses.put(ConfigurableType.class, ConfigurableTypeInitCommand.class);

		initCommandClasses.put(ChainedEventCaster.class, ChainedEventCasterInitCommand.class);

		initCommandClasses.put(ComponentRepositoryObserver.class,
			ComponentFactoryObserverInitCommand.class);
	}

	public static Set<Class< ? >> getBaseTypes()
	{
		return Collections.unmodifiableSet(componentFactories.keySet());
	}

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

	public static void setDefaultComponentFactory(Class< ? > factoryClass)
	{
		defaultComponentFactoryClass = factoryClass;
	}

	public static ComponentFactory getComponentFactory(Class< ? > clazz)
	{
		ComponentFactory factory = null;
		try
		{
			Class< ? > factoryClass = componentFactories.get(clazz);
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

	public static void registerComponentType(Class< ? > typeClass, Class< ? > componentFactoryClass)
	{
		componentFactories.put(typeClass, componentFactoryClass);
	}

	public static void deRegisterComponentType(Class< ? > typeClass)
	{
		componentFactories.remove(typeClass);
	}

	public static List<Class< ? >> getInitCommandTypes()
	{
		return Collections.unmodifiableList(initCommandTypes);
	}

	public static InitCommand getInitCommand(Class< ? > clazz) throws ConfigException
	{

		Class< ? > c = initCommandClasses.get(clazz);
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
			throw new ConfigException("init command for " + clazz.getName() + " not found");
		}
	}

	public static void registerInitCommand(Class< ? > typeClass, Class< ? > initCommandClass)
	{
		initCommandTypes.add(typeClass);
		initCommandClasses.put(typeClass, initCommandClass);
	}

	public static void registerInitCommand(Class< ? > typeClass, Class< ? > initCommandClass,
			int index)
	{
		initCommandTypes.add(index, typeClass);
		initCommandClasses.put(typeClass, initCommandClass);
	}

	public static void deRegisterInitCommand(Class< ? > typeClass)
	{
		initCommandTypes.remove(typeClass);
		initCommandClasses.remove(typeClass);
	}
}
