/*
 * Created on 21-mrt-2003
 *
 */
package nl.openedge.access;

import java.security.Principal;


/**
 * @author vries, hillenius
 *
 * a group 
 */
public interface Group extends Principal {
	
	/**
	 * sets name
	 * @param name
	 */
	public void setName(String name);
			
}
