package nl.openedge.modules.types.initcommands;

import java.lang.reflect.Method;

import nl.openedge.modules.observers.ChainedEvent;
import nl.openedge.modules.observers.ChainedEventObserver;
import nl.openedge.modules.observers.ObserveException;

/**
 * @author Eelco Hillenius
 */
public class ChainedEventObserverDecorator extends ComponentFactoryObserverDecorator implements
		ChainedEventObserver
{
	@Override
	public void recieveChainedEvent(ChainedEvent evt)
	{
		Object decorated = getDecorated();
		Class< ? > clazz = decorated.getClass();
		try
		{
			Method initMethod =
				clazz.getMethod("recieveChainedEvent", new Class[] {ChainedEvent.class});
			initMethod.invoke(decorated, new Object[] {evt});
		}
		catch (Exception e)
		{
			throw new ObserveException(e);
		}
	}
}
