/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters.impl;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;

import nl.openedge.gaps.core.Constants;
import nl.openedge.gaps.core.parameters.InputException;
import nl.openedge.gaps.core.parameters.Parameter;
import nl.openedge.gaps.core.parameters.ParameterDescriptor;
import nl.openedge.gaps.core.parameters.ParameterInput;
import nl.openedge.gaps.core.parameters.ParameterValue;
import nl.openedge.gaps.core.parameters.TextInput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Parameter voor percentage waarden.
 */
public final class PercentageParameter extends Parameter {

    /** Descriptor. */
    private static final transient ParameterDescriptor DESCRIPTOR = new Descriptor();

    /** Number format Nederlandse locale. */
    private static final transient NumberFormat DEFAULT_NUMBER_FORMAT = NumberFormat
            .getNumberInstance(Constants.NEDERLANDSE_LOCALE);

    /** Te gebruiken log. */
    private static transient Log log = LogFactory.getLog(DateParameter.class);

    /**
     * Construct.
     */
    public PercentageParameter() {
        super();
    }

    /**
     * @see nl.openedge.gaps.core.parameters.Parameter#getDescriptor()
     */
    public ParameterDescriptor getDescriptor() {
        return DESCRIPTOR;
    }

    /**
     * @see nl.openedge.gaps.core.parameters.Parameter#createValue(java.util.Map,
     *      java.lang.String)
     */
    public ParameterValue createValue(Map context, String valueAsString) throws InputException {

        ParameterValue value = null;
        try {
            Number number = DEFAULT_NUMBER_FORMAT.parse(valueAsString);
            Double doubleVal = new Double(number.doubleValue());
            value = new PercentageParameterValue(doubleVal);
            value.setAttributes(context);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            throw new InputException(e);
        }
        return value;
    }

    /**
     * Shortcut naar parameter waarde (geeft fractie 20,5% -> 0.205).
     * @return instantie van Number of null indien de waarde niet was gezet
     */
    public Number getNumber() {
        if (getValue() != null) {
            return (Number) getValue().getValue();
        } else {
            return null;
        }
    }

    /**
     * Shortcut naar parameter waarde factor (geeft fractie 20,5% -> 0.205).
     * @return instantie van Double of null indien de waarde niet was gezet
     */
    public Double getFactor() {
        if (getValue() != null) {
            return new Double(((PercentageParameterValue) getValue()).getFactorValue());
        } else {
            return null;
        }
    }

    /**
     * Shortcut naar parameter waarde percentage (geeft fractie 20,5% -> 20.5).
     * @return instantie van Double of null indien de waarde niet was gezet
     */
    public Double getPercentage() {
        if (getValue() != null) {
            return new Double(((PercentageParameterValue) getValue()).getPercentageValue());
        } else {
            return null;
        }
    }

    /**
     * @see nl.openedge.gaps.core.parameters.Parameter#createValue(java.util.Map,
     *      java.lang.String[])
     */
    public ParameterValue createValue(Map context, String[] valueAsString) throws InputException {

        return createValueWithSingleString(context, valueAsString);
    }

    /**
     * Descriptor klasse.
     */
    private static final class Descriptor extends ParameterDescriptor {

        /** toegestane input waarden. */
        private static final ParameterInput[] POSSIBLE_VALUES = new ParameterInput[] { new TextInput(
                Descriptor.class) };

        /**
         * @see nl.openedge.gaps.core.parameters.ParameterDescriptor#getPossibleValues()
         */
        public Object getPossibleValues() {
            return POSSIBLE_VALUES;
        }
    }
}