package nl.openedge.baritus.validation;

/**
 * Convenience class with default error message handling.
 * 
 * @author Eelco Hillenius
 */
public abstract class AbstractFieldValidator extends AbstractValidator implements FieldValidator,
		ValidationRuleDependend
{

	/**
	 * Construct emtpy.
	 */
	public AbstractFieldValidator()
	{

	}

	/**
	 * Construct with the given rule.
	 * 
	 * @param rule
	 *            activation rule
	 */
	public AbstractFieldValidator(ValidationActivationRule rule)
	{
		super(rule);
	}

	/**
	 * Get the override value. By default returns the value unchanged.
	 * 
	 * @return Object unchanged value
	 * @see nl.openedge.baritus.validation.FieldValidator#getOverrideValue(java.lang.Object)
	 */
	@Override
	public Object getOverrideValue(Object value)
	{
		return value;
	}

}
