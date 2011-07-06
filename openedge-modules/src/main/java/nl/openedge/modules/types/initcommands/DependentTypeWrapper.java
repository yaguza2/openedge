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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.openedge.modules.ComponentLookupException;
import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.observers.ComponentObserver;
import nl.openedge.modules.observers.ComponentsLoadedEvent;
import ognl.Ognl;
import ognl.OgnlException;

/**
 * Tries to solve the dependencies after all components have been loaded. <br>
 * Users can set the static property 'failOnCycle' to true if they want the resolving of
 * dependencies stopped (and thus stop the loading of the component repository) when a circular
 * dependency is detected. Be sure to set this property BEFORE initialising the component
 * repository. <br>
 * If 'failOnCycle' is false (the default), the resolving of dependencies will not stop when a
 * circular dependency is detected. In this case, instead of getting the instance of the component
 * for which the circular dependency was detected from the repository, a cached instance will be
 * used for setting the dependency of the current dependent component. <br>
 * WARNING: as dependencies are potentially loaded from a temporary cache instead of via the
 * component repository when 'failOnCycle' is false, it is not safe to work with components with
 * state (ThrowAwayTypes). If there a cycle was detected, the same instance of a component is shared
 * by more than one module, even if the normal behaviour of the type factory would be to create a
 * new intance (like with ThrowAwayTypes). In most cases, this probably would not be a problem, but
 * if it is, consider setting the 'failOnCycle' property to true or just using the component
 * repository directely in the components that have dependencies instead tagging it as a dependent
 * type.
 * 
 * @author Eelco Hillenius
 */
public final class DependentTypeWrapper
{

	/**
	 * observe components loaded event.
	 */
	private class RegisterOnce implements ComponentObserver
	{

		/**
		 * fired after all components are (re)loaded.
		 * 
		 * @param evt event
		 */
		public void modulesLoaded(ComponentsLoadedEvent evt)
		{
			// test it
			setDependencies(componentInstance);
		}
	}

	/** log. */
	private static Logger log = LoggerFactory.getLogger(DependentTypeWrapper.class);

	/** fail on cycle. */
	private static boolean failOnCycle = false;

	/** used for cycle check. */
	private static ThreadLocal referenceHolder = new ThreadLocal();

	/** used for temporary storing of resolved dependencies. */
	private static ThreadLocal resolvedComponentsHolder = new ThreadLocal();

	/** just need to react to components loaded event once. */
	private static boolean wasAdded = false;

	/** the decorated instance. */
	private Object componentInstance;

	/** aliases of components that this component depends on. */
	private List namedDependencies = null;

	/** instance of module factory. */
	private ComponentRepository moduleFactory = null;

	/** name of the component. */
	private String componentName = null;

	/**
	 * construct.
	 */
	public DependentTypeWrapper()
	{
		// nothing here
	}

	/**
	 * Execute the command.
	 * @param cInstance component instance
	 * @throws InitCommandException when init command threw an error
	 * @throws ConfigException when the configuration is faulty
	 */
	public void execute(Object cInstance) throws InitCommandException,
			ConfigException
	{
		setDependencies(cInstance);
	}

	/**
	 * Set the dependencies.
	 * @param cInstance component instance
	 */
	public void setDependencies(Object cInstance)
	{

		if (namedDependencies != null && (namedDependencies.size() > 0))
		{

			Set references = (Set) referenceHolder.get();
			if (references == null)
			{
				references = new TreeSet();
				references.add(componentName);
				referenceHolder.set(references);
			}

			Map resolved = (Map) resolvedComponentsHolder.get();
			if (resolved == null)
			{
				resolved = new HashMap();
				resolved.put(componentName, cInstance);
				resolvedComponentsHolder.set(resolved);
			}

			for (Iterator i = namedDependencies.iterator(); i.hasNext();)
			{
				NamedDependency dep = (NamedDependency) i.next();
				Object dependency = null;
				if (references.contains(dep.getModuleName()))
				{
					// got a cycle!
					if (failOnCycle)
					{
						String name = dep.getModuleName();
						String message = "\ncomponent with name "
								+ this.componentName + " has a cyclic dependency:"
								+ " component with name " + name
								+ " was allready referenced. \nHere's a list of"
								+ " references where the cycle was detected:\n"
								+ references.toString() + " -> " + name + "\n";
						throw new CyclicDependencyException(message);
					}
					else
					{
						dependency = resolved.get(dep.getModuleName());
					}
				}
				else
				{
					references.add(dep.getModuleName());
					// get module from repo
					dependency = moduleFactory.getComponent(dep.getModuleName());
				}

				resolved.put(dep.getModuleName(), dependency);

				try
				{
					// set module as a property
					Ognl.setValue(dep.getPropertyName(), cInstance, dependency);
				}
				catch (OgnlException e)
				{
					log.error(e.getMessage(), e);
					throw new ComponentLookupException(e);
				}

			}

			referenceHolder.set(null);
			resolvedComponentsHolder.set(null);
		}
	}

	/**
	 * Get the component instance.
	 * @return DependentType the wrapped instance
	 */
	public Object getComponentInstance()
	{
		return componentInstance;
	}

	/**
	 * Set the component instance.
	 * @param instance the instance to wrap
	 */
	public void setComponentInstance(Object instance)
	{
		componentInstance = instance;
	}

	/**
	 * Get the logical dependencies.
	 * @return List named dependencies
	 */
	public List getNamedDependencies()
	{
		return namedDependencies;
	}

	/**
	 * Set the logical dependencies.
	 * @param namedDependencies name dependencies
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
	 * get component name.
	 * 
	 * @return String
	 */
	public String getComponentName()
	{
		return componentName;
	}

	/**
	 * set component name.
	 * 
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
		if (!wasAdded)
		{
			wasAdded = true;
			moduleFactory.addObserver(new RegisterOnce());
		}
	}

	/**
	 * Get whether to fail when a cycle is detected.
	 * @return whether to fail when a cycle is detected
	 */
	public static boolean isFailOnCycle()
	{
		return failOnCycle;
	}

	/**
	 * Set whether to fail when a cycle is detected.
	 * @param b whether to fail when a cycle is detected
	 */
	public static void setFailOnCycle(boolean b)
	{
		failOnCycle = b;
	}

}
