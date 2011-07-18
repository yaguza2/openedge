package nl.openedge.modules.types.base;

import nl.openedge.modules.ComponentLookupException;
import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.observers.ComponentObserver;
import nl.openedge.modules.observers.ComponentsLoadedEvent;
import nl.openedge.modules.types.AbstractComponentFactory;
import nl.openedge.modules.types.initcommands.InitCommandException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * wrapper for singleton components.
 * 
 * @author Eelco Hillenius
 */
public final class SingletonTypeFactory extends AbstractComponentFactory implements
		ComponentObserver
{
	private static final Logger log = LoggerFactory.getLogger(SingletonTypeFactory.class);

	private Object singletonInstance;

	private boolean executeInitCommands = true;

	@Override
	public Object getComponent()
	{
		synchronized (this)
		{
			if (this.singletonInstance == null)
			{
				try
				{
					singletonInstance = getComponentClass().newInstance();
				}
				catch (InstantiationException e)
				{
					throw new ComponentLookupException(e);
				}
				catch (IllegalAccessException e)
				{
					throw new ComponentLookupException(e);
				}
			}
			try
			{
				executeRequestLevelInitCommands(singletonInstance);
			}
			catch (InitCommandException e)
			{
				e.printStackTrace();
				throw new ComponentLookupException(e);
			}
			catch (ConfigException e)
			{
				e.printStackTrace();
				throw new ComponentLookupException(e);
			}

			if (executeInitCommands)
			{
				executeInitCommands = false;

				try
				{
					executeInitCommands(singletonInstance);
				}
				catch (InitCommandException e)
				{
					e.printStackTrace();
					throw new ComponentLookupException(e);
				}
				catch (ConfigException e)
				{
					log.error(e.getMessage(), e);
					throw new ComponentLookupException(e);
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
