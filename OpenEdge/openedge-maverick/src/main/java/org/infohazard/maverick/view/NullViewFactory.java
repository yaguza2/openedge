/*
 * $Id: NullViewFactory.java,v 1.2 2002/06/06 12:23:56 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/view/NullViewFactory.java,v $
 */

package org.infohazard.maverick.view;

import javax.servlet.ServletConfig;

import org.infohazard.maverick.ViewDefinition;
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
	@Override
	public void init(Element factoryNode, ServletConfig servletCfg)
	{
	}

	/**
	 */
	@Override
	public View createView(Element viewNode)
	{
		return singletonNull;
	}

	/**
	 * Simple implementation does nothing.
	 */
	public static class NullView implements View
	{
		/** Nothing! */
		@Override
		public void go(ViewContext vctx)
		{
		}

		@Override
		public ViewDefinition getViewDefinition()
		{
			return null;
		}
	}
}
