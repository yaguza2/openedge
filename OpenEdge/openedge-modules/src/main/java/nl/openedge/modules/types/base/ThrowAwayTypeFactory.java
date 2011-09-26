package nl.openedge.modules.types.base;

import nl.openedge.modules.ComponentLookupException;
import nl.openedge.modules.types.AbstractComponentFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * wrapper for throw away components.
 * 
 * @author Eelco Hillenius
 */
public final class ThrowAwayTypeFactory extends AbstractComponentFactory
{
	private static final Logger log = LoggerFactory.getLogger(ThrowAwayTypeFactory.class);

	@Override
	public Object getComponent()
	{
		Object instance = null;
		try
		{
			instance = getComponentClass().newInstance();
		}
		catch (InstantiationException ex)
		{
			throw new ComponentLookupException(ex);
		}
		catch (IllegalAccessException ex)
		{
			throw new ComponentLookupException(ex);
		}

		try
		{
			executeRequestLevelInitCommands(instance);
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
			throw new ComponentLookupException(e);
		}

		try
		{
			executeInitCommands(instance);
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
			throw new ComponentLookupException(e);
		}

		return instance;
	}
}
