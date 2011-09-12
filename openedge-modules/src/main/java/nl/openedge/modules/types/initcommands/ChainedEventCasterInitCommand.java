package nl.openedge.modules.types.initcommands;

import java.lang.reflect.Method;

import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.observers.ChainedEventCaster;
import nl.openedge.modules.observers.ChainedEventObserver;
import nl.openedge.modules.observers.ComponentObserver;
import nl.openedge.modules.observers.ComponentsLoadedEvent;

import org.jdom.Element;

/**
 * Command that populates instances using BeanUtils.
 * 
 * @author Eelco Hillenius
 */
public class ChainedEventCasterInitCommand implements InitCommand, ComponentObserver
{
	private ComponentRepository componentRepository = null;

	private boolean executeInitCommands = true;

	@Override
	public void init(String componentName, Element componentNode, ComponentRepository cRepo)
	{
		this.componentRepository = cRepo;
	}

	@Override
	public void execute(Object componentInstance) throws ConfigException
	{
		if (executeInitCommands)
		{
			executeInitCommands = false;

			if (componentInstance instanceof ChainedEventCaster)
			{
				((ChainedEventCaster) componentInstance).addObserver(this.componentRepository);
			}
			else
			{
				Class< ? > clazz = componentInstance.getClass();
				try
				{
					Method initMethod =
						clazz.getMethod("addObserver", new Class[] {ChainedEventObserver.class});
					initMethod.invoke(componentInstance, new Object[] {this.componentRepository});
				}
				catch (Exception e)
				{
					throw new ConfigException(e);
				}
			}
		}
	}

	@Override
	public void modulesLoaded(ComponentsLoadedEvent evt)
	{
	}
}
