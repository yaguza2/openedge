package nl.openedge.access.tools;

import java.net.URL;

import nl.openedge.access.AccessException;
import nl.openedge.access.AccessFactory;
import nl.openedge.access.UserManager;
import nl.openedge.access.UserPrincipal;

import org.apache.commons.cli.*;

/** 
 * Tool for user maintenance from command line
 * 
 * @author Hillenius
 * $Id$
 */
public final class UserTool {

	/** the options */
	protected static Options options = new Options();
	
	/** reference to the access factory */
	protected static AccessFactory accessFactory;

	/** construct and add options */
	public UserTool() {
		
		options.addOption("adduser", false, "adds a user");
		options.addOption("deluser", false, "deletes a user");
		options.addOption("resetpwd", false, "changes the password of a user");
		options.addOption("help", false, "prints this message");
		
		options.addOption("u", "user", true, "the username of the user");
		options.addOption("p", "pwd", true, "the username of the user");
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
		
		if(System.getProperty("configfile") != null) {
			String config = System.getProperty("configfile");
			System.out.println("loading " + config + " for configuration");
			try {
				loadAccessFactory(config);
			} catch(Exception e) {				
				System.err.println(e.getMessage());
				return;	
			}
		} else {
			System.out.println("env var 'configfile' not set... loading " +
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
		else printHelp();
	}
	
	/** print usage */
	protected void printHelp() {
		
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "usertool", options );
	}
	
	/** add a user */
	protected void addUser(CommandLine line) {
		
		String username = line.getOptionValue('u');
		String password = line.getOptionValue('p');
		
		System.out.println("create user '" + username + "' with password '" + 
			password + "'...");
			
		UserManager userManager = accessFactory.getUserManager();
		UserPrincipal user = null;
		try {
			user = userManager.createUser(username, password);
			System.out.println("user created (" + user + ")");
		} catch(AccessException ae) {
			System.err.println("Unable to create user\nReason: " + 
				ae.getMessage());
		}	
	}
	
	/** delete a user */
	protected void delUser(CommandLine line) {
	
		String username = line.getOptionValue('u');
		
		System.out.println("deleting user '" + username + "'...");
			
		UserManager userManager = accessFactory.getUserManager();
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
	
	/** reset password */
	protected void setPwd(CommandLine line) {
		
		String username = line.getOptionValue('u');
		String password = line.getOptionValue('p');
		
		System.out.println("reset password to '" + password + "' for user '" + 
			username + "'...");
			
		UserManager userManager = accessFactory.getUserManager();
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
	

	/** tool for maintaining users */
	public static void main(String[] args) {
		
		UserTool tool = new UserTool();
		tool.interpret(args);
	}
	
	/**
	 * load the access factory
	 * @throws Exception
	 */
	protected void loadAccessFactory(URL config) throws Exception {
	
		try {
			accessFactory = new AccessFactory(config);
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
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}		
	}
	
}
