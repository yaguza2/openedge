/*
 * $Id$
 * $Revision$
 * $date$
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
