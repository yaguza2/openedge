package nl.openedge.baritus.validation;

/**
 * Convenience class with default error message handling.
 * 
 * @author Eelco Hillenius
 */
public abstract class AbstractFormValidator extends AbstractValidator implements FormValidator,
		ValidationRuleDependend
{

	/**
	 * Construct emtpy.
	 */
	public AbstractFormValidator()
	{
		super();
	}

	/**
	 * Construct with the given rule.
	 * 
	 * @param rule
	 *            activation rule
	 */
	public AbstractFormValidator(ValidationActivationRule rule)
	{
		super(rule);
	}

}
