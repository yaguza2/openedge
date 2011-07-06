/*
 * $Id: TrivialViewFactory.java,v 1.5 2002/06/19 03:27:18 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/view/TrivialViewFactory.java,v $
 */

package org.infohazard.maverick.view;

import org.infohazard.maverick.flow.*;
import java.io.IOException;
import java.io.Reader;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.xml.transform.Source;
import org.jdom.Element;
import org.w3c.dom.Node;


/**
 * <p>Creates simple views which expect the model itself to be the
 * source of content.  Controllers which use a trivial view should
 * provide a model which is one of:</p>
 *
 * <ul>
 *   <li>java.lang.String</li>
 *   <li>java.io.Reader</li>
 *   <li>javax.xml.transform.Source</li>
 *   <li>org.w3c.dom.Node</li>
 * </ul>
 *
 * <p>The model will be rendered to the output stream (or transform) unmolested.</p>
 *
 * <p>Trivial views can have transforms.  Neither the view elements nor
 * the factory elements have any additional parameters.</p>
 *
 * @author Jeff Schnitzer
 */
public class TrivialViewFactory implements ViewFactory
{
	/**
	 */
	public void init(Element factoryNode, ServletConfig servletCfg) throws ConfigException
	{
		// Do nothing
	}

	/**
	 */
	public View createView(Element viewNode) throws ConfigException
	{
		return new TrivialView();
	}
	
	/**
	 */
	protected static class TrivialView implements View
	{
		/**
		 */
		public void go(ViewContext vctx) throws IOException, ServletException
		{
			TransformStep next = vctx.getNextStep();
			
			if (vctx.getModel() == null)
			{
				// Nothing
			}
			else if (vctx.getModel() instanceof String)
			{
				next.go((String)vctx.getModel());
			}
			else if (vctx.getModel() instanceof Reader)
			{
				next.go((Reader)vctx.getModel());
			}
			else if (vctx.getModel() instanceof Source)
			{
				next.go((Source)vctx.getModel());
			}
			else if (vctx.getModel() instanceof Node)
			{
				Source src = new javax.xml.transform.dom.DOMSource((Node)vctx.getModel());
				next.go(src);
			}
			else
			{
				throw new ServletException("TrivialView does not understand a model of type "
						 + vctx.getModel().getClass().getName() + ", only String, "
						 + "Reader, javax.xml.transform.Source, and org.w3c.dom.Node");
			}
		}
	}
}