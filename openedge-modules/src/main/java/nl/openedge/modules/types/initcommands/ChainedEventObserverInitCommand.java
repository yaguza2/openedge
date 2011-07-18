package nl.openedge.modules.types.initcommands;

import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.observers.ChainedEventObserver;

import org.jdom.Element;

/**
 * Command that populates instances using BeanUtils.
 * 
 * @author Eelco Hillenius
 */
public final class ChainedEventObserverInitCommand implements InitCommand
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
		if (componentInstance instanceof ChainedEventObserver)
			componentRepository.addObserver((ChainedEventObserver) componentInstance);
		else
		{
			ChainedEventObserverDecorator deco = new ChainedEventObserverDecorator();
			deco.setDecorated(componentInstance);
			componentRepository.addObserver(deco);
		}
	}
}
