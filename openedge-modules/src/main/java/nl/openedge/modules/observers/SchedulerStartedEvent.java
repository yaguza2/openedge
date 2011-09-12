package nl.openedge.modules.observers;

import java.util.EventObject;

import org.quartz.Scheduler;

/**
 * fired when scheduler was started.
 * 
 * @author Eelco Hillenius
 */
public final class SchedulerStartedEvent extends EventObject
{
	private static final long serialVersionUID = 1L;

	/** reference to scheduler. */
	private Scheduler scheduler = null;

	/**
	 * Construct.
	 * 
	 * @param source
	 *            sender of event
	 * @param scheduler
	 *            subject of event
	 */
	public SchedulerStartedEvent(Object source, Scheduler scheduler)
	{
		super(source);
		this.scheduler = scheduler;
	}

	/**
	 * Get reference to scheduler.
	 * 
	 * @return Scheduler
	 */
	public Scheduler getScheduler()
	{
		return scheduler;
	}

	/**
	 * Set reference to scheduler.
	 * 
	 * @param scheduler
	 *            Scheduler
	 */
	public void setScheduler(Scheduler scheduler)
	{
		this.scheduler = scheduler;
	}

}
