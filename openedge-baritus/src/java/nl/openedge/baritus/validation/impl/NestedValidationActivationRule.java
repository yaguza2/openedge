/*
 * $Id: NestedValidationActivationRule.java,v 1.1 2004-02-27 08:24:18 eelco12 Exp $
 * $Revision: 1.1 $
 * $Date: 2004-02-27 08:24:18 $
 *
 * ====================================================================
 * Copyright (c) 2003
 * All rights reserved.
 */
package nl.openedge.baritus.validation.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.validation.ValidationActivationRule;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * @author Eelco Hillenius
 */
public class NestedValidationActivationRule implements ValidationActivationRule
{

	private List rules = null;

	/**
	 * allows validation if all nested rules return true.
	 * @param cctx controller context
	 * @param formBeanContext form bean context
	 * @return boolean if all nested rules return true, false if one of them did not
	 * 
	 * @see nl.openedge.baritus.validation.ValidationActivationRule#allowValidation(org.infohazard.maverick.flow.ControllerContext, nl.openedge.baritus.FormBeanContext)
	 */
	public boolean allowValidation(
		ControllerContext cctx,
		FormBeanContext formBeanContext)
	{
		boolean allow = true;
		
		if(rules != null)
		{
			for(Iterator i = rules.iterator(); i.hasNext(); )
			{
				ValidationActivationRule nested = (ValidationActivationRule)i.next();
				allow = nested.allowValidation(cctx, formBeanContext);
				
				if(!allow) break;
			}
		}
		
		return allow;
	}
	
	/**
	 * add a validation activation rule to the end of the list.
	 * @param rule validation activation rule to be added
	 */
	public void addRule(ValidationActivationRule rule)
	{
		if(rules == null)
		{
			rules = new ArrayList();
		}
		rules.add(rule);
	}

	/**
	 * add a validation activation rule to the list at the provided position.
	 * 
	 * @param index the position in the list at which the provided rule should
	 * 	be inserted
	 * @param rule validation activation rule to be added
	 */
	public void addRule(int index, ValidationActivationRule rule)
	{
		if(rules == null)
		{
			rules = new ArrayList();
		}
		rules.add(index, rule);
	}
	
	/**
	 * add list of rules to current list of rules.
	 * 
	 * @param rules list of rules to be added
	 */
	public void addAllRules(List rules)
	{
		if(rules == null)
		{
			rules = new ArrayList();
		}
		rules.addAll(rules);
	}


	/**
	 * remove a validation activation rule from list.
	 * @param rule validation activation rule to be removed
	 */
	public void removeRule(ValidationActivationRule rule)
	{
		if(rules != null)
		{
			rules.remove(rule);
		}
	}
	
	/**
	 * remove a validation activation rule at the provided position
	 * @param index the position at which the rule should be removed
	 */
	public void removeRule(int index)
	{
		if(rules != null)
		{
			rules.remove(index);
		}
	}
	
	/**
	 * clear the list of rules.
	 */
	public void clear()
	{
		if(rules != null)
		{
			rules.clear();
			rules = null;	
		}
	}


	/**
	 * get list of validation activation rules.
	 * @return List validation activation rules
	 */
	public List getRules()
	{
		return rules;
	}

	/**
	 * set list of validation activation rules.
	 * @param list validation activation rules
	 */
	public void setRules(List list)
	{
		rules = list;
	}

}
