/*
 * $Header$
 * $Revision$
 * $Date$
 */
package nl.openedge.modules;

import java.util.EventObject;

import org.quartz.Scheduler;

/**
 * fired when scheduler was started
 * @author Eelco Hillenius
 */
public class SchedulerStartedEvent extends EventObject {

	protected Scheduler scheduler = null;

	/**
	 * @param source	sender of event
	 * @param scheduler	subject of event
	 */
	public SchedulerStartedEvent(Object source, Scheduler scheduler) {
		super(source);
		this.scheduler = scheduler;
	}
	
	

}
