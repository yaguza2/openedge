/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters.test;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;
import nl.openedge.gaps.core.versions.Version;
import nl.openedge.gaps.core.versions.VersionRegistry;
import nl.openedge.gaps.util.CustomHSQLDialect;
import nl.openedge.util.hibernate.HibernateHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Tests voor {@link nl.openedge.gaps.core.parameters.ParameterRegistry}.
 */
public class HSQLParameterTest extends AbstractParameterTest
{

	/** Log. */
	private static Log log = LogFactory.getLog(HSQLParameterTest.class);

	/**
	 * Construct.
	 */
	public HSQLParameterTest()
	{
		// noop
	}

	/**
	 * Construct.
	 * @param name
	 */
	public HSQLParameterTest(String name)
	{
		super(name);
	}

	/**
	 * Suite methode.
	 * @return Test suite
	 */
	public static Test suite() throws Exception
	{
		TestSuite suite = new TestSuite();
		suite.addTest(new HSQLParameterTest("testGroepen"));
		suite.addTest(new HSQLParameterTest("testSaveGetDelParameter"));
		suite.addTest(new HSQLParameterTest("testCreateString"));
		suite.addTest(new HSQLParameterTest("testCreatePercentage"));
		suite.addTest(new HSQLParameterTest("testCreateBoolean"));
		suite.addTest(new HSQLParameterTest("testCreateDate"));
		suite.addTest(new HSQLParameterTest("testCreateFixedSet"));
		suite.addTest(new HSQLParameterTest("testParameterInExpressie"));
		suite.addTest(new HSQLParameterTest("testNavigatie"));
		suite.addTest(new HSQLParameterTest("testParameterGroepOvererving"));
		suite.addTest(new HSQLParameterTest("testRelatiefBrowsen"));
		suite.addTest(new HSQLParameterTest("testUpload"));
		TestSetupDeco testDeco = new TestSetupDeco(suite);
		return testDeco;
	}

	/**
	 * Setup decorator voor het opzetten van de registries met dummy implementaties.
	 */
	private static class TestSetupDeco extends TestSetup
	{

		/** log. */
		private static Log log = LogFactory.getLog(TestSetupDeco.class);

		/**
		 * @param test
		 */
		public TestSetupDeco(Test test)
		{
			super(test);
		}

		/**
		 * @see junit.extensions.TestSetup#setUp()
		 */
		protected void setUp() throws Exception
		{

			try
			{
				String configFile = "/hibernate.cfg.xml";
				Util.initAndCreateDB(configFile, new CustomHSQLDialect());

				SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
				Date vdate = Util.getTestDatum();
				Version version = VersionRegistry.createVersion(vdate, sdf.format(vdate),
						null);
				version.setGoedgekeurd(true);
				VersionRegistry.updateVersion(version);

			}
			catch (Throwable e)
			{
				log.error(e.getMessage(), e);
				throw new Exception(e);
			}
		}

		/**
		 * @see junit.extensions.TestSetup#tearDown()
		 */
		protected void tearDown() throws Exception
		{
			HibernateHelper.closeSession();
		}
	}
}