/*
 * $Header$
 * $Revision$
 * $Date$
 */
package nl.openedge.modules;

/**
 * @author Eelco Hillenius
 */
class ThrowAwayAdapter extends ModuleAdapter {
	
	/**
	 * get instance of module
	 * @return new instance for each request
	 * @see nl.openedge.modules.ModuleAdapter#getModule()
	 */
	public Object getModule() throws ModuleException {	
		
		Object instance = null;
		try {	
			instance = moduleClass.newInstance();
		} catch (InstantiationException ex) {		
			throw new ModuleException(ex);
		} catch (IllegalAccessException ex) {	
			throw new ModuleException(ex);
		}
		// do we have to configure?
		if(instance instanceof Configurable) {
			try {
				((Configurable)instance).init(configNode);		
			} catch(ConfigException e) {
				throw new ModuleException(e);
			}
		}
		return instance;
	}
}
