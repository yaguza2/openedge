package nl.openedge.access;

import java.security.Principal;

import javax.security.auth.Subject;

/**
 * 
 * Logon decorators can add principals to a subject.
 * Classes implementing this interface should be configured in
 * the JAAS configuration file.
 * 
 * The configured decorators are evaluated in the commit() method of the 
 * LoginModule and the resulting Principals will be stored with the subject
 * 
 * Note that, allthough the implementations of OpenEdge Access does support
 * this behaviour, the Implementors of the LogonModule could decide not to 
 * implement it.
 * 
 * @author Hillenius
 * $Id$
 */
public interface LoginDecorator {

	/**
	 * get extra principals for this subject
	 * @param subject immutable subject
	 * @return
	 */
	public Principal[] getPrincipals(final Subject subject);

}
