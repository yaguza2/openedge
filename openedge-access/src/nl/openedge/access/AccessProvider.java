package nl.openedge.access;

import java.util.List;

/**
 * @author vries, hillenius
 * interface for providing the resources and the permissions for entities
 * 
 */
public interface AccessProvider extends Configurable {
	
	/**
	 * creates a permission on the resource for the given entity
	 * @param entity
	 * @param resource
	 * @param permission
	 */
	public void createPermission(
		Entity entity,
		Resource resource,
		Permission permission) throws AccessException;

	/**
	 * deletes a permission on the resource for the given entity
	 * @param entity entity to set permission for
	 * @param resource
	 * @param permission
	 */
	public void deletePermission(
		Entity entity,
		Resource resource,
		Permission permission) throws AccessException;
		
	/**
	 * creates a global permission on the resource
	 * TODO: contract for propagation to entity permissions
	 * @param resource
	 * @param permission
	 */
	public void createPermission(
		Resource resource,
		Permission permission) throws AccessException;

	/**
	 * deletes a global permission on the resource
	 * TODO: contract for propagation to entity permissions
	 * @param resource
	 * @param permission
	 */
	public void deletePermission(
		Resource resource,
		Permission permission) throws AccessException;

	/**
	 * retrieves permissions on this resource for an entity
	 * @param entity
	 * @param resource
	 * @return List
	 */
	public PermissionSet getPermissions(Entity entity, Resource resource) 
		throws AccessException;

	/**
	 * returns the global permissions on this resource
	 * @param resource
	 * @return List
	 */
	public PermissionSet getPermissions(Resource resource)
		throws AccessException;

	/**
	 * creates a resource
	 * @param resource
	 */
	public void createResource(Resource resource)
		throws AccessException;

	/**
	 * returns the resource with key resourcekey, null if not found
	 * @param resourceKey
	 * @return Resource
	 */
	public Resource getResource(String resourceKey)
		throws AccessException;

	/**
	 * list all available resources
	 * @return List
	 */
	public List listResources() throws AccessException;

	/**
	 * removes resource from this provider
	 * @param resource
	 */
	public void deleteResource(Resource resource) throws AccessException;

}
