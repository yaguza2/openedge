/*
 * Created on 4-apr-2003
 */
package nl.openedge.access.impl.jdbc;

import java.util.List;
import java.util.Map;

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
import nl.openedge.access.util.XML;

/**
 * @author Hillenius
 * $Id$
 */
public class JDBCAccessProvider implements AccessProvider {

	/** datasource */
	protected DataSource dataSource = null;
	
	protected Map params = null;
	
	/** name of datasource to use */
	public final static String KEY_DATASOURCE_NAME = "datasource";

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
			this.dataSource = (DataSource)envCtx.lookup(datasourceName);
				
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ConfigException(ex.getMessage(), ex.getCause());
		}	
	}

	/**
	 * @see nl.openedge.access.AccessProvider#createPermission(nl.openedge.access.Entity, nl.openedge.access.Resource, nl.openedge.access.Permission)
	 */
	public void createPermission(
			Entity entity,
			Resource resource,
			Permission permission)
			throws AccessException {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	/**
	 * @see nl.openedge.access.AccessProvider#getResource(java.lang.String)
	 */
	public Resource getResource(String resourceKey) throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see nl.openedge.access.AccessProvider#listResources()
	 */
	public List listResources() throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see nl.openedge.access.AccessProvider#deleteResource(nl.openedge.access.Resource)
	 */
	public void deleteResource(Resource resource) throws AccessException {
		// TODO Auto-generated method stub

	}

}
