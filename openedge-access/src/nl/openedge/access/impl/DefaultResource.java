package nl.openedge.access.impl;

import nl.openedge.access.Resource;

/**
 * @author vries
 * standard implementation of a Resource
 */
public class DefaultResource implements Resource {
	
	private String resourceKey;

	/**
	 * constructor, with key
	 * @param key
	 */
	public DefaultResource(String key) {
		resourceKey = key;
	}


	/**	 
	 * @see nl.openedge.oecms.security.Resource#getResourceKey()
	 */
	public String getResourceKey() {
		return resourceKey;
	}

	/**	 
	 * @see nl.openedge.oecms.security.Resource#setResourceKey(java.lang.String)
	 */
	public void setResourceKey(String resourceKey) {
		this.resourceKey = resourceKey;
	}

}
