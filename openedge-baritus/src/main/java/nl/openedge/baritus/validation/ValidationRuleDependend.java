package nl.openedge.baritus.validation;

/**
 * FieldValidators that implement this interface can have their validation done optionaly.
 * Whether or not the actual validation will be done, depends on the outcome of the
 * registered rule
 * 
 * @author Eelco Hillenius
 */
public interface ValidationRuleDependend
{
	/**
	 * Set the validation rule.
	 * 
	 * @param rule
	 *            the rule
	 */
	public void setValidationRule(ValidationActivationRule rule);

	/**
	 * Remove the validation rule.
	 */
	public void removeValidationActivationRule();

	/**
	 * Get the registered validation rule.
	 * 
	 * @return the registered validation rule or null if none was registered
	 */
	public ValidationActivationRule getValidationActivationRule();
}
