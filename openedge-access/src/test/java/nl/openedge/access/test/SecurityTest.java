/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package nl.openedge.access.test;

import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;

import nl.openedge.access.NamedPermission;
import nl.openedge.access.PassiveCallbackHandler;


/**
 * Security related tests
 * 
 * @author E.F. Hillenius
 */
public class SecurityTest extends AbstractTestBase {

	/** username to use with tests */
	protected static String username = "Eelco";
	
	/** password to use with tests */
	protected static String password = "illy";
	
	/** login module name to use with tests */
	protected static String moduleAlias = "TestLoginModule";

	/** if first test succeded, save subject for later use */
	protected static Subject subject = null;

	/**
	 * construct with name
	 * @param name
	 */
	public SecurityTest(String name) throws Exception {
		super(name);
	}

	/** test a user login */
	public void testLogin() {
		
		try {
			// create the callback handler
			PassiveCallbackHandler cbh = 
				new PassiveCallbackHandler(username, password);
			// create the login context
			LoginContext lc = new LoginContext(moduleAlias, cbh);	
			lc.login();
			// if we get here, login succeded
			subject = lc.getSubject();
			assertNotNull(subject);
			
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/** test permission that is available for everyone */
	public void testNamedPermission4All() {
		
		// test named permission that everybody SHOULD have permission for
		String uri = "/forall";
		UriAction action = new UriAction(uri);
		try {
			Subject.doAsPrivileged(null, action, null);
			// if we get here, the user was authorised
		} catch (SecurityException se) {
			// Subject does not have permission
			se.printStackTrace();
			fail(se.getMessage());
		}
	}
	
	/** test permission */
	public void testNamedPermission4Group() {
		
		// test named permission that subject with group SHOULD have permission for
		String uri = "/forgroup";
		UriAction action = new UriAction(uri);
		try {
			// Subject.doAs(subject, action) does NOT work... dunno why?
			// nevertheless, this does. 
			Subject.doAsPrivileged(subject, action, null);
			// if we get here, the user was authorised
		} catch (SecurityException se) {
			// Subject does not have permission
			se.printStackTrace();
			fail(se.getMessage());
		}
	}
	
	/** test permission */
	public void testNamedPermission4SameUser() {
		
		// test named permission that subject with user SHOULD have permission for
		String uri = "/foreelco";
		UriAction action = new UriAction(uri);
		try {
			Subject.doAsPrivileged(subject, action, null);
			// if we get here, the user was authorised
		} catch (SecurityException se) {
			// Subject does not have permission
			se.printStackTrace();
			fail(se.getMessage());
		}
	}
	
	/** test permission */
	public void testNamedPermission4OtherUser() {
		
		// test named permission that subject with user SHOULD NOT have permission for
		String uri = "/forsander";
		UriAction action = new UriAction(uri);
		try {
			Subject.doAsPrivileged(subject, action, null);
			// if we get here, the user was authorised which is NOT right
			fail("user " + subject + " got wrong permission!");
		} catch (SecurityException se) {
			// Subject does not have permission
			// which is perfectely right!
		}
	}
	
	/** test permission */
	public void testNamedPermission4Noone() {
		
		// test named permission that no-one have permission for
		// (as it is not in the policy file)
		String uri = "/notInConifig";
		UriAction action = new UriAction(uri);
		try {
			Subject.doAsPrivileged(subject, action, null);
			// if we get here, the user was authorised which is NOT right
			fail("user " + subject + " got wrong permission!");
		} catch (SecurityException se) {
			// Subject does not have permission
			// which is perfectely right!
		}
	}
	
	
//---------------------------- Utility ---------------------------------------
	/** action for checking permissions for a specific subject */
	class UriAction implements PrivilegedAction {
		
		// uri to check on
		private String uri;
		
		/** construct with uri */
		public UriAction(String uri) {
			this.uri = uri;
		}
		
		/** run check */
		public Object run() {
			Permission p = new NamedPermission(uri);
			AccessController.checkPermission(p);
			return null;
		}
	}

}
