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
	public void addAllRules(List<ValidationActivationRule> rules)
	{
		if (this.rules == null)
		{
			this.rules = new ArrayList<ValidationActivationRule>();
		}
		this.rules.addAll(rules);
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
