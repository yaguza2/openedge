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
import java.util.List;

import junit.framework.TestCase;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;

import org.easymock.MockControl;

/**
 * HibernateHelperTest.
 */
public class HibernateInvokerTest extends TestCase
{
	/**
	 * Vlag voor controle of de methode wel aangeroepen is.
	 */
	boolean closeResourcesAangeroepen = false;

	/**
	 * Mock object voor HibernateInvoker.
	 */
	private final class MockHibernateHelper extends HibernateInvoker
	{

		/**
		 * @see nl.openedge.medischevaria.util.HibernateHelper#getFactory()
		 */
		SessionFactory getFactory()
		{
			return mockFactory;
		}

		/**
		 * @see nl.openedge.util.hibernate.HibernateInvoker#getSession()
		 */
		protected Session getSession() throws HibernateException
		{
			return mockSession;
		}

		/**
		 * @see nl.openedge.util.hibernate.HibernateInvoker#closeResources()
		 */
		protected void closeResources() throws HibernateException
		{
			closeResourcesAangeroepen = true;
		}
	}

	/**
	 * Mock session factory.
	 */
	private SessionFactory mockFactory;

	/**
	 * Het HibernateCommand dat geexecuteerd moet worden.
	 */
	private HibernateCommand mockCommand;

	/**
	 * De mock Hibernate sessie.
	 */
	private Session mockSession;

	/**
	 * De mock Hibernate query.
	 */
	private Query mockQuery;

	/**
	 * Control voor het maken van mock Factory objecten.
	 */
	private MockControl mockFactoryControl = MockControl.createControl(SessionFactory.class);

	/**
	 * Control voor het maken van mock Session objecten.
	 */
	private MockControl mockSessionControl = MockControl.createControl(Session.class);

	/**
	 * Control voor het maken van mock Query objecten.
	 */
	private MockControl mockQueryControl = MockControl.createControl(Query.class);

	/**
	 * Control voor het maken van mock HibernateCommand objecten.
	 */
	private MockControl mockCommandControl = MockControl.createControl(HibernateCommand.class);

	/**
	 * Creeert de mock objecten voor het gebruik in deze tests.
	 */
	public void setUp()
	{
		mockFactory = (SessionFactory) mockFactoryControl.getMock();
		mockSession = (Session) mockSessionControl.getMock();
		mockQuery = (Query) mockQueryControl.getMock();
		mockCommand = (HibernateCommand) mockCommandControl.getMock();
		closeResourcesAangeroepen = false;
	}

	/**
	 * Controleer of execute functie werkt. initHibernateSession is uitgeschakeld via inner class
	 * MockHibernateHelper, het verkrijgen van een session loopt via session field.
	 * 
	 * @throws HibernateException
	 *             indien uitvoeren niet lukt
	 */
	public void testExecute() throws HibernateException
	{

		List mockResultaat = new ArrayList();
		mockResultaat.add(new TestClass());

		mockCommand.execute(mockSession);
		mockCommandControl.setVoidCallable();

		mockSessionControl.replay();
		mockQueryControl.replay();
		mockFactoryControl.replay();
		mockCommandControl.replay();

		HibernateInvoker hibernateHelper = new MockHibernateHelper();

		hibernateHelper.execute(mockCommand);

		assertTrue("Aanroep van closeResources", closeResourcesAangeroepen);

		mockSessionControl.verify();
		mockQueryControl.verify();
		mockFactoryControl.verify();
		mockCommandControl.verify();
	}

	/**
	 * Controleer of execute functie werkt als er uit het command een exception gegooid wordt.
	 * initHibernateSession is uitgeschakeld via inner class MockHibernateHelper, het verkrijgen van
	 * een session loopt via session field.
	 * 
	 * @throws HibernateException
	 *             indien uitvoeren niet lukt
	 */
	public void testExecuteHibernateException() throws HibernateException
	{

		List mockResultaat = new ArrayList();
		mockResultaat.add(new TestClass());

		HibernateException e = new HibernateException("Test");
		mockCommand.execute(mockSession);
		mockCommandControl.setThrowable(e);

		mockSessionControl.replay();
		mockQueryControl.replay();
		mockFactoryControl.replay();
		mockCommandControl.replay();

		HibernateInvoker hibernateHelper = new MockHibernateHelper();

		try
		{
			hibernateHelper.execute(mockCommand);
			fail("execute zou een exception hebben moeten opgegooid");
		}
		catch (RuntimeException e1)
		{
			assertTrue("exception zou van type "
					+ HibernateCommandException.class.getName() + " moeten zijn",
					(e1 instanceof HibernateCommandException));
		}

		assertTrue("Aanroep van closeResources", closeResourcesAangeroepen);

		mockSessionControl.verify();
		mockQueryControl.verify();
		mockFactoryControl.verify();
		mockCommandControl.verify();
	}
}