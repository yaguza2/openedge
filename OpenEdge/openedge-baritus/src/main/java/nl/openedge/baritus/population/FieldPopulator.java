package nl.openedge.baritus.population;

import nl.openedge.baritus.FormBeanContext;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * A field populator knows how to populate properties from (request) parameters. Users can
 * implement this interface to override the default population behaviour and register
 * instances with field names.
 * 
 * @author Eelco Hillenius
 */
public interface FieldPopulator
{
	/**
	 * set a property on the given form
	 * 
	 * @param cctx
	 *            maverick context
	 * @param formBeanContext
	 *            context with instance of the form to set the property on
	 * @param name
	 *            name of the property
	 * @param value
	 *            unconverted value to set
	 * @return boolean true if the property was set successfully, false otherwise
	 * @throws Exception
	 */
	public boolean setProperty(ControllerContext cctx, FormBeanContext formBeanContext,
			String name, Object value) throws Exception;

}
