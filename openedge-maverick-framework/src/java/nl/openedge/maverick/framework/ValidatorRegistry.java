/*
 * $Id$
 * $Revision$
 * $Date$
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
 
package nl.openedge.maverick.framework;

import java.util.ArrayList;
import java.util.List;

import nl.openedge.maverick.framework.validation.FieldValidator;
import nl.openedge.maverick.framework.validation.FormValidator;
import nl.openedge.maverick.framework.validation.ValidatorActivationRule;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;

/**
 * Registry for populators.
 * 
 * @author Eelco Hillenius
 */
final class ValidatorRegistry
{


	private MultiMap fieldValidators = null;
	
	private List formValidators = null;
	
	private List globalValidatorActivationRules = null;

	/**
	 * register a field validator for the given fieldName. 
	 * multiple fieldValidators for one key are allowed.
	 * @param fieldName name of field
	 * @param validator validator instance
	 */
	public void addValidator(String fieldName, FieldValidator validator)
	{
		if(fieldValidators == null)
		{
			fieldValidators = new MultiHashMap();
		}
		fieldValidators.put(fieldName, validator);
	}
	
	/**
	 * register a form validator.
	 * @param validator the form level validator
	 */
	public void addValidator(FormValidator validator)
	{
		if(formValidators == null)
		{
			formValidators = new ArrayList();
		}
		formValidators.add(validator);
	}
	
	/**
	 * de-register the fieldValidators that were registered with the given fieldName
	 * @param fieldName name of field
	 */
	public void removeValidators(String fieldName)
	{
		if(fieldValidators != null)
		{
			fieldValidators.remove(fieldName);
		}
	}
	
	/**
	 * de-register the given validator that was registered with the given fieldName
	 * @param fieldName name of field
	 * @param the validator to remove for the given field
	 */
	public void removeValidator(String fieldName, FieldValidator validator)
	{
		if(fieldValidators != null)
		{
			fieldValidators.remove(fieldName, validator);
		}
	}
	
	/**
	 * de-register the given form level validator
	 * @param validator form validator
	 */
	public void removeValidator(FormValidator validator)
	{
		if(formValidators != null)
		{
			formValidators.remove(validator);
		}
	}
	
	/**
	 * register the rule for the whole form
	 * @param fieldName name of field
	 * @param validator validator instance
	 */
	public void addGlobalValidatorActivationRule(ValidatorActivationRule rule)
	{
		if(globalValidatorActivationRules == null)
		{
			globalValidatorActivationRules = new ArrayList();
		}
		globalValidatorActivationRules.add(rule);
	}
	
	/**
	 * de-register the given rule for the whole form
	 * @param fieldName
	 */
	public void removeGlobalValidatorActivationRule(ValidatorActivationRule rule)
	{
		if(globalValidatorActivationRules != null)
		{
			globalValidatorActivationRules.remove(rule);
			if(globalValidatorActivationRules.isEmpty()) globalValidatorActivationRules = null;
		}
	}
	
	/**
	 * get the fieldValidators that were registered with the given fieldName
	 * @param fieldName name of the field
	 * @return FieldValidator the instance of FieldValidator that was registered with the given 
	 * 		fieldName or null if none was registered with that name
	 */
	public MultiMap getValidators(String fieldName)
	{
		return (fieldValidators != null) ? 
			(MultiMap)fieldValidators.get(fieldName) : null;
	}

	/**
	 * get all global activation rules
	 * @return List all global activation rules
	 */
	public List getGlobalValidatorActivationRules()
	{
		return globalValidatorActivationRules;
	}

	/**
	 * get all form validators
	 * @return List all form validators
	 */
	public List getFormValidators()
	{
		return formValidators;
	}

	/**
	 * get all field validators
	 * @return get all field validators
	 */
	public MultiMap getFieldValidators()
	{
		return fieldValidators;
	}

}
