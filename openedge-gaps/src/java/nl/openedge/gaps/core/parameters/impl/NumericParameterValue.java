/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters.impl;

import nl.openedge.gaps.core.parameters.ParameterValue;

/**
 * Houder voor numerieke parameterwaarden.
 */
public final class NumericParameterValue extends ParameterValue
{
    /** serial UUID. */
	private static final long serialVersionUID = 4222735569372954350L;

    /**
	 * Construct.
	 * @param value de waarde waarmee deze parameter dient te worden aangemaakt
	 */
	public NumericParameterValue(Object value)
	{
		super(value);
	}

	/**
	 * Geeft de werkelijke (onderliggende) waarde als een double.
	 * @return de werkelijke (onderliggende) waarde als een double
	 */
	public double getDoubleValue()
	{
		return ((Number) value).doubleValue();
	}

	/**
	 * Geeft de werkelijke (onderliggende) waarde als een int.
	 * @return de werkelijke (onderliggende) waarde als een int
	 */
	public int getIntValue()
	{
		return ((Number) value).intValue();
	}

	/**
	 * Geeft de werkelijke (onderliggende) waarde als een long.
	 * @return de werkelijke (onderliggende) waarde als een long
	 */
	public double getLongValue()
	{
		return ((Number) value).longValue();
	}

	/**
	 * Geeft de werkelijke (onderliggende) waarde als een float.
	 * @return de werkelijke (onderliggende) waarde als een float
	 */
	public double getFloatValue()
	{
		return ((Number) value).floatValue();
	}

	/**
	 * Geeft de werkelijke (onderliggende) waarde als een Number.
	 * @return de werkelijke (onderliggende) waarde als een Number
	 */
	public Number getNumber()
	{
		return (Number) value;
	}
}