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
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.beanutils.BeanUtils;

import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.ComponentLookupException;
import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.observers.ComponentsLoadedEvent;
import nl.openedge.modules.observers.ComponentObserver;

/**
 * Tries to solve the dependencies after all components have been loaded
 * @author Eelco Hillenius
 */
public class DependentTypeWrapper
{
	
	/** the decorated instance */
	protected Object componentInstance;
	
	/** aliases of components that this component depends on */
	protected List namedDependencies = null;
	
	/** just need to react to components loaded event once */
	protected static boolean wasAdded = false;
	protected static boolean modulesLoaded = false;
	
	/** instance of module factory */
	protected ComponentRepository moduleFactory = null;
	
	/** name of the component */
	protected String componentName = null;
	
	/** used for cycle check */
	protected static ThreadLocal referenceHolder = new ThreadLocal(); 
	
	/**
	 * construct
	 */
	public DependentTypeWrapper()
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
		// else ignore until components are loaded
	}
	
	public void setDependencies(Object componentInstance)
		throws CyclicDependencyException
	{

		if(namedDependencies != null && (namedDependencies.size() > 0))
		{
	
			Set references = (Set)referenceHolder.get();
			
			if(references == null)
			{
				references = new TreeSet();
				references.add(componentName);
				referenceHolder.set(references);
			}
			
			for(Iterator i = namedDependencies.iterator(); i.hasNext(); )
			{
				NamedDependency dep = (NamedDependency)i.next();
				
				if(references.contains(dep.getModuleName()))
				{
					// got a cycle!
					String name = dep.getModuleName();
					String message = "\n\ncomponent with name " +
						this.componentName + " has a cyclic dependency:" +
						" component with name " + name + 
						" was allready referenced. \nHere's a list of" +
						" references where the cycle was detected:\n" +
						references.toString() + " -> " + name + "\n";
					
					throw new CyclicDependencyException(message);
				}
				else
				{
					references.add(dep.getModuleName());
				}
				
				// get module
				Object gotYa = moduleFactory.getComponent(dep.getModuleName());
				
				try
				{
					// set module as a property
					BeanUtils.setProperty(
						componentInstance, dep.getPropertyName(), gotYa);	
				}
				catch(Exception e)
				{
					e.printStackTrace();
					throw new ComponentLookupException(e);
				}
			}
			
			referenceHolder.set(null);
		}
	}
	
	/**
	 * @return DependentType the wrapped instance
	 */
	public Object getComponentInstance()
	{
		return componentInstance;
	}

	/**
	 * @param instance the instance to wrap
	 */
	public void setComponentInstance(Object instance)
	{
		componentInstance = instance;
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
	 * get component name
	 * @return String
	 */
	public String getComponentName()
	{
		return componentName;
	}

	/**
	 * set component name
	 * @param componentName name of the component
	 */
	public void setComponentName(String componentName)
	{
		this.componentName = componentName;
	}

	/**
	 * @return ComponentRepository module factory
	 */
	public ComponentRepository getModuleFactory()
	{
		return moduleFactory;
	}

	/**
	 * @param factory module factory
	 */
	public void setModuleFactory(ComponentRepository factory)
	{
		moduleFactory = factory;
		// register for components loaded event
		if(!wasAdded)
		{
			wasAdded = true;
			moduleFactory.addObserver(new RegisterOnce());
		}
	}


	/**
	 * observe components loaded event
	 */
	class RegisterOnce implements ComponentObserver
	{

		/**
		 * fired after all components are (re)loaded; 
		 * @param evt event
		 */
		public void modulesLoaded(ComponentsLoadedEvent evt)
		{
			// set flag
			DependentTypeWrapper.modulesLoaded = true;
			
			// test it
			setDependencies(componentInstance);
		}	
	}

}