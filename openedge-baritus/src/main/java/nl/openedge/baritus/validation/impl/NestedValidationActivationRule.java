/*
 * $Id: NestedValidationActivationRule.java,v 1.2 2004-02-27 19:53:47 eelco12 Exp $
 * $Revision: 1.2 $
 * $Date: 2004-02-27 19:53:47 $
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

package nl.openedge.baritus.validation.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.validation.ValidationActivationRule;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * You can use a list of validators while registering just one by using
 * NestedValidationActionRule.
 * 
 * @author Eelco Hillenius
 */
public class NestedValidationActivationRule implements ValidationActivationRule
{

	private List<ValidationActivationRule> rules = null;

	/**
	 * allows validation if all nested rules return true.
	 * 
	 * @param cctx
	 *            controller context
	 * @param formBeanContext
	 *            form bean context
	 * @return boolean if all nested rules return true, false if one of them did not
	 * 
	 * @see nl.openedge.baritus.validation.ValidationActivationRule#allowValidation(org.infohazard.maverick.flow.ControllerContext,
	 *      nl.openedge.baritus.FormBeanContext)
	 */
	@Override
	public boolean allowValidation(ControllerContext cctx, FormBeanContext formBeanContext)
	{
		boolean allow = true;

		if (rules != null)
		{
			for (Iterator<ValidationActivationRule> i = rules.iterator(); i.hasNext();)
			{
				ValidationActivationRule nested = i.next();
				allow = nested.allowValidation(cctx, formBeanContext);

				if (!allow)
					break;
			}
		}

		return allow;
	}

	/**
	 * add a validation activation rule to the end of the list.
	 * 
	 * @param rule
	 *            validation activation rule to be added
	 */
	public void addRule(ValidationActivationRule rule)
	{
		if (rules == null)
		{
			rules = new ArrayList<ValidationActivationRule>();
		}
		rules.add(rule);
	}

	/**
	 * add a validation activation rule to the list at the provided position.
	 * 
	 * @param index
	 *            the position in the list at which the provided rule should be inserted
	 * @param rule
	 *            validation activation rule to be added
	 */
	public void addRule(int index, ValidationActivationRule rule)
	{
		if (rules == null)
		{
			rules = new ArrayList<ValidationActivationRule>();
		}
		rules.add(index, rule);
	}

	/**
	 * add list of rules to current list of rules.
	 * 
	 * @param rules
	 *            list of rules to be added
	 */
	public void addAllRules(List rules)
	{
		if (rules == null)
		{
			rules = new ArrayList();
		}
		rules.addAll(rules);
	}

	/**
	 * remove a validation activation rule from list.
	 * 
	 * @param rule
	 *            validation activation rule to be removed
	 */
	public void removeRule(ValidationActivationRule rule)
	{
		if (rules != null)
		{
			rules.remove(rule);
		}
	}

	/**
	 * remove a validation activation rule at the provided position
	 * 
	 * @param index
	 *            the position at which the rule should be removed
	 */
	public void removeRule(int index)
	{
		if (rules != null)
		{
			rules.remove(index);
		}
	}

	/**
	 * clear the list of rules.
	 */
	public void clear()
	{
		if (rules != null)
		{
			rules.clear();
			rules = null;
		}
	}

	/**
	 * get list of validation activation rules.
	 * 
	 * @return List validation activation rules
	 */
	public List<ValidationActivationRule> getRules()
	{
		return rules;
	}

	/**
	 * set list of validation activation rules.
	 * 
	 * @param list
	 *            validation activation rules
	 */
	public void setRules(List<ValidationActivationRule> list)
	{
		rules = list;
	}

}
