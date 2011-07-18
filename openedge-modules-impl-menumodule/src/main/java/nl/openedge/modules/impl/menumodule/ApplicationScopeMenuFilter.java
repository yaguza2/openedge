package nl.openedge.modules.impl.menumodule;

/**
 * Marker interface for application scope filters.
 * 
 * @author Eelco Hillenius
 */
public interface ApplicationScopeMenuFilter extends MenuFilter
{
	/** context key for configuration document, value = 'xml_config'. */
	String CONTEXT_KEY_CONFIGURATION = "xml_config";
}
