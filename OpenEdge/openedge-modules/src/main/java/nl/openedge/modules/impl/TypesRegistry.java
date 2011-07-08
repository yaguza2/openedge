/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003, Open Edge B.V.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions 
 * in binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution. Neither the name of OpenEdge B.V. 
 * nor the names of its contributors may be used to endorse or promote products 
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
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
import nl.openedge.modules.types.initcommands.BeanType;
import nl.openedge.modules.types.initcommands.BeanTypeInitCommand;
import nl.openedge.modules.types.initcommands.ChainedEventCasterInitCommand;
import nl.openedge.modules.types.initcommands.ComponentFactoryObserverInitCommand;
import nl.openedge.modules.types.initcommands.ConfigurableType;
import nl.openedge.modules.types.initcommands.ConfigurableTypeInitCommand;
import nl.openedge.modules.types.initcommands.DependentType;
import nl.openedge.modules.types.initcommands.DependentTypeInitCommand;
import nl.openedge.modules.types.initcommands.InitCommand;
import nl.openedge.modules.types.initcommands.ServletContextAwareType;
import nl.openedge.modules.types.initcommands.ServletContextAwareTypeInitCommand;

import org.quartz.Job;

/**
 * Registry for types and init commands. Couples on classes.
 * 
 * @author Eelco Hillenius
 */
public final class TypesRegistry
{
	/** default number of component factories. */
	private static final int DFT_TYPE_SIZE = 5;

	/** default number of command types. */
	private static final int DFT_CMD_SIZE = 6;

	/**
	 * Map of component factories. Keyed on types, the values are instances of BuilderFactory.
	 */
	private static Map componentFactories = new HashMap(DFT_TYPE_SIZE);

	/**
	 * List of command types (Class). These types can do additional processing (like configurating)
	 * with types at initialization time NOTE: order can matter here! E.g. its best to first add
	 * BeanType, and then ConfigurableType as usually it's handy to have a component populated first
	 * and then do some more initialization with it.
	 */
	private static List initCommandTypes = new ArrayList(DFT_TYPE_SIZE);

	/**
	 * Map of init commands. Keyed on types, the values are classes of commands for the types.
	 */
	private static Map initCommandClasses = new HashMap(DFT_CMD_SIZE);

	/**
	 * the default component factory class will be used when the component is not of a type
	 * registered as a base type in this registry.
	 */
	private static Class defaultComponentFactoryClass = SingletonTypeFactory.class;

	/*
	 * set the defaults
	 */
	static
	{

		// the component factories
		componentFactories.put(SingletonType.class, SingletonTypeFactory.class);

		componentFactories.put(ThreadSingletonType.class,
				ThreadSingletonTypeFactory.class);

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

		initCommandClasses.put(ChainedEventCaster.class,
				ChainedEventCasterInitCommand.class);

		initCommandClasses.put(ComponentRepositoryObserver.class,
				ComponentFactoryObserverInitCommand.class);
	}

	/**
	 * get the base types.
	 * 
	 * @return List base types
	 */
	public static Set getBaseTypes()
	{
		return Collections.unmodifiableSet(componentFactories.keySet());
	}

	/**
	 * get the default component factory that is to be used when components are not of a type
	 * registered as a base type in this registry.
	 * 
	 * @return ComponentFactory
	 * @throws RegistryException when the registry is faulty
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
	 * set the default adapter factory class that is to be used when components are not of a type
	 * registered as a base type in this registry.
	 * 
	 * @param factoryClass the default component factory
	 */
	public static void setDefaultComponentFactory(Class factoryClass)
	{
		defaultComponentFactoryClass = factoryClass;
	}

	/**
	 * get the component factory for the given type.
	 * 
	 * @param clazz type to get the factory for
	 * @return ComponentFactory
	 * @throws RegistryException when the registry is faulty
	 */
	public static ComponentFactory getComponentFactory(Class clazz)
	{
		ComponentFactory factory = null;
		try
		{
			Class factoryClass = (Class) componentFactories.get(clazz);
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
	 * register an component type and factory class for the given class.
	 * 
	 * @param typeClass the class
	 * @param componentFactoryClass the class of the component factory
	 */
	public static void registerComponentType(Class typeClass, Class componentFactoryClass)
	{
		componentFactories.put(typeClass, componentFactoryClass);
	}

	/**
	 * de-register an component type with the given type class.
	 * 
	 * @param typeClass the class
	 */
	public static void deRegisterComponentType(Class typeClass)
	{
		componentFactories.remove(typeClass);
	}

	/**
	 * get the init commands.
	 * 
	 * @return List command types
	 */
	public static List getInitCommandTypes()
	{
		return Collections.unmodifiableList(initCommandTypes);
	}

	/**
	 * get the init command for the given type.
	 * 
	 * @param clazz the type to get the init command for
	 * @return InitCommand the command
	 * @throws ConfigException when an configuration error occurs if no command was found or
	 *             instantiation failed
	 */
	public static InitCommand getInitCommand(Class clazz) throws ConfigException
	{

		Class c = (Class) initCommandClasses.get(clazz);
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
			throw new ConfigException("init command for "
					+ clazz.getName() + " not found");
		}
	}

	/**
	 * register an init command for the given class.
	 * 
	 * @param typeClass the type class
	 * @param initCommandClass the class of the command
	 */
	public static void registerInitCommand(Class typeClass, Class initCommandClass)
	{
		initCommandTypes.add(typeClass);
		initCommandClasses.put(typeClass, initCommandClass);
	}

	/**
	 * register an init command for the given class at location index.
	 * 
	 * @param typeClass the type class
	 * @param initCommandClass the class of the command
	 * @param index location in command list
	 */
	public static void registerInitCommand(Class typeClass, Class initCommandClass,
			int index)
	{
		initCommandTypes.add(index, typeClass);
		initCommandClasses.put(typeClass, initCommandClass);
	}

	/**
	 * de-register an init command.
	 * 
	 * @param typeClass the type class
	 */
	public static void deRegisterInitCommand(Class typeClass)
	{
		initCommandTypes.remove(typeClass);
		initCommandClasses.remove(typeClass);
	}

}
