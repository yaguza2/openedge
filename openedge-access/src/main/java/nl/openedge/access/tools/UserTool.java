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
package nl.openedge.access.tools;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import nl.openedge.access.AccessException;
import nl.openedge.access.AccessFactory;
import nl.openedge.access.RolePrincipal;
import nl.openedge.access.UserManager;
import nl.openedge.access.UserPrincipal;

import org.apache.commons.cli.*;

/** 
 * Tool for user maintenance from command line.
 * 
 * This tool uses 'oeaccess.xml' in this package as the default configuration 
 * document. You can overide this location by setting the environment variable 
 * 'oeaccess.configFile' to the correct url.
 * 
 * @author Eelco Hillenius
 */
public final class UserTool {

	/** the options */
	protected static Options options = new Options();
	
	/** reference to the access factory */
	protected static AccessFactory accessFactory;
	
	protected static UserManager userManager;

	/** construct and add options */
	public UserTool() {
		
		options.addOption("adduser", false, "adds a user");
		options.addOption("deluser", false, "deletes a user");
		options.addOption("resetpwd", false, "changes the password of a user");
		options.addOption("help", false, "prints this message");
		options.addOption("listroles", false, "lists roles (for a user if given)");
		options.addOption("listusers", false, "lists users (for a role if given)");
		options.addOption("addrole", false, "adds a role (for a user if given)");
		options.addOption("delrole", false, "deletes a role completely");
		options.addOption("removerole", false, "removes a role for a user");

		options.addOption("u", "user", true, "the username of the user");
		options.addOption("p", "pwd", true, "the password of the user");
		options.addOption("r", "role", true, "the name of the role");
	}
	
	/** interpret the command line */
	protected void interpret(String[] args) {
		
		CommandLineParser parser = new GnuParser();
		CommandLine line = null;	
		try {
			line = parser.parse( options, args );
		} catch(ParseException e) {
			System.err.println( "Parsing failed.  Reason: " + e.getMessage() );
			// print help?
			return;
		}
		
		if(line.hasOption("help")) {
			printHelp();
			return;
		}
		
		if(System.getProperty("oeaccess.configFile") != null) {
			String config = System.getProperty("oeaccess.configFile");
			System.out.println("loading " + config + " for configuration");
			try {
				loadAccessFactory(config);
			} catch(Exception e) {				
				System.err.println(e.getMessage());
				return;	
			}
		} else {
			System.out.println("env var 'oeaccess.configFile' not set... loading " +
				"oeaccess.xml from this package for configuration");
			// try to load from class path, current package:
			String config = "oeaccess.xml";
			try {
				URL location = getClass().getResource(config);
				loadAccessFactory(location);
			} catch(Exception e) {				
				System.err.println(e.getMessage());
				return;	
			}
		}
		
		// do command
		if(line.hasOption("adduser")) addUser(line);
		else if(line.hasOption("deluser")) delUser(line);
		else if(line.hasOption("resetpwd")) setPwd(line);
		else if(line.hasOption("listroles")) listRoles(line);
		else if(line.hasOption("listusers")) listUsers(line);
		else if(line.hasOption("addrole")) addRole(line);
		else if(line.hasOption("delrole")) delRole(line);
		else if(line.hasOption("removerole")) removeRole(line);
		else printHelp();
	}
	
	/** prints usage */
	protected void printHelp() {
		
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "usertool", options );
	}
	
	/** adds a user */
	protected void addUser(CommandLine line) {
		
		String username = line.getOptionValue('u');
		String password = line.getOptionValue('p');
		
		if(username == null || password == null) {
			System.err.println("please provide user (-u) and password (-p)");
			return;
		}
		System.out.println("create user '" + username + "' with password '" + 
			password + "'...");
		UserPrincipal user = null;
		try {
			user = userManager.createUser(username, password);
			System.out.println("user created (" + user + ")");
		} catch(AccessException ae) {
			System.err.println("Unable to create user\nReason: " + 
				ae.getMessage());
		}	
	}
	
	/** deletes a user */
	protected void delUser(CommandLine line) {
	
		String username = line.getOptionValue('u');
		if(username == null) {
			System.err.println("please provide user (-u)");
			return;
		}		
		System.out.println("deleting user '" + username + "'...");
		try {
			UserPrincipal user = userManager.getUser(username);
			if(user == null) throw new AccessException("user " + username +
					" was not found");
			userManager.deleteUser(user);
			System.out.println("user deleted");
		} catch(AccessException ae) {
			System.err.println("Unable to delete user\nReason: " + 
				ae.getMessage());
		}		
	}
	
	/** resets a password for a user */
	protected void setPwd(CommandLine line) {
		
		String username = line.getOptionValue('u');
		String password = line.getOptionValue('p');
		if(username == null || password == null) {
			System.err.println("please provide user (-u) and password (-p)");
			return;
		}
		System.out.println("reset password to '" + password + "' for user '" + 
			username + "'...");
		try {
			UserPrincipal user = userManager.getUser(username);
			if(user == null) throw new AccessException("user " + username +
					" was not found");
			userManager.resetPassword(user, password);
			System.out.println("password reset");
		} catch(AccessException ae) {
			System.err.println("Unable to reset password\nReason: " + 
				ae.getMessage());
		}		
	}
	
	/** lists users. If option r (role) is available list users for that role */
	protected void listUsers(CommandLine line) {
		
		String rolename = line.getOptionValue('r');
		if(rolename == null) {
			
			System.out.println("listing users...");
			try {
				List users = userManager.listUsers();
				if(users != null && (!users.isEmpty())) {
					for(Iterator i = users.listIterator(); i.hasNext(); ) {
						UserPrincipal u = (UserPrincipal)i.next();
						System.out.println("\t- " + u);
					}
				} else {
					System.out.println("\t[none]");
				}
			} catch(AccessException ae) {
				System.err.println("Unable to list users\nReason: " + 
					ae.getMessage());				
			}
		} else {
			
			System.out.println("listing users for role " + rolename);
			try {
				RolePrincipal role = userManager.getRole(rolename);
				if(role == null) {
					System.err.println("role " + rolename + " does not exist");
					return;
				}
				List users = userManager.listUsersInRole(role);
				if(users != null && (!users.isEmpty())) {
					for(Iterator i = users.listIterator(); i.hasNext(); ) {
						UserPrincipal u = (UserPrincipal)i.next();
						System.out.println("\t- " + u);
					}
				} else {
					System.out.println("\t[none]");
				}
			} catch(AccessException ae) {
				System.err.println("Unable to list users\nReason: " + 
					ae.getMessage());				
			}
		}
	}
	
	/** lists roles. If option u (user) is available list roles for that user */
	protected void listRoles(CommandLine line) {
		
		String username = line.getOptionValue('u');
		if(username == null) {
			
			System.out.println("listing roles...");
			try {
				List roles = userManager.listRoles();
				if(roles != null && (!roles.isEmpty())) {
					for(Iterator i = roles.listIterator(); i.hasNext(); ) {
						RolePrincipal r = (RolePrincipal)i.next();
						System.out.println("\t- " + r);
					}
				} else {
					System.out.println("\t[none]");
				}
			} catch(AccessException ae) {
				System.err.println("Unable to list roles\nReason: " + 
					ae.getMessage());				
			}
		} else {
			
			System.out.println("listing roles for user " + username);
			try {
				UserPrincipal user = userManager.getUser(username);
				if(user == null) {
					System.err.println("user " + username + " does not exist");
					return;
				}
				List roles = userManager.listRolesForUser(user);
				if(roles != null && (!roles.isEmpty())) {
					for(Iterator i = roles.listIterator(); i.hasNext(); ) {
						RolePrincipal r = (RolePrincipal)i.next();
						System.out.println("\t- " + r);
					}
				} else {
					System.out.println("\t[none]");
				}
			} catch(AccessException ae) {
				System.err.println("Unable to list roles\nReason: " + 
					ae.getMessage());				
			}
		}		
	}
	
	/** 
	 * adds a role. 
	 * If option u (user) is available add a role for that user; in this
	 * case if the role does not exist yet, create the role itself as well. 
	 */
	protected void addRole(CommandLine line) {

		String username = line.getOptionValue('u');
		String rolename = line.getOptionValue('r');
		if(rolename == null) {
			System.err.println("please provide role (-r)");
			return;
		}
		if(username == null) {
			
			System.out.println("add role " + rolename + "...");
			try {
				RolePrincipal r = userManager.createRole(rolename);
				if(r != null) {
					System.out.println("role " + r + " created");
				} else {
					System.err.println("role " + rolename + " was not created" +
						" for an unknown reason");
				}
			} catch(AccessException ae) {
				System.err.println("Unable to create role\nReason: " + 
					ae.getMessage());				
			}
		} else {
			
			try {
				
				UserPrincipal u = userManager.getUser(username);
				if(u == null) {
					System.err.println("user " + username + " does not exist");
					return;					
				}
				RolePrincipal r = userManager.getRole(rolename);
				if(r == null) {
					System.out.println("add role " + rolename + "...");
					r = userManager.createRole(rolename);
					System.out.println("role " + r + " created");
				}
				if(r != null) {
					System.out.println("add role " + r + "to user " + u + "...");
					userManager.addUserToRole(u, r);
					System.out.println("role " + r + "added");
				} else {
					System.err.println("role " + rolename + " was not created" +
						" for an unknown reason");
				}
			} catch(AccessException ae) {
				System.err.println("Unable to create\nReason: " + 
					ae.getMessage());				
			}
		}		
	}
	
	/** 
	 * deletes a role. This command deletes the user-role map for this role as well
	 */
	protected void delRole(CommandLine line) {

		String rolename = line.getOptionValue('r');
		if(rolename == null) {
			System.err.println("please provide role (-r)");
			return;
		}
		try {
			RolePrincipal r = userManager.getRole(rolename);
			if(r == null) {
				System.err.println("role " + rolename + " does not exist");
			} else {
				System.out.println("delete role " + rolename + "...");
				userManager.deleteRole(r);		
			}
		} catch(AccessException ae) {
			System.err.println("Unable to create role\nReason: " + 
				ae.getMessage());				
		}			
	}
	
	/** 
	 * removes a role from the user map
	 */
	protected void removeRole(CommandLine line) {

		String rolename = line.getOptionValue('r');
		String username = line.getOptionValue('u');
		if(rolename == null) {
			System.err.println("please provide role (-r)");
			return;
		}
		if(username == null) {
			System.err.println("please provide user (-u)");
			return;
		}
		try {
			RolePrincipal r = userManager.getRole(rolename);
			if(r == null) {
				System.err.println("role " + rolename + " does not exist");
				return;
			}
			UserPrincipal u = userManager.getUser(username);
			if(u == null) {
				System.err.println("user " + username + " does not exist");
				return;
			}
			System.out.println("remove role " + r + "for user " + u + "...");
			userManager.removeUserFromRole(u, r);
			System.out.println("role removed");

		} catch(AccessException ae) {
			System.err.println("Unable to create role\nReason: " + 
				ae.getMessage());				
		}			
	}
	

	/** tool for maintaining users */
	public static void main(String[] args) {
		
		UserTool tool = new UserTool();
		tool.interpret(args);
	}
	
	/**
	 * loads the access factory
	 * @throws Exception
	 */
	protected void loadAccessFactory(URL config) throws Exception {
	
		try {
			accessFactory = new AccessFactory(config);
			userManager = accessFactory.getUserManager();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}	
	}
	
	/**
	 * load the access factory
	 * @throws Exception
	 */
	protected void loadAccessFactory(String config) throws Exception {
	
		try {
			accessFactory = new AccessFactory(config);
			userManager = accessFactory.getUserManager();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}		
	}
	
}
