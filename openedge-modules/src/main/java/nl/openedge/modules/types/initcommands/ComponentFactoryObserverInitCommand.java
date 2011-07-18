package nl.openedge.modules.types.initcommands;

import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.observers.ComponentRepositoryObserver;

import org.jdom.Element;

/**
 * Command that populates instances using BeanUtils.
 * 
 * @author Eelco Hillenius
 */
public final class ComponentFactoryObserverInitCommand implements InitCommand
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
		if (componentInstance instanceof ComponentRepositoryObserver)
			componentRepository.addObserver((ComponentRepositoryObserver) componentInstance);
		else
		{
			ComponentFactoryObserverDecorator deco = new ComponentFactoryObserverDecorator();
			deco.setDecorated(componentInstance);
			componentRepository.addObserver(deco);
		}
	}
}
