/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c) 2004, Open Edge B.V.,  All Rights Reserved.
 */
package nl.openedge.util.hibernate;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * AbstractHibernateCommandTest controleert de AbstractQueryCommand binnen een Unit test. Eigenlijk
 * wordt alleen de <code>execute</code> methode getest, aangezien de overige methoden niet
 * aanroepbaar zijn vanaf de buitenwereld.
 */
public class AbstractQueryCommandTest extends TestCase
{
	/**
	 * De mock Hibernate sessie.
	 */
	private Session mockSession;

	/**
	 * De mock Hibernate query.
	 */
	private Query mockQuery;

	/**
	 * Control voor het maken van mock Session objecten.
	 */
	private MockControl mockSessionControl = MockControl.createControl(Session.class);

	/**
	 * Control voor het maken van mock Query objecten.
	 */
	private MockControl mockQueryControl = MockControl.createControl(Query.class);

	/**
	 * Creeert de mock objecten voor het gebruik in deze tests.
	 */
	public void setUp()
	{
		mockSession = (Session) mockSessionControl.getMock();
		mockQuery = (Query) mockQueryControl.getMock();
	}

	/**
	 * Controleer of de juiste query aan Hibernate wordt gevraagd en of de query daadwerkelijk al
	 * uitgevoerd wordt en niet uitgesteld tot de getResultaat() functie. Als laatste wordt
	 * gecontroleerd of de juiste lijst wel wordt teruggegeven.
	 * 
	 * @throws HibernateException
	 *             nooit, maar noodzakelijk voor de compilatie.
	 */
	public void testExecute() throws HibernateException
	{
		// instellen van specifieke zaken voor deze test
		mockSession.getNamedQuery("myTestQuery");
		mockSessionControl.setReturnValue(mockQuery);

		ArrayList mockResult = new ArrayList();
		mockQuery.list();
		mockQueryControl.setReturnValue(mockResult);

		mockQueryControl.replay();
		mockSessionControl.replay();

		AbstractQueryCommand command = new AbstractQueryCommand("myTestQuery")
		{
		};
		// de eigenlijke test
		command.execute(mockSession);

		mockSessionControl.verify();
		mockQueryControl.verify();

		assertEquals(mockResult, command.getResultaat());
	}

	/**
	 * Controleert of een exceptie uit hibernate.Session.getNamedQuery() wel doorgegeven wordt aan
	 * de omliggende omgeving.
	 * 
	 * @throws Exception
	 *             speciaal voor compiler.
	 */
	public void testNamedQueryException() throws Exception
	{
		HibernateException exception = new HibernateException("Test exception");

		// instellen van specifieke zaken voor deze test
		mockSession.getNamedQuery("myTestQuery");
		mockSessionControl.setThrowable(exception);

		mockQueryControl.replay();
		mockSessionControl.replay();

		AbstractQueryCommand command = new AbstractQueryCommand("myTestQuery")
		{
		};
		try
		{
			// de eigenlijke test
			command.execute(mockSession);
		}
		catch (HibernateException e)
		{
			assertEquals(exception, e);
		}
		mockSessionControl.verify();
		mockQueryControl.verify();
	}

	/**
	 * Controleert of een HibernateException gegooid vanuit setParameters wel doorgegeven wordt aan
	 * de omgeving.
	 * 
	 * @throws Exception
	 *             speciaal voor de compiler.
	 */
	public void testSetParameterException() throws Exception
	{
		HibernateException exception = new HibernateException("Test exception");

		// instellen van specifieke zaken voor deze test
		mockSession.getNamedQuery("myTestQuery");
		mockSessionControl.setReturnValue(mockQuery);

		mockQueryControl.replay();
		mockSessionControl.replay();

		AbstractQueryCommand command = new SetParametersExceptionCommand("myTestQuery", exception,
				mockQuery);
		try
		{
			// de eigenlijke test
			command.execute(mockSession);
		}
		catch (HibernateException e)
		{
			assertEquals(exception, e);
		}
		mockSessionControl.verify();
		mockQueryControl.verify();
	}

	/**
	 * Controleert of een HibernateException gegooid vanuit Query.list wel doorgegeven wordt aan de
	 * omgeving.
	 * 
	 * @throws Exception
	 *             Speciaal voor de compiler.
	 */
	public void testListException() throws Exception
	{
		HibernateException exception = new HibernateException("Test exception");

		// instellen van specifieke zaken voor deze test
		mockSession.getNamedQuery("myTestQuery");
		mockSessionControl.setReturnValue(mockQuery);

		mockQuery.list();
		mockQueryControl.setThrowable(exception);

		mockQueryControl.replay();
		mockSessionControl.replay();

		AbstractQueryCommand command = new AbstractQueryCommand("myTestQuery")
		{
		};
		try
		{
			// de eigenlijke test
			command.execute(mockSession);
		}
		catch (HibernateException e)
		{
			assertEquals(exception, e);
		}
		mockSessionControl.verify();
		mockQueryControl.verify();
	}
}