/*
 * Created on 21-mrt-2003
 *
 */
package nl.openedge.access;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * @author vries, hillenius
 *
 * a user
 */
public interface User extends Principal {
	
	/**
	 * get generic user attibutes
	 * @return Map
	 */
	public Map getAttributes();

	/**
	 * Sets generic attributes.
	 * @param attributes The attributes to set
	 */
	public void setAttributes(Map attributes);
	
	/**
	 * get groups for this user
	 * @return
	 */
	public List getGroups();

}
