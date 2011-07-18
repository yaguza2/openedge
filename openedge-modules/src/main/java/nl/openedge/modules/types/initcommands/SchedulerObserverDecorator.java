package nl.openedge.modules.types.initcommands;

import java.lang.reflect.Method;

import nl.openedge.modules.observers.ObserveException;
import nl.openedge.modules.observers.SchedulerObserver;
import nl.openedge.modules.observers.SchedulerStartedEvent;

public class SchedulerObserverDecorator extends ComponentFactoryObserverDecorator implements
		SchedulerObserver
{
	@Override
	public void schedulerStarted(SchedulerStartedEvent evt)
	{
		Object decorated = getDecorated();
		Class< ? > clazz = decorated.getClass();
		try
		{
			Method initMethod =
				clazz.getMethod("schedulerStarted", new Class[] {SchedulerStartedEvent.class});
			initMethod.invoke(decorated, new Object[] {evt});
		}
		catch (Exception e)
		{
			throw new ObserveException(e);
		}
	}
}
