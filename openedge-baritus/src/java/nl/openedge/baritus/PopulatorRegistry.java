/*
 * $Id: PopulatorRegistry.java,v 1.4 2004-04-05 09:56:22 eelco12 Exp $
 * $Revision: 1.4 $
 * $Date: 2004-04-05 09:56:22 $
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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import nl.openedge.baritus.population.FieldPopulator;
import nl.openedge.baritus.population.OgnlFieldPopulator;

/**
 * Registry for populators. Each instance of FormBeanBase has its own instance.
 * 
 * @author Eelco Hillenius
 */
public final class PopulatorRegistry
{

	private Map fieldPopulators = null;
	
	private Map regexFieldPopulators = null;
	
	private FieldPopulator defaultFieldPopulator = null;
	
	/**
	 * construct registry with the current instance of FormBeanBase
	 * @param formBeanCtrl
	 */
	public PopulatorRegistry(FormBeanCtrlBase formBeanCtrl)
	{
		defaultFieldPopulator = new OgnlFieldPopulator(formBeanCtrl);
	}
	
	/**
	 * Register a field populator for the given fieldName. 
	 * Field populators override the default population of a property on the current form
	 * @param fieldName name of field
	 * @param populator populator instance
	 */
	public void addPopulator(String fieldName, FieldPopulator populator)
	{
		if(fieldPopulators == null)
		{
			fieldPopulators = new HashMap();
		}
		fieldPopulators.put(fieldName, populator);
	}

	
	/**
	 * de-register the field populator that was registered with the given fieldName
	 * @param fieldName name of field
	 */
	public void removePopulator(String fieldName)
	{
		if(fieldPopulators != null)
		{
			fieldPopulators.remove(fieldName);
			if(fieldPopulators.isEmpty()) fieldPopulators = null;
		}
	}
	
	/**
	 * Register a custom populator that overrides the default population
	 * process for all request parameters that match the regular expression stored in
	 * the provided pattern.
	 * 
	 * @param pattern regex pattern
	 * @param populator populator instance
	 */
	public void addPopulator(Pattern pattern, FieldPopulator populator)
	{
		if(regexFieldPopulators == null)
		{
			regexFieldPopulators = new HashMap();
		}
		regexFieldPopulators.put(pattern, populator);
	}

	
	/**
	 * Remove a populator that was registered for the provided pattern
	 * @param pattern regex pattern
	 */
	public void removePopulator(Pattern pattern)
	{
		if(regexFieldPopulators != null)
		{
			regexFieldPopulators.remove(pattern);
		}
	}
	
	/**
	 * get the populators that were registered with regex patterns
	 * @return Map the populators that were registered with regex patterns
	 */
	public Map getRegexFieldPopulators()
	{
		return regexFieldPopulators;
	}
	
	/**
	 * get the field populator for the provided fieldName, null if none registered
	 * @param fieldName name of the field
	 * @return the field populator for the provided fieldName, null if none registered
	 */
	public FieldPopulator getFieldPopulator(String fieldName)
	{
		return (fieldPopulators != null) ? 
			(FieldPopulator)fieldPopulators.get(fieldName) : null;
	}

	/**
	 * get the field populators
	 * @return Map the field populators
	 */
	public Map getFieldPopulators()
	{
		return fieldPopulators;
	}
	
	/**
	 * get the default field populator
	 * @return FieldPopulator the default field populator
	 */
	public FieldPopulator getDefaultFieldPopulator()
	{
		return defaultFieldPopulator;
	}

	/**
	 * set the default field populator
	 * @param populator the default field populator
	 */
	public void setDefaultFieldPopulator(FieldPopulator populator)
	{
		defaultFieldPopulator = populator;
	}

}
