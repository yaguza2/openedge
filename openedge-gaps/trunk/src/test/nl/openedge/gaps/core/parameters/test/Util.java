/*
 * $Id$ $Revision$
 * $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters.test;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.dialect.Dialect;
import nl.openedge.util.hibernate.ConfigException;
import nl.openedge.util.hibernate.HibernateHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Test utility.
 */
public class Util
{

	/** Log. */
	private static Log log = LogFactory.getLog(Util.class);

	/** test datum. */
	private static Date testDatum;

	// init
	static
	{
		Calendar cal = Calendar.getInstance();
		cal.set(2004, 2, 2);
		testDatum = cal.getTime();
	}

	/** hid. */
	private Util()
	{
		// no nada
	}

	/**
	 * Geeft testdatum.
	 * @return testdatum
	 */
	public static Date getTestDatum()
	{
		return testDatum;
	}

	/**
	 * Zet testdatum.
	 * @param testDatum testdatum
	 */
	public static void setTestDatum(Date testDatum)
	{
		Util.testDatum = testDatum;
	}

	/**
	 * Initialiseer en creeer de database.
	 * @throws ConfigException configuratiefout
	 * @throws HibernateException hibernatefout
	 * @throws SQLException sqlfout
	 */
	public static void initAndCreateDB(String configFile, Dialect dialect)
			throws HibernateException, SQLException, ConfigException
	{

		URL configUrl = Util.class.getResource(configFile);
		HibernateHelper.setConfigURL(configUrl);
		HibernateHelper.init();

		Session session = HibernateHelper.getSession();
		Connection conn = session.connection();
		Statement stmt = conn.createStatement();
		Configuration config = HibernateHelper.getConfiguration();
		String[] drops = config.generateDropSchemaScript(dialect);
		String[] creates = config.generateSchemaCreationScript(dialect);
		execStmt(conn, stmt, splitAltTables(drops, true));
		execStmt(conn, stmt, splitAltTables(drops, false));
		execStmt(conn, stmt, creates);
		HibernateHelper.closeSession();
	}

	/**
	 * Filter statements op begin van statement.
	 * @param drops statements
	 * @param inclAlterFlag als true, dan de alters, anders de rest (drops)
	 * @return gedeelte van input
	 */
	private static String[] splitAltTables(String[] drops, boolean inclAlterFlag)
	{
		List temp = new ArrayList();
		for (int i = 0; i < drops.length; i++)
		{
			if (inclAlterFlag)
			{
				if (drops[i].toLowerCase().trim().startsWith("alter"))
				{
					temp.add(drops[i]);
				}
			}
			else
			{
				if (!drops[i].toLowerCase().trim().startsWith("alter"))
				{
					temp.add(drops[i]);
				}
			}
		}
		return (String[]) temp.toArray(new String[temp.size()]);
	}

	/**
	 * Voer gegeven statements uit.
	 * @param conn connection
	 * @param stmt statement object
	 * @param stmts uit te voeren statements
	 * @throws SQLException
	 */
	public static void execStmt(Connection conn, Statement stmt, String[] stmts)
			throws SQLException
	{
		for (int i = 0; i < stmts.length; i++)
		{
			log.info("exec: " + stmts[i]);
			try
			{
				stmt.executeUpdate(stmts[i]);
				conn.commit();
			}
			catch (SQLException e)
			{
				log.error(e.getMessage());
			}
		}
	}
}