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

	/** logger. */
	private Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * construct.
	 */
	public LooselyTypedComponentRepository()
	{
		// nothing here
	}

	/**
	 * add one component.
	 * 
	 * @param name
	 *            component name
	 * @param clazz
	 *            component class
	 * @param node
	 *            component config node
	 * @throws ConfigException
	 *             when an configuration error occurs
	 */
	@Override
	protected void addComponent(String name, Class clazz, Element node) throws ConfigException
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

	/**
	 * get the component factory.
	 * 
	 * @param name
	 *            component name
	 * @param clazz
	 *            component class
	 * @param node
	 *            configuration node
	 * @return ComponentFactory
	 * @throws ConfigException
	 *             when an configuration error occurs
	 */
	@Override
	protected ComponentFactory getComponentFactory(String name, Class clazz, Element node)
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
	protected void addInitCommands(ComponentFactory factory, Class clazz, Element node)
			throws ConfigException
	{

		Element cmdNode = node.getChild("init-commands");
		if (cmdNode != null)
		{
			List commands = new ArrayList();
			List cmdList = cmdNode.getChildren("command");

			for (Iterator i = cmdList.iterator(); i.hasNext();)
			{
				Element cnode = (Element) i.next();
				String commandName = cnode.getTextNormalize();

				InitCommand initCommand = TypesRegistry.getInitCommand(commandName);

				// initialize the command
				initCommand.init(factory.getName(), node, this);
				// add command to the list
				commands.add(initCommand);

				InitCommand[] cmds =
					(InitCommand[]) commands.toArray(new InitCommand[commands.size()]);

				if (cmds.length > 0)
				{
					factory.setInitCommands(cmds);
				}

			}

		}

	}

	/**
	 * @see nl.openedge.components.ComponentRepository#getModulesByType(java.lang.Class,
	 *      boolean)
	 */
	@Override
	public List getComponentsByType(Class type, boolean exact)
	{
		List sublist = new ArrayList();

		if (type == null)
		{
			return sublist;
		}

		if (exact)
		{
			for (Iterator i = getComponents().values().iterator(); i.hasNext();)
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
			for (Iterator i = getComponents().values().iterator(); i.hasNext();)
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
