package nl.openedge.util.baritus;

import java.util.Locale;
import java.util.regex.Pattern;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.FormBeanCtrlBase;
import nl.openedge.baritus.interceptors.Interceptor;
import nl.openedge.baritus.population.FieldPopulator;
import nl.openedge.baritus.util.MultiHashMap;
import nl.openedge.baritus.validation.FieldValidator;
import nl.openedge.baritus.validation.FormValidator;
import nl.openedge.baritus.validation.ValidationActivationRule;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * Abstract class for testing controls in a simulated Baritus/ Maverick/ webapp
 * environment, so that constructs like validators, populators etc can be tested in
 * isolation without much effort. This basecontrol keeps references to things like the
 * formBean, formBeanContext and logical view and overrides some of the propected methods
 * from Baritus with public methods in order to make testing easier.
 * 
 * @author Eelco Hillenius
 */
public abstract class AbstractBaritusTestCtrl extends FormBeanCtrlBase
{

	/**
	 * reference to the current form bean.
	 */
	protected Object bean = null;

	/**
	 * reference to the current form bean context.
	 */
	protected FormBeanContext formBeanContext = null;

	/**
	 * reference to the current logical view.
	 */
	protected String view = SUCCESS;

	/**
	 * get current test bean.
	 * 
	 * @return Object current test bean
	 */
	public Object getTestBean()
	{
		return bean;
	}

	/**
	 * get view.
	 * 
	 * @return String view
	 */
	public String getView()
	{
		return view;
	}

	/**
	 * @see nl.openedge.baritus.FormBeanBase#getLocaleForRequest(org.infohazard.maverick.flow.ControllerContext,
	 *      nl.openedge.baritus.FormBeanContext)
	 */
	@Override
	protected Locale getLocaleForRequest(final ControllerContext cctx,
			final FormBeanContext deFormBeanContext)
	{
		// hack to be able to get the formBeanContext
		this.formBeanContext = deFormBeanContext;

		return super.getLocaleForRequest(cctx, formBeanContext);
	}

	/**
	 * get formBeanContext.
	 * 
	 * @return FormBeanContext form bean context
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
	 * @param deFormBeanContext
	 *            context
	 * @return String logical name of view
	 */
	@Override
	protected String getErrorView(final ControllerContext cctx,
			final FormBeanContext deFormBeanContext)
	{
		this.view = ERROR;
		return ERROR;
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#addValidator(java.lang.String,
	 *      nl.openedge.baritus.validation.FieldValidator)
	 */
	@Override
	public void addValidator(final String fieldName, final FieldValidator validator)
	{
		super.addValidator(fieldName, validator);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#addValidator(nl.openedge.baritus.validation.FormValidator)
	 */
	@Override
	public void addValidator(final FormValidator validator)
	{
		super.addValidator(validator);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#addInterceptor(int,
	 *      nl.openedge.baritus.interceptors.Interceptor)
	 */
	@Override
	protected void addInterceptor(final int index, final Interceptor interceptor)
	{
		super.addInterceptor(index, interceptor);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#addInterceptor(nl.openedge.baritus.interceptors.Interceptor)
	 */
	@Override
	protected void addInterceptor(final Interceptor interceptor)
	{
		super.addInterceptor(interceptor);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#addPopulator(java.util.regex.Pattern,
	 *      nl.openedge.baritus.population.FieldPopulator)
	 */
	@Override
	protected void addPopulator(final Pattern pattern, final FieldPopulator populator)
	{
		super.addPopulator(pattern, populator);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#addPopulator(java.lang.String,
	 *      nl.openedge.baritus.population.FieldPopulator)
	 */
	@Override
	protected void addPopulator(final String fieldName, final FieldPopulator populator)
	{
		super.addPopulator(fieldName, populator);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#addValidationActivationRule(nl.openedge.baritus.validation.ValidationActivationRule)
	 */
	@Override
	protected void addValidationActivationRule(final ValidationActivationRule rule)
	{
		super.addValidationActivationRule(rule);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#getDefaultPopulator()
	 */
	@Override
	protected FieldPopulator getDefaultPopulator()
	{
		return super.getDefaultPopulator();
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#getValidators(java.lang.String)
	 */
	@Override
	protected MultiHashMap getValidators(final String fieldName)
	{
		return super.getValidators(fieldName);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#removeInterceptor(nl.openedge.baritus.interceptors.Interceptor)
	 */
	@Override
	protected void removeInterceptor(final Interceptor interceptor)
	{
		super.removeInterceptor(interceptor);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#removePopulator(java.util.regex.Pattern)
	 */
	@Override
	protected void removePopulator(final Pattern pattern)
	{
		super.removePopulator(pattern);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#removePopulator(java.lang.String)
	 */
	@Override
	protected void removePopulator(final String fieldName)
	{
		super.removePopulator(fieldName);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#removeValidationActivationRule(nl.openedge.baritus.validation.ValidationActivationRule)
	 */
	@Override
	protected void removeValidationActivationRule(final ValidationActivationRule rule)
	{
		super.removeValidationActivationRule(rule);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#removeValidator(nl.openedge.baritus.validation.FormValidator)
	 */
	@Override
	protected void removeValidator(final FormValidator validator)
	{
		super.removeValidator(validator);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#removeValidator(java.lang.String,
	 *      nl.openedge.baritus.validation.FieldValidator)
	 */
	@Override
	protected void removeValidator(final String fieldName, final FieldValidator validator)
	{
		super.removeValidator(fieldName, validator);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#removeValidators(java.lang.String)
	 */
	@Override
	protected void removeValidators(final String fieldName)
	{
		super.removeValidators(fieldName);
	}

	/**
	 * @see nl.openedge.baritus.FormBeanCtrlBase#setDefaultPopulator(nl.openedge.baritus.population.FieldPopulator)
	 */
	@Override
	protected void setDefaultPopulator(final FieldPopulator populator)
	{
		super.setDefaultPopulator(populator);
	}
}
