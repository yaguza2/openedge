package nl.openedge.access;

import java.io.Serializable;
import java.security.Principal;

/**
 * Logon decorators can decorate principals. Classes implementing this interface should be
 * configured in the JAAS configuration file.
 * <p>
 * The configured decorators are evaluated in the commit() method of the LoginModule for
 * all &lt;code&gt;Principal&lt;/code&gt;s. The returned principals that can be an
 * 'enriched' instance of the given principal are stored with the subject. DO NOT change
 * the name of the principals! This makes it other principals effectively. The only reason
 * that default constructors and &lt;code&gt;setName(String)&lt;/code&gt; are supported in
 * principals is to allow them to be handled by persistence engines like OJB.
 * <p>
 * The resulting Principals will be stored with the subject.
 * <p>
 * Note that, although the implementations of OpenEdge Access do support
 * {@code LoginDecorator}s, the Implementors of the LogonModule may not support it.
 * 
 * @author Eelco Hillenius
 */
public interface LoginDecorator extends Serializable
{
	/**
	 * get extra principals for this subject
	 * 
	 * @param principal
	 *            immutable set of principals
	 * @return the decorated principals to be saved with the subject.
	 */
	public Principal[] decorate(final Principal[] principal);
}
