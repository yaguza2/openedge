/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters.test;

import java.text.SimpleDateFormat;
import java.util.Date;

import nl.openedge.gaps.core.versions.Version;
import nl.openedge.gaps.core.versions.VersionRegistry;
import nl.openedge.gaps.util.CustomOracle9Dialect;
import nl.openedge.util.hibernate.HibernateHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Performancetester voor HSQL.
 */
public class OraclePerformanceTester extends AbstractPerformanceTester
{

	/** Log. */
	private static Log log = LogFactory.getLog(AbstractPerformanceTester.class);

	/**
	 * Construct.
	 */
	public OraclePerformanceTester()
	{
		super();
	}

	/**
	 * Run app.
	 * @param args pgm argumenten
	 */
	public static void main(String[] args)
	{

		try
		{
			String configFile = "/hibernate-oracle.cfg.xml";
			Util.initAndCreateDB(configFile, new CustomOracle9Dialect());

			SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
			Date vdate = Util.getTestDatum();
			Version version = VersionRegistry.createVersion(vdate, sdf.format(vdate), null);
			version.setGoedgekeurd(true);
			VersionRegistry.updateVersion(version);

		}
		catch (Throwable e)
		{
			log.error(e.getMessage(), e);
			return;
		}
		AbstractPerformanceTester tester = new OraclePerformanceTester();
		try
		{
//			tester.testParameterToevoegingen(1000);
			tester.testBatch();
//			tester.testSimple1();
//			tester.testSimple2();
//			tester.testSimple3();
//			tester.testSimple4();
//			tester.testSimple5();
//			tester.testSimple6();
//			tester.testSimple7();
			HibernateHelper.closeSession();
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}
	}
}