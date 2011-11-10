package nl.openedge.modules.observers;

/**
 * a chained event observer listenes for events that are fired by other components that
 * are <code>ChainedEventCaster</code> s or the <code>ComponentRepository</code>.
 * 
 * @author Eelco Hillenius
 */
public interface ChainedEventObserver extends ComponentRepositoryObserver
{

	/**
	 * recieve a chained event.
	 * 
	 * @param evt
	 *            the chained event
	 */
	void recieveChainedEvent(ChainedEvent evt);

}
