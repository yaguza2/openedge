package nl.openedge.modules.types.initcommands;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import nl.openedge.modules.ComponentLookupException;
import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.observers.ComponentObserver;
import nl.openedge.modules.observers.ComponentsLoadedEvent;
import ognl.Ognl;
import ognl.OgnlException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tries to solve the dependencies after all components have been loaded. <br>
 * Users can set the static property 'failOnCycle' to true if they want the resolving of
 * dependencies stopped (and thus stop the loading of the component repository) when a
 * circular dependency is detected. Be sure to set this property BEFORE initializing the
 * component repository.
 * 
 * If 'failOnCycle' is false (the default), the resolving of dependencies will not stop
 * when a circular dependency is detected. In this case, instead of getting the instance
 * of the component for which the circular dependency was detected from the repository, a
 * cached instance will be used for setting the dependency of the current dependent
 * component.
 * 
 * WARNING: as dependencies are potentially loaded from a temporary cache instead of via
 * the component repository when 'failOnCycle' is false, it is not safe to work with
 * components with state (ThrowAwayTypes). If there a cycle was detected, the same
 * instance of a component is shared by more than one module, even if the normal behavior
 * of the type factory would be to create a new instance (like with ThrowAwayTypes). In
 * most cases, this probably would not be a problem, but if it is, consider setting the
 * 'failOnCycle' property to true or just using the component repository directly in the
 * components that have dependencies instead tagging it as a dependent type.
 * 
 * @author Eelco Hillenius
 */
public final class DependentTypeWrapper
{
	/**
	 * observe components loaded event.
	 */
	private class RegisterOnce implements ComponentObserver
	{
		@Override
		public void modulesLoaded(ComponentsLoadedEvent evt)
		{
			setDependencies(componentInstance);
		}
	}

	private static Logger log = LoggerFactory.getLogger(DependentTypeWrapper.class);

	private static boolean failOnCycle = false;

	private static ThreadLocal<Set<String>> referenceHolder = new ThreadLocal<Set<String>>();

	private static ThreadLocal<Map<String, Object>> resolvedComponentsHolder =
		new ThreadLocal<Map<String, Object>>();

	private static boolean wasAdded = false;

	private Object componentInstance;

	private List<NamedDependency> namedDependencies = null;

	private ComponentRepository moduleFactory = null;

	private String componentName = null;

	public void execute(Object cInstance)
	{
		setDependencies(cInstance);
	}

	public void setDependencies(Object cInstance)
	{
		if (namedDependencies != null && (namedDependencies.size() > 0))
		{
			Set<String> references = referenceHolder.get();
			if (references == null)
			{
				references = new TreeSet<String>();
				references.add(componentName);
				referenceHolder.set(references);
			}

			Map<String, Object> resolved = resolvedComponentsHolder.get();
			if (resolved == null)
			{
				resolved = new HashMap<String, Object>();
				resolved.put(componentName, cInstance);
				resolvedComponentsHolder.set(resolved);
			}

			for (Iterator<NamedDependency> i = namedDependencies.iterator(); i.hasNext();)
			{
				NamedDependency dep = i.next();
				Object dependency = null;
				if (references.contains(dep.getModuleName()))
				{
					// got a cycle!
					if (failOnCycle)
					{
						String name = dep.getModuleName();
						String message =
							"\ncomponent with name " + this.componentName
								+ " has a cyclic dependency:" + " component with name " + name
								+ " was allready referenced. \nHere's a list of"
								+ " references where the cycle was detected:\n"
								+ references.toString() + " -> " + name + "\n";
						throw new CyclicDependencyException(message);
					}
					else
					{
						dependency = resolved.get(dep.getModuleName());
					}
				}
				else
				{
					references.add(dep.getModuleName());
					// get module from repo
					dependency = moduleFactory.getComponent(dep.getModuleName());
				}

				resolved.put(dep.getModuleName(), dependency);

				try
				{
					// set module as a property
					Ognl.setValue(dep.getPropertyName(), cInstance, dependency);
				}
				catch (OgnlException e)
				{
					log.error(e.getMessage(), e);
					throw new ComponentLookupException(e);
				}
			}

			referenceHolder.set(null);
			resolvedComponentsHolder.set(null);
		}
	}

	public Object getComponentInstance()
	{
		return componentInstance;
	}

	public void setComponentInstance(Object instance)
	{
		componentInstance = instance;
	}

	public List<NamedDependency> getNamedDependencies()
	{
		return namedDependencies;
	}

	public void setNamedDependencies(List<NamedDependency> namedDependencies)
	{
		this.namedDependencies = namedDependencies;
	}

	public static boolean isWasAdded()
	{
		return wasAdded;
	}

	public String getComponentName()
	{
		return componentName;
	}

	public void setComponentName(String componentName)
	{
		this.componentName = componentName;
	}

	public ComponentRepository getModuleFactory()
	{
		return moduleFactory;
	}

	public void setModuleFactory(ComponentRepository factory)
	{
		moduleFactory = factory;
		// register for components loaded event
		if (!wasAdded)
		{
			wasAdded = true;
			moduleFactory.addObserver(new RegisterOnce());
		}
	}

	public static boolean isFailOnCycle()
	{
		return failOnCycle;
	}

	public static void setFailOnCycle(boolean b)
	{
		failOnCycle = b;
	}
}
