/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c) 2004, Open Edge B.V.,  All Rights Reserved.
 */
package nl.openedge.util.hibernate;

import junit.framework.TestCase;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;

/**
 * SetParametersExceptionCommand is een speciale mock class voor het gooien van een
 * HibernateException vanuit de aanroep van setParameters().
 */
public class SetParametersExceptionCommand extends AbstractQueryCommand
{
	/**
	 * De exceptie die gegooid moet worden.
	 */
	private HibernateException exception;

	/**
	 * De query waarop gecontroleerd moet worden.
	 */
	private Query mockQuery;

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            de naam van de query.
	 * @param e
	 *            de exceptie die gegooid moet worden.
	 * @param deMockQuery
	 *            de query die gecontroleerd moet worden.
	 */
	public SetParametersExceptionCommand(final String name, final HibernateException e,
			final Query deMockQuery)
	{
		super(name);
		exception = e;
		mockQuery = deMockQuery;
	}

	/**
	 * @see nl.openedge.medischevaria.util.AbstractQueryCommand#setParameters(net.sf.hibernate.Query)
	 */
	protected void setParameters(final Query query) throws HibernateException
	{
		TestCase.assertEquals(mockQuery, query);
		throw exception;
	}
}