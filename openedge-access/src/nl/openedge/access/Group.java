/*
 * Created on 21-mrt-2003
 *
 */
package nl.openedge.access;


/**
 * @author vries
 *
 * a group 
 */
public interface Group extends Entity {
	
	/**
	 * returns name
	 * @return String
	 */
	public String getName();
	
	/**
	 * sets name
	 * @param name
	 */
	public void setName(String name);
			
}
