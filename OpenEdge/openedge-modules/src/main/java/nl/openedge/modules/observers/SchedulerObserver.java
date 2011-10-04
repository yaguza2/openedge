package nl.openedge.modules.observers;

/**
 * a scheduler observer gets the chance to do extra configuration (like adding quartz
 * calendars and global listeners etc.) BEFORE jobs and triggers are actually scheduled by
 * the module factory. NOTE that this contract depends on the implementation of the
 * ComponentRepository, so at this stage, only the default implementations that are
 * packaged with this framework guarantee this behaviour.
 * 
 * @author Eelco Hillenius
 */
public interface SchedulerObserver extends ComponentRepositoryObserver
{

	/**
	 * fired after initialisation and startup of the Quartz scheduler, before the actual
	 * scheduling of jobs and triggers.
	 * 
	 * @param evt
	 *            holds instance of scheduler
	 */
	void schedulerStarted(SchedulerStartedEvent evt);

}
