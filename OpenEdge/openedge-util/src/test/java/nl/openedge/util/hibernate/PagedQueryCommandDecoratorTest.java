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

import org.easymock.MockControl;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * PagedQueryCommandDecoratorTest controleert de PagedQueryCommandDecorator binnen een Unit test.
 */
public class PagedQueryCommandDecoratorTest extends TestCase
{
	/**
	 * Maximum aantal records per resultset.
	 */
	final int max = 20;

	/**
	 * Start record in de resultset.
	 */
	final int start = 13;

	/**
	 * De mock Hibernate sessie.
	 */
	private Session mockSession;

	/**
	 * De mock Hibernate query.
	 */
	private Query mockQuery;

	/**
	 * De mock query command.
	 */
	private MockQueryCommand mockCommand = new MockQueryCommand();

	/**
	 * Control voor het maken van mock Session objecten.
	 */
	private MockControl mockSessionControl = MockControl.createControl(Session.class);

	/**
	 * Control voor het maken van mock Query objecten.
	 */
	private MockControl mockQueryControl = MockControl.createControl(Query.class);

	/**
	 * MockQueryCommand om te controleren of de decorator wel alle calls delegeert naar zijn
	 * decoratedCommand. Is geimplementeerd omdat EasyMock geen extends snapt, maar enkel
	 * interfaces.
	 * 
	 * @author Martijn Dashorst
	 */
	public class MockQueryCommand extends AbstractQueryCommand
	{
		/**
		 * Vlag of functie aangeroepen is.
		 */
		boolean setParametersCalled = false;

		/**
		 * Vlag of functie aangeroepen is.
		 */
		boolean getQueryCalled = false;

		/**
		 * Vlag of functie aangeroepen is.
		 */
		boolean setResultaatCalled = false;

		/**
		 * Default constructor.
		 * 
		 * @param deQuery
		 */
		public MockQueryCommand()
		{
			super("MyTestQuery");
		}

		/**
		 * Mock implementatie van getQuery.
		 * 
		 * @param hibernateSession
		 *            de sessie waarop de query gezocht moet worden.
		 * @throws HibernateException
		 *             nooit.
		 * @return de mock query.
		 */
		protected Query getQuery(final Session hibernateSession) throws HibernateException
		{
			getQueryCalled = true;
			assertEquals(mockSession, hibernateSession);
			mockSession.getNamedQuery(getQueryNaam());
			return mockQuery;
		}

		/**
		 * Mock implementatie van setParameters.
		 * 
		 * @param query
		 *            het Query object waarop de parameters gezet moeten worden.
		 * @throws HibernateException
		 *             nooit.
		 */
		protected void setParameters(final Query query) throws HibernateException
		{
			setParametersCalled = true;
			assertEquals(mockQuery, query);
		}

		/**
		 * Mock implementatie van setResultaat.
		 * 
		 * @param query
		 *            het Query object waarop de parameters gezet moeten worden.
		 * @throws HibernateException
		 *             nooit.
		 */
		protected void setResultaat(final Query query) throws HibernateException
		{
			setResultaatCalled = true;
			assertEquals(mockQuery, query);
		}

		/**
		 * Controleert of het mock object op de juiste manier gebruikt is.
		 */
		public void verify()
		{
			assertTrue("getQuery is not called", getQueryCalled);
			assertTrue("setParameters is not called", setParametersCalled);
			assertTrue("setResultaat is not called", setResultaatCalled);
		}
	}

	/**
	 * Creeert de mock objecten voor het gebruik in deze tests.
	 */
	public void setUp()
	{
		mockSession = (Session) mockSessionControl.getMock();
		mockQuery = (Query) mockQueryControl.getMock();
	}

	/**
	 * Test of de setMaxResults en setFirstResult methodes wel goed aangeroepen worden door de
	 * decorator.
	 * 
	 * @throws HibernateException
	 *             nooit.
	 */
	public void testSetMaxEnFirst() throws HibernateException
	{
		mockSession.getNamedQuery("MyTestQuery");
		mockSessionControl.setReturnValue(mockQuery);

		mockQuery.setMaxResults(max);
		mockQueryControl.setReturnValue(mockQuery);
		mockQuery.setFirstResult(start);
		mockQueryControl.setReturnValue(mockQuery);

		mockQueryControl.replay();
		mockSessionControl.replay();

		PagedQueryCommandDecorator decorator = new PagedQueryCommandDecorator(mockCommand, start,
				max);
		decorator.execute(mockSession);

		mockQueryControl.verify();
		mockSessionControl.verify();
		mockCommand.verify();
	}

	/**
	 * Controleert of een exceptie uit hibernate.Session.getNamedQuery() wel doorgegeven wordt aan
	 * de omliggende omgeving.
	 * 
	 * @throws Exception
	 *             nooit.
	 */
	public void testNamedQueryException() throws Exception
	{
		HibernateException exception = new HibernateException("Test exception");

		// instellen van specifieke zaken voor deze test
		mockSession.getNamedQuery("myTestQuery");
		mockSessionControl.setThrowable(exception);

		mockQueryControl.replay();
		mockSessionControl.replay();

		AbstractQueryCommand decoratedMockCommand = new AbstractQueryCommand("myTestQuery")
		{
		};
		PagedQueryCommandDecorator decorator = new PagedQueryCommandDecorator(decoratedMockCommand,
				start, max);
		try
		{
			// de eigenlijke test
			decorator.execute(mockSession);
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
	 *             voor de compiler.
	 */
	public void testSetParameterException() throws Exception
	{
		HibernateException exception = new HibernateException("Test exception");

		// instellen van specifieke zaken voor deze test
		mockSession.getNamedQuery("myTestQuery");
		mockSessionControl.setReturnValue(mockQuery);

		mockQueryControl.replay();
		mockSessionControl.replay();

		AbstractQueryCommand decoratedMockCommand = new SetParametersExceptionCommand(
				"myTestQuery", exception, mockQuery);
		PagedQueryCommandDecorator decorator = new PagedQueryCommandDecorator(decoratedMockCommand,
				start, max);
		try
		{
			// de eigenlijke test
			decorator.execute(mockSession);
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
	 *             voor de compiler.
	 */
	public void testListException() throws Exception
	{
		HibernateException exception = new HibernateException("Test exception");

		// instellen van specifieke zaken voor deze test
		mockSession.getNamedQuery("myTestQuery");
		mockSessionControl.setReturnValue(mockQuery);

		mockQuery.list();
		mockQueryControl.setThrowable(exception);

		mockQuery.setMaxResults(max);
		mockQueryControl.setReturnValue(mockQuery);
		mockQuery.setFirstResult(start);
		mockQueryControl.setReturnValue(mockQuery);

		mockQueryControl.replay();
		mockSessionControl.replay();

		AbstractQueryCommand decoratedCommand = new AbstractQueryCommand("myTestQuery")
		{
		};
		PagedQueryCommandDecorator decorator = new PagedQueryCommandDecorator(decoratedCommand,
				start, max);
		try
		{
			// de eigenlijke test
			decorator.execute(mockSession);
		}
		catch (HibernateException e)
		{
			assertEquals(exception, e);
		}
		mockSessionControl.verify();
		mockQueryControl.verify();
	}
}
