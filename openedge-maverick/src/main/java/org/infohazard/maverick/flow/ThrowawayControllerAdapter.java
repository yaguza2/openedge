/*
 * $Id: ThrowawayControllerAdapter.java,v 1.5 2004/06/27 17:40:55 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/ThrowawayControllerAdapter.java,v $
 */
package org.infohazard.maverick.flow;

import javax.servlet.ServletException;

import org.jdom.Element;

/**
 * This adapter masquerades as a singleton controller but actually creates single-use
 * instance controllers.
 * 
 * created January 27, 2002
 * 
 * @author Jeff Schnitzer
 * @version $Revision: 1.5 $ $Date: 2004/06/27 17:40:55 $
 */
public class ThrowawayControllerAdapter implements ControllerSingleton
{
	/** class of controller. */
	protected Class< ? extends Controller> controllerClass;

	/**
	 * Create the adapter.
	 * 
	 * @param controllerClass
	 *            the controller class
	 */
	public ThrowawayControllerAdapter(Class< ? extends Controller> controllerClass)
	{
		this.controllerClass = controllerClass;
	}

	/**
	 * Currently unused.
	 * 
	 * @param controllerNode
	 * @exception ConfigException
	 */
	@Override
	public void init(Element controllerNode) throws ConfigException
	{
	}

	/**
	 * Instantiates a single-use controller, executes it, and returns the result.
	 * 
	 * @param cctx
	 *            the controller context.
	 * @return String logical view name (result of command method call of controller)
	 */
	@Override
	public String go(ControllerContext cctx) throws ServletException
	{
		try
		{
			Controller instance = this.controllerClass.newInstance();

			return instance.go(cctx);
		}
		catch (InstantiationException ex)
		{
			throw new ServletException(ex);
		}
		catch (IllegalAccessException ex)
		{
			throw new ServletException(ex);
		}
	}
}
