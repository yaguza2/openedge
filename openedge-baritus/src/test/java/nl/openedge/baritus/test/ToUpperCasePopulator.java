package nl.openedge.baritus.test;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.population.AbstractFieldPopulator;
import nl.openedge.baritus.util.ValueUtils;
import ognl.Ognl;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * simple test populator that converts input to a uppercase
 * 
 * @author Eelco Hillenius
 */
public class ToUpperCasePopulator extends AbstractFieldPopulator
{
	@Override
	public boolean setProperty(ControllerContext cctx, FormBeanContext formBeanContext,
			String name, Object value) throws Exception
	{

		if (value != null)
		{
			String val = ValueUtils.convertToString(value);
			val = val.toUpperCase();

			Ognl.setValue(name, formBeanContext.getBean(), val);
		}

		return true;
	}
}
