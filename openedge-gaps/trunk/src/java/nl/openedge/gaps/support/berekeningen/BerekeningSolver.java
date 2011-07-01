/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.support.berekeningen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import nl.openedge.gaps.core.NotFoundException;
import nl.openedge.gaps.core.RegistryException;
import nl.openedge.gaps.support.ParameterBrowser;
import nl.openedge.gaps.support.berekeningen.analysis.DepthFirstAdapter;
import nl.openedge.gaps.support.berekeningen.functies.Function;
import nl.openedge.gaps.support.berekeningen.functies.FunctionRegistry;
import nl.openedge.gaps.support.berekeningen.node.ACallExp;
import nl.openedge.gaps.support.berekeningen.node.ACastMinusExp;
import nl.openedge.gaps.support.berekeningen.node.ACastPlusExp;
import nl.openedge.gaps.support.berekeningen.node.ADivExp;
import nl.openedge.gaps.support.berekeningen.node.AIdExp;
import nl.openedge.gaps.support.berekeningen.node.AMinusExp;
import nl.openedge.gaps.support.berekeningen.node.AMultExp;
import nl.openedge.gaps.support.berekeningen.node.ANumberExp;
import nl.openedge.gaps.support.berekeningen.node.APlusExp;
import nl.openedge.gaps.support.berekeningen.node.Start;
import nl.openedge.gaps.support.berekeningen.node.Switchable;
import nl.openedge.gaps.support.berekeningen.node.TFuncid;
import nl.openedge.gaps.support.berekeningen.node.TId;
import nl.openedge.gaps.support.berekeningen.node.TNumber;
import nl.openedge.gaps.support.gapspath.GPathInterpreterException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Interpreteert de parse resultaten.
 */
public class BerekeningSolver extends DepthFirstAdapter
{

	/** log. */
	private static Log log = LogFactory.getLog(BerekeningSolver.class);

	/** houder voor eventuele context variabelen. */
	private Map context;

	/**
	 * tijdelijke numberStack voor numerieke (Double) waarden tussenresultaten/
	 * berekeningen.
	 */
	private Stack numberStack = new Stack();

	/** uiteindelijke resultaat na parse/ apply. */
	private Double result;

	/**
	 * Groepen/ Parameter browser.
	 */
	private ParameterBrowser gPathBrowser = new ParameterBrowser();

	/**
	 * Geeft resultaat na parse / apply.
	 * @return Double resultaat na parse/ apply
	 */
	public Double getResult()
	{
		if (result == null && (!numberStack.isEmpty()))
		{
			// waarschijnlijk slechts een deel uitgevoerd;
			// probeer laatste element van stack
			result = (Double) numberStack.pop();
			numberStack.clear();
		}
		return result;
	}

	/**
	 * @see nl.openedge.gaps.support.berekeningen.analysis.DepthFirstAdapter#outStart(nl.openedge.gaps.support.berekeningen.node.Start)
	 */
	public void outStart(Start node)
	{
		if (numberStack.isEmpty())
		{
			result = null;
		}
		else
		{
			result = (Double) numberStack.pop();
			numberStack.clear();
		}
	}

	/**
	 * @see nl.openedge.gaps.support.berekeningen.analysis.Analysis#caseAPlusExp(nl.openedge.gaps.support.berekeningen.node.APlusExp)
	 */
	public void outAPlusExp(APlusExp node)
	{
		Double y = (Double) numberStack.pop();
		Double x = (Double) numberStack.pop();
		double tempResult = x.doubleValue() + y.doubleValue();
		if (log.isDebugEnabled())
		{
			log.debug("op: " + x + " + " + y + " = " + tempResult);
		}
		numberStack.push(new Double(tempResult));
	}

	/**
	 * @see nl.openedge.gaps.support.berekeningen.analysis.DepthFirstAdapter#outADivExp(nl.openedge.gaps.support.berekeningen.node.ADivExp)
	 */
	public void outADivExp(ADivExp node)
	{
		Double y = (Double) numberStack.pop();
		Double x = (Double) numberStack.pop();
		double tempResult = x.doubleValue() / y.doubleValue();
		if (log.isDebugEnabled())
		{
			log.debug("op: " + x + " / " + y + " = " + tempResult);
		}
		numberStack.push(new Double(tempResult));
	}

	/**
	 * @see nl.openedge.gaps.support.berekeningen.analysis.DepthFirstAdapter#outAMinusExp(nl.openedge.gaps.support.berekeningen.node.AMinusExp)
	 */
	public void outAMinusExp(AMinusExp node)
	{
		Double y = (Double) numberStack.pop();
		Double x = (Double) numberStack.pop();
		double tempResult = x.doubleValue() - y.doubleValue();
		if (log.isDebugEnabled())
		{
			log.debug("op: " + x + " - " + y + " = " + tempResult);
		}
		numberStack.push(new Double(tempResult));
	}

	/**
	 * @see nl.openedge.gaps.support.berekeningen.analysis.DepthFirstAdapter#outAMultExp(nl.openedge.gaps.support.berekeningen.node.AMultExp)
	 */
	public void outAMultExp(AMultExp node)
	{
		Double y = (Double) numberStack.pop();
		Double x = (Double) numberStack.pop();
		double tempResult = x.doubleValue() * y.doubleValue();
		if (log.isDebugEnabled())
		{
			log.debug("op: " + x + " * " + y + " = " + tempResult);
		}
		numberStack.push(new Double(tempResult));
	}

	/**
	 * @see nl.openedge.gaps.support.berekeningen.analysis.DepthFirstAdapter#outANumberExp(nl.openedge.gaps.support.berekeningen.node.ANumberExp)
	 */
	public void outANumberExp(ANumberExp node)
	{
		TNumber number = node.getNumber();
		Double value = Double.valueOf(number.getText());
		if (log.isDebugEnabled())
		{
			log.debug("literal: " + value);
		}
		numberStack.push(value);
	}

	/**
	 * @see nl.openedge.gaps.support.berekeningen.analysis.DepthFirstAdapter#outACastMinusExp(nl.openedge.gaps.support.berekeningen.node.ACastMinusExp)
	 */
	public void outACastMinusExp(ACastMinusExp node)
	{
		changeSign();
	}

	/**
	 * @see nl.openedge.gaps.support.berekeningen.analysis.DepthFirstAdapter#outACastPlusExp(nl.openedge.gaps.support.berekeningen.node.ACastPlusExp)
	 */
	public void outACastPlusExp(ACastPlusExp node)
	{
		changeSign();
	}

	/**
	 * Verander sign.
	 */
	private void changeSign()
	{
		Double current = (Double) numberStack.pop();
		double value = (0 - current.doubleValue());
		if (log.isDebugEnabled())
		{
			log.debug("sgn: " + value);
		}
		numberStack.push(new Double(value));
	}

	/**
	 * @see nl.openedge.gaps.support.berekeningen.analysis.DepthFirstAdapter#outACallExp(nl.openedge.gaps.support.berekeningen.node.ACallExp)
	 */
	public void caseACallExp(ACallExp node)
	{

		TFuncid id = node.getFunc();
		String functionName = id.getText();
		// zoek functie op
		try
		{
			Function function = FunctionRegistry.getFunction(functionName);
			List parameters = node.getParams();
			List parameterValues = new ArrayList(parameters.size());
			for (Iterator i = parameters.iterator(); i.hasNext();)
			{
				Switchable param = (Switchable) i.next();
				BerekeningSolver translation = new BerekeningSolver();
				translation.setContext(this.context);
				param.apply(translation);
				Double parameterValue = translation.getResult();
				parameterValues.add(parameterValue);
				if (log.isDebugEnabled())
				{
					log.info(functionName
							+ " -> param: (" + param.getClass() + ") " + param + " = "
							+ parameterValue);
				}
			}
			Object value = function.perform(this.context, parameterValues.toArray());
			if (value instanceof Number)
			{
				Double number = null;
				number = new Double(((Number) value).doubleValue());
				if (log.isDebugEnabled())
				{
					log.debug("ctx-param: " + value);
				}
				numberStack.push(number);
			}
			else
			{
				throw new BerekeningSolverException("resultaat functie "
						+ functionName + " is van een ongeldig type in deze context");
				// TODO ondersteuning voor bijv boolean
			}
		}
		catch (RegistryException e)
		{
			log.error(e.getMessage(), e);
			throw new BerekeningSolverException(e);
		}
		catch (NotFoundException e)
		{
			log.error(e.getMessage(), e);
			throw new BerekeningSolverException(e);
		}
	}

	/**
	 * @see nl.openedge.gaps.support.berekeningen.analysis.DepthFirstAdapter#outAIdExp(nl.openedge.gaps.support.berekeningen.node.AIdExp)
	 */
	public void outAIdExp(AIdExp node)
	{
		TId variable = node.getId();
		String varName = variable.getText();
		// we kijken eerst of er een variabele in de context aanwezig is
		Object value = getContextVariable(varName);
		if (value != null)
		{ // en zo ja, gebruik deze
			handleContextVariable(varName, value);
		}
		else
		{ // geen context variabele geregisteerd; zoek in registry naar
			// parameter
			handleParameter(varName);
		}
	}

	/**
	 * Actie bij gevonden context variabele.
	 * @param name variabele naam
	 * @param value waarde
	 */
	private void handleContextVariable(String name, Object value)
	{

		if (value instanceof Number)
		{
			Double number = null;
			number = new Double(((Number) value).doubleValue());
			if (log.isDebugEnabled())
			{
				log.debug("ctx-param: " + value);
			}
			numberStack.push(number);
		}
		else
		{
			throw new BerekeningSolverException("variabele "
					+ name + " is van een ongeldig type in deze context");
			// TODO ondersteuning voor bijv boolean
		}
	}

	/**
	 * Zoek een variabele op in de context.
	 * @param name variabele naam
	 * @return de variabele indien gevonden, anders null
	 */
	public Object getContextVariable(String name)
	{
		String varName = getContextParamName(name);
		Object value = getContextParam(varName);
		return value;
	}

	/**
	 * Actie behandelen parameter (indien geen context variabele is gevonden).
	 * @param name variabele naam
	 */
	public void handleParameter(String name)
	{

		String expr = getDefinedParamName(name);
		try
		{
			Object tempResult = gPathBrowser.navigate(expr);
			if (log.isDebugEnabled())
			{
				log.debug("param: " + tempResult);
			}
			if (tempResult == null)
			{
				throw new BerekeningSolverException("expressie "
						+ expr + " gaf geen resultaten");
			}
			if (tempResult instanceof Number)
			{
				numberStack.push(tempResult);
			}
			else
			{
				throw new BerekeningSolverException("objecten van het type "
						+ tempResult.getClass().getName()
						+ " worden niet ondersteund in berekeningen (object = " + tempResult);
			}
		}
		catch (GPathInterpreterException e)
		{
			throw new BerekeningSolverException(e);
		}

	}

	/**
	 * Get context.
	 * @return the context.
	 */
	public Map getContext()
	{
		return context;
	}

	/**
	 * Geeft parameter uit context met null check.
	 * @param key sleutel
	 * @return waarde bij sleutel of null indien niet gevonden
	 */
	public Object getContextParam(String key)
	{
		Object value = null;
		if (context != null)
		{
			value = context.get(key);
		}
		return value;
	}

	/**
	 * Geeft context parameter naam zonder evt voorloop.
	 * @param unstrippedKey ongestripte naam
	 * @return gestripte naam
	 */
	private String getContextParamName(String unstrippedKey)
	{
		return stripFirstChar(unstrippedKey, ':');
	}

	/**
	 * Geeft voorgedefinieerde parameter naam zonder evt voorloop.
	 * @param unstrippedKey ongestripte naam
	 * @return gestripte naam
	 */
	private String getDefinedParamName(String unstrippedKey)
	{
		return stripFirstChar(unstrippedKey, ':');
	}

	/**
	 * Strip invoer van evt eerste karakter.
	 * @param unstrippedKey nog niet gestripte key
	 * @param c te strippen karakter
	 * @return de gestripte key
	 */
	private String stripFirstChar(String unstrippedKey, char c)
	{
		String key;
		if (unstrippedKey.indexOf(c) == 0)
		{
			key = unstrippedKey.substring(1);
		}
		else
		{
			key = unstrippedKey;
		}
		return key;
	}

	/**
	 * Set context.
	 * @param context context to set.
	 */
	public void setContext(Map context)
	{
		this.context = context;
	}

}