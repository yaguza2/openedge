package nl.openedge.access;

/**
 * @author vries
 *
 * a resource, identified by a unique key
 * holds current permissions
 */
public interface Resource {
	
	/**
	 * sets key
	 * @param key
	 */
	public void setResourceKey(String key);

	/**
	 * gets key
	 * @return String
	 */
	public String getResourceKey();

}
