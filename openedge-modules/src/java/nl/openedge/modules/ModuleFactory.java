/*
 * $Header$
 * $Revision$
 * $Date$
 */
package nl.openedge.modules;

import javax.servlet.ServletContext;

import nl.openedge.util.config.ConfigException;

import org.jdom.Element;
import org.quartz.Scheduler;

/**
 * The ModuleFactory constructs and initialises objects.
 * 
 * @author Eelco Hillenius
 */
public interface ModuleFactory extends CriticalEventObserver
{
	
	/**
	 * initialize the module factory
	 * @param factoryNode
	 * @param servletContext
	 * @throws ConfigException
	 */
	public void init(Element factoryNode, ServletContext servletContext) 
					throws ConfigException;

	/**
	 * add observer of module factory events
	 * @param observer
	 */
	public abstract void addObserver(ModuleFactoryObserver observer);

	/**
	 * remove observer of module factory events
	 * @param observer
	 */
	public abstract void removeObserver(ModuleFactoryObserver observer);

	/**
	 * returns instance of module
	 * can throw ModuleException (runtime exception) if a loading or 
	 * initialisation error occured or when no module was found stored 
	 * under the given name
	 * @param name the name (alias) of module
	 * @return Object module instance
	 */
	public abstract Object getModule(String name);

	/**
	 * get the quartz sceduler
	 * @return Scheduler
	 */
	public abstract Scheduler getScheduler();
}