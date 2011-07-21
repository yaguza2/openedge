package nl.openedge.modules.types.initcommands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.openedge.modules.ComponentRepository;

import org.jdom.Element;

/**
 * Command that populates instances using BeanUtils.
 * 
 * @author Eelco Hillenius
 */
public final class DependentTypeInitCommand implements RequestLevelInitCommand
{
	private ComponentRepository componentRepository = null;

	private List<NamedDependency> namedDependencies = null;

	private String componentName;

	@Override
	public void init(String cName, Element cNode, ComponentRepository cRepo)
	{

		this.componentRepository = cRepo;
		this.componentName = cName;
		loadDependencies(cNode);
	}

	protected void loadDependencies(Element componentNode)
	{
		List< ? > namedDeps = componentNode.getChildren("dependency");
		namedDependencies = new ArrayList<NamedDependency>(namedDeps.size());

		for (Iterator< ? > i = namedDeps.iterator(); i.hasNext();)
		{
			Element node = (Element) i.next();
			String moduleName = node.getAttributeValue("componentName");
			String propertyName = node.getAttributeValue("propertyName");

			namedDependencies.add(new NamedDependency(moduleName, propertyName));
		}
	}

	@Override
	public void execute(Object componentInstance)
	{
		DependentTypeWrapper solver = new DependentTypeWrapper();
		solver.setComponentName(this.componentName);
		solver.setComponentInstance(componentInstance);
		solver.setNamedDependencies(this.namedDependencies);
		solver.setModuleFactory(this.componentRepository);
		solver.execute(componentInstance);
	}
}
