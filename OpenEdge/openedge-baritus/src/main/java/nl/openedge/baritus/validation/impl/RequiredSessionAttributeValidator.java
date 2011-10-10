package nl.openedge.baritus.validation.impl;

import javax.servlet.http.HttpSession;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.validation.AbstractFormValidator;
import nl.openedge.baritus.validation.ValidationActivationRule;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * Checks whether a session attribute exists with the key that was set for property
 * sessionAttributeKey.
 * 
 * @author Eelco Hillenius
 */
public class RequiredSessionAttributeValidator extends AbstractFormValidator
{

	private String sessionAttributeKey = null;

	private String errorMessageKey = "required.session.attribute.not.found";

	/**
	 * Construct.
	 */
	public RequiredSessionAttributeValidator()
	{

	}

	/**
	 * Construct with errorMessageKey.
	 * 
	 * @param errorMessageKey
	 */
	public RequiredSessionAttributeValidator(String errorMessageKey)
	{
		setErrorMessageKey(errorMessageKey);
	}

	/**
	 * Construct with rule.
	 * 
	 * @param rule
	 */
	public RequiredSessionAttributeValidator(ValidationActivationRule rule)
	{
		super(rule);
	}

	/**
	 * Construct with errorMessageKey and rule.
	 * 
	 * @param errorMessageKey
	 * @param rule
	 */
	public RequiredSessionAttributeValidator(String errorMessageKey, ValidationActivationRule rule)
	{
		setErrorMessageKey(errorMessageKey);
		setValidationRule(rule);
	}

	/**
	 * Construct with message prefix and session attribute key to check for.
	 * 
	 * @param errorMessageKey
	 */
	public RequiredSessionAttributeValidator(String errorMessageKey, String sessionAttributeKey)
	{
		setErrorMessageKey(errorMessageKey);
		setSessionAttributeKey(sessionAttributeKey);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * nl.openedge.baritus.validation.FormValidator#isValid(org.infohazard.maverick.flow
	 * .ControllerContext, nl.openedge.baritus.FormBeanContext)
	 */
	@Override
	public boolean isValid(ControllerContext cctx, FormBeanContext formBeanContext)
	{
		boolean valid = false;
		HttpSession session = cctx.getRequest().getSession(false);
		if (session != null)
		{
			valid = (session.getAttribute(sessionAttributeKey) != null);
		}

		if (!valid)
		{
			setErrorMessage(formBeanContext, sessionAttributeKey, getErrorMessageKey(),
				new Object[] {getFieldName(formBeanContext, sessionAttributeKey)});
		}

		return valid;
	}

	/**
	 * get the session attribute key that will be checked for
	 * 
	 * @return String the session attribute key that will be checked for
	 */
	public String getSessionAttributeKey()
	{
		return sessionAttributeKey;
	}

	/**
	 * set the session attribute key that will be checked for
	 * 
	 * @param string
	 *            the session attribute key that will be checked for
	 */
	public void setSessionAttributeKey(String string)
	{
		sessionAttributeKey = string;
	}

	/**
	 * Get key of error message.
	 * 
	 * @return String key of error message
	 */
	public String getErrorMessageKey()
	{
		return errorMessageKey;
	}

	/**
	 * Set key of error message.
	 * 
	 * @param string
	 *            key of error message
	 */
	public void setErrorMessageKey(String string)
	{
		errorMessageKey = string;
	}

}
