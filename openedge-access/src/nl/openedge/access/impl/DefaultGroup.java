/*
 * Created on 5-apr-2003
 */
package nl.openedge.access.impl;

import nl.openedge.access.Group;

/**
 * @author E.F. Hillenius
 * $Id$
 */
public class DefaultGroup implements Group {

	/** name of group */
	protected String name;

	/**
	 * construct
	 */
	public DefaultGroup() {
		// nothing here
	}

	/**
	 * @see nl.openedge.access.Group#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @see java.security.Principal#getName()
	 */
	public String getName() {
		return name;
	}

}
