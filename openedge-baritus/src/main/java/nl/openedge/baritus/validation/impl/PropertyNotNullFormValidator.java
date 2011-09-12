package nl.openedge.baritus.validation.impl;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.validation.AbstractFormValidator;
import nl.openedge.baritus.validation.ValidationActivationRule;
import ognl.Ognl;

import org.infohazard.maverick.flow.ControllerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Checks whether the form contains a non null property with the name of property
 * propertyName.
 * 
 * @author Eelco Hillenius
 */
public class PropertyNotNullFormValidator extends AbstractFormValidator
{

	private String propertyName;

	private String errorMessageKey = "object.not.found";

	private Logger log = LoggerFactory.getLogger(PropertyNotNullFormValidator.class);

	/**
	 * construct
	 */
	public PropertyNotNullFormValidator()
	{

	}

	/**
	 * construct with errorMessageKey
	 * 
	 * @param errorMessageKey
	 */
	public PropertyNotNullFormValidator(String errorMessageKey)
	{
		setErrorMessageKey(errorMessageKey);
	}

	/**
	 * construct with rule
	 * 
	 * @param rule
	 *            validation rule
	 */
	public PropertyNotNullFormValidator(ValidationActivationRule rule)
	{
		setValidationRule(rule);
	}

	/**
	 * construct with error message and rule
	 * 
	 * @param errorMessageKey
	 *            message key
	 * @param rule
	 *            validation rule
	 */
	public PropertyNotNullFormValidator(String errorMessageKey, ValidationActivationRule rule)
	{
		setErrorMessageKey(errorMessageKey);
		setValidationRule(rule);
	}

	/**
	 * construct with property name and errorMessageKey
	 * 
	 * @param propertyName
	 *            name of property
	 * @param errorMessageKey
	 *            message key
	 */
	public PropertyNotNullFormValidator(String propertyName, String errorMessageKey)
	{
		setPropertyName(propertyName);
		setErrorMessageKey(errorMessageKey);
	}

	/**
	 * check whether the form contains a not null property with the name of property
	 * propertyName
	 * 
	 * @return boolean true if property in form with name of property propertyName exists
	 *         and is not null, false otherwise
	 * @see nl.openedge.baritus.validation.FormValidator#isValid(org.infohazard.maverick.flow.ControllerContext,
	 *      nl.openedge.baritus.FormBeanContext)
	 */
	@Override
	public boolean isValid(ControllerContext cctx, FormBeanContext formBeanContext)
	{
		Object bean = formBeanContext.getBean();
		boolean valid = false;
		try
		{
			Object property = Ognl.getValue(propertyName, bean);
			valid = (property != null);
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}

		if (!valid)
		{
			setErrorMessage(formBeanContext, propertyName, getErrorMessageKey(),
				new Object[] {getFieldName(formBeanContext, propertyName)});
		}

		return valid;

	}

	/**
	 * get the name of the property
	 * 
	 * @return String name of property
	 */
	public String getPropertyName()
	{
		return propertyName;
	}

	/**
	 * set the name of the property
	 * 
	 * @param string
	 *            name of the property
	 */
	public void setPropertyName(String string)
	{
		propertyName = string;
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
