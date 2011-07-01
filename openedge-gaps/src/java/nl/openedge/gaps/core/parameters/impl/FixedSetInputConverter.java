/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters.impl;

import nl.openedge.gaps.core.parameters.ConversionException;

/**
 * Classes die dit interface implementeren kunnen gebruikt worden voor conversies bij
 * {@link FixedSetParameter}s; door deze constructie kunnen we FixedSetParameter
 * hergebruiken voor sets van verschillende types.
 */
public interface FixedSetInputConverter
{

	/**
	 * Voer de conversie uit van de string waarde naar het gewenste object.
	 * @param valueAsString de waarde als een string
	 * @return de instantie van het gewenste object
	 * @throws ConversionException indien de invoer niet kan worden geconverteerd
	 */
	Object convert(String valueAsString) throws ConversionException;
}