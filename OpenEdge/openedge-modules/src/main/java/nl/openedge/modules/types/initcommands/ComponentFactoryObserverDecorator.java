package nl.openedge.modules.types.initcommands;

import nl.openedge.modules.observers.ComponentRepositoryObserver;

/**
 * Decorator for component factory observer.
 * 
 * @author Eelco Hillenius
 */
public class ComponentFactoryObserverDecorator implements ComponentRepositoryObserver
{
	private Object decorated;

	public Object getDecorated()
	{
		return decorated;
	}

	public void setDecorated(Object object)
	{
		decorated = object;
	}
}
