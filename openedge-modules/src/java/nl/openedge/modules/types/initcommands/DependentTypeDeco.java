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
package nl.openedge.modules.types.initcommands;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import nl.openedge.modules.ModuleFactory;
import nl.openedge.modules.ModuleLookupException;
import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.observers.ModulesLoadedEvent;
import nl.openedge.modules.observers.ModulesLoadedObserver;

/**
 * Tries to solve the dependencies after all modules have been loaded
 * @author Eelco Hillenius
 */
public class DependentTypeDeco
{
	
	/** the decorated instance */
	protected DependentType componentInstance;
	
	/** aliases of components that this component depends on */
	protected List namedDependencies = null;
	
	/** just need to react to modules loaded event once */
	protected static boolean wasAdded = false;
	protected static boolean modulesLoaded = false;
	
	/** instance of module factory */
	protected ModuleFactory moduleFactory = null;
	
	/**
	 * construct
	 */
	public DependentTypeDeco()
	{
		// nothing here
	}
	
	/**
	 * execute command
	 */
	public void execute(Object componentInstance) 
		throws InitCommandException, ConfigException
	{
		
		if(modulesLoaded)
		{
		
			setDependencies(componentInstance);	
		} 
		// else ignore until modules are loaded
	}
	
	public void setDependencies(Object componentInstance)
	{

		if(namedDependencies != null && (namedDependencies.size() > 0))
		{
			
			for(Iterator i = namedDependencies.iterator(); i.hasNext(); )
			{
				NamedDependency dep = (NamedDependency)i.next();
				
				// get module
				Object gotYa = moduleFactory.getModule(dep.getModuleName());
				
				try
				{
					// set module as a property
					BeanUtils.setProperty(
						componentInstance, dep.getPropertyName(), gotYa);	
				}
				catch(Exception e)
				{
					e.printStackTrace();
					throw new ModuleLookupException(e);
				}
			}
		}
	}
	
	/**
	 * @return DependentType the wrapped instance
	 */
	public DependentType getComponentInstance()
	{
		return componentInstance;
	}

	/**
	 * @param type the instance to wrap
	 */
	public void setComponentInstance(DependentType type)
	{
		componentInstance = type;
	}

	/**
	 * @return List named dependencies
	 */
	public List getNamedDependencies()
	{
		return namedDependencies;
	}

	/**
	 * @param list name dependencies
	 */
	public void setNamedDependencies(List namedDependencies)
	{
		this.namedDependencies = namedDependencies;
	}

	/**
	 * @return boolean
	 */
	public static boolean isWasAdded()
	{
		return wasAdded;
	}

	/**
	 * @return ModuleFactory module factory
	 */
	public ModuleFactory getModuleFactory()
	{
		return moduleFactory;
	}

	/**
	 * @param factory module factory
	 */
	public void setModuleFactory(ModuleFactory factory)
	{
		moduleFactory = factory;
		// register for modules loaded event
		if(!wasAdded)
		{
			wasAdded = true;
			moduleFactory.addObserver(new RegisterOnce());
		}
	}


	/**
	 * observe modules loaded event
	 */
	class RegisterOnce implements ModulesLoadedObserver
	{

		/**
		 * fired after all modules are (re)loaded; 
		 * @param evt event
		 */
		public void modulesLoaded(ModulesLoadedEvent evt)
		{
			// set flag
			DependentTypeDeco.modulesLoaded = true;
			
			// test it
			setDependencies(componentInstance);
		}	
	} 

}
