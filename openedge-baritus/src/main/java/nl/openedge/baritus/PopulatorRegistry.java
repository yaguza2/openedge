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

	private Map<String, FieldPopulator> fieldPopulators = null;

	private Map<Pattern, FieldPopulator> regexFieldPopulators = null;

	private FieldPopulator defaultFieldPopulator = null;

	/**
	 * construct registry with the current instance of FormBeanBase
	 * 
	 * @param formBeanCtrl
	 */
	public PopulatorRegistry(FormBeanCtrlBase formBeanCtrl)
	{
		defaultFieldPopulator = new OgnlFieldPopulator(formBeanCtrl);
	}

	/**
	 * Register a field populator for the given fieldName. Field populators override the
	 * default population of a property on the current form
	 * 
	 * @param fieldName
	 *            name of field
	 * @param populator
	 *            populator instance
	 */
	public void addPopulator(String fieldName, FieldPopulator populator)
	{
		if (fieldPopulators == null)
		{
			fieldPopulators = new HashMap<String, FieldPopulator>();
		}
		fieldPopulators.put(fieldName, populator);
	}

	/**
	 * de-register the field populator that was registered with the given fieldName
	 * 
	 * @param fieldName
	 *            name of field
	 */
	public void removePopulator(String fieldName)
	{
		if (fieldPopulators != null)
		{
			fieldPopulators.remove(fieldName);
			if (fieldPopulators.isEmpty())
				fieldPopulators = null;
		}
	}

	/**
	 * Register a custom populator that overrides the default population process for all
	 * request parameters that match the regular expression stored in the provided
	 * pattern.
	 * 
	 * @param pattern
	 *            regex pattern
	 * @param populator
	 *            populator instance
	 */
	public void addPopulator(Pattern pattern, FieldPopulator populator)
	{
		if (regexFieldPopulators == null)
		{
			regexFieldPopulators = new HashMap<Pattern, FieldPopulator>();
		}
		regexFieldPopulators.put(pattern, populator);
	}

	/**
	 * Remove a populator that was registered for the provided pattern
	 * 
	 * @param pattern
	 *            regex pattern
	 */
	public void removePopulator(Pattern pattern)
	{
		if (regexFieldPopulators != null)
		{
			regexFieldPopulators.remove(pattern);
		}
	}

	/**
	 * get the populators that were registered with regex patterns
	 * 
	 * @return Map the populators that were registered with regex patterns
	 */
	public Map<Pattern, FieldPopulator> getRegexFieldPopulators()
	{
		return regexFieldPopulators;
	}

	/**
	 * get the field populator for the provided fieldName, null if none registered
	 * 
	 * @param fieldName
	 *            name of the field
	 * @return the field populator for the provided fieldName, null if none registered
	 */
	public FieldPopulator getFieldPopulator(String fieldName)
	{
		return (fieldPopulators != null) ? fieldPopulators.get(fieldName) : null;
	}

	/**
	 * get the field populators
	 * 
	 * @return Map the field populators
	 */
	public Map<String, FieldPopulator> getFieldPopulators()
	{
		return fieldPopulators;
	}

	/**
	 * get the default field populator
	 * 
	 * @return FieldPopulator the default field populator
	 */
	public FieldPopulator getDefaultFieldPopulator()
	{
		return defaultFieldPopulator;
	}

	/**
	 * set the default field populator
	 * 
	 * @param populator
	 *            the default field populator
	 */
	public void setDefaultFieldPopulator(FieldPopulator populator)
	{
		defaultFieldPopulator = populator;
	}

}
