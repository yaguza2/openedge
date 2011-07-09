/*
 * $Id: RedirectViewFactory.java,v 1.4 2002/06/06 12:23:56 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/view/RedirectViewFactory.java,v $
 */

package org.infohazard.maverick.view;

import javax.servlet.ServletConfig;

import org.infohazard.maverick.flow.ConfigException;
import org.infohazard.maverick.flow.View;
import org.infohazard.maverick.flow.ViewFactory;
import org.infohazard.maverick.util.XML;
import org.jdom.Element;

/**
 * <p>
 * Creates views which result in HTTP redirects.
 * </p>
 * 
 * <p>
 * Views will handle the model in the following way:
 * </p>
 * 
 * <ul>
 * <li>
 * If the model is a String, that is used as the base URL and the path attribute is
 * ignored.</li>
 * <li>
 * If the model is a Map, the key/value pairs are converted into parameters for the target
 * URL. This behavior is deprecated; you should use ControllerContext.setParam() instead.</li>
 * </ul>
 * 
 * <p>
 * Params set on the ControllerContext will become query parameters.
 * </p>
 * 
 * <p>
 * Redirect views cannot have transforms and have no attributes other than "path".
 * </p>
 */
public class RedirectViewFactory implements ViewFactory
{
	/**
	 */
	public void init(Element factoryNode, ServletConfig servletCfg) throws ConfigException
	{
		// Nothing to do.
	}

	/**
	 */
	public View createView(Element viewNode) throws ConfigException
	{
		String path = XML.getValue(viewNode, "path");

		// If it doesn't exist, the path must be set by the model().
		if (path == null)
			path = "";

		return new RedirectView(path);
	}
}
