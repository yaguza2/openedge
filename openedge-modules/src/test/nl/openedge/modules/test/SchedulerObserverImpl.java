/*
 * $Id$
 * $Revision$
 * $date$
 */
package nl.openedge.modules.test;

import nl.openedge.modules.observers.SchedulerObserver;
import nl.openedge.modules.observers.SchedulerStartedEvent;

/**
 * @author Eelco Hillenius
 */
public class SchedulerObserverImpl implements SchedulerObserver
{
	
	private SchedulerStartedEvent evt;
	
	/**
	 * fired after initialisation and startup of the Quartz scheduler, 
	 * before the actual scheduling of jobs and triggers
	 * @param evt holds instance of scheduler
	 */
	public void schedulerStarted(SchedulerStartedEvent evt)
	{
		this.evt = evt;
	}

	/**
	 * @return SchedulerStartedEvent
	 */
	public SchedulerStartedEvent getEvt()
	{
		return evt;
	}

}
