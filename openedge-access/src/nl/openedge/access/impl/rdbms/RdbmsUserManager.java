/*
 * Created on 5-apr-2003
 */
package nl.openedge.access.impl.rdbms;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import nl.openedge.access.AccessException;
import nl.openedge.access.ConfigException;
import nl.openedge.access.Group;
import nl.openedge.access.User;
import nl.openedge.access.UserManager;
import nl.openedge.access.impl.DefaultGroup;
import nl.openedge.access.impl.DefaultUser;
import nl.openedge.access.util.PasswordHelper;
import nl.openedge.access.util.XML;

/**
 * @author E.F. Hillenius
 * $Id$
 */
public class RdbmsUserManager extends RdbmsBase implements UserManager {

	/** logger */
	private Log log = LogFactory.getLog(this.getClass());

	protected Map params = null;
	
	/** name of datasource to use */
	public final static String KEY_DATASOURCE_NAME = "datasource";

	/** construct */
	public RdbmsUserManager() {

	}

	/**
	 * @see nl.openedge.access.Configurable#init(org.jdom.Element)
	 */
	public void init(Element configNode) throws ConfigException {

		this.params = XML.getParams(configNode);
		
		String datasourceName = (String)params.get(KEY_DATASOURCE_NAME);
		if(datasourceName == null) throw new ConfigException(
				KEY_DATASOURCE_NAME + " is mandatory for " + getClass());
			
		try {
			Context ctx = new InitialContext();
			String environmentContext = (String)params.get("environmentContext");
			Context envCtx = (Context) ctx.lookup(environmentContext);
			setDataSource((DataSource)envCtx.lookup(datasourceName));
				
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ConfigException(ex);
		}
		log.info("initialised");
	}

	/**
	 * @see nl.openedge.access.UserManager#createUser(java.lang.String)
	 */
	public User createUser(String name, String password) throws AccessException {

		DefaultUser user = null;
		try {
			String cryptedPassword = new String(
				PasswordHelper.cryptPassword(password.toCharArray()));
			Object[] params = new Object[]{ name, cryptedPassword };
			int result = excecuteUpdate(
				queries.getProperty("insertUserStmt"), params);
			if(result == 1) {
				user = new DefaultUser();
				user.setName(name);
			} else {
				throw new AccessException("query failed for an unknown reason");
			}
		} catch(Exception e) {
			throw new AccessException(e);
		}
		return user;
	}
	
	/**
	 * @see nl.openedge.access.UserManager#resetPassword(nl.openedge.access.User, java.lang.String)
	 */
	public void resetPassword(User user, String newPassword) throws AccessException {
		
		try {
			String cryptedPassword = new String(
				PasswordHelper.cryptPassword(newPassword.toCharArray()));
			Object[] params = new Object[]{ cryptedPassword };
			int result = excecuteUpdate(
				queries.getProperty("insertUserStmt"), params);
			if(result != 1) {
				throw new AccessException("query failed for an unknown reason");
			}
		} catch(Exception e) {
			throw new AccessException(e);
		}		
	}

	/**
	 * @see nl.openedge.access.UserManager#createGroup(java.lang.String)
	 */
	public Group createGroup(String name) throws AccessException {

		DefaultGroup group = null;
		try {
			Object[] params = new Object[]{ name };
			int result = excecuteUpdate(
				queries.getProperty("insertGroupStmt"), params);
			if(result == 1) {
				group = new DefaultGroup();
				group.setName(name);
			} else {
				throw new AccessException("query failed for an unknown reason");
			}
		} catch(Exception e) {
			throw new AccessException(e);
		}
		return group;
	}

	/**
	 * @see nl.openedge.access.UserManager#getUser(java.lang.Integer)
	 * loads user, user attributes and groups that user is member of
	 */
	public User getUser(String name) throws AccessException {

		DefaultUser user = null;
		try {
			Object[] params = new Object[]{ name };
			QueryResult result = excecuteQuery(
				queries.getProperty("selectUserStmt"), params);
			// note that name should be unique (pk); if there is more than one row 
			// no user will be returned at all!
			if(result != null && result.getRowCount() == 1) {
				
				Map row = result.getRows()[0];
				user = new DefaultUser();
				user.setName((String)row.get("name"));
				// add attributes
				addAttributes(user);
				// add groups
				addGroups(user);
			}
		} catch(SQLException e) {
			throw new AccessException(e);
		}
		return user;
	}
	
	/**
	 * add user attibutes to user
	 * @param user
	 * @throws SQLException
	 */
	protected void addAttributes(DefaultUser user) throws SQLException {
		
		Object[] params = new Object[]{ user.getName() };
		QueryResult attrResult = excecuteQuery(
			queries.getProperty("selectUserAttributesStmt"), params);
		Map attributes = new HashMap();
		Map[] rows = attrResult.getRows();
		if(rows != null) for(int i = 0; i < rows.length; i++) {
			attributes.put(rows[i].get("attrib_key"), rows[i].get("attrib_value"));
		}         
		user.setAttributes(attributes);	
	}

	/**
	 * add groups that user is member of to user
	 * @param user
	 * @throws SQLException
	 */
	protected void addGroups(DefaultUser user) 
			throws SQLException, AccessException {
         
		user.setGroups(listGroupsForUser(user));	
	}

	/**
	 * @see nl.openedge.access.UserManager#getGroup(java.lang.Integer)
	 */
	public Group getGroup(String name) throws AccessException {
		
		Group group = null;
		try {
			Object[] params = new Object[]{ name };
			QueryResult result = excecuteQuery(
				queries.getProperty("selectGroupStmt"), params);
			if(result != null && result.getRowCount() == 1) {
				
				Map row = result.getRows()[0];
				group = new DefaultGroup();
				group.setName((String)row.get("name"));
			}
		} catch(SQLException e) {
			throw new AccessException(e);
		}		
		return group;
	}

	/**
	 * @see nl.openedge.access.UserManager#listUsers()
	 */
	public List listUsers() throws AccessException {

		List users = new ArrayList();
		try {
			Object[] params = new Object[]{ };
			QueryResult result = excecuteQuery(
				queries.getProperty("listUsersStmt"), params);
			if(result != null) {
				
				Map[] rows = result.getRows();
				if(rows != null) for(int i = 0; i < rows.length; i++) {
					DefaultUser user = new DefaultUser();
					user.setName((String)rows[i].get("name"));
					users.add(user);
				}
			}
		} catch(SQLException e) {
			throw new AccessException(e);
		}
		return users;
	}

	/**
	 * @see nl.openedge.access.UserManager#listGroups()
	 */
	public List listGroups() throws AccessException {

		List groups = new ArrayList();
		try {
			Object[] params = new Object[]{ };
			QueryResult result = excecuteQuery(
				queries.getProperty("listGroupsStmt"), params);
			if(result != null) {
				
				Map[] rows = result.getRows();
				if(rows != null) for(int i = 0; i < rows.length; i++) {
					DefaultUser group = new DefaultUser();
					group.setName((String)rows[i].get("name"));
					groups.add(group);
				}
			}
		} catch(SQLException e) {
			throw new AccessException(e);
		}
		return groups;
	}

	/**
	 * @see nl.openedge.access.UserManager#listUsersInGroup(nl.openedge.access.Group)
	 */
	public List listUsersInGroup(Group group) throws AccessException {
		
		List users = new ArrayList();
		if(group == null) return users;
		try {
			Object[] params = new Object[]{ group.getName() };
			QueryResult result = excecuteQuery(
				queries.getProperty("selectUsersInGroupStmt"), params);
			if(result != null) {
				
				Map[] rows = result.getRows();
				if(rows != null) for(int i = 0; i < rows.length; i++) {
					DefaultUser user = new DefaultUser();
					user.setName((String)rows[i].get("name"));
					users.add(user);
				}
			}
		} catch(SQLException e) {
			throw new AccessException(e);
		}
		return users;
	}

	/**
	 * @see nl.openedge.access.UserManager#listGroupsForUsers(nl.openedge.access.User)
	 */
	public List listGroupsForUser(User user) throws AccessException {
		
		List groups = new ArrayList();
		if(user == null) return groups;
		try {
			Object[] params = new Object[]{ user.getName() };
			QueryResult groupResult = excecuteQuery(
				queries.getProperty("selectUserGroupsStmt"), params);
			Map[] rows = groupResult.getRows();
			if(rows != null) for(int i = 0; i < rows.length; i++) {
				DefaultGroup group = new DefaultGroup();
				group.setName((String)rows[i].get("name"));
				groups.add(group); 
			} 
		} catch(SQLException e) {
			throw new AccessException(e);
		}
		return groups;
	}

	/**
	 * @see nl.openedge.access.UserManager#isUserInGroup(nl.openedge.access.User, nl.openedge.access.Group)
	 */
	public boolean isUserInGroup(User user, Group group) throws AccessException {

		boolean result = false;
		try {
			Object[] params = new Object[]{ user.getName(), group.getName() };
			QueryResult groupResult = excecuteQuery(
				queries.getProperty("selectUserGroupStmt"), params);
			Map[] rows = groupResult.getRows();
			if(rows != null && rows[0] != null) {
				result = true;
			} 
		} catch(SQLException e) {
			throw new AccessException(e);
		}
		return result;
	}

	/**
	 * @see nl.openedge.access.UserManager#addUserToGroup(nl.openedge.access.User, nl.openedge.access.Group)
	 */
	public void addUserToGroup(User user, Group group) throws AccessException {

		if(user == null || group == null) return;
		try {
			Object[] params = new Object[]{ user.getName(), group.getName() };
			int result = excecuteUpdate(
				queries.getProperty("addUserToGroupStmt"), params);
			if(result != 1) {
				throw new AccessException("query failed for an unknown reason");
			}
		} catch(Exception e) {
			throw new AccessException(e);
		}
	}

	/**
	 * @see nl.openedge.access.UserManager#removeUserFromGroup(nl.openedge.access.User, nl.openedge.access.Group)
	 */
	public void removeUserFromGroup(User user, Group group) throws AccessException {
		
		if(user == null || group == null) return;
		try {
			Object[] params = new Object[]{ user.getName(), group.getName() };
			int result = excecuteUpdate(
				queries.getProperty("removeUserFromGroupStmt"), params);
			if(result != 1) {
				throw new AccessException("query failed for an unknown reason");
			}
		} catch(Exception e) {
			throw new AccessException(e);
		}
	}

	/**
	 * @see nl.openedge.access.UserManager#removeUser(nl.openedge.access.User)
	 */
	public void removeUser(User user) throws AccessException {
		
		if(user == null) return;
		try {
			Object[] params = new Object[]{ user.getName() };
			int result = excecuteUpdate(
				queries.getProperty("deleteUserStmt"), params);
			if(result != 1) {
				throw new AccessException("query failed for an unknown reason");
			}
		} catch(Exception e) {
			throw new AccessException(e);
		}
	}

	/**
	 * @see nl.openedge.access.UserManager#removeGroup(nl.openedge.access.Group)
	 */
	public void removeGroup(Group group) throws AccessException {

		if(group == null) return;
		try {
			Object[] params = new Object[]{ group.getName() };
			int result = excecuteUpdate(
				queries.getProperty("deleteGroupStmt"), params);
			if(result != 1) {
				throw new AccessException("query failed for an unknown reason");
			}
		} catch(Exception e) {
			throw new AccessException(e);
		}
	}

}
