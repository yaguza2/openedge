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
package nl.openedge.access.impl.rdbms;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import nl.openedge.access.AccessException;
import nl.openedge.access.ConfigException;
import nl.openedge.access.LoginDecorator;
import nl.openedge.access.RolePrincipal;
import nl.openedge.access.UserPrincipal;
import nl.openedge.access.UserManager;
import nl.openedge.access.util.PasswordHelper;
import nl.openedge.access.util.XML;

/**
 * Low level JDBC implementation of user manager
 * reads its statements from properties file or, for basic statements builds
 * them on the fly
 * 
 * @author E.F. Hillenius
 */
public class RdbmsUserManager extends RdbmsBase implements UserManager {

	/** logger */
	private Log log = LogFactory.getLog(this.getClass());

	protected Map params = null;
	
	/** name of datasource to use */
	public final static String KEY_DATASOURCE_NAME = "datasource";
	
	/** decorators for login modules */
	protected LoginDecorator[] loginDecorators = null; 

	/** construct */
	public RdbmsUserManager() {

	}

	/**
	 * @see nl.openedge.access.Configurable#init(org.jdom.Element)
	 */
	public void init(Element configNode) throws ConfigException {

		this.params = XML.getParams(configNode);
		log.info("initialised");
	}

	/**
	 * @see nl.openedge.access.UserManager#createUser(java.lang.String, java.lang.String)
	 */
	public UserPrincipal createUser(String name, String password) throws AccessException {

		UserPrincipal user = null;
		try {
			String cryptedPassword = new String(
				PasswordHelper.cryptPassword(password.toCharArray()));
			
			HashMap fields = new HashMap(2);
			fields.put("name", name);
			fields.put("password", cryptedPassword);
			
			int result = insert("oeaccess_user", fields);
			if(result == 1) {
				user = new UserPrincipal(name);
			} else {
				throw new AccessException("query failed for an unknown reason");
			}
		} catch(Exception e) {
			throw new AccessException(e);
		}
		return user;
	}
	
	/**
	 * @see nl.openedge.access.UserManager#getUser(java.lang.String)
	 * loads user, user attributes and roles that user is member of
	 */
	public UserPrincipal getUser(String name) throws AccessException {

		UserPrincipal user = null;
		try {
			HashMap keyFields = new HashMap(1);
			keyFields.put("name", name);
			
			QueryResult result = select("oeaccess_user", keyFields);
			// note that name should be unique (pk); if there is more than one row 
			// no user will be returned at all!
			if(result != null && result.getRowCount() == 1) {
				
				Map row = result.getRows()[0];
				user = new UserPrincipal((String)row.get("name"));
				// add attributes
				addAttributes(user);
				// add roles
				addRoles(user);
			}
		} catch(SQLException e) {
			throw new AccessException(e);
		}
		return user;
	}
	
	/**
	 * @see nl.openedge.access.UserManager#listUsers()
	 */
	public List listUsers() throws AccessException {

		List users = new ArrayList();
		try {
			QueryResult result = select("oeaccess_user", new HashMap(0));
			if(result != null) {
				
				Map[] rows = result.getRows();
				if(rows != null) for(int i = 0; i < rows.length; i++) {
					UserPrincipal user = new UserPrincipal(
						(String)rows[i].get("name"));
					users.add(user);
				}
			}
		} catch(SQLException e) {
			throw new AccessException(e);
		}
		return users;
	}
	
	/**
	 * @see nl.openedge.access.UserManager#deleteUser(nl.openedge.access.UserPrincipal)
	 */
	public void deleteUser(UserPrincipal user) throws AccessException {
		
		if(user == null) return;
		try {
			HashMap keyFields = new HashMap(1);
			keyFields.put("name", user.getName());
			int result = delete("oeaccess_user", keyFields);
			if(result != 1) {
				throw new AccessException("query failed for an unknown reason");
			}
			keyFields.clear();
			keyFields.put("user_name", user.getName());
			result = delete("oeaccess_user_role", keyFields);
			result = delete("oeaccess_user_attribs", keyFields);
		
		} catch(Exception e) {
			throw new AccessException(e);
		}
	}
	
	/**
	 * @see nl.openedge.access.UserManager#resetPassword(nl.openedge.access.UserPrincipal, java.lang.String)
	 */
	public void resetPassword(UserPrincipal user, String newPassword) throws AccessException {
		
		try {
			String cryptedPassword = new String(
				PasswordHelper.cryptPassword(newPassword.toCharArray()));
			HashMap keyFields = new HashMap(1);
			keyFields.put("name", user.getName());
			HashMap fields = new HashMap(1);
			fields.put("password", cryptedPassword);
			
			int result = update("oeaccess_user", keyFields, fields);
			if(result != 1) {
				throw new AccessException("query failed for an unknown reason");
			}
		} catch(Exception e) {
			throw new AccessException(e);
		}		
	}
	
	/**
	 * @see nl.openedge.access.UserManager#getUserAttributes(nl.openedge.access.UserPrincipal)
	 */
	public Map getUserAttributes(UserPrincipal user) throws AccessException {
	
		Map attributes = new HashMap();
		if(user == null) return attributes;
		try {
			HashMap keyFields = new HashMap(1);
			keyFields.put("user_name", user.getName());
			QueryResult attrResult = select("oeaccess_user_attribs", keyFields);
			Map[] rows = attrResult.getRows();
			if(rows != null) for(int i = 0; i < rows.length; i++) {
				attributes.put(rows[i].get("attrib_key"), rows[i].get("attrib_value"));
			}         
		} catch(SQLException e) {
			throw new AccessException(e);
		}
		return attributes;
	}
	
	/**
	 * @see nl.openedge.access.UserManager#getUserAttribute(nl.openedge.access.UserPrincipal, java.lang.String)
	 */
	public Object getUserAttribute(UserPrincipal user, String key) throws AccessException {
		
		Object value = null;
		try {
			HashMap keyFields = new HashMap(2);
			keyFields.put("user_name", user.getName());
			keyFields.put("attrib_key", key);
			QueryResult result = select("oeaccess_user_attribs", keyFields);
			if(result != null && result.getRowCount() == 1) {
				
				Map row = result.getRows()[0];
				value = row.get("attrib_value");
			}
		} catch(SQLException e) {
			throw new AccessException(e);
		}		
		return value;		
	}
	
	/**
	 * @see nl.openedge.access.UserManager#setUserAttibute(nl.openedge.access.UserPrincipal, java.lang.String, java.lang.Object)
	 */
	public void setUserAttibute(UserPrincipal user, String key, Object value)
		throws AccessException {
	
		// check if the attribute allready exists
		Object oldValue = getUserAttribute(user, key);
		
		if(oldValue == null) { // create the attribute
			
			try {
				HashMap fields = new HashMap(2);
				fields.put("user_name", user.getName());
				fields.put("attrib_key", key);
				int result = insert("oeaccess_user_attribs", fields);
				if(result != 1) {
					throw new AccessException("query failed for an unknown reason");
				}
			} catch(Exception e) {
				throw new AccessException(e);
			}
		} else { // update the attribute
		
			try {
				HashMap keyFields = new HashMap(1);
				HashMap fields = new HashMap(1);
				keyFields.put("user_name", user.getName());
				fields.put("attrib_key", key);
				int result = insert("oeaccess_user_attribs", fields);
				if(result != 1) {
					throw new AccessException("query failed for an unknown reason");
				}
			} catch(Exception e) {
				throw new AccessException(e);
			}	
		}	
	}
		
	/**
	 * @see nl.openedge.access.UserManager#removeUserAttribute(nl.openedge.access.UserPrincipal, java.lang.String)
	 */
	public void removeUserAttribute(UserPrincipal user, String key) 
			throws AccessException {
	
		if(user == null || key == null) return;
		try {
			HashMap keyFields = new HashMap(2);
			keyFields.put("user_name", user.getName());
			keyFields.put("attrib_key", key);
			int result = delete("oeaccess_user_attribs", keyFields);
			if(result != 1) {
				throw new AccessException("query failed for an unknown reason");
			}
		} catch(Exception e) {
			throw new AccessException(e);
		}			
	}
	
	/**
	 * @see nl.openedge.access.UserManager#removeUserAttribute(nl.openedge.access.UserPrincipal)
	 */
	public void removeUserAttribute(UserPrincipal user) throws AccessException {
	
		if(user == null) return;
		try {
			HashMap keyFields = new HashMap(1);
			keyFields.put("user_name", user.getName());
			int result = delete("oeaccess_user_attribs", keyFields);
			if(result != 1) {
				throw new AccessException("query failed for an unknown reason");
			}
		} catch(Exception e) {
			throw new AccessException(e);
		}	
	}
	
	/**
	 * add user attibutes to user
	 * @param user
	 * @throws SQLException
	 */
	protected void addAttributes(UserPrincipal user) throws AccessException {
		       
		user.setAttributes(getUserAttributes(user));	
	}
	
	/**
	 * @see nl.openedge.access.UserManager#createRole(java.lang.String)
	 */
	public RolePrincipal createRole(String name) throws AccessException {

		RolePrincipal role = null;
		try {
			HashMap fields = new HashMap(1);
			fields.put("name", name);
			int result = insert("oeaccess_role", fields);
			if(result == 1) {
				role = new RolePrincipal(name);
			} else {
				throw new AccessException("query failed for an unknown reason");
			}
		} catch(Exception e) {
			throw new AccessException(e);
		}
		return role;
	}
	
	/**
	 * @see nl.openedge.access.UserManager#getRole(java.lang.String)
	 */
	public RolePrincipal getRole(String name) throws AccessException {
		
		RolePrincipal role = null;
		try {
			HashMap keyFields = new HashMap(1);
			keyFields.put("name", name);
			QueryResult result = select("oeaccess_role", keyFields);
			if(result != null && result.getRowCount() == 1) {
				
				Map row = result.getRows()[0];
				role = new RolePrincipal((String)row.get("name"));
			}
		} catch(SQLException e) {
			throw new AccessException(e);
		}		
		return role;
	}
	
	/**
	 * @see nl.openedge.access.UserManager#deleteRole(nl.openedge.access.RolePrincipal)
	 */
	public void deleteRole(RolePrincipal role) throws AccessException {

		if(role == null) return;
		try {
			HashMap keyFields = new HashMap(1);
			keyFields.put("name", role.getName());
			int result = delete("oeaccess_role", keyFields);
			if(result != 1) {
				throw new AccessException("query failed for an unknown reason");
			}
			keyFields.clear();
			keyFields.put("role_name", role.getName());
			result = delete("oeaccess_user_role", keyFields);
	
		} catch(Exception e) {
			throw new AccessException(e);
		}
	}

	/**
	 * add roles that user is member of to user
	 * @param user
	 * @throws SQLException
	 */
	protected void addRoles(UserPrincipal user) throws AccessException {
         
		user.setRoles(listRolesForUser(user));	
	}


	/**
	 * @see nl.openedge.access.UserManager#listRoles()
	 */
	public List listRoles() throws AccessException {

		List roles = new ArrayList();
		try {
			QueryResult result = select("oeaccess_role", new HashMap(0));
			if(result != null) {
				
				Map[] rows = result.getRows();
				if(rows != null) for(int i = 0; i < rows.length; i++) {
					RolePrincipal role = new RolePrincipal(
						(String)rows[i].get("name"));
					roles.add(role);
				}
			}
		} catch(SQLException e) {
			throw new AccessException(e);
		}
		return roles;
	}

	/**
	 * @see nl.openedge.access.UserManager#listUsersInRole(nl.openedge.access.RolePrincipal)
	 */
	public List listUsersInRole(RolePrincipal role) throws AccessException {
		
		List users = new ArrayList();
		if(role == null) return users;
		try {
			Object[] params = new Object[]{ role.getName() };
			QueryResult result = excecuteQuery(
				queries.getProperty("selectUsersInRoleStmt"), params);
			if(result != null) {
				
				Map[] rows = result.getRows();
				if(rows != null) for(int i = 0; i < rows.length; i++) {
					UserPrincipal user = new UserPrincipal(
						(String)rows[i].get("name"));
					users.add(user);
				}
			}
		} catch(SQLException e) {
			throw new AccessException(e);
		}
		return users;
	}

	/**
	 * @see nl.openedge.access.UserManager#listRolesForUser(nl.openedge.access.UserPrincipal)
	 */
	public Set listRolesForUser(UserPrincipal user) throws AccessException {
		
		Set roles = new HashSet();
		if(user == null) return roles;
		try {
			Object[] params = new Object[]{ user.getName() };
			QueryResult roleResult = excecuteQuery(
				queries.getProperty("selectUserRolesStmt"), params);
			Map[] rows = roleResult.getRows();
			if(rows != null) for(int i = 0; i < rows.length; i++) {
				RolePrincipal role = new RolePrincipal(
					(String)rows[i].get("name"));
				roles.add(role); 
			} 
		} catch(SQLException e) {
			throw new AccessException(e);
		}
		return roles;
	}

	/**
	 * @see nl.openedge.access.UserManager#isUserInRole(nl.openedge.access.UserPrincipal, nl.openedge.access.RolePrincipal)
	 */
	public boolean isUserInRole(UserPrincipal user, RolePrincipal role) throws AccessException {

		boolean result = false;
		try {
			Object[] params = new Object[]{ user.getName(), role.getName() };
			QueryResult roleResult = excecuteQuery(
				queries.getProperty("selectUserRoleStmt"), params);
			Map[] rows = roleResult.getRows();
			if(rows != null && rows[0] != null) {
				result = true;
			} 
		} catch(SQLException e) {
			throw new AccessException(e);
		}
		return result;
	}

	/**
	 * @see nl.openedge.access.UserManager#addUserToRole(nl.openedge.access.UserPrincipal, nl.openedge.access.RolePrincipal)
	 */
	public void addUserToRole(UserPrincipal user, RolePrincipal role) throws AccessException {

		if(user == null || role == null) return;
		try {
			HashMap fields = new HashMap(2);
			fields.put("user_name", user.getName());
			fields.put("role_name", role.getName());
			int result = insert("oeaccess_user_role", fields);
			if(result != 1) {
				throw new AccessException("query failed for an unknown reason");
			}
		} catch(Exception e) {
			throw new AccessException(e);
		}
	}

	/**
	 * @see nl.openedge.access.UserManager#removeUserFromRole(nl.openedge.access.UserPrincipal, nl.openedge.access.RolePrincipal)
	 */
	public void removeUserFromRole(UserPrincipal user, RolePrincipal role) throws AccessException {
		
		if(user == null || role == null) return;
		try {
			HashMap keyFields = new HashMap(2);
			keyFields.put("user_name", user.getName());
			keyFields.put("role_name", role.getName());
			int result = delete("oeaccess_user_role", keyFields);
			if(result != 1) {
				throw new AccessException("query failed for an unknown reason");
			}
		} catch(Exception e) {
			throw new AccessException(e);
		}
	}

	/**
	 * @return LoginDecorator[]
	 */
	public LoginDecorator[] getLogonDecorators() {
		return loginDecorators;
	}

	/**
	 * Sets the logonDecorators.
	 * @param logonDecorators The logonDecorators to set
	 */
	public void setLogonDecorators(LoginDecorator[] logonDecorators) {
		this.loginDecorators = logonDecorators;
	}

}
