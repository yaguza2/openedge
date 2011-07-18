package nl.openedge.modules.impl.menumodule;

/**
 * Marker interface for request scope filters.
 * 
 * @author Eelco Hillenius
 */
public interface RequestScopeMenuFilter extends MenuFilter
{
	/**
	 * Context key for the http servlet request. The servlet/ command is responsible for
	 * setting the variable. value = "_request".
	 */
	String CONTEXT_KEY_REQUEST = "_request";
}
