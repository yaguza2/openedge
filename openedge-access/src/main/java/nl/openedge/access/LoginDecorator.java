package nl.openedge.access;

import java.security.Principal;

/**
 * 
 * Logon decorators can add principals to a subject.
 * Classes implementing this interface should be configured like:<code><pre>
 *	<login-decorators>
 *		<decorator class="org.stuff.MyDecorator"/>
 *		<decorator class="com.company.AnotherDecorator"/>
 *	</login-decorators>
 * </pre></code>
 * within the <code><user-manager></user-manager></code> tags.
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

	public Principal[] getPrincipals();

}
