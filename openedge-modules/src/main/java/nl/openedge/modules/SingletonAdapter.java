/*
 * $Header$
 * $Revision$
 * $Date$
 */
package nl.openedge.modules;

import org.apache.commons.beanutils.BeanUtils;

/**
 * @author Eelco Hillenius
 */
class SingletonAdapter extends ModuleAdapter {

	protected Object singletonInstance;

	/**
	 * construct with class and create and store singleton instance
	 * @param moduleClass	class of module
	 * @see nl.openedge.modules.ModuleAdapter#setModuleClass(java.lang.Class) 
	 */
	protected void setModuleClass(Class moduleClass) throws ConfigException {
		// set instance
		try {	
			this.singletonInstance = moduleClass.newInstance();
		} catch (InstantiationException ex) {		
			throw new ConfigException(ex);
		} catch (IllegalAccessException ex) {	
			throw new ConfigException(ex);
		}
		this.moduleClass = moduleClass;
		// is this a bean?
		if(singletonInstance instanceof BeanModule) {
			// try to set its properties
			try {
				BeanUtils.populate(singletonInstance, this.properties);
			} catch(Exception e) {
				throw new ConfigException(e);	
			}
		}
		// do we have to configure?
		if(singletonInstance instanceof Configurable) {
			((Configurable)singletonInstance).init(this.configNode);		
		}
	}

	/**
	 * get instance of module
	 * @return new instance for each request
	 * @see nl.openedge.modules.ModuleAdapter#getModule()
	 */
	public Object getModule() throws ModuleException {
		return singletonInstance;
	}

}
