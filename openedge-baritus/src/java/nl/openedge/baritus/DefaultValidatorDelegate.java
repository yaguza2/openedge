/*
 * $Id: DefaultValidatorDelegate.java,v 1.2 2004-02-27 08:24:18 eelco12 Exp $
 * $Revision: 1.2 $
 * $Date: 2004-02-27 08:24:18 $
 *
 * ====================================================================
 * Copyright (c) 2003, Open Edge B.V.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions 
 * in binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution. Neither the name of OpenEdge B.V. 
 * nor the names of its contributors may be used to endorse or promote products 
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
 
package nl.openedge.baritus;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import nl.openedge.baritus.util.MessageUtils;
import nl.openedge.baritus.validation.FieldValidator;
import nl.openedge.baritus.validation.FormValidator;
import nl.openedge.baritus.validation.ValidationRuleDependend;
import nl.openedge.baritus.validation.ValidationActivationRule;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.lang.exception.NestableException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infohazard.maverick.flow.ControllerContext;

/**
 * @author Eelco Hillenius
 */
final class DefaultValidatorDelegate implements ValidatorDelegate
{
	// validator registry
	private ValidatorRegistry validatorRegistry = null;
	
	// instance of formbean ctrl
	private FormBeanCtrl ctrl = null;
	
	/* population log */
	private static Log populationLog = LogFactory.getLog(LogConstants.POPULATION_LOG);
	
	/**
	 * construct with validator registry and instance of ctrl
	 * @param validatorRegistry
	 * @param ctrl
	 */
	public DefaultValidatorDelegate(
		ValidatorRegistry validatorRegistry,
		FormBeanCtrl ctrl)
	{
		this.validatorRegistry = validatorRegistry;
		this.ctrl = ctrl;
	}
	
	/**
	 * @see nl.openedge.baritus.ValidatorDelegate#doCustomValidation(org.infohazard.maverick.flow.ControllerContext, nl.openedge.baritus.FormBeanContext, nl.openedge.baritus.ExecutionParams, java.util.Map, java.util.Locale, boolean)
	 */
	public boolean doValidation(
		ControllerContext cctx, 
		FormBeanContext formBeanContext, 
		ExecutionParams execParams,
		Map parameters,
		Locale locale,
		boolean succeeded)
	{
		
		if(parameters == null) return succeeded;
		
		MultiMap fieldValidators = validatorRegistry.getFieldValidators();
		List formValidators = validatorRegistry.getFormValidators();
		List globalValidatorActivationRules = 
			validatorRegistry.getGlobalValidatorActivationRules();
		
		if( (fieldValidators != null && (!fieldValidators.isEmpty())) ||
			(formValidators != null && (!formValidators.isEmpty())))
		{
	
			boolean doCustomValidation = true;
			// see if there's any globally (form level) defined rules
			if(globalValidatorActivationRules != null && (!globalValidatorActivationRules.isEmpty()))
			{
				for(Iterator i = globalValidatorActivationRules.iterator(); i.hasNext(); )
				{
					ValidationActivationRule rule = (ValidationActivationRule)i.next();
					doCustomValidation = rule.allowValidation(cctx, formBeanContext); // fire rule
					if(!doCustomValidation) break;
				}
			}
			
			if(doCustomValidation)
			{
				// if fieldValidators were registered
				if(fieldValidators != null && (!fieldValidators.isEmpty()))
				{
					Iterator names = parameters.keySet().iterator(); // loop through the properties
					while(names.hasNext())
					{
						String name = (String)names.next();
						if (name == null) continue;
						if(formBeanContext.getOverrideField(name) == null) 
							// see if there allready was an error registered
						{
							Collection propertyValidators = 
								(Collection)fieldValidators.get(name);
							// these are the fieldValidators for one property
							if(propertyValidators != null)
							{
								try
								{
									succeeded = doValidationForOneField(
										cctx, formBeanContext, locale, succeeded, 
										name, propertyValidators);
								}
								catch (Exception e)
								{
									if(populationLog.isDebugEnabled())
									{
										// when in debug mode, print the stacktrace
										populationLog.error(e.getMessage(), e);
									}
									populationLog.error(e.getMessage());
									// ignore
								}
							}	
						} // else an error allready occured; do not validate
					}	
				}
				// if we are still successful so far, check with the form level validators
				if( (succeeded || execParams.isDoFormValidationIfFieldValidationFailed()) 
					&& (formValidators != null))
				{
					// check all registered until either all fired successfully or
					// one did not fire succesfully
					for(Iterator i = formValidators.iterator(); i.hasNext(); )
					{
						FormValidator fValidator = (FormValidator)i.next();
						boolean fireValidator = true;
						if(fValidator instanceof ValidationRuleDependend)
						{
							ValidationActivationRule fRule = 
								((ValidationRuleDependend)fValidator).getValidationActivationRule();
							if(fRule != null)
							{
								if(!fRule.allowValidation(cctx, formBeanContext))
								{
									fireValidator = false;
								}	
							}
						}
						if(fireValidator)
						{
							if(!fValidator.isValid(cctx, formBeanContext))
							{
								succeeded = false;
								String[] msg = fValidator.getErrorMessage(
									cctx, formBeanContext, locale);
									
								if(msg != null && (msg.length > 0))
								{
									formBeanContext.setError(msg[0], msg[1]);	
								}
							}	
						}
						// else ignore
					}
				}
			}

		}
		
		return succeeded;
	}
	
	/* handle the custom validation for one field */
	private boolean doValidationForOneField(
		ControllerContext cctx,
		FormBeanContext formBeanContext,
		Locale locale,
		boolean succeeded,
		String name,
		Collection propertyValidators)
		throws Exception
	{
		// get target value;
		// this could be done a bit more efficient, as we allready had
		// the (converted) value when populating. Working more efficient 
		// (like with a converted value cache) would make the API of
		// populators less straightforward, and by getting the property
		// from the bean instead of using the converted value, we are
		// sure that we get the property the proper (java beans) way.
		Object value = PropertyUtils.getProperty(formBeanContext.getBean(), name);
		
		// for all validators for this field
		for(Iterator j = propertyValidators.iterator(); j.hasNext(); )
		{
			FieldValidator validator = (FieldValidator)j.next();
			boolean validateField = true;
								
			if(validator instanceof ValidationRuleDependend) // should we execute rule
			{
				ValidationActivationRule rule = 
					((ValidationRuleDependend)validator)
						.getValidationActivationRule();
				if(rule != null)
				{
					validateField = rule.allowValidation(cctx, formBeanContext); //test
					
					if(populationLog.isDebugEnabled())
					{
						populationLog.debug( name + ": rule " + rule + 
							((validateField) ? " ALLOWS" : " DISALLOWS") +
							" validation with " + validator);
					}
				}
			}
								
			if(validateField)
			{
				// execute validation method
				boolean success;
				try
				{
					success = validator.isValid(cctx, formBeanContext, name, value);
				}
				catch (Exception e)
				{
					String msg = "validator " + validator + " threw exception: " +
						e.getMessage() + " on property " + name + " with value " +
						value;
					throw new NestableException(msg, e);
				}
				
				if(populationLog.isDebugEnabled())
				{
					populationLog.debug( "validation" +
						((success) ? " PASSED" : " FAILED") +
						" for field " + name + " using validator " + validator);
				}
				
				if(!success)
				{
					succeeded = false;
					try
					{
						String msgName = MessageUtils.getLocalizedMessage(
							ctrl.getPropertyNameKey(name));
						String msg = validator.getErrorMessage(
							cctx, formBeanContext, (msgName != null) ? msgName : name, value, locale);
						
						if(msg != null)
						{
							formBeanContext.setError(name, msg);	
						}
					}
					catch (Exception e)
					{
						if(populationLog.isDebugEnabled())
						{
							// print with stacktrace if debug enabled
							populationLog.error(e.getMessage(), e);
						}
						else
						{
							populationLog.error(e.getMessage());
						}
						formBeanContext.setError(name, e.getMessage());
					}
					ctrl.setOverrideField(
						cctx, formBeanContext, name, value, null, validator);
					break;
				}	
			}
		}
		return succeeded;	
	}
}
