package nl.openedge.baritus.test;

import java.util.Locale;
import java.util.regex.Pattern;

import nl.openedge.baritus.ExecutionParams;
import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.FormBeanCtrlBase;
import nl.openedge.baritus.population.IgnoreFieldPopulator;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * @author Eelco Hillenius
 */
public class MockCtrl extends FormBeanCtrlBase
{
	private MockBean bean = null;

	private FormBeanContext formBeanContext = null;

	private String view = SUCCESS;

	public MockCtrl()
	{
		ExecutionParams params = getExecutionParams(null);
		params.setIncludeSessionAttributes(true);
		params.setIncludeRequestAttributes(true);
		fixExecutionParams(params);

		addPopulator("uppercaseTest", new ToUpperCasePopulator());
		addPopulator("ignore", new IgnoreFieldPopulator());
		// block property by field name
		addPopulator(Pattern.compile("(.)*ByRegex$"), new IgnoreFieldPopulator());
		// block property by regex pattern

		addValidator("toValidate1", new MockFieldValidator());
		addValidator("toValidate2", new MockFieldValidator()); // test form
		// toValidate2[..]
		addValidator("toValidate3[0]", new MockFieldValidator()); // test form
		// toValidate3[..]

		addValidator(new MockFormValidator1());
	}

	@Override
	protected String perform(FormBeanContext formBeanContext, ControllerContext cctx)
	{
		return view;
	}

	@Override
	protected Object makeFormBean(FormBeanContext formBeanContext, ControllerContext cctx)
	{
		this.bean = new MockBean();
		return bean;
	}

	/**
	 * get test bean
	 * 
	 * @return TestBean instance of test bean
	 */
	public MockBean getTestBean()
	{
		return bean;
	}

	/**
	 * get view
	 * 
	 * @return String name of view
	 */
	public String getView()
	{
		return view;
	}

	@Override
	protected Locale getLocaleForRequest(ControllerContext cctx, FormBeanContext formBeanContext)
	{
		// hack to be able to get the formBeanContext
		this.formBeanContext = formBeanContext;

		return super.getLocaleForRequest(cctx, formBeanContext);
	}

	/**
	 * get formBeanContext
	 * 
	 * @return FormBeanContext
	 */
	public FormBeanContext getFormBeanContext()
	{
		return formBeanContext;
	}

	/**
	 * Get error view. This is 'error' by default.
	 * 
	 * @param cctx
	 *            controller context
	 * @param formBeanContext
	 *            context
	 * @return String logical name of view
	 */
	@Override
	protected String getErrorView(ControllerContext cctx, FormBeanContext formBeanContext)
	{
		this.view = ERROR;
		return ERROR;
	}
}
