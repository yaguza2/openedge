/*
 * $Id: DefaultValidatorDelegate.java,v 1.7 2004-04-07 10:43:39 eelco12 Exp $
 * $Revision: 1.7 $
 * $Date: 2004-04-07 10:43:39 $
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import nl.openedge.baritus.util.MultiHashMap;
import nl.openedge.baritus.validation.FieldValidator;
import nl.openedge.baritus.validation.FormValidator;
import nl.openedge.baritus.validation.ValidationRuleDependend;
import nl.openedge.baritus.validation.ValidationActivationRule;

import ognl.Ognl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infohazard.maverick.flow.ControllerContext;

/**
 * @author Eelco Hillenius
 */
public final class DefaultValidatorDelegate implements ValidatorDelegate
{
	// validator registry
	private ValidatorRegistry validatorRegistry = null;
	
	// instance of formbean ctrl
	private FormBeanCtrlBase ctrl = null;
	
	/* population log */
	private static Log populationLog = LogFactory.getLog(LogConstants.POPULATION_LOG);
	
	private static char[] BREAKSYMBOLS = new char[]{'[','('};
	
	/**
	 * construct with validator registry and instance of ctrl
	 * @param validatorRegistry
	 * @param ctrl
	 */
	public DefaultValidatorDelegate(
		ValidatorRegistry validatorRegistry,
		FormBeanCtrlBase ctrl)
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
		
		MultiHashMap fieldValidators = validatorRegistry.getFieldValidators();
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
							succeeded = doValidationForOneField(
								fieldValidators, cctx, formBeanContext, 
								locale, succeeded, name);	
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
							}	
						}
						// else ignore
					}
				}
			}
		}
		
		return succeeded;
	}
	
	/* execute validation for one field */
	private boolean doValidationForOneField(
		MultiHashMap fieldValidators,
		ControllerContext cctx,
		FormBeanContext formBeanContext,
		Locale locale,
		boolean succeeded,
		String name)
	{
		if(formBeanContext.getOverrideField(name) == null) 
			// see if there allready was an error registered
		{
			Collection propertyValidators = 
				getFieldValidatorsForField(name, fieldValidators);
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
		
		return succeeded;
	}
	
	/* Get the validators for a field, possibly null. */
	private List getFieldValidatorsForField(
		String name, MultiHashMap fieldValidators)
	{
		List propertyValidators = null;
		propertyValidators = getFieldValidatorsForFieldRecursively(
			name, fieldValidators, propertyValidators);
		return propertyValidators;	
	}
	
	/* 
	 * Get the validators for a field, null if none found.
	 * work our way back to simple property name
	 * e.g., take complex (bogus) case 'myproperty('key1')[1]('key2')[2]',
	 * we should be able to look for registered validators with:
	 * 	- myproperty['key1'][1]['key2'][2]
	 * 	- myproperty['key1'][1]['key2']
	 * 	- myproperty['key1'][1]
	 * 	- myproperty['key1']
	 * 	- myproperty 
	 */
	private List getFieldValidatorsForFieldRecursively(
		String currentName, MultiHashMap fieldValidators, List propertyValidators)
	{
		List validators = (List)fieldValidators.get(currentName);
		if(validators != null)
		{
			if(propertyValidators == null) propertyValidators = new ArrayList();
			propertyValidators.addAll(validators);
		}
		
		int delim = 0;
		for(int i = 0; i < BREAKSYMBOLS.length; i++)
		{
			int ix = currentName.lastIndexOf(BREAKSYMBOLS[i]);
			if(ix > -1)
			{
				delim = ix;
				break;
			}	
		}
		
		if(delim > 0)
		{
			// just cut off wihout further checking
			currentName = currentName.substring(0, delim);
			propertyValidators = getFieldValidatorsForFieldRecursively(
				currentName, fieldValidators, propertyValidators);
		}
		
		return propertyValidators;
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
		Object value = Ognl.getValue(name, formBeanContext.getBean());
		
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
					throw new Exception(msg, e);
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
					ctrl.setOverrideField(
						cctx, formBeanContext, name, value, null, validator);
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
