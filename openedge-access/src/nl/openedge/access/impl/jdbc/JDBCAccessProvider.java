package nl.openedge.access.impl.jdbc;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.jdom.Element;

import nl.openedge.access.AccessException;
import nl.openedge.access.AccessProvider;
import nl.openedge.access.ConfigException;
import nl.openedge.access.Entity;
import nl.openedge.access.Permission;
import nl.openedge.access.PermissionSet;
import nl.openedge.access.Resource;
import nl.openedge.access.impl.DefaultResource;
import nl.openedge.access.util.XML;

/**
 * @author Hillenius
 * $Id$
 */
public class JDBCAccessProvider extends JDBCBase implements AccessProvider {
	
	protected Map params = null;
	
	/** name of datasource to use */
	public final static String KEY_DATASOURCE_NAME = "datasource";
	
	private String createResourceStmt;
	private String updateResourceStmt;
	private String deleteResourceStmt;
	
	private String createUserPermissionStmt;
	private String updateUserPermissionStmt;
	private String deleteUserPermissionStmt;
	
	private String createGroupPermissionStmt;
	private String updateGroupPermissionStmt;
	private String deleteGroupPermissionStmt;

	private String getResourceStmt;
	private String getUserPermissionsStmt;
	private String getGroupPermissionsStmt;

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
			// relative to standard JNDI root for J2EE app
			Context envCtx = (Context) ctx.lookup("java:comp/env");
			setDataSource((DataSource)envCtx.lookup(datasourceName));
				
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ConfigException(ex.getMessage(), ex.getCause());
		}
		
		try {
			Properties props = new Properties();
			props.load(getClass().getResourceAsStream(
					"JDBCAccessProvider.properties"));
			
			this.createResourceStmt = 
					props.getProperty("createResourceStmt");
			this.updateResourceStmt = 
					props.getProperty("updateResourceStmt");
			this.deleteResourceStmt = 
					props.getProperty("deleteResourceStmt");
	
			this.createUserPermissionStmt = 
					props.getProperty("createUserPermissionStmt");
			this.updateUserPermissionStmt = 
					props.getProperty("updateUserPermissionStmt");
			this.deleteUserPermissionStmt = 
					props.getProperty("deleteUserPermissionStmt");
	
			this.createGroupPermissionStmt = 
					props.getProperty("createGroupPermissionStmt");
			this.updateGroupPermissionStmt = 
					props.getProperty("updateGroupPermissionStmt");
			this.deleteGroupPermissionStmt = 
					props.getProperty("deleteGroupPermissionStmt");

			this.getResourceStmt = 
					props.getProperty("getResourceStmt");
			this.getUserPermissionsStmt = 
					props.getProperty("getUserPermissionsStmt");
			this.getGroupPermissionsStmt = 
					props.getProperty("getGroupPermissionsStmt");
					
		} catch(Exception e) {
			e.printStackTrace();
		}
			
	}
	
	private void loadStatements() {
		
	}

	/**
	 * @see nl.openedge.access.AccessProvider#createPermission(nl.openedge.access.Entity, nl.openedge.access.Resource, nl.openedge.access.Permission)
	 */
	public void createPermission(
			Entity entity,
			Resource resource,
			Permission permission)
			throws AccessException {
		//TODO invullen

	}

	/**
	 * @see nl.openedge.access.AccessProvider#deletePermission(nl.openedge.access.Entity, nl.openedge.access.Resource, nl.openedge.access.Permission)
	 */
	public void deletePermission(
			Entity entity,
			Resource resource,
			Permission permission)
			throws AccessException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see nl.openedge.access.AccessProvider#createPermission(nl.openedge.access.Resource, nl.openedge.access.Permission)
	 */
	public void createPermission(Resource resource, Permission permission)
			throws AccessException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see nl.openedge.access.AccessProvider#deletePermission(nl.openedge.access.Resource, nl.openedge.access.Permission)
	 */
	public void deletePermission(Resource resource, Permission permission)
			throws AccessException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see nl.openedge.access.AccessProvider#getPermissions(nl.openedge.access.Entity, nl.openedge.access.Resource)
	 */
	public PermissionSet getPermissions(Entity entity, Resource resource)
			throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see nl.openedge.access.AccessProvider#getPermissions(nl.openedge.access.Resource)
	 */
	public PermissionSet getPermissions(Resource resource)
			throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see nl.openedge.access.AccessProvider#createResource(nl.openedge.access.Resource)
	 */
	public void createResource(Resource resource) throws AccessException {
		
		Object[] params = new Object[]{resource.getResourceKey(), new Integer(0)};
		try {
			int result = excecuteUpdate(this.createResourceStmt, params);
		} catch(SQLException e) {
			throw new AccessException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * @see nl.openedge.access.AccessProvider#getResource(java.lang.String)
	 */
	public Resource getResource(String resourceKey) throws AccessException {

		DefaultResource resource = null;
		Object[] params = new Object[]{resourceKey};
		try {
			QueryResult result = excecuteQuery(this.getResourceStmt, params);
			if(result.getRowCount() == 1) {
				Map row = result.getRows()[0];
				resource = new DefaultResource((String)row.get("name"));
				resource.setResourcePermissions(
					((Integer)row.get("permission")).intValue());
			}
		} catch(SQLException e) {
			throw new AccessException(e.getMessage(), e.getCause());
		}
		return resource;
	}

	/**
	 * @see nl.openedge.access.AccessProvider#deleteResource(nl.openedge.access.Resource)
	 */
	public void deleteResource(Resource resource) throws AccessException {

		Object[] params = new Object[]{resource.getResourceKey()};
		try {
			int result = excecuteUpdate(this.deleteResourceStmt, params);
		} catch(SQLException e) {
			throw new AccessException(e.getMessage(), e.getCause());
		}

	}
	
	/**
	 * @see nl.openedge.access.AccessProvider#listResources()
	 */
	public List listResources() throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

}
