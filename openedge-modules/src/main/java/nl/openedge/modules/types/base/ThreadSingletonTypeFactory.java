package nl.openedge.modules.types.base;

import nl.openedge.modules.ComponentLookupException;
import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.observers.ComponentObserver;
import nl.openedge.modules.observers.ComponentsLoadedEvent;
import nl.openedge.modules.types.AbstractComponentFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * wrapper for singleton components per Thread.
 * 
 * @author Eelco Hillenius
 */
public final class ThreadSingletonTypeFactory extends AbstractComponentFactory implements
		ComponentObserver
{
	private static final Logger log = LoggerFactory.getLogger(ThreadSingletonTypeFactory.class);

	private ThreadLocal<Object> singletonInstanceHolder = new ThreadLocal<Object>();

	private boolean executeInitCommands = true;

	@Override
	public Object getComponent()
	{
		Object singletonInstance = singletonInstanceHolder.get();

		synchronized (this)
		{
			if (singletonInstance == null)
			{
				try
				{
					singletonInstance = getComponentClass().newInstance();
					singletonInstanceHolder.set(singletonInstance);
				}
				catch (InstantiationException e)
				{
					throw new ComponentLookupException(e);
				}
				catch (IllegalAccessException e)
				{
					throw new ComponentLookupException(e);
				}

				try
				{
					executeRequestLevelInitCommands(singletonInstance);
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
					throw new ComponentLookupException(e);
				}

				if (executeInitCommands)
				{
					try
					{
						executeInitCommands(singletonInstance);
					}
					catch (Exception e)
					{
						log.error(e.getMessage(), e);
						throw new ComponentLookupException(e);
					}
				}
			}
		}

		return singletonInstance;
	}

	@Override
	public void setComponentRepository(ComponentRepository componentRepository)
	{
		super.setComponentRepository(componentRepository);
		componentRepository.addObserver(this);
	}

	@Override
	public void modulesLoaded(ComponentsLoadedEvent evt)
	{
	}
}
