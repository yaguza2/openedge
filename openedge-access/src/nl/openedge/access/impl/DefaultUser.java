package nl.openedge.access.impl;

import java.util.List;
import java.util.Map;

import nl.openedge.access.User;

/**
 * @author E.F. Hillenius
 * $Id$
 */
public class DefaultUser implements User {

	protected String name;

	/** attributes for this user */
	protected Map attributes;
	
	/** groups that user is member of */
	protected List groups;

	/**
	 * construct 
	 */
	public DefaultUser() {
		// nothing here
	}

	/**
	 * @see nl.openedge.access.User#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @see nl.openedge.access.User#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see nl.openedge.access.User#getAttributes()
	 */
	public Map getAttributes() {
		return attributes;
	}

	/**
	 * @see nl.openedge.access.User#setAttributes(java.util.Map)
	 */
	public void setAttributes(Map attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return List
	 */
	public List getGroups() {
		return groups;
	}

	/**
	 * Sets the groups.
	 * @param groups The groups to set
	 */
	public void setGroups(List groups) {
		this.groups = groups;
	}

}
