/*
 * $Header$
 * $Revision$
 * $Date$
 */
package nl.openedge.modules;

import java.io.Serializable;

import javax.naming.Referenceable;

import org.quartz.Scheduler;

/**
 * The ModuleFactory constructs and initialises objects.
 * 
 * @author Eelco Hillenius
 */
public interface ModuleFactory extends CriticalEventObserver, 
					Referenceable, Serializable {
	
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
	 * @param name the name (alias) of module
	 * @return Object module instance
	 * @throws ModuleException if a loading or initialisation error occured or
	 * 				when no module was found stored under given name
	 */
	public abstract Object getModule(String name) throws ModuleException;
	
	/**
	 * get the quartz sceduler
	 * @return Scheduler
	 */
	public abstract Scheduler getScheduler();
}