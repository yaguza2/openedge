package nl.openedge.util.maverick;

import javax.servlet.ServletException;

import org.infohazard.maverick.flow.Controller;
import org.infohazard.maverick.flow.ControllerContext;
import org.infohazard.maverick.flow.ControllerSingleton;
import org.jdom.Element;

/**
 * This adapter masquerades as a singleton controller but actually creates single-use
 * instance controllers AND initializes the controllers if they are of type
 * ControllerSingleton.
 * 
 * @author Eelco Hillenius
 */
public final class AllwaysReloadControllerAdapter implements ControllerSingleton
{
	/** class of controller. */
	private Class< ? > controllerClass;

	/** reference to the xml node of the controller. */
	private Element controllerNode;

	/**
	 * Create the adapter.
	 * 
	 * @param controllerClass
	 *            the controller class
	 */
	public AllwaysReloadControllerAdapter(Class< ? > controllerClass)
	{
		this.controllerClass = controllerClass;
	}

	/**
	 * Save the reference to the controller node so we can use it later on.
	 * 
	 * @see org.infohazard.maverick.flow.ControllerSingleton#init(org.jdom.Element)
	 */
	@Override
	public void init(Element configControllerNode)
	{
		this.controllerNode = configControllerNode;
	}

	/**
	 * Instantiates a single-use controller, executes it, and returns the result. If the
	 * controller is of type ControllerSingleton, it is initialized with the saved
	 * reference of the xml node of the controller first.
	 * 
	 * @param cctx
	 *            the controller context.
	 * @return String logical view name (result of command method call of controller)
	 * @throws ServletException
	 *             when the decorated controller threw a servlet exception
	 */
	@Override
	public String go(ControllerContext cctx) throws ServletException
	{
		try
		{
			Controller instance = (Controller) this.controllerClass.newInstance();
			if (instance instanceof ControllerSingleton)
			{
				((ControllerSingleton) instance).init(controllerNode);
			}
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
