/*
 * $Header$
 * $Revision$
 * $Date$
 */
package nl.openedge.modules;

import org.jdom.Element;

/**
 * @author Eelco Hillenius
 */
abstract class ModuleAdapter {

	/** class of module */
	protected Class moduleClass = null;
	
	/** name (alias) of module */
	protected String name = null;
	
	/** 
	 * if the module wants to have the possiblity to configure from the
	 * configuration file, this is it's node
	 */
	protected Element configNode = null;

	/**
	 * construct with class
	 * @param moduleClass	class of module
	 */
	protected void setModuleClass(Class moduleClass) throws ConfigException {
		// test first
		Object instance = null;
		try {	
			instance = moduleClass.newInstance();
		} catch (InstantiationException ex) {		
			throw new ConfigException(ex);
		} catch (IllegalAccessException ex) {	
			throw new ConfigException(ex);
		}
		// class is ok so far
		// test configuration as well
		if(instance instanceof Configurable) {
			((Configurable)instance).init(configNode);		
		}
		// all's ok
		this.moduleClass = moduleClass;
	}
	
	/**
	 * sets the name from config
	 * @param name	alias for this instance
	 */
	protected void setName(String name) {
		this.name = name;
	}
	
	/**
	 * gets the name from config
	 * @return String
	 */
	public final String getName() {
		return name;
	}
	
	/**
	 * set configuration node of this module instance
	 * @param configNode XML (JDOM) node
	 */
	public final void setConfigNode(Element configNode) {
		this.configNode = configNode;
	}

	/** get instantiated module */
	public abstract Object getModule() throws ModuleException;

	/**
	 * @return Class of module
	 */
	public Class getModuleClass() {
		return moduleClass;
	}

}
