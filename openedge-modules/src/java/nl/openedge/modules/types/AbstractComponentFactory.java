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
import java.util.List;

import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.types.initcommands.InitCommand;
import nl.openedge.modules.types.initcommands.InitCommandException;
import nl.openedge.modules.types.initcommands.RequestLevelInitCommand;

import org.jdom.Element;

/**
 * common base for component factories.
 * 
 * @author Eelco Hillenius
 */
public abstract class AbstractComponentFactory implements ComponentFactory
{

	/** class of component. */
	private Class componentClass = null;

	/** name (alias) of component. */
	private String name = null;

	/** component repository for two way navigation. */
	private ComponentRepository componentRepository = null;

	/** init commands. */
	private InitCommand[] initCommands = null;

	/** request level init commands. */
	private RequestLevelInitCommand[] reqInitCommands = null;

	/**
	 * construct with class.
	 * 
	 * @param componentClass class of component
	 * @throws ConfigException when an configuration error occurs
	 */
	public void setComponentClass(Class componentClass) throws ConfigException
	{
		// test first
		Object instance = null;
		try
		{
			instance = componentClass.newInstance();
		}
		catch (InstantiationException ex)
		{
			throw new ConfigException(ex);
		}
		catch (IllegalAccessException ex)
		{
			throw new ConfigException(ex);
		}
		this.componentClass = componentClass;
	}

	/**
	 * execute the init commands.
	 * 
	 * @param componentInstance instance to execute commands on
	 * @throws InitCommandException
	 * @throws ConfigException when an configuration error occurs
	 * @throws InitCommandException when an init command throws an exception
	 */
	protected void executeInitCommands(Object componentInstance)
			throws InitCommandException, ConfigException
	{

		if (initCommands != null && (initCommands.length > 0))
		{
			for (int i = 0; i < initCommands.length; i++)
			{
				initCommands[i].execute(componentInstance);
			}
		}
	}

	/**
	 * execute the request level init commands.
	 * 
	 * @param componentInstance instance to execute commands on
	 * @throws InitCommandException
	 * @throws ConfigException when an configuration error occurs
	 * @throws InitCommandException when an init command throws an exception
	 */
	protected void executeRequestLevelInitCommands(Object componentInstance)
			throws InitCommandException, ConfigException
	{

		if (reqInitCommands != null && (reqInitCommands.length > 0))
		{
			for (int i = 0; i < reqInitCommands.length; i++)
			{
				reqInitCommands[i].execute(componentInstance);
			}
		}
	}

	/**
	 * gets the name from config.
	 * 
	 * @return String name
	 */
	public final String getName()
	{
		return name;
	}

	/**
	 * sets the name from config.
	 * 
	 * @param name alias for this instance
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Get class of component.
	 * @return Class of component
	 */
	public Class getComponentClass()
	{
		return componentClass;
	}

	/**
	 * get component factory.
	 * 
	 * @return ComponentRepository
	 */
	public ComponentRepository getComponentRepository()
	{
		return componentRepository;
	}

	/**
	 * set component factory.
	 * 
	 * @param factory component factory
	 */
	public void setComponentRepository(ComponentRepository factory)
	{
		componentRepository = factory;
	}

	/**
	 * Get init commands.
	 * @return init commands
	 */
	public InitCommand[] getInitCommands()
	{
		return initCommands;
	}

	/**
	 * Set init commands.
	 * @param commands init commands
	 */
	public void setInitCommands(InitCommand[] commands)
	{
		List tempInit = new ArrayList();
		List tempReqInit = new ArrayList();
		if (commands != null)
		{
			for (int i = 0; i < commands.length; i++)
			{
				if (commands[i] instanceof RequestLevelInitCommand)
				{
					tempReqInit.add(commands[i]);
				}
				else
				{
					tempInit.add(commands[i]);
				}
			}
		}
		InitCommand[] cmds = (InitCommand[]) tempInit.toArray(new InitCommand[tempInit
				.size()]);
		this.initCommands = cmds;

		RequestLevelInitCommand[] reqcmds = (RequestLevelInitCommand[]) tempReqInit
				.toArray(new RequestLevelInitCommand[tempReqInit.size()]);
		this.reqInitCommands = reqcmds;
	}

	/**
	 * set config node.
	 * 
	 * @param componentNode config node
	 * @throws ConfigException when an configuration error occurs
	 */
	public void setComponentNode(Element componentNode) throws ConfigException
	{
		// nothing by default
	}

	/**
	 * get component instance.
	 * 
	 * @return instance of component
	 */
	public abstract Object getComponent();

}