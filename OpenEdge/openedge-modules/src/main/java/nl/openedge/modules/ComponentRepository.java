package nl.openedge.modules;

import java.io.Serializable;
import java.util.List;

import javax.servlet.ServletContext;

import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.observers.ChainedEventObserver;
import nl.openedge.modules.observers.ComponentRepositoryObserver;

import org.jdom.Element;
import org.quartz.Scheduler;

/**
 * The ComponentRepository functions as the repository for components. This is the main
 * class that clients have to deal with. The common pattern is to get an instance of the
 * ComponentRepository, and then get the component by name/ alias. E.g: &lt;code&gt;
 * ComponentRepository rep = RepositoryFactory.getRepository(); rModule =
 * (MyComponent)rep.getComponent("myAlias"); &lt;/code&gt; The repository also is the the
 * point to get the Quartz scheduler from, and where members of the
 * <code>ComponentRepositoryObserver</code> family can register as observers.
 * 
 * @author Eelco Hillenius
 */
public interface ComponentRepository extends ChainedEventObserver, Serializable
{

	/**
	 * initialize the component repository.
	 * 
	 * @param rootNode
	 *            root node of the config
	 * @param servletContext
	 *            servlet context
	 * @throws ConfigException
	 *             when an configuration error occurs
	 */
	void start(Element rootNode, ServletContext servletContext) throws ConfigException;

	/**
	 * add observer of component repository events.
	 * 
	 * @param observer
	 *            observer to add
	 */
	void addObserver(ComponentRepositoryObserver observer);

	/**
	 * remove observer of component repository events.
	 * 
	 * @param observer
	 *            observer to remove
	 */
	void removeObserver(ComponentRepositoryObserver observer);

	/**
	 * returns instance of component can throw ComponentLookupException (runtime
	 * exception) if a loading or initialisation error occured or when no component was
	 * found stored under the given name.
	 * 
	 * @param name
	 *            the name (alias) of component
	 * @return Object component instance
	 */
	Object getComponent(String name);

	/**
	 * get all components that are instance of the given type.
	 * 
	 * @param type
	 *            the class
	 * @param exact
	 *            If true, only exact matches will be returned. If false, superclasses and
	 *            interfaces will be taken into account
	 * @return List list of components. Never null, possibly empty
	 */
	List<Object> getComponentsByType(Class< ? > type, boolean exact);

	/**
	 * returns all known names.
	 * 
	 * @return String[] names
	 */
	String[] getComponentNames();

	/**
	 * get the quartz scheduler.
	 * 
	 * @return Scheduler
	 */
	Scheduler getScheduler();

	/**
	 * get the servlet context this component repository was possibly started with.
	 * 
	 * @return ServletContext the servlet context this repository was started with, or
	 *         null if this repository was not started within a servlet environment (or
	 *         does not know about it)
	 */
	ServletContext getServletContext();
}
