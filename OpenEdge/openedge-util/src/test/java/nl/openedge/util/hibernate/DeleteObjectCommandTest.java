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
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * AbstractUpdateCommand.
 */
public class DeleteObjectCommandTest extends TestCase
{
	/**
	 * Dummy object dat gesaved wordt.
	 */
	private Object saveObject;

	/**
	 * De Hibernate Session.
	 */
	private Session mockSession;

	/**
	 * De Hibernate Transaction.
	 */
	private Transaction mockTransaction;

	/**
	 * MockControl voor Transaction.
	 */
	private MockControl mockTransactionControl = MockControl.createControl(Transaction.class);

	/**
	 * MockControl voor Session.
	 */
	private MockControl mockSessionControl = MockControl.createControl(Session.class);

	/**
	 * Creeert de mock objecten voor het gebruik in deze tests.
	 */
	@Override
	public void setUp()
	{
		mockSession = (Session) mockSessionControl.getMock();
		mockTransaction = (Transaction) mockTransactionControl.getMock();
		saveObject = new Object();
	}

	/**
	 * Controleer of het juiste object gepersisteerd wordt via saveOrUpdate en of dit
	 * binnen 1 transactie gebeurt.
	 * 
	 * @throws HibernateException
	 *             nooit, maar noodzakelijk voor de compilatie.
	 */
	public void testExecute() throws HibernateException
	{
		// instellen van specifieke zaken voor deze test
		mockSession.beginTransaction();
		mockSessionControl.setReturnValue(mockTransaction);

		mockSession.delete(saveObject);
		mockTransaction.commit();

		mockSessionControl.replay();
		mockTransactionControl.replay();

		DeleteObjectsCommand command = new DeleteObjectsCommand();
		// de eigenlijke test

		command.add(saveObject);
		command.execute(mockSession);

		mockSessionControl.verify();
		mockTransactionControl.verify();
	}

	/**
	 * Tests if the order of deletion is the same as specified.
	 * 
	 * @throws HibernateException
	 *             never.
	 */
	public void testExecuteOrder() throws HibernateException
	{
		// explicit testing of calling order
		mockSessionControl = MockControl.createStrictControl(Session.class);
		mockSession = (Session) mockSessionControl.getMock();

		Object[] objects = new Object[] {new Object(), new Object(), new Object()};

		// instellen van specifieke zaken voor deze test
		mockSession.beginTransaction();
		mockSessionControl.setReturnValue(mockTransaction);

		for (int i = 0; i < objects.length; i++)
		{
			mockSession.delete(objects[i]);
		}
		mockTransaction.commit();

		mockSessionControl.replay();
		mockTransactionControl.replay();

		DeleteObjectsCommand command = new DeleteObjectsCommand();
		// de eigenlijke test

		for (int i = 0; i < objects.length; i++)
		{
			command.add(objects[i]);
		}
		command.execute(mockSession);

		mockSessionControl.verify();
		mockTransactionControl.verify();
	}
}
