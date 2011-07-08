/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c) 2004, Open Edge B.V.,  All Rights Reserved.
 */
package nl.openedge.util.hibernate;

/**
 * RuntimeException die wordt opgegooid door HibernateCommands indien zich onverwachte situaties
 * voordoen.
 */
public class HibernateCommandException extends RuntimeException
{

	/**
	 * Creeer.
	 */
	public HibernateCommandException()
	{
		super();
	}

	/**
	 * Creeer met foutbericht.
	 * 
	 * @param message
	 *            foutbericht
	 */
	public HibernateCommandException(final String message)
	{
		super(message);
	}

	/**
	 * Creeer met oorzaak.
	 * 
	 * @param cause
	 *            oorzaak
	 */
	public HibernateCommandException(final Throwable cause)
	{
		super(cause);
	}

	/**
	 * Creeer met foutbericht en oorzaak.
	 * 
	 * @param message
	 *            foutboodschap
	 * @param cause
	 *            oorzaak
	 */
	public HibernateCommandException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

}
