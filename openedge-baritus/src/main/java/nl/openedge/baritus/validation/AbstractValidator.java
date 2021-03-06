package nl.openedge.baritus.validation;

import java.util.Locale;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.FormBeanCtrlBase;
import nl.openedge.baritus.util.MessageUtils;

/**
 * convenience class
 * 
 * @author Eelco Hillenius
 */
public abstract class AbstractValidator implements ValidationRuleDependend
{

	private ValidationActivationRule validationActivationRule = null;

	/**
	 * construct
	 */
	public AbstractValidator()
	{
		// nothing here
	}

	/**
	 * construct with validator activation rule
	 * 
	 * @param rule
	 *            validator activation rule
	 */
	public AbstractValidator(ValidationActivationRule rule)
	{
		setValidationRule(rule);
	}

	/**
	 * get localized message for given key
	 * 
	 * @param key
	 *            key of message
	 * @return String localized message
	 */
	public String getLocalizedMessage(String key)
	{
		return getLocalizedMessage(key, Locale.getDefault());
	}

	/**
	 * get localized message for given key and locale. If locale is null, the default
	 * locale will be used
	 * 
	 * @param key
	 *            key of message
	 * @param locale
	 *            locale for message
	 * @return String localized message
	 */
	public String getLocalizedMessage(String key, Locale locale)
	{
		return MessageUtils.getLocalizedMessage(key, locale);
	}

	/**
	 * Get localized message for given key and format it with the given parameters. If
	 * locale is null, the default locale will be used
	 * 
	 * @param key
	 *            key of message
	 * @param parameters
	 *            parameters for the message
	 * @return String localized message
	 */
	public String getLocalizedMessage(String key, Object[] parameters)
	{
		return getLocalizedMessage(key, null, parameters);
	}

	/**
	 * Get localized message for given key and locale and format it with the given
	 * parameters. If locale is null, the default locale will be used
	 * 
	 * @param key
	 *            key of message
	 * @param locale
	 *            locale for message
	 * @param parameters
	 *            parameters for the message
	 * @return String localized message
	 */
	public String getLocalizedMessage(String key, Locale locale, Object[] parameters)
	{
		return MessageUtils.getLocalizedMessage(key, locale, parameters);
	}

	/**
	 * @see nl.openedge.baritus.validation.ValidationRuleDependend#getValidationActivationRule()
	 */
	@Override
	public ValidationActivationRule getValidationActivationRule()
	{
		return this.validationActivationRule;
	}

	/**
	 * @see nl.openedge.baritus.validation.ValidationRuleDependend#removeValidationActivationRule()
	 */
	@Override
	public void removeValidationActivationRule()
	{
		this.validationActivationRule = null;
	}

	/**
	 * @see nl.openedge.baritus.validation.ValidationRuleDependend#setValidationRule(nl.openedge.baritus.validation.ValidationActivationRule)
	 */
	@Override
	public void setValidationRule(ValidationActivationRule rule)
	{
		this.validationActivationRule = rule;
	}

	/**
	 * Get the - possibly translated using the current locale that is stored in the form
	 * bean context - name of the field. If lookup in the resources failed, the provided
	 * name will be returned as is.
	 * 
	 * @param formBeanContext
	 *            form bean context
	 * @param name
	 *            original name of field
	 * @return String the - possibly translated - name of the field. If lookup in the
	 *         resources failed, the provided name will be returned as is.
	 */
	protected String getFieldName(FormBeanContext formBeanContext, String name)
	{
		FormBeanCtrlBase ctrl = formBeanContext.getController();
		Locale locale = formBeanContext.getCurrentLocale();
		String fieldName = getLocalizedMessage(ctrl.getPropertyNameKey(name), locale);
		return (fieldName != null) ? fieldName : name;
	}

	/**
	 * set error message in formBeanContext using the provided name, the current locale
	 * that is stored in the form bean context, the provided errorMessageKey and the
	 * provided arguments for parsing the localized message; the message will be stored in
	 * the form bean context with key that was provided as argument name. If no message
	 * was found, the provided errorMessageKey will be used.
	 * 
	 * @param formBeanContext
	 *            form bean context
	 * @param name
	 *            untransalated name of the field
	 * @param errorMessageKey
	 *            message key for error
	 * @param messageArguments
	 *            arguments for parsing the message
	 */
	protected void setErrorMessage(FormBeanContext formBeanContext, String name,
			String errorMessageKey, Object[] messageArguments)
	{
		Locale locale = formBeanContext.getCurrentLocale();
		String msg = getLocalizedMessage(errorMessageKey, locale, messageArguments);
		if (msg == null)
			msg = errorMessageKey;
		formBeanContext.setError(name, msg);
	}

}
