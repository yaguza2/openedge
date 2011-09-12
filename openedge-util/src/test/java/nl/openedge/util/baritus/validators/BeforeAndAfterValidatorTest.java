package nl.openedge.util.baritus.validators;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.validation.FieldValidator;
import nl.openedge.util.YearMonthDayHelper;
import nl.openedge.util.baritus.BaritusControlTest;

import org.infohazard.maverick.flow.MaverickContext;

/**
 * Unit test for BeforeValidator and AfterValidator.
 * 
 * @author Eelco Hillenius
 */
public class BeforeAndAfterValidatorTest extends BaritusControlTest
{

	// ----------------- TESTS FOR AFTER VALIDATOR ---------------------------

	/**
	 * Check valid on day diff.
	 */
	public void testBeforeBeforeOnDay()
	{
		Date toCheck = YearMonthDayHelper.getDate(2003, 06, 10);
		FieldValidator validator = new BeforeValidator(toCheck);
		testFieldValidationError("testDate", "11-06-2003", validator);
	}

	/**
	 * Check valid on month diff.
	 */
	public void testValidBeforeOnMonth()
	{
		Date toCheck = YearMonthDayHelper.getDate(2003, 06, 10);
		FieldValidator validator = new BeforeValidator(toCheck);
		testFieldValidationError("testDate", "10-07-2003", validator);
	}

	/**
	 * Check valid on year diff.
	 */
	public void testValidBeforeOnYear()
	{
		Date toCheck = YearMonthDayHelper.getDate(2003, 06, 10);
		FieldValidator validator = new BeforeValidator(toCheck);
		testFieldValidationError("testDate", "10-06-2005", validator);
	}

	/**
	 * Check not valid on day diff.
	 */
	public void testInValidBeforeOnDay()
	{
		Date toCheck = YearMonthDayHelper.getDate(2003, 06, 10);
		FieldValidator validator = new BeforeValidator(toCheck);
		testFieldValidationSuccess("testDate", "9-06-2003", validator);
	}

	/**
	 * Check not valid on month diff.
	 */
	public void testInValidBeforeOnMonth()
	{
		Date toCheck = YearMonthDayHelper.getDate(2003, 06, 10);
		FieldValidator validator = new BeforeValidator(toCheck);
		testFieldValidationSuccess("testDate", "10-05-2003", validator);
	}

	/**
	 * Check not valid on year diff.
	 */
	public void testInValidBeforeOnYear()
	{
		Date toCheck = YearMonthDayHelper.getDate(2003, 06, 10);
		FieldValidator validator = new BeforeValidator(toCheck);
		testFieldValidationSuccess("testDate", "10-06-2001", validator);
	}

	// ----------------- TESTS FOR AFTER VALIDATOR ---------------------------

	/**
	 * Check valid on day diff.
	 */
	public void testValidAfterOnDay()
	{
		Date toCheck = YearMonthDayHelper.getDate(2003, 06, 10);
		FieldValidator validator = new AfterValidator(toCheck);
		testFieldValidationSuccess("testDate", "11-06-2003", validator);
	}

	/**
	 * Check valid on month diff.
	 */
	public void testValidAfterOnMonth()
	{
		Date toCheck = YearMonthDayHelper.getDate(2003, 06, 10);
		FieldValidator validator = new AfterValidator(toCheck);
		testFieldValidationSuccess("testDate", "10-07-2003", validator);
	}

	/**
	 * Check valid on year diff.
	 */
	public void testValidAfterOnYear()
	{
		Date toCheck = YearMonthDayHelper.getDate(2003, 06, 10);
		FieldValidator validator = new AfterValidator(toCheck);
		testFieldValidationSuccess("testDate", "10-06-2005", validator);
	}

	/**
	 * Check not valid on day diff.
	 */
	public void testInValidAfterOnDay()
	{
		Date toCheck = YearMonthDayHelper.getDate(2003, 06, 10);
		FieldValidator validator = new AfterValidator(toCheck);
		testFieldValidationError("testDate", "9-06-2003", validator);
	}

	/**
	 * Check not valid on month diff.
	 */
	public void testInValidAfterOnMonth()
	{
		Date toCheck = YearMonthDayHelper.getDate(2003, 06, 10);
		FieldValidator validator = new AfterValidator(toCheck);
		testFieldValidationError("testDate", "10-05-2003", validator);
	}

	/**
	 * Check not valid on year diff.
	 */
	public void testInValidAfterOnYear()
	{
		Date toCheck = YearMonthDayHelper.getDate(2003, 06, 10);
		FieldValidator validator = new AfterValidator(toCheck);
		testFieldValidationError("testDate", "10-06-2001", validator);
	}

	/**
	 * Test whether validation succeeded.
	 * 
	 * @param fieldname
	 *            field name
	 * @param testValue
	 *            string value test
	 * @param validator
	 *            field validator
	 */
	protected void testFieldValidationSuccess(final String fieldname, final String testValue,
			final FieldValidator validator)
	{
		TestCtrl ctrl = new TestCtrl();
		ctrl.addValidator(fieldname, validator);
		Map requestParams = new HashMap();
		requestParams.put(fieldname, testValue);
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.init(null);
			ctrl.go(mockMavCtx);
			FormBeanContext formBeanContext = ctrl.getFormBeanContext();
			assertNull(
				"no errors should be registered; error == " + formBeanContext.getError(fieldname),
				formBeanContext.getError(fieldname));
			assertEquals("success", ctrl.getView());
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Test whether validation failed.
	 * 
	 * @param fieldname
	 *            field name
	 * @param testValue
	 *            string value test
	 * @param validator
	 *            field validator
	 */
	protected void testFieldValidationError(final String fieldname, final String testValue,
			final FieldValidator validator)
	{
		TestCtrl ctrl = new TestCtrl();
		ctrl.addValidator(fieldname, validator);
		Map requestParams = new HashMap();
		requestParams.put(fieldname, testValue);
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.init(null);
			ctrl.go(mockMavCtx);
			FormBeanContext formBeanContext = ctrl.getFormBeanContext();
			assertNotNull("an error should be registered", formBeanContext.getError(fieldname));
			assertEquals("error", ctrl.getView());
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
