/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.support.berekeningen.functies;

import nl.openedge.gaps.core.parameters.AbstractDescriptor;

/**
 * Metadata over een functie.
 */
public abstract class FunctionDescriptor extends AbstractDescriptor
{

	/**
	 * Construct.
	 */
	public FunctionDescriptor()
	{
		super();
	}

	/**
	 * Geeft de types van de verwachte argumenten en hun namen.
	 * @return array van FunctionArgument instanties dat de verwachte parameter elementen
	 *         representeert
	 */
	public abstract FunctionArgument[] getArgumentTypes();
}