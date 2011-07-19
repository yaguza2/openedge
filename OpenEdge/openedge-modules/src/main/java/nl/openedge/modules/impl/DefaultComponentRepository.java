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
