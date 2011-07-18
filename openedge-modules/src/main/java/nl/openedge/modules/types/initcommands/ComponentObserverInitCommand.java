package nl.openedge.modules.types.initcommands;

import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.observers.ComponentObserver;

import org.jdom.Element;

/**
 * Command that populates instances using Ognl.
 * 
 * @author Eelco Hillenius
 */
public final class ComponentObserverInitCommand implements InitCommand
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
		if (componentInstance instanceof ComponentObserver)
			componentRepository.addObserver((ComponentObserver) componentInstance);
		else
		{
			ComponentObserverDecorator deco = new ComponentObserverDecorator();
			deco.setDecorated(componentInstance);
			componentRepository.addObserver(deco);
		}
	}
}
