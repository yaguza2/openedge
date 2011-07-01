/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters.impl;

import java.util.Map;

import nl.openedge.gaps.core.parameters.InputException;
import nl.openedge.gaps.core.parameters.Parameter;
import nl.openedge.gaps.core.parameters.ParameterDescriptor;
import nl.openedge.gaps.core.parameters.ParameterInput;
import nl.openedge.gaps.core.parameters.ParameterValue;
import nl.openedge.gaps.core.parameters.TextInput;

/**
 * Parameter voor string typen.
 */
public final class StringParameter extends Parameter
{

	/** Descriptor. */
	private static final transient ParameterDescriptor DESCRIPTOR = new Descriptor();

	/**
	 * Construct.
	 */
	public StringParameter()
	{
		super();
	}

	/**
	 * @see nl.openedge.gaps.core.parameters.Parameter#getDescriptor()
	 */
	public ParameterDescriptor getDescriptor()
	{
		return DESCRIPTOR;
	}

	/**
	 * @see nl.openedge.gaps.core.parameters.Parameter#createValue(java.util.Map,
	 *      java.lang.String)
	 */
	public ParameterValue createValue(Map context, String valueAsString)
			throws InputException
	{

		ParameterValue value = new ParameterValue(valueAsString);
		value.setAttributes(context);
		return value;
	}

	/**
	 * Shortcut naar parameter waarde.
	 * @return instantie van String of null indien de waarde niet was gezet
	 */
	public String getString()
	{
		if (getValue() != null)
		{
			return (String) getValue().getValue();
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
	 * Descriptor klasse.
	 */
	private static final class Descriptor extends ParameterDescriptor
	{

		/** toegestane input waarden. */
		private static final ParameterInput[] POSSIBLE_VALUES = new ParameterInput[] {new TextInput(
				Descriptor.class)};

		/**
		 * @see nl.openedge.gaps.core.parameters.ParameterDescriptor#getPossibleValues()
		 */
		public Object getPossibleValues()
		{
			return POSSIBLE_VALUES;
		}
	}
}