package nl.openedge.access.impl.rdbms;

import java.security.Principal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import nl.openedge.access.AccessException;
import nl.openedge.access.AccessProvider;
import nl.openedge.access.ConfigException;
import nl.openedge.access.Permission;
import nl.openedge.access.PermissionSet;
import nl.openedge.access.Resource;
import nl.openedge.access.impl.DefaultResource;
import nl.openedge.access.util.XML;

/**
 * @author Hillenius
 * $Id$
 */
public class RdbmsAccessProvider extends RdbmsBase implements AccessProvider {
	
	/** logger */
	private Log log = LogFactory.getLog(this.getClass());
	
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
			String environmentContext = (String)params.get("environmentContext");
			Context envCtx = (Context) ctx.lookup(environmentContext);
			setDataSource((DataSource)ctx.lookup(datasourceName));
				
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ConfigException(ex);
		}
		log.info("initialised");
	}


	/**
	 * @see nl.openedge.access.AccessProvider#createPermission(nl.openedge.access.Principal, nl.openedge.access.Resource, nl.openedge.access.Permission)
	 */
	public void createPermission(
			Principal entity,
			Resource resource,
			Permission permission)
			throws AccessException {
		//TODO invullen

	}

	/**
	 * @see nl.openedge.access.AccessProvider#deletePermission(nl.openedge.access.Principal, nl.openedge.access.Resource, nl.openedge.access.Permission)
	 */
	public void deletePermission(
			Principal entity,
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
	 * @see nl.openedge.access.AccessProvider#getPermissions(nl.openedge.access.Principal, nl.openedge.access.Resource)
	 */
	public PermissionSet getPermissions(Principal entity, Resource resource)
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
			int result = excecuteUpdate(
				queries.getProperty("createResourceStmt"), params);
			if(result != 1) {
				throw new AccessException("query failed for an unknown reason");
			}
		} catch(SQLException e) {
			throw new AccessException(e);
		}
	}

	/**
	 * @see nl.openedge.access.AccessProvider#getResource(java.lang.String)
	 */
	public Resource getResource(String resourceKey) throws AccessException {

		DefaultResource resource = null;
		Object[] params = new Object[]{resourceKey};
		try {
			QueryResult result = excecuteQuery(
				queries.getProperty("getResourceStmt"), params);
			if(result.getRowCount() == 1) {
				Map row = result.getRows()[0];
				resource = new DefaultResource((String)row.get("name"));
				resource.setResourcePermissions(
					((Integer)row.get("permission")).intValue());
			}
		} catch(SQLException e) {
			throw new AccessException(e);
		}
		return resource;
	}

	/**
	 * @see nl.openedge.access.AccessProvider#deleteResource(nl.openedge.access.Resource)
	 */
	public void deleteResource(Resource resource) throws AccessException {

		Object[] params = new Object[]{resource.getResourceKey()};
		try {
			int result = excecuteUpdate(
				queries.getProperty("deleteResourceStmt"), params);
			if(result != 1) {
				throw new AccessException("query failed for an unknown reason");
			}
		} catch(SQLException e) {
			throw new AccessException(e);
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
