package nl.openedge.baritus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.openedge.baritus.util.MultiHashMap;
import nl.openedge.baritus.validation.FieldValidator;
import nl.openedge.baritus.validation.FormValidator;
import nl.openedge.baritus.validation.ValidationActivationRule;
import nl.openedge.baritus.validation.ValidationRuleDependend;
import ognl.Ognl;

import org.infohazard.maverick.flow.ControllerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultValidatorDelegate implements ValidatorDelegate
{
	private ValidatorRegistry validatorRegistry = null;

	private FormBeanCtrlBase ctrl = null;

	private static Logger populationLog = LoggerFactory.getLogger(LogConstants.POPULATION_LOG);

	private static char[] BREAKSYMBOLS = new char[] {'[', '('};

	public DefaultValidatorDelegate(ValidatorRegistry validatorRegistry, FormBeanCtrlBase ctrl)
	{
		this.validatorRegistry = validatorRegistry;
		this.ctrl = ctrl;
	}

	@Override
	public boolean doValidation(ControllerContext cctx, FormBeanContext formBeanContext,
			ExecutionParams execParams, Map<String, Object> parameters, boolean succeeded)
	{
		if (parameters == null)
			return succeeded;

		MultiHashMap fieldValidators = validatorRegistry.getFieldValidators();
		List<FormValidator> formValidators = validatorRegistry.getFormValidators();
		List<ValidationActivationRule> globalValidatorActivationRules =
			validatorRegistry.getGlobalValidatorActivationRules();

		if ((fieldValidators != null && (!fieldValidators.isEmpty()))
			|| (formValidators != null && (!formValidators.isEmpty())))
		{

			boolean doCustomValidation = true;
			// see if there's any globally (form level) defined rules
			if (globalValidatorActivationRules != null
				&& (!globalValidatorActivationRules.isEmpty()))
			{
				for (Iterator<ValidationActivationRule> i =
					globalValidatorActivationRules.iterator(); i.hasNext();)
				{
					ValidationActivationRule rule = i.next();
					doCustomValidation = rule.allowValidation(cctx, formBeanContext); // fire
																						// rule
					if (!doCustomValidation)
						break;
				}
			}

			if (doCustomValidation)
			{
				// if fieldValidators were registered
				if (fieldValidators != null && (!fieldValidators.isEmpty()))
				{
					Iterator<String> names = parameters.keySet().iterator(); // loop
																				// through
																				// the
																				// properties
					while (names.hasNext())
					{
						String name = names.next();
						if (name == null)
							continue;
						if (formBeanContext.getOverrideField(name) == null)
						// see if there allready was an override registered
						{
							succeeded =
								doValidationForOneField(fieldValidators, cctx, formBeanContext,
									succeeded, name);
						} // else an error allready occured; do not validate
					}
				}
				// if we are still successful so far, check with the form level validators
				if ((succeeded || execParams.isDoFormValidationIfFieldValidationFailed())
					&& (formValidators != null))
				{
					// check all registered until either all fired successfully or
					// one did not fire succesfully
					for (Iterator<FormValidator> i = formValidators.iterator(); i.hasNext();)
					{
						FormValidator fValidator = i.next();
						succeeded =
							doFormValidationForOneValidator(cctx, formBeanContext, fValidator,
								succeeded);
					}
				}
			}
		}

		return succeeded;
	}

	/* execute validation for one form validator */
	private boolean doFormValidationForOneValidator(ControllerContext cctx,
			FormBeanContext formBeanContext, FormValidator fValidator, boolean succeeded)
	{

		boolean success = true;
		try
		{
			boolean validateForm = true;
			if (fValidator instanceof ValidationRuleDependend)
			{
				ValidationActivationRule fRule =
					((ValidationRuleDependend) fValidator).getValidationActivationRule();
				if (fRule != null)
				{
					if (!fRule.allowValidation(cctx, formBeanContext))
					{
						validateForm = false;
					}

					if (populationLog.isDebugEnabled())
					{
						populationLog.debug("rule " + fRule
							+ ((validateForm) ? " ALLOWS" : " DISALLOWS") + " validation with "
							+ fValidator);
					}
				}
			}
			if (validateForm)
			{
				success = fValidator.isValid(cctx, formBeanContext);

				if (populationLog.isDebugEnabled())
				{
					populationLog.debug("validation" + ((success) ? " PASSED" : " FAILED")
						+ " using validator " + fValidator);
				}
			}
		}
		catch (Exception e)
		{
			success = false;
			String msg = "validator " + fValidator + " threw exception: " + e.getMessage();
			populationLog.error(msg);
			populationLog.error(e.getMessage(), e);
		}

		if (!success)
			succeeded = false;

		return succeeded;
	}

	/* execute validation for one field */
	private boolean doValidationForOneField(MultiHashMap fieldValidators, ControllerContext cctx,
			FormBeanContext formBeanContext, boolean succeeded, String name)
	{
		Collection<FieldValidator> propertyValidators =
			getFieldValidatorsForField(name, fieldValidators);

		// these are the fieldValidators for one property
		if (propertyValidators != null)
		{
			try
			{
				succeeded =
					doValidationForOneField(cctx, formBeanContext, succeeded, name,
						propertyValidators);
			}
			catch (Exception e)
			{
				succeeded = false;
				populationLog.error(e.getMessage(), e);
			}
		}

		return succeeded;
	}

	/* Get the validators for a field, possibly null. */
	private List<FieldValidator> getFieldValidatorsForField(String name,
			MultiHashMap fieldValidators)
	{
		List<FieldValidator> propertyValidators = null;
		propertyValidators =
			getFieldValidatorsForFieldRecursively(name, fieldValidators, propertyValidators);
		return propertyValidators;
	}

	/*
	 * Get the validators for a field, null if none found. work our way back to simple
	 * property name e.g., take complex (bogus) case 'myproperty('key1')[1]('key2')[2]',
	 * we should be able to look for registered validators with: -
	 * myproperty['key1'][1]['key2'][2] - myproperty['key1'][1]['key2'] -
	 * myproperty['key1'][1] - myproperty['key1'] - myproperty
	 */
	private List<FieldValidator> getFieldValidatorsForFieldRecursively(String currentName,
			MultiHashMap fieldValidators, List<FieldValidator> propertyValidators)
	{
		@SuppressWarnings("unchecked")
		List<FieldValidator> validators = (List<FieldValidator>) fieldValidators.get(currentName);
		if (validators != null)
		{
			if (propertyValidators == null)
				propertyValidators = new ArrayList<FieldValidator>();
			propertyValidators.addAll(validators);
		}

		int delim = 0;
		for (int i = 0; i < BREAKSYMBOLS.length; i++)
		{
			int ix = currentName.lastIndexOf(BREAKSYMBOLS[i]);
			if (ix > -1)
			{
				delim = ix;
				break;
			}
		}

		if (delim > 0)
		{
			// just cut off wihout further checking
			currentName = currentName.substring(0, delim);
			propertyValidators =
				getFieldValidatorsForFieldRecursively(currentName, fieldValidators,
					propertyValidators);
		}

		return propertyValidators;
	}

	/* handle the custom validation for one field */
	private boolean doValidationForOneField(ControllerContext cctx,
			FormBeanContext formBeanContext, boolean succeeded, String name,
			Collection<FieldValidator> propertyValidators) throws Exception
	{
		// get target value;
		// this could be done a bit more efficient, as we allready had
		// the (converted) value when populating. Working more efficient
		// (like with a converted value cache) would make the API of
		// populators less straightforward, and by getting the property
		// from the bean instead of using the converted value, we are
		// sure that we get the property the proper (java beans) way.
		Object value = Ognl.getValue(name, formBeanContext.getBean());

		// for all validators for this field
		for (Iterator<FieldValidator> j = propertyValidators.iterator(); j.hasNext();)
		{
			FieldValidator validator = j.next();
			boolean validateField = true;

			if (validator instanceof ValidationRuleDependend) // should we execute rule
			{
				ValidationActivationRule rule =
					((ValidationRuleDependend) validator).getValidationActivationRule();
				if (rule != null)
				{
					validateField = rule.allowValidation(cctx, formBeanContext); // test

					if (populationLog.isDebugEnabled())
					{
						populationLog.debug(name + ": rule " + rule
							+ ((validateField) ? " ALLOWS" : " DISALLOWS") + " validation with "
							+ validator);
					}
				}
			}

			if (validateField)
			{
				// execute validation method
				boolean success;
				try
				{
					success = validator.isValid(cctx, formBeanContext, name, value);
				}
				catch (Exception e)
				{
					String msg =
						"validator " + validator + " threw exception: " + e.getMessage()
							+ " on property " + name + " with value " + value;
					populationLog.error(msg);
					throw e;
				}

				if (populationLog.isDebugEnabled())
				{
					populationLog.debug("validation" + ((success) ? " PASSED" : " FAILED")
						+ " for field " + name + " using validator " + validator);
				}

				if (!success)
				{
					succeeded = false;
					ctrl.setOverrideField(cctx, formBeanContext, name, value, null, validator);
					break;
				}
			}
		}
		return succeeded;
	}

	/**
	 * @return char[]
	 */
	public static char[] getBREAKSYMBOLS()
	{
		return BREAKSYMBOLS;
	}

	/**
	 * @param cs
	 */
	public static void setBREAKSYMBOLS(char[] cs)
	{
		BREAKSYMBOLS = cs;
	}

}
