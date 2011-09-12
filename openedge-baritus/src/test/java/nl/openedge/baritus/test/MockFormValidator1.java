package nl.openedge.baritus.test;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.validation.AbstractFormValidator;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * @author hillenius
 */
public class MockFormValidator1 extends AbstractFormValidator
{
	/**
	 * @param cctx
	 * @param formBeanContext
	 * @return
	 * @see nl.openedge.baritus.validation.FormValidator#isValid(org.infohazard.maverick.flow.ControllerContext,
	 *      nl.openedge.baritus.FormBeanContext)
	 */
	@Override
	public boolean isValid(ControllerContext cctx, FormBeanContext formBeanContext)
	{
		boolean valid = true;
		MockBean bean = (MockBean) formBeanContext.getBean();

		// test runtime exception
		if (bean.getToValidate4().equals("kill"))
		{
			throw new RuntimeException("big mistake");
		}

		valid = bean.getToValidate4().equals("validValue");
		if (!valid)
		{
			formBeanContext.setError("toValidate4", "wrong input");
		}

		return valid;
	}

}
