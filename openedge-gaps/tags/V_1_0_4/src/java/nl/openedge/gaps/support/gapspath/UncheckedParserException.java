/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.support.gapspath;

import nl.openedge.gaps.core.UncheckedModelException;

/**
 * Basisklasse voor unchecked exceptions die met de parser packages samenhangen.
 */
public abstract class UncheckedParserException extends UncheckedModelException
{

	/**
	 * Construct.
	 */
	public UncheckedParserException()
	{
		super();
	}

	/**
	 * Construct.
	 * @param message message
	 */
	public UncheckedParserException(String message)
	{
		super(message);
	}

	/**
	 * Construct.
	 * @param cause cause
	 */
	public UncheckedParserException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Construct.
	 * @param message message
	 * @param cause cause
	 */
	public UncheckedParserException(String message, Throwable cause)
	{
		super(message, cause);
	}

}