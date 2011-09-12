package nl.openedge.baritus.test;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.validation.AbstractFieldValidator;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * @author hillenius
 */
public class MockFieldValidator extends AbstractFieldValidator
{
	private String errorMessageKey = "test.key";

	/**
	 * @param cctx
	 * @param formBeanContext
	 * @param fieldName
	 * @param value
	 * @return boolean
	 * @see nl.openedge.baritus.validation.FieldValidator#isValid(org.infohazard.maverick.flow.ControllerContext,
	 *      nl.openedge.baritus.FormBeanContext, java.lang.String, java.lang.Object)
	 */
	@Override
	public boolean isValid(ControllerContext cctx, FormBeanContext formBeanContext,
			String fieldName, Object value)
	{
		boolean valid = false;

		String testVal = (String) value;

		// test runtime exception
		if ("kill".equals(testVal))
		{
			throw new RuntimeException("big mistake");
		}

		valid = "validValue".equals(testVal);

		if (!valid)
		{
			setErrorMessage(formBeanContext, fieldName, errorMessageKey, null);
		}

		return valid;

	}

}
