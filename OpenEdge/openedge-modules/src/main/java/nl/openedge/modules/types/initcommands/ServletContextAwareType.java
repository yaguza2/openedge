package nl.openedge.modules.types.initcommands;

import javax.servlet.ServletContext;

import nl.openedge.modules.config.ConfigException;

/**
 * Component that wants to know about the current servlet context. Note that this can be
 * null (and always is outside a servlet container).
 * 
 * @author Eelco Hillenius
 */
public interface ServletContextAwareType
{
	/**
	 * set the servlet context. Note that this can be null depending on how the framework
	 * was instantiated.
	 * 
	 * @param servletContext
	 *            the servlet context
	 * @throws ConfigException
	 *             when an configuration error occurs
	 */
	void setServletContext(ServletContext servletContext) throws ConfigException;
}
