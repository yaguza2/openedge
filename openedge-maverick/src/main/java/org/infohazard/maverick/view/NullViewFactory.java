/*
 * $Id: NullViewFactory.java,v 1.2 2002/06/06 12:23:56 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/view/NullViewFactory.java,v $
 */

package org.infohazard.maverick.view;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.infohazard.maverick.flow.ConfigException;
import org.infohazard.maverick.flow.View;
import org.infohazard.maverick.flow.ViewContext;
import org.infohazard.maverick.flow.ViewFactory;
import org.jdom.Element;

/**
 * <p>
 * Creates a view which does absolutely nothing. The Controller is assumed to have
 * manually written all data to the response. The model is ignored.
 * </p>
 * 
 * <p>
 * Null views have no extra attributes and cannot have transforms.
 * </p>
 */
public class NullViewFactory implements ViewFactory
{
	/**
	 */
	static View singletonNull = new NullView();

	/**
	 */
	public void init(Element factoryNode, ServletConfig servletCfg) throws ConfigException
	{
	}

	/**
	 */
	public View createView(Element viewNode) throws ConfigException
	{
		return singletonNull;
	}

	/**
	 * Simple implementation does nothing.
	 */
	public static class NullView implements View
	{
		/** Nothing! */
		public void go(ViewContext vctx) throws IOException, ServletException
		{
		}
	}
}
