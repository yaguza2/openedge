/*
 * $Header$
 * $Revision$
 * $Date$
 */
package nl.openedge.modules;

/**
 * a scheduler observer gets the chance to do extra configuration 
 * (like adding quartz calendars and global listeners etc. BEFORE
 *  jobs and triggers are actually scheduled by the module factory.
 * 
 * @author Eelco Hillenius
 */
public interface SchedulerObserver extends ModuleFactoryObserver {

	public void schedulerStarted(SchedulerStartedEvent evt);

}
