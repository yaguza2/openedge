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

import nl.openedge.modules.ModuleFactory;
import nl.openedge.modules.ModuleLookpupException;
import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.types.initcommands.InitCommand;
import nl.openedge.modules.types.initcommands.InitCommandException;

/**
 * common base for module wrappers
 * @author Eelco Hillenius
 */
public abstract class ModuleAdapter
{

	/** class of module */
	protected Class moduleClass = null;

	/** name (alias) of module */
	protected String name = null;
	
	/** module factory for two way navigation */
	protected ModuleFactory moduleFactory = null;

	/** init commands */
	private InitCommand[] initCommands = null;

	/**
	 * construct with class
	 * @param moduleClass	class of module
	 */
	public void setModuleClass(Class moduleClass) throws ConfigException
	{
		// test first
		Object instance = null;
		try
		{
			instance = moduleClass.newInstance();
		}
		catch (InstantiationException ex)
		{
			throw new ConfigException(ex);
		}
		catch (IllegalAccessException ex)
		{
			throw new ConfigException(ex);
		}
		this.moduleClass = moduleClass;
	}

	/**
	 * execute the commands
	 * @param componentInstance instance to execute commands on
	 * @throws InitCommandException
	 * @throws ConfigException
	 */
	protected void executeInitCommands(Object componentInstance)
		throws InitCommandException, ConfigException 
	{
		
		if(initCommands != null && (initCommands.length > 0))
		{
			for(int i = 0; i < initCommands.length; i++)
			{
				initCommands[i].execute(componentInstance);
			}
		}
	}

	/**
	 * gets the name from config
	 * @return String
	 */
	public final String getName()
	{
		return name;
	}

	/**
	 * sets the name from config
	 * @param name	alias for this instance
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return Class of module
	 */
	public Class getModuleClass()
	
	{
		return moduleClass;
	}
	
	/**
	 * get module factory
	 * @return ModuleFactory
	 */
	public ModuleFactory getModuleFactory()
	{
		return moduleFactory;
	}

	/**
	 * set module factory
	 * @param factory module factory
	 */
	public void setModuleFactory(ModuleFactory factory)
	{
		moduleFactory = factory;
	}

	/**
	 * @return
	 */
	public InitCommand[] getInitCommands()
	{
		return initCommands;
	}

	/**
	 * @param commands
	 */
	public void setInitCommands(InitCommand[] commands)
	{
		this.initCommands = commands;
	}
	
	/** get instantiated module */
	public abstract Object getModule() throws ModuleLookpupException;

}
