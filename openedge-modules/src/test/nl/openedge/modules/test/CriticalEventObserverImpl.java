package nl.openedge.modules.test;

import nl.openedge.modules.CriticalEvent;
import nl.openedge.modules.CriticalEventObserver;

/**
 * @author Eelco Hillenius
 */
public class CriticalEventObserverImpl implements CriticalEventObserver
{

	// event
	private CriticalEvent evt = null;

	/**
	 * @see nl.openedge.modules.CriticalEventObserver#criticalEventOccured(nl.openedge.modules.CriticalEvent)
	 */
	public void criticalEventOccured(CriticalEvent evt)
	{

		System.out.println("critical event: " + evt + " received from " + evt.getSource());
		this.evt = evt;
	}

	/**
	 * gets the event
	 * @return CriticalEvent
	 */
	public CriticalEvent getCriticalEvent()
	{
		return evt;
	}

}
