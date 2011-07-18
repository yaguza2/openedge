package nl.openedge.modules.types.initcommands;

import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.observers.SchedulerObserver;

import org.jdom.Element;

/**
 * Command that populates instances using Ognl.
 * 
 * @author Eelco Hillenius
 */
public class SchedulerObserverInitCommand implements InitCommand
{
	private ComponentRepository componentRepository = null;

	@Override
	public void init(String componentName, Element componentNode, ComponentRepository cRepo)
	{
		this.componentRepository = cRepo;
	}

	@Override
	public void execute(Object componentInstance)
	{
		if (componentInstance instanceof SchedulerObserver)
			componentRepository.addObserver((SchedulerObserver) componentInstance);
		else
		{
			ChainedEventObserverDecorator deco = new ChainedEventObserverDecorator();
			deco.setDecorated(componentInstance);
			componentRepository.addObserver(deco);
		}
	}
}
