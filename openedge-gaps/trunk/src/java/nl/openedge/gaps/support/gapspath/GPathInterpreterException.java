/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.support.gapspath;

/**
 * Exception die kan worden gegooid indien er zaken misgaan bij het gebruiken van de
 * interpreter.
 */
public final class GPathInterpreterException extends ParserException
{
    /** serial UUID. */
	private static final long serialVersionUID = 6902056327112509701L;

    /**
	 * Construct.
	 */
	public GPathInterpreterException()
	{
		super();
	}

	/**
	 * Construct.
	 * @param message message
	 */
	public GPathInterpreterException(String message)
	{
		super(message);
	}

	/**
	 * Construct.
	 * @param cause cause
	 */
	public GPathInterpreterException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Construct.
	 * @param message message
	 * @param cause cause
	 */
	public GPathInterpreterException(String message, Throwable cause)
	{
		super(message, cause);
	}

}