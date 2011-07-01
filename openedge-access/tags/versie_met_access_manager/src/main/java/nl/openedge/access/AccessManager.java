package nl.openedge.access;

/**
 * @author vries
 *
 * this manager manages the resources, it can check if a user 
 * or group has permissions on a resource and change permissions
 * on resources for roles and users 
 */
public interface AccessManager extends Configurable {
	
	
	/**
	 * checks permission on resource using credentials
	 * @param credentials
	 * @param resource
	 * @param permission
	 * @return boolean true if permission is granted, false otherwise
	 * @exception AccessException
	 */
	public boolean hasPermission(
		Credentials credentials,
		Resource resource,
		AccessPermission permission) throws AccessException;	

	/**
	 * sets <code>AccessProvider</code>
	 * @param accessProvider
	 */
	public void setAccessProvider(AccessProvider accessProvider);

	/**
	 * gets <code>AccessProvider</code>
	 * @return AccessProvider
	 */
	public AccessProvider getAccessProvider();
}
