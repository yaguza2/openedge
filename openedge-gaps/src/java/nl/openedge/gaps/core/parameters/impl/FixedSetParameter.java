/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nl.openedge.gaps.core.parameters.ConversionException;
import nl.openedge.gaps.core.parameters.InputException;
import nl.openedge.gaps.core.parameters.Parameter;
import nl.openedge.gaps.core.parameters.ParameterDescriptor;
import nl.openedge.gaps.core.parameters.ParameterInput;
import nl.openedge.gaps.core.parameters.ParameterValue;
import nl.openedge.gaps.core.parameters.ValueOutOfRangeException;

/**
 * Parameter type voor een beperkte set geldige waarden, bijvoorbeeld te renderen als een
 * drop down box, list of een verzameling checkboxen.
 */
public final class FixedSetParameter extends Parameter
{

	/** Set geldige inputs. */
	private Set inputs = new HashSet();

	/**
	 * Set met waarden voor snelle benadering - hoeven we niet door de inputs te itereren.
	 */
	private Set values = new HashSet();

	/** De optionele converter voor de invoer. */
	private FixedSetInputConverter converter = null;

	/**
	 * Construct.
	 */
	public FixedSetParameter()
	{
		super();
	}

	/**
	 * Construct.
	 * @param validInputs array met invoer definities die als geldig worden aangemerkt
	 * @param converter de optionele converter voor de invoer
	 */
	public FixedSetParameter(ParameterInput[] validInputs,
			FixedSetInputConverter converter)
	{
		this();
		internalSetValidInputs(validInputs);
		this.converter = converter;
	}

	/**
	 * Construct.
	 * @param validInputs array met invoer definities die als geldig worden aangemerkt
	 */
	public FixedSetParameter(ParameterInput[] validInputs)
	{
		this(validInputs, null);
	}

	/**
	 * Construct.
	 * @param converter de optionele converter voor de invoer
	 */
	public FixedSetParameter(FixedSetInputConverter converter)
	{
		this(null, converter);

	}

	/**
	 * @see nl.openedge.gaps.core.parameters.Parameter#getDescriptor()
	 */
	public ParameterDescriptor getDescriptor()
	{
		return new Descriptor(inputs);
	}

	/**
	 * @see nl.openedge.gaps.core.parameters.Parameter#createValue(java.util.Map,
	 *      java.lang.String)
	 */
	public ParameterValue createValue(Map context, String valueAsString)
			throws InputException
	{

		Object realValue = null;
		// doe conversie indien van toepassing
		if (converter != null)
		{
			try
			{
				realValue = converter.convert(valueAsString);
			}
			catch (Exception e)
			{
				// catch: extra check voor het geval de converter zich niet
				// netjes aan
				// het contract houdt
				throw new ConversionException(e);
			}
		}
		else
		{
			realValue = valueAsString;
		}
		checkValueInSet(realValue); // check
		ParameterValue value = new ParameterValue(realValue); // geen exception
		// -> goed
		value.setAttributes(context);
		return value;
	}

	/**
	 * check of de gegeven - geconverteerde waarde - tot de toegestane set behoort.
	 * @param realValue de te checken waarde
	 * @throws ValueOutOfRangeException indien de waarde NIET tot de toegestane set
	 *             behoort
	 */
	private void checkValueInSet(Object realValue) throws ValueOutOfRangeException
	{
		// check of de gegeven - geconverteerde waarde - tot de toegestane set
		// behoort
		if (!values.contains(realValue))
		{
			throw new ValueOutOfRangeException(realValue
					+ " behoort niet tot de set toegestane waarden");
		}
	}

	/**
	 * Shortcut naar parameter waarde.
	 * @return instantie van geselecteerde object of null indien de waarde niet was gezet
	 */
	public Object getSelected()
	{
		if (getValue() != null)
		{
			return getValue().getValue();
		}
		else
		{
			return null;
		}
	}

	/**
	 * @see nl.openedge.gaps.core.parameters.Parameter#createValue(java.util.Map,
	 *      java.lang.String[])
	 */
	public ParameterValue createValue(Map context, String[] valueAsString)
			throws InputException
	{

		return createValueWithSingleString(context, valueAsString);
	}

	/**
	 * Get inputs.
	 * @return the inputs.
	 */
	public Set getInputs()
	{
		return inputs;
	}

	/**
	 * Voeg een input optie toe.
	 * @param input input optie
	 */
	public void addInput(ParameterInput input)
	{
		inputs.add(input);
	}

	/**
	 * Set inputs.
	 * @param inputs inputs to set.
	 */
	public void setInputs(Set inputs)
	{
		this.inputs = inputs;
	}

	/**
	 * Get converter.
	 * @return converter.
	 */
	public FixedSetInputConverter getConverter()
	{
		return converter;
	}

	/**
	 * Set converter.
	 * @param converter converter.
	 */
	public void setConverter(FixedSetInputConverter converter)
	{
		this.converter = converter;
	}

	/**
	 * Zet de geldige invoer range.
	 * @param validInputs de geldige invoer range
	 * @param validInputs
	 */
	public void setValidInputs(ParameterInput[] validInputs)
	{
		inputs.clear();
		values.clear();
		internalSetValidInputs(validInputs);
	}

	/**
	 * Zet de geldige invoer range.
	 * @param validInputs de geldige invoer range
	 */
	private void internalSetValidInputs(ParameterInput[] validInputs)
	{
		if (validInputs != null)
		{
			inputs.addAll(Arrays.asList(validInputs));
			int size = validInputs.length;
			for (int i = 0; i < size; i++)
			{
				values.add(validInputs[i].getValue());
			}
		}
	}

	/**
	 * Descriptor klasse.
	 */
	private static final class Descriptor extends ParameterDescriptor
	{

		/** de geldige waarden. */
		private ParameterInput[] possibleValues;

		/**
		 * Creeer klasse met set geldige inputs.
		 * @param inputs set geldige inputs
		 */
		public Descriptor(Set inputs)
		{
			possibleValues = (ParameterInput[]) inputs.toArray(new ParameterInput[inputs
					.size()]);
		}

		/**
		 * @see nl.openedge.gaps.core.parameters.ParameterDescriptor#getPossibleValues()
		 */
		public Object getPossibleValues()
		{
			return possibleValues;
		}
	}
}