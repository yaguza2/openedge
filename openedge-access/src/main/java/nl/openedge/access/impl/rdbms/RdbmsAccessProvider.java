package nl.openedge.access.impl.rdbms;

import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import nl.openedge.access.AccessException;
import nl.openedge.access.AccessProvider;
import nl.openedge.access.ConfigException;
import nl.openedge.access.AccessPermission;
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
		log.info("initialised");
	}


	/**
	 * @see nl.openedge.access.AccessProvider#createPermission(nl.openedge.access.Principal, nl.openedge.access.Resource, nl.openedge.access.AccessPermission)
	 */
	public void createPermission(
			Principal entity,
			Resource resource,
			AccessPermission permission)
			throws AccessException {
		//TODO invullen

	}

	/**
	 * @see nl.openedge.access.AccessProvider#deletePermission(nl.openedge.access.Principal, nl.openedge.access.Resource, nl.openedge.access.AccessPermission)
	 */
	public void deletePermission(
			Principal entity,
			Resource resource,
			AccessPermission permission)
			throws AccessException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see nl.openedge.access.AccessProvider#createPermission(nl.openedge.access.Resource, nl.openedge.access.AccessPermission)
	 */
	public void createPermission(Resource resource, AccessPermission permission)
			throws AccessException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see nl.openedge.access.AccessProvider#deletePermission(nl.openedge.access.Resource, nl.openedge.access.AccessPermission)
	 */
	public void deletePermission(Resource resource, AccessPermission permission)
			throws AccessException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see nl.openedge.access.AccessProvider#createResource(nl.openedge.access.Resource)
	 */
	public void createResource(Resource resource) throws AccessException {
		
		HashMap fields = new HashMap(2);
		fields.put("name", resource.getResourceKey());
		fields.put("permission", new Integer(0));
		try {
			int result = insert("oeaccess_resource", fields);
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
		try {
			HashMap keyFields = new HashMap(1);
			keyFields.put("name", resourceKey);
			QueryResult result = select("oeaccess_resource", keyFields);
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

		try {
			HashMap keyFields = new HashMap(1);
			keyFields.put("name", resource.getResourceKey());
			int result = delete("oeaccess_resource", keyFields);
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
		
		List resources = new ArrayList();
		try {
			QueryResult result = select("oeaccess_resource", new HashMap(0));
			if(result.getRowCount() == 1) {
				Map row = result.getRows()[0];
				DefaultResource resource = new DefaultResource((String)row.get("name"));
				resource.setResourcePermissions(
					((Integer)row.get("permission")).intValue());
				resources.add(resource);
			}
		} catch(SQLException e) {
			throw new AccessException(e);
		}
		return resources;
	}

}
