package nl.openedge.modules.types.initcommands;

import java.lang.reflect.Method;

import nl.openedge.modules.observers.ComponentObserver;
import nl.openedge.modules.observers.ComponentsLoadedEvent;
import nl.openedge.modules.observers.ObserveException;

/**
 * Observer decorator.
 * 
 * @author Eelco Hillenius
 */
public class ComponentObserverDecorator extends ComponentFactoryObserverDecorator implements
		ComponentObserver
{
	@Override
	public void modulesLoaded(ComponentsLoadedEvent evt)
	{
		Object decorated = getDecorated();
		Class< ? > clazz = decorated.getClass();
		try
		{
			Method initMethod =
				clazz.getMethod("modulesLoaded", new Class[] {ComponentsLoadedEvent.class});
			initMethod.invoke(decorated, new Object[] {evt});
		}
		catch (Exception e)
		{
			throw new ObserveException(e);
		}
	}
}
