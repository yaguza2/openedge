package nl.openedge.baritus;

import java.util.ArrayList;
import java.util.List;

import nl.openedge.baritus.util.MultiHashMap;
import nl.openedge.baritus.validation.FieldValidator;
import nl.openedge.baritus.validation.FormValidator;
import nl.openedge.baritus.validation.ValidationActivationRule;

/**
 * Registry for populators. Each instance of FormBeanBase has its own instance.
 * 
 * @author Eelco Hillenius
 */
public final class ValidatorRegistry
{
	private MultiHashMap fieldValidators = null;

	private List<FormValidator> formValidators = null;

	private List<ValidationActivationRule> globalValidatorActivationRules = null;

	/**
	 * register a field validator for the given fieldName. multiple fieldValidators for
	 * one key are allowed.
	 * 
	 * @param fieldName
	 *            name of field
	 * @param validator
	 *            validator instance
	 */
	public void addValidator(String fieldName, FieldValidator validator)
	{
		if (fieldValidators == null)
		{
			fieldValidators = new MultiHashMap();
		}
		fieldValidators.put(fieldName, validator);
	}

	/**
	 * register a form validator.
	 * 
	 * @param validator
	 *            the form level validator
	 */
	public void addValidator(FormValidator validator)
	{
		if (formValidators == null)
		{
			formValidators = new ArrayList<FormValidator>();
		}
		formValidators.add(validator);
	}

	/**
	 * de-register the fieldValidators that were registered with the given fieldName
	 * 
	 * @param fieldName
	 *            name of field
	 */
	public void removeValidators(String fieldName)
	{
		if (fieldValidators != null)
		{
			fieldValidators.remove(fieldName);
		}
	}

	/**
	 * de-register the given validator that was registered with the given fieldName
	 * 
	 * @param fieldName
	 *            name of field
	 * @param validator
	 *            the validator to remove for the given field
	 */
	public void removeValidator(String fieldName, FieldValidator validator)
	{
		if (fieldValidators != null)
		{
			fieldValidators.remove(fieldName, validator);
		}
	}

	/**
	 * de-register the given form level validator
	 * 
	 * @param validator
	 *            form validator
	 */
	public void removeValidator(FormValidator validator)
	{
		if (formValidators != null)
		{
			formValidators.remove(validator);
		}
	}

	/**
	 * register the rule for the whole form
	 * 
	 * @param rule
	 *            validator activation rule
	 */
	public void addValidationActivationRule(ValidationActivationRule rule)
	{
		if (globalValidatorActivationRules == null)
		{
			globalValidatorActivationRules = new ArrayList<ValidationActivationRule>();
		}
		globalValidatorActivationRules.add(rule);
	}

	/**
	 * de-register the given rule for the whole form
	 * 
	 * @param rule
	 *            validator activation rule
	 */
	public void removeValidationActivationRule(ValidationActivationRule rule)
	{
		if (globalValidatorActivationRules != null)
		{
			globalValidatorActivationRules.remove(rule);
			if (globalValidatorActivationRules.isEmpty())
				globalValidatorActivationRules = null;
		}
	}

	/**
	 * get the fieldValidators that were registered with the given fieldName
	 * 
	 * @param fieldName
	 *            name of the field
	 * @return FieldValidator the instance of FieldValidator that was registered with the
	 *         given fieldName or null if none was registered with that name
	 */
	public MultiHashMap getValidators(String fieldName)
	{
		return (fieldValidators != null) ? (MultiHashMap) fieldValidators.get(fieldName) : null;
	}

	/**
	 * get all global activation rules
	 * 
	 * @return List all global activation rules
	 */
	public List<ValidationActivationRule> getGlobalValidatorActivationRules()
	{
		return globalValidatorActivationRules;
	}

	/**
	 * get all form validators
	 * 
	 * @return List all form validators
	 */
	public List<FormValidator> getFormValidators()
	{
		return formValidators;
	}

	/**
	 * get all field validators
	 * 
	 * @return get all field validators
	 */
	public MultiHashMap getFieldValidators()
	{
		return fieldValidators;
	}

}
