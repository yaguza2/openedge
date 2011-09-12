package nl.openedge.modules.observers;

/**
 * observers that implement this interface will be notified when all components are
 * (re)loaded (and have gone through some basic tests).
 * 
 * @author Eelco Hillenius
 */
public interface ComponentObserver extends ComponentRepositoryObserver
{
	/**
	 * fired after all components are (re)loaded.
	 * 
	 * @param evt
	 *            event
	 */
	void modulesLoaded(ComponentsLoadedEvent evt);
}
