package nl.openedge.access;

/**
 * @author vries, hillenius
 *
 * Credentials
 * this acts like the ticket to the rest of the API. At least, it should hold
 * a reference to a user object.
 */
public interface Credentials {
	
	/**
	 * credentials should have a pointer to a user object
	 * @return UserPrincipal
	 */
	public UserPrincipal getUser();
		
}
