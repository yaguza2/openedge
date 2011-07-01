/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.support.gapspath;

/**
 * Basisklasse voor checked exceptions die met de parser packages samenhangen.
 */
public abstract class ParserException extends UncheckedParserException
{

	/**
	 * Construct.
	 */
	public ParserException()
	{
		super();
	}

	/**
	 * Construct.
	 * @param message message
	 */
	public ParserException(String message)
	{
		super(message);
	}

	/**
	 * Construct.
	 * @param cause cause
	 */
	public ParserException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Construct.
	 * @param message message
	 * @param cause cause
	 */
	public ParserException(String message, Throwable cause)
	{
		super(message, cause);
	}

}