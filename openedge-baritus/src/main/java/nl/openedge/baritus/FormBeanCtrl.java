package nl.openedge.baritus;

import org.infohazard.maverick.flow.ConfigException;
import org.infohazard.maverick.flow.ControllerSingleton;
import org.jdom.Element;

/**
 * Singleton implementation.
 * 
 * @author Eelco Hillenius
 */
public abstract class FormBeanCtrl extends FormBeanCtrlBase implements ControllerSingleton
{

	/**
	 * Guaranteed to be called once with the XML configuration of the controller from the
	 * master config file.
	 * 
	 * @see org.infohazard.maverick.flow.ControllerSingleton#init(org.jdom.Element)
	 * @param controllerNode
	 * @throws ConfigException
	 */
	@Override
	public void init(Element controllerNode) throws ConfigException
	{
		// initialise here...
	}

}
