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
package nl.openedge.modules.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;

import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.observers.ChainedEventCaster;
import nl.openedge.modules.observers.ComponentFactoryObserver;
import nl.openedge.modules.types.base.JobTypeBuilderFactory;
import nl.openedge.modules.types.base.SingletonType;
import nl.openedge.modules.types.base.SingletonTypeBuilderFactory;
import nl.openedge.modules.types.base.ThreadSingletonType;
import nl.openedge.modules.types.base.ThreadSingletonTypeBuilderFactory;
import nl.openedge.modules.types.base.ThrowAwayType;
import nl.openedge.modules.types.base.ThrowAwayTypeBuilderFactory;
import nl.openedge.modules.types.initcommands.BeanType;
import nl.openedge.modules.types.initcommands.BeanTypeInitCommand;
import nl.openedge.modules.types.initcommands.ConfigurableType;
import nl.openedge.modules.types.initcommands.ConfigurableTypeInitCommand;
import nl.openedge.modules.types.initcommands.CriticalEventCasterInitCommand;
import nl.openedge.modules.types.initcommands.DependentType;
import nl.openedge.modules.types.initcommands.DependentTypeInitCommand;
import nl.openedge.modules.types.initcommands.ComponentFactoryObserverInitCommand;
import nl.openedge.modules.types.initcommands.InitCommand;

/**
 * Registry for types, adapters and init commands
 * @author Eelco Hillenius
 */
public class TypesRegistry
{

	/* 
	 * List of base types (Class)
	 */
	private static List baseTypes = new ArrayList(3);
	
	/*
	 * Map of adapter factories. Keyed on types, the values
	 * are instances of BuilderFactory
	 */
	private static Map baseTypeAdapterFactories = new HashMap(3);
	
	/*
	 * List of command types (Class). These types can do additional
	 * processing (like configurating) with types at initialization time
	 * NOTE: order can matter here! 
	 * E.g. its best to first add BeanType, and then ConfigurableType
	 * as usually it's handy to have a component populated first
	 * and then do some more initialization with it
	 */
	private static List initCommandTypes = new ArrayList(2);
	
	/*
	 * Map of init commands. Keyed on types, the values
	 * are classes of commands for the types
	 */
	private static Map initCommandClasses = new HashMap(2);
	
	/*
	 * the default adapter factory will be used when the 
	 * component is not of a type registered as a base type
	 * in this registry
	 */
	private static BuilderFactory defaultAdapterFactory = 
		new SingletonTypeBuilderFactory();

	/*
	 * set the defaults 
	 */
	static 
	{
		// add the default basetypes
		baseTypes.add(SingletonType.class);
		baseTypes.add(ThreadSingletonType.class);
		baseTypes.add(ThrowAwayType.class);
		baseTypes.add(Job.class);
		
		// and the adapter factories for them
		
		baseTypeAdapterFactories.put(
			SingletonType.class, 
			new SingletonTypeBuilderFactory());
			
		baseTypeAdapterFactories.put(
			ThreadSingletonType.class, 
			new ThreadSingletonTypeBuilderFactory());
			
		baseTypeAdapterFactories.put(
			ThrowAwayType.class, 
			new ThrowAwayTypeBuilderFactory());
			
		baseTypeAdapterFactories.put(
			Job.class, 
			new JobTypeBuilderFactory());
			
		
		// add the default enhancer types
		initCommandTypes.add(BeanType.class);
		initCommandTypes.add(ConfigurableType.class);
		initCommandTypes.add(ChainedEventCaster.class);
		initCommandTypes.add(ComponentFactoryObserver.class);
		initCommandTypes.add(DependentType.class);

		// and the commands for them
		initCommandClasses.put(
			BeanType.class, 
			BeanTypeInitCommand.class);
			
		initCommandClasses.put(
			ConfigurableType.class, 
			ConfigurableTypeInitCommand.class);
			
		initCommandClasses.put(
			ChainedEventCaster.class, 
			CriticalEventCasterInitCommand.class);
			
		initCommandClasses.put(
			ComponentFactoryObserver.class, 
			ComponentFactoryObserverInitCommand.class);
			
		initCommandClasses.put(
			DependentType.class, 
			DependentTypeInitCommand.class);
	}

	/**
	 * get the base types
	 * @return List
	 */
	public static List getBaseTypes()
	{
		return Collections.unmodifiableList(baseTypes);
	}
	
	/**
	 * get the default adapter factory that is to be used when 
	 * components are not of a type registered as a base type
	 * in this registry
	 * @return BuilderFactory
	 */
	public static BuilderFactory getDefaultAdapterFactory()
	{
		return defaultAdapterFactory;
	}

	/**
	 * set the default adapter factory that is to be used when 
	 * components are not of a type registered as a base type
	 * in this registry
	 * @param factory the default adapter factory
	 */
	public static void setDefaultAdapterFactory(BuilderFactory factory)
	{
		defaultAdapterFactory = factory;
	}

	/**
	 * get the adapter factory for the given type
	 * @param clazz type to get adapter factory for
	 * @return
	 */
	public static BuilderFactory getAdapterFactory(Class clazz)
	{
		return (BuilderFactory)baseTypeAdapterFactories.get(clazz);
	}
	
	/**
	 * register an adapter factory for the given class
	 * @param clazz the class
	 * @param adapterFactory the adapter factory
	 */
	public static void registerAdapterFactory(
		Class clazz, 
		BuilderFactory adapterFactory)
	{
		baseTypeAdapterFactories.put(clazz, adapterFactory);
	}

	/**
	 * get the init commands
	 * @return List command types
	 */
	public static List getInitCommandTypes()
	{
		return Collections.unmodifiableList(initCommandTypes);
	}
	
	/**
	 * get the init command for the given type
	 * @param clazz the type to get the init command for
	 * @return InitCommand the command
	 * @throws ConfigException if no command was found or instantiation failed
	 */
	public static InitCommand getInitCommand(Class clazz) throws ConfigException
	{
		
		Class c = (Class)initCommandClasses.get(clazz);
		if(c != null)
		{
			try
			{
				return (InitCommand)c.newInstance();	
			}
			catch (Exception e)
			{
				throw new ConfigException(e);
			}
		}
		else
		{
			throw new ConfigException(
				"init command for " + clazz.getName() + " not found");
		}
	}
	
	/**
	 * register an init command for the given class
	 * @param clazz the class
	 * @param adapterFactory the adapter factory
	 */
	public static void registerInitCommand(
		Class typeClass, 
		Class initCommandClass)
	{
		initCommandClasses.put(typeClass, initCommandClass);
	}

}
