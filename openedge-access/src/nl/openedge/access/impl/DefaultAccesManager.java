/*
 * Created on 4-apr-2003
 */
package nl.openedge.access.impl;

import org.jdom.Element;

import nl.openedge.access.AccessException;
import nl.openedge.access.AccessManager;
import nl.openedge.access.AccessProvider;
import nl.openedge.access.ConfigException;
import nl.openedge.access.Credentials;
import nl.openedge.access.AccessPermission;
import nl.openedge.access.Resource;

/**
 * @author Hillenius
 * $Id$
 */
public class DefaultAccesManager implements AccessManager {

	/* concrete provider */
	protected AccessProvider accessProvider;

	/**
	 * @see nl.openedge.access.Configurable#init(org.jdom.Element)
	 */
	public void init(Element configNode) throws ConfigException {
		
	}

	/**
	 * @see nl.openedge.access.AccessManager#hasPermission(nl.openedge.access.Credentials, nl.openedge.access.Resource, nl.openedge.access.AccessPermission)
	 */
	public boolean hasPermission(
			Credentials credentials,
			Resource resource,
			AccessPermission permission)
			throws AccessException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @return AccessProvider
	 */
	public AccessProvider getAccessProvider() {
		return accessProvider;
	}

	/**
	 * Sets the accessProvider.
	 * @param accessProvider The accessProvider to set
	 */
	public void setAccessProvider(AccessProvider accessProvider) {
		this.accessProvider = accessProvider;
	}

}
