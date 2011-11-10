package nl.openedge.modules.observers;

/**
 * A chained event caster can fire chained events. The events that chained event casters
 * fire*, are observed by implementors of ChainedEventObserver* (e.g. 'emergency events')
 * to other parts of the system (e.g. a controller servlet).
 * 
 * @author Eelco Hillenius
 */
public interface ChainedEventCaster
{

	/**
	 * Adds an observer for ChainedEvents.
	 * 
	 * @param observer
	 *            the observer
	 */
	void addObserver(ChainedEventObserver observer);

}
