/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters.impl;

import java.util.Locale;

import com.voicetribe.util.convert.ConvertUtils;

import nl.openedge.gaps.core.parameters.ParameterValue;

/**
 * Houder voor numerieke parameterwaarden.
 */
public final class PercentageParameterValue extends ParameterValue
{
    /** serial UUID. */
	private static final long serialVersionUID = 8219029670786588105L;

    /** Constante voor percentage deling. */
	private static final double PERCENTAGE_FACTOR = 100d;

	/**
	 * Construct.
	 * @param value de waarde waarmee deze parameter dient te worden aangemaakt
	 */
	public PercentageParameterValue(Object value)
	{
		super(value);
	}

	/**
	 * Geeft de waarde als percentage; als de waarde bijvoorbeeld 50,5% is, retourneert
	 * deze methode 50.5d.
	 * @return de waarde als percentage
	 */
	public double getPercentageValue()
	{
		return ((Number) value).doubleValue();
	}

	/**
	 * Geeft de waarde als factor; als de waarde bijvoorbeeld 50,5% is, retourneert deze
	 * methode 0.505d.
	 * @return de waarde als percentage
	 */
	public double getFactorValue()
	{
		return ((Number) value).doubleValue() / PERCENTAGE_FACTOR;
	}

	/**
	 * Geeft de factor als een Double.
	 * @return de factor als een Double
	 * @see nl.openedge.gaps.core.parameters.ParameterValue#getValue()
	 */
	public Object getValue()
	{
		return new Double(getFactorValue());
	}

	/**
	 * Geeft de geformatteerde waarde.
	 * @param locale locale
	 * @return de geformatteerde waarde
	 */
	public String getFormattedValue(Locale locale)
	{
	    return ConvertUtils.getObjectFormatted(
	            new Double(getPercentageValue()), locale);
	}
}