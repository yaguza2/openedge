/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c) 2004, Open Edge B.V.,  All Rights Reserved.
 */
package nl.openedge.util.hibernate;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Voert alle testen uit.
 */
public final class AllHibernateTests
{

	/**
	 * Verborgen constructor.
	 */
	private AllHibernateTests()
	{
		// hidden constructor
	}

	/**
	 * Maake een Test die alle andere tests bevat.
	 * 
	 * @return een allesomvattende test.
	 * @throws Exception
	 *             indien er een onverwachte fout optreed
	 */
	public static Test suite() throws Exception
	{
		TestSuite suite = new TestSuite("Hibernate tests");

		//$JUnit-BEGIN$

		suite.addTestSuite(HibernateInvokerTest.class);
		suite.addTestSuite(UpdateCommandTest.class);
		suite.addTestSuite(PagedQueryCommandDecoratorTest.class);

		//$JUnit-END$

		return new HibernateHelperDecorator(suite);
	}
}