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
package nl.openedge.modules.impl.lt;

import java.util.HashMap;
import java.util.Map;

import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.types.ComponentFactory;
import nl.openedge.modules.types.RegistryException;
import nl.openedge.modules.types.base.JobTypeFactory;
import nl.openedge.modules.types.base.SingletonTypeFactory;
import nl.openedge.modules.types.base.ThreadSingletonTypeFactory;
import nl.openedge.modules.types.base.ThrowAwayTypeFactory;
import nl.openedge.modules.types.initcommands.BeanTypeInitCommand;
import nl.openedge.modules.types.initcommands.ChainedEventCasterInitCommand;
import nl.openedge.modules.types.initcommands.ChainedEventObserverInitCommand;
import nl.openedge.modules.types.initcommands.ComponentFactoryObserverInitCommand;
import nl.openedge.modules.types.initcommands.ComponentObserverInitCommand;
import nl.openedge.modules.types.initcommands.ConfigurableTypeInitCommand;
import nl.openedge.modules.types.initcommands.DependentTypeInitCommand;
import nl.openedge.modules.types.initcommands.InitCommand;
import nl.openedge.modules.types.initcommands.SchedulerObserverInitCommand;
import nl.openedge.modules.types.initcommands.ServletContextAwareTypeInitCommand;

/**
 * Registry for types and init commands that can be used with the loosely typed implementation of
 * the component repository. Couples on strings.
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
	private static Class defaultComponentFactoryClass = SingletonTypeFactory.class;

	/**
	 * If true, the default factory will be used for types that are not of any know type. Otherwise
	 * (if false), an exception will be thrown when a component is not of any known type.
	 */
	private static boolean useDefaultFactory = true;

	/**
	 * map of component factories.
	 */
	private static Map componentFactories = new HashMap(DFT_TYPE_SIZE);

	/**
	 * map of init commands.
	 */
	private static Map initCommandClasses = new HashMap(DFT_CMD_SIZE);

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

		initCommandClasses.put("servletContextAware",
				ServletContextAwareTypeInitCommand.class);

		initCommandClasses.put("configurable", ConfigurableTypeInitCommand.class);

		initCommandClasses.put("chainedEventCaster", ChainedEventCasterInitCommand.class);

		initCommandClasses.put("componentFactoryObserver",
				ComponentFactoryObserverInitCommand.class);

		initCommandClasses.put("chainedEventObserver",
				ChainedEventObserverInitCommand.class);

		initCommandClasses.put("schedulerObserver", SchedulerObserverInitCommand.class);

		initCommandClasses.put("componentObserver", ComponentObserverInitCommand.class);
	}

	/**
	 * get the default component factory that is to be used when components are not of any known
	 * type and use default == true.
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
	 * set the default adapter factory class that is to be used when components are not of any known
	 * type and use default == true.
	 * 
	 * @param factoryClass the default component factory
	 */
	public static void setDefaultComponentFactory(Class factoryClass)
	{
		defaultComponentFactoryClass = factoryClass;
	}

	/**
	 * get the component factory for the given type name.
	 * 
	 * @param typeName name of the type to get the factory for
	 * @return ComponentFactory
	 * @throws RegistryException when the registry is faulty
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
			Class factoryClass = (Class) componentFactories.get(typeName);
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
	 * @param typeName the name of the type
	 * @param componentFactoryClass the class of the component factory
	 */
	public static void registerComponentType(String typeName, Class componentFactoryClass)
	{
		componentFactories.put(typeName, componentFactoryClass);
	}

	/**
	 * de-register an component type with the given name.
	 * 
	 * @param typeName the name of the type
	 */
	public static void deRegisterComponentType(String typeName)
	{
		componentFactories.remove(typeName);
	}

	/**
	 * get the init command with the given name.
	 * 
	 * @param commandName the name of the command
	 * @return InitCommand the command
	 * @throws ConfigException when an configuration error occurs if no command was found or
	 *             instantiation failed
	 */
	public static InitCommand getInitCommand(String commandName) throws ConfigException
	{

		Class c = (Class) initCommandClasses.get(commandName);
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
			throw new ConfigException("init command with name "
					+ commandName + " not found");
		}
	}

	/**
	 * register an init command for the given name.
	 * 
	 * @param commandName the name of the command
	 * @param initCommandClass class of the command
	 */
	public static void registerInitCommand(Class commandName, Class initCommandClass)
	{
		initCommandClasses.put(commandName, initCommandClass);
	}

	/**
	 * de-register an init command for the given name.
	 * 
	 * @param commandName the name of the command
	 */
	public static void deRegisterInitCommand(Class commandName)
	{
		initCommandClasses.remove(commandName);
	}

	/**
	 * Whether to use the default factory.
	 * @return whether to use the default factory
	 */
	public static boolean isUseDefaultFactory()
	{
		return useDefaultFactory;
	}

	/**
	 * Set whether to use the default factory.
	 * @param b whether to use the default factory
	 */
	public static void setUseDefaultFactory(boolean b)
	{
		useDefaultFactory = b;
	}

}