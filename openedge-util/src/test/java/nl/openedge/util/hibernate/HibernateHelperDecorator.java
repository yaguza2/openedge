/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c) 2004, Open Edge B.V.,  All Rights Reserved.
 */
package nl.openedge.util.hibernate;

import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;

import junit.extensions.TestSetup;
import junit.framework.Test;

import org.hibernate.Session;

/**
 */
public class HibernateHelperDecorator extends TestSetup
{

	/**
	 * construct.
	 * 
	 * @param test
	 *            test case
	 */
	public HibernateHelperDecorator(final Test test)
	{
		super(test);
	}

	/**
	 * Start HibernateHelper, creeer tabellen en voeg testdata toe.
	 * 
	 * @throws Exception
	 * @see junit.extensions.TestSetup#setUp()
	 */
	public void setUp() throws Exception
	{

		Session session = null;
		try
		{
			URL hibernateConfig = HibernateHelperDecorator.class
					.getResource("/hibernate.test.cfg.xml");
			HibernateHelper.setConfigURL(hibernateConfig);

			session = HibernateHelper.getSession();

			Connection conn = session.connection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("create table test( id integer )");

			MockClass test = new MockClass();
			session.save(test);
			session.flush();

		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		finally
		{
			HibernateHelper.closeSession();
		}
	}

	/**
	 * Verwijder testtabellen.
	 * 
	 * @throws Exception
	 * @see junit.extensions.TestSetup#tearDown()
	 */
	public void tearDown() throws Exception
	{

		Session session = null;
		// start HibernateHelper & creeer tabellen
		try
		{
			URL hibernateConfig = HibernateHelperDecorator.class
					.getResource("/hibernate.test.cfg.xml");
			HibernateHelper.setConfigURL(hibernateConfig);

			session = HibernateHelper.getSession();
			Connection conn = session.connection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("drop table test");

		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		finally
		{
			HibernateHelper.closeSession();
		}
	}
}