/*
 * $Id$
 * $Revision$
 * $date$
 */
package nl.openedge.modules.test;

import nl.openedge.modules.ModuleFactory;
import nl.openedge.modules.observers.ModulesLoadedEvent;
import nl.openedge.modules.observers.ModulesLoadedObserver;


/**
 * @author Eelco Hillenius
 */
public class ModulesLoadedObserverImpl implements ModulesLoadedObserver
{
	
	private ModulesLoadedEvent evt;
	
	/**
	 * fired after all modules are (re)loaded
	 * @param evt event
	 */
	public void modulesLoaded(ModulesLoadedEvent evt)
	{
		this.evt = evt;
		
		ModuleFactory mf = (ModuleFactory)evt.getSource();
		String[] names = mf.getModuleNames();
		
	}

	/**
	 * @return SchedulerStartedEvent
	 */
	public ModulesLoadedEvent getEvt()
	{
		return evt;
	}

}
