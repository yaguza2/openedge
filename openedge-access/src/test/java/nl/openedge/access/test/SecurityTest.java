/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003, Open Edge B.V.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions 
 * in binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution. Neither the name of OpenEdge B.V. 
 * nor the names of its contributors may be used to endorse or promote products 
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package nl.openedge.access.test;

import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;

import nl.openedge.access.NamedPermission;
import nl.openedge.access.PassiveCallbackHandler;
import nl.openedge.access.RolePrincipal;
import nl.openedge.access.UserManager;
import nl.openedge.access.UserPrincipal;


/**
 * Security related tests
 * 
 * @author E.F. Hillenius
 */
public class SecurityTest extends AbstractTestBase {

	/** username to use with tests */
	protected static String username = "eelco";
	
	/** password to use with tests */
	protected static String password = "hillenius";
	
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
	
	/**
	 * prepare db
	 */
	protected void setUp() throws Exception {
	
		UserManager userManager = accessFactory.getUserManager();
		UserPrincipal user = userManager.createUser(username, password);
		RolePrincipal role = userManager.createRole("tableadmin");
		userManager.addUserToRole(user, role);
	}
	
	/**
	 * clean db
	 */
	protected void tearDown() throws Exception {
	
		UserManager userManager = accessFactory.getUserManager();
		UserPrincipal user = null;
		user = userManager.getUser(username);
		userManager.deleteUser(user);
		RolePrincipal role = userManager.getRole("tableadmin");
		userManager.deleteRole(role);
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
