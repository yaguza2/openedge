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

/**
 * Parameter voor boolean waarden.
 */
public final class BooleanParameter extends Parameter
{
    /** serial UUID. */
	private static final long serialVersionUID = 7323610389383248275L;

    /** Descriptor. */
	private static final transient ParameterDescriptor DESCRIPTOR = new Descriptor();

	/**
	 * Construct.
	 */
	public BooleanParameter()
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

		Boolean bool = Boolean.valueOf(valueAsString);
		ParameterValue value = new ParameterValue(bool);
		value.setAttributes(context);
		return value;
	}

	/**
	 * Shortcut naar parameter waarde.
	 * @return Boolean.TRUE, Boolean.FALSE of null indien de waarde niet was gezet
	 */
	public Boolean getBoolean()
	{
		if (getValue() != null)
		{
			return (Boolean) getValue().getValue();
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

	/** Input voor true. */
	private static final class BooleanTrueInput extends ParameterInput
	{
	    /** serial UUID. */
		private static final long serialVersionUID = 5330543003118537625L;

        /**
		 * @see nl.openedge.gaps.core.parameters.ParameterInput#getValue()
		 */
		public Object getValue()
		{
			return Boolean.TRUE;
		}
	}

	/** Input voor false. */
	private static final class BooleanFalseInput extends ParameterInput
	{
	    /** serial UUID. */
		private static final long serialVersionUID = 8417932073470822941L;

        /**
		 * @see nl.openedge.gaps.core.parameters.ParameterInput#getValue()
		 */
		public Object getValue()
		{
			return Boolean.FALSE;
		}
	}

	/**
	 * Descriptor klasse.
	 */
	private static final class Descriptor extends ParameterDescriptor
	{
	    /** serial UUID. */
		private static final long serialVersionUID = -4946883051756975846L;

        /** toegestane input waarden. */
		private static final ParameterInput[] POSSIBLE_VALUES = new ParameterInput[] {
				new BooleanTrueInput(), new BooleanFalseInput()};

		/**
		 * @see nl.openedge.gaps.core.parameters.ParameterDescriptor#getPossibleValues()
		 */
		public Object getPossibleValues()
		{
			return POSSIBLE_VALUES;
		}
	}

}