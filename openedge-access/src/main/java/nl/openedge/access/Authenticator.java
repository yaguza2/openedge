/*
 * Created on 21-mrt-2003
 *
 */
package nl.openedge.access;

import java.util.Map;

/**
 * @author vries, hillenius
 *
 * authenticates a user and returns his Credentials
 */
public interface Authenticator {

	/**
	 * default key for a user name (value == "_user")
	 */
	public final static String USER_NAME_KEY = "_user";
	
	/**
	 * default key for a user name (value == "_password")
	 */
	public final static String USER_PASSWORD_KEY = "_password"; 

	/**
	 * authenticate a user with given evidence. This could be a simple user name
	 * and password pair or other implementation specific data
	 * @param evidence
	 * @return Credentials
	 */
	public Credentials authenticate(Map evidence) throws AccessException;
}
