package nl.openedge.util.maverick;

import javax.servlet.ServletConfig;

import org.infohazard.maverick.flow.AbstractControllerFactory;
import org.infohazard.maverick.flow.ConfigException;
import org.infohazard.maverick.flow.Controller;
import org.infohazard.maverick.util.XML;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom implementation of the controller factory that, in case attribute
 * 'allways-reload' is true, creates and initializes a new instance of controllers even if
 * they are of type ControllerSingleton. This can be usefull when testing controllers; eg
 * when you make changes in the init method, you want these changes be loaded while in a
 * production environment, you want the initialization just done once.
 * <p>
 * Configure like:
 * 
 * <pre>
 * 
 *  &lt;maverick version=&quot;2.0&quot;&gt;
 *   &lt;modules&gt;
 *     &lt;controller-factory 
 * 			provider=&quot;nl.openedge.util.maverick.CustomControllerFactory&quot;&gt;
 *       &lt;allways-reload value=&quot;true&quot;/&gt;
 *     &lt;/controller-factory&gt;
 *   &lt;/modules&gt; 
 *   ...
 *  &lt;/maverick&gt;
 * 
 * </pre>
 * 
 * </p>
 * 
 * @author Eelco Hillenius
 */
public class AllwaysReloadControllerFactory extends AbstractControllerFactory
{
	/**
	 * Switch attribute that states if a controll should allways be recreated on a call
	 * (even if it is a controller singleton), value = 'allways-reload'.
	 */
	public static final String ATTRIB_ALLWAYS_RELOAD = "allways-reload";

	/** log. */
	private static Logger log = LoggerFactory.getLogger(AllwaysReloadControllerFactory.class);

	/** whether controllers should allways be reloaded, regardless of their types. */
	private boolean allwaysReload = false;

	/**
	 * @see org.infohazard.maverick.flow.ControllerFactory#init(org.jdom.Element,
	 *      javax.servlet.ServletConfig)
	 */
	@Override
	public void init(Element factoryNode, ServletConfig servletCfg)
	{
		String allwaysReloadS = XML.getValue(factoryNode, ATTRIB_ALLWAYS_RELOAD);
		if (allwaysReloadS != null)
		{
			allwaysReload = Boolean.valueOf(allwaysReloadS).booleanValue();
			log.info("allwaysReload set to " + allwaysReload);
		}
		else
		{
			log.info("attribute " + ATTRIB_ALLWAYS_RELOAD + " not set");
		}
	}

	/**
	 * @see org.infohazard.maverick.flow.AbstractControllerFactory#decorateController(org.jdom.Element,
	 *      org.infohazard.maverick.flow.Controller)
	 */
	@Override
	protected Controller decorateController(Element controllerNode, Controller controllerToDecorate)
			throws ConfigException
	{
		Controller controller = controllerToDecorate;
		if (allwaysReload)
		{
			controller = new AllwaysReloadControllerAdapter(controller.getClass());
		}
		// let the framework do any additional decorating (ControllerWithParameters)
		controller = super.decorateController(controllerNode, controller);

		return controller;
	}
}
