/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003
 * All rights reserved.
 */
package nl.openedge.maverick.framework.validation;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Eelco Hillenius
 */
public abstract class AbstractValidator
{

	private ValidatorActivationRule validatorActivationRule = null;

	private String messagePrefix = "input.field.";

	/**
	 * construct
	 */
	public AbstractValidator()
	{
		// nothing here
	}
	
	/**
	 * construct with message prefix
	 * @param messagePrefix message prefix
	 */
	public AbstractValidator(String messagePrefix)
	{
		setMessagePrefix(messagePrefix);
	}
	
	/* get resource bundle */
	protected ResourceBundle getBundle(Locale locale)
	{
		ResourceBundle res = null;
		if(locale != null)
		{
			res = ResourceBundle.getBundle("resources", locale);
		}
		else
		{
			res = ResourceBundle.getBundle("resources");
		}
		return res;		
	}
	
	/**
	 * get localized message for given key
	 * @param key key of message
	 * @return String localized message
	 */
	public String getLocalizedMessage(String key)
	{
		return getLocalizedMessage(key, Locale.getDefault());
	}
	
	/**
	 * get localized message for given key and locale. 
	 * If locale is null, the default locale will be used
	 * @param key key of message
	 * @param locale locale for message
	 * @return String localized message
	 */
	public String getLocalizedMessage(String key, Locale locale)
	{	
		String msg = null;
		try
		{
			msg = getBundle(locale).getString(key);
		}
		catch (RuntimeException e)
		{
			e.printStackTrace();
		}
		return msg;
	}
	
	/**
	 * get localized message for given key and locale
	 * and format it with the given parameters. 
	 * If locale is null, the default locale will be used
	 * @param key key of message
	 * @param locale locale for message
	 * @param parameters parameters for the message
	 * @return String localized message
	 */
	public String getLocalizedMessage(
			String key, Object[] parameters)
	{	
		return getLocalizedMessage(key, null, parameters);
	}
	
	/**
	 * get localized message for given key and locale
	 * and format it with the given parameters. 
	 * If locale is null, the default locale will be used
	 * @param key key of message
	 * @param locale locale for message
	 * @param parameters parameters for the message
	 * @return String localized message
	 */
	public String getLocalizedMessage(
			String key, Locale locale, Object[] parameters)
	{
		ResourceBundle res = getBundle(locale);
		String msg = res.getString(key);
		MessageFormat fmt = new MessageFormat(msg);
		String formattedMessage = 
			MessageFormat.format(msg, parameters);
		return formattedMessage;
	}

	/**
	 * get the prefix for error message keys
	 * @return String
	 */
	public String getMessagePrefix()
	{
		return messagePrefix;
	}

	/**
	 * set the prefix for error message keys
	 * @param string
	 */
	public void setMessagePrefix(String string)
	{
		messagePrefix = string;
	}

	/**
	 * @see nl.openedge.maverick.framework.validation.ValidationRuleDependend#getValidationActivationRule()
	 */
	public ValidatorActivationRule getValidationActivationRule()
	{
		return this.validatorActivationRule;
	}

	/**
	 * @see nl.openedge.maverick.framework.validation.ValidationRuleDependend#removeValidationActivationRule()
	 */
	public void removeValidationActivationRule()
	{
		this.validatorActivationRule = null;
	}

	/**
	 * @see nl.openedge.maverick.framework.validation.ValidationRuleDependend#setValidationRule(nl.openedge.maverick.framework.validation.ValidatorActivationRule)
	 */
	public void setValidationRule(ValidatorActivationRule rule)
	{
		this.validatorActivationRule = rule;
	}

}
