package nl.openedge.util.baritus.validators;

import nl.openedge.baritus.ExecutionParams;
import nl.openedge.baritus.FormBeanContext;
import nl.openedge.util.baritus.AbstractBaritusTestCtrl;

import org.infohazard.maverick.flow.ConfigException;
import org.infohazard.maverick.flow.ControllerContext;
import org.jdom.Element;

/**
 * Dummy control for testing.
 * 
 * @author Eelco Hillenius
 */
public class TestCtrl extends AbstractBaritusTestCtrl
{
	@Override
	protected String perform(final FormBeanContext deFormBeanContext, final ControllerContext cctx)
	{
		return view;
	}

	@Override
	protected Object makeFormBean(final FormBeanContext deFormBeanContext,
			final ControllerContext cctx)
	{
		this.bean = new MockBean();
		return bean;
	}

	public void init(final Element controllerNode) throws ConfigException
	{
		ExecutionParams params = getExecutionParams(null);
		params.setIncludeSessionAttributes(true);
		params.setIncludeRequestAttributes(true);
	}
}
