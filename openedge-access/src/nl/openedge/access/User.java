/*
 * Created on 21-mrt-2003
 *
 */
package nl.openedge.access;

import java.util.Map;

/**
 * @author vries
 *
 * a user
 */
public interface User extends Entity {
	
	/**
	 * returns id
	 * @return Integer
	 */
	public Integer getId();
	
	/**
	 * sets id
	 * @param id
	 */
	public void setId(Integer id);
	
	/**
	 * sets name
	 * @param name
	 */	
	public void setName(String name);
	
	/**
	 * returns name
	 * @return String
	 */
	public String getName();
	
	/**
	 * get generic user attibutes
	 * @return Map
	 */
	public Map getAttributes();

	/**
	 * Sets generic attributes.
	 * @param attributes The attributes to set
	 */
	public void setAttributes(Map info);

}
