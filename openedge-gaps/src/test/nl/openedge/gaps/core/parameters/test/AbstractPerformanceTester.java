/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters.test;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.hibernate.FlushMode;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import nl.openedge.gaps.core.parameters.Parameter;
import nl.openedge.gaps.core.parameters.impl.ParameterWrapper;
import nl.openedge.gaps.core.versions.Version;
import nl.openedge.gaps.support.ParameterBrowser;
import nl.openedge.gaps.support.ParameterBuilder;
import nl.openedge.util.hibernate.HibernateHelper;
import nl.openedge.util.ser.SerializedAndZipped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.rsasign.t;

/**
 * Deze klasse voert performance testen uit.
 */
public abstract class AbstractPerformanceTester
{

	/** Log. */
	private static Log log = LogFactory.getLog(AbstractPerformanceTester.class);

	/**
	 * Construct.
	 */
	public AbstractPerformanceTester()
	{
		//
	}

	/**
	 * Test performance.
	 * @param aantal aantal parameters
	 * @throws Exception
	 */
	public void testParameterToevoegingen(int aantal) throws Exception
	{

		ParameterBuilder builder = new ParameterBuilder();
		int paramGroupCounter = 1;
		Parameter param = null;
		int stepCounter = 0;
		builder.navigate("/:DEFAULT");
		ParameterBrowser browser = new ParameterBrowser();
		try
		{
			long begin = System.currentTimeMillis();
			for (int i = 0; i < aantal; i++)
			{
				String nbr = String.valueOf(i);
				param = builder.createString(nbr, nbr);
				if (stepCounter > 100)
				{
					System.out.print(".");
					stepCounter = 0;
					builder.createParameterGroup(
							"DEFAULT_" + paramGroupCounter,
							"TEST_" + paramGroupCounter, true);
					paramGroupCounter++;
				}
				else
				{
					stepCounter++;
				}
			}
			long end = System.currentTimeMillis();
			log.info(aantal + " toevoegingen: " + ((end - begin) / 1000d) + " sec.");

			begin = System.currentTimeMillis();
			String pg = "/:DEFAULT/";
			stepCounter = 0;
			paramGroupCounter = 1;
			for (int i = 0; i < aantal; i++)
			{
				browser.navigate(pg + i + "@value");
				if (stepCounter > 100)
				{
					System.out.print(".");
					stepCounter = 0;
					pg = "/:DEFAULT_" + paramGroupCounter + "/";
					paramGroupCounter++;
				}
				else
				{
					stepCounter++;
				}
			}
			end = System.currentTimeMillis();
			log.info(aantal + " opvragingen: " + ((end - begin) / 1000d) + " sec.");
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * Test batch performance.
	 * @throws Exception
	 */
	public void testBatch() throws Exception
	{
		long begin;
		long end;
		InputStream is = null;
		ParameterBuilder pb = new ParameterBuilder();
		pb.beginBatch();
		pb.createStructuralGroup("datatest", "test voor uploadfunctie", true);
		pb.createParameterGroup("tabellen", "tabellen", true);
		is = AbstractParameterTest.class.getResourceAsStream("batch1.txt");
		begin = System.currentTimeMillis();
		pb.createNumericData(null, is, 0, true, false, ParameterBuilder.TAB_EN_SPACE_CHARS);
		pb.commitBatch();
		end = System.currentTimeMillis();
		log.info("batch save (1000 entries) kostte: " + ((end - begin) / 1000d) + " sec.");
		ParameterBrowser browser = new ParameterBrowser();
		begin = System.currentTimeMillis();
		String pname = "DAT_1_1";
		String query = null;
		Object value = null;
		int j = 1;
		int k = 1;
		int l = 0;
		for(int i = 0; i < 1000; i++)
		{
			pname = "DAT_" + k + "_" + j + "['" + l + "']";
			query = "/datatest:tabellen/" + pname + "@value";
			value = browser.navigate(query);
			if(value == null)
			{
				throw new Exception("geen waarde voor " + query);
			}
			if(l == 9)
			{
				l = 0;
				if(j == 10)
				{
					j = 1;
					k++;
				}
				else
				{
					j++;
				}
			}
			else
			{
				l++;
			}
		}
		end = System.currentTimeMillis();
		log.info("batch get met PB (1000 entries) kostte: " + ((end - begin) / 1000d) + " sec.");
	}

	/**
	 * Test batch performance.
	 * @throws Exception
	 */
	public void testSimple1() throws Exception
	{
		Session session = HibernateHelper.getSession();
		Transaction tx = null;
		long begin = System.currentTimeMillis();
		ParameterWrapper w = null;
		for(int i = 0; i < 1000; i++)
		{
			tx = session.beginTransaction();
			w = new ParameterWrapper();
			w.setId(new Long(i));
			w.setLocalId("/foo");
			w.setParameterGroupId("/bar");
			w.setPath("/foo:bar/blah");
			w.setVersionId("1");
			SerializedAndZipped z = new SerializedAndZipped();
			z.setCompressedData(new String("blahblah").getBytes());
			z.setUncompressedDataLength(1);
			w.setData(z);
			session.save(w);
			tx.commit();
		}
		long end = System.currentTimeMillis();
		log.info("batch:1 (1000 entries) kostte: " + ((end - begin) / 1000d) + " sec.");
	}

	/**
	 * Test batch performance.
	 * @throws Exception
	 */
	public void testSimple2() throws Exception
	{
		Session session = HibernateHelper.getSession();
		Transaction tx = null;
		long begin = System.currentTimeMillis();
		ParameterWrapper w = null;
		tx = session.beginTransaction();
		for(int i = 0; i < 1000; i++)
		{
			w = new ParameterWrapper();
			w.setId(new Long(i));
			w.setLocalId("/foo");
			w.setParameterGroupId("/bar");
			w.setPath("/foo:bar/blah");
			w.setVersionId("1");
			SerializedAndZipped z = new SerializedAndZipped();
			z.setCompressedData(new String("blahblah").getBytes());
			z.setUncompressedDataLength(1);
			w.setData(z);
			session.save(w);
		}
		tx.commit();
		long end = System.currentTimeMillis();
		log.info("batch:2 (1000 entries) kostte: " + ((end - begin) / 1000d) + " sec.");
	}

	/**
	 * Test batch performance.
	 * @throws Exception
	 */
	public void testSimple3() throws Exception
	{
		List ids = new ArrayList();
		Session session = HibernateHelper.getSession();
		Transaction tx = null;
		long begin = System.currentTimeMillis();
		ParameterWrapper w = null;
		tx = session.beginTransaction();
		for(int i = 0; i < 1000; i++)
		{
			w = new ParameterWrapper();
			w.setId(new Long(i));
			w.setLocalId("/foo");
			w.setParameterGroupId("/bar");
			w.setPath("/foo:bar/blah");
			w.setVersionId("1");
			SerializedAndZipped z = new SerializedAndZipped();
			z.setCompressedData(new String("blahblah").getBytes());
			z.setUncompressedDataLength(1);
			w.setData(z);
			ids.add(session.save(w));
		}
		long end = System.currentTimeMillis();
		log.info("batch/read:3 (1000 entries) kostte: " + ((end - begin) / 1000d) + " sec.");
		Serializable id = null;
		for(int i = 0; i < 1000; i++)
		{
			id = (Serializable)ids.get(i);
			w = (ParameterWrapper)session.load(ParameterWrapper.class, id);
			if(w == null) // wellicht overbodig door ObjectNotFoundException, maar voor de vorm
			{
				throw new Exception("object niet gevonden!");
			}
		}
		tx.commit();
		end = System.currentTimeMillis();
		log.info("batch/write:3 (1000 entries) kostte: " + ((end - begin) / 1000d) + " sec.");
	}

	/**
	 * Test batch performance.
	 * @throws Exception
	 */
	public void testSimple4() throws Exception
	{
		List ids = new ArrayList();
		Session session = HibernateHelper.getSession();
		Transaction tx = null;
		long begin = System.currentTimeMillis();
		Version v = null;
		tx = session.beginTransaction();
		for(int i = 0; i < 1000; i++)
		{
			v = new Version(new Date(), "version_" + i);
			ids.add(session.save(v));
		}
		long end = System.currentTimeMillis();
		log.info("batch/read:4 (1000 entries) kostte: " + ((end - begin) / 1000d) + " sec.");
		Serializable id = null;
		for(int i = 0; i < 1000; i++)
		{
			id = (Serializable)ids.get(i);
			v = (Version)session.load(Version.class, id);
			if(v == null) // wellicht overbodig door ObjectNotFoundException, maar voor de vorm
			{
				throw new Exception("object niet gevonden!");
			}
		}
		tx.commit();
		end = System.currentTimeMillis();
		log.info("batch/write:4 (1000 entries) kostte: " + ((end - begin) / 1000d) + " sec.");
	}

	/**
	 * Test batch performance.
	 * @throws Exception
	 */
	public void testSimple5() throws Exception
	{
		List ids = new ArrayList();
		Session session = HibernateHelper.getSession();
		Transaction tx = null;
		long begin = System.currentTimeMillis();
		Version v = null;
		Serializable id = null;
		tx = session.beginTransaction();
		for(int i = 0; i < 1000; i++)
		{
			v = new Version(new Date(), "version_" + i);
			id = (Serializable)session.save(v);
			ids.add(id);
		}
		long end = System.currentTimeMillis();
		log.info("batch/save:5 (1000 entries) kostte: " + ((end - begin) / 1000d) + " sec.");
		List qres = null;
		tx.commit();
		tx = session.beginTransaction();
		for(int i = 0; i < 1000; i++)
		{
			id = (Serializable)ids.get(i);
			String query = "from " + Version.class.getName() + " v where " + " v.id = ?";
			qres = session.find(query, id, Hibernate.LONG);
			if(qres == null || (qres.isEmpty()))
			{
				throw new Exception("object " + id + " niet gevonden!");
			}
			v = (Version)qres.get(0);
			//log.info("version gevonden: " + v);
		}
		tx.commit();
		end = System.currentTimeMillis();
		log.info("batch/read:5 (1000 entries) kostte: " + ((end - begin) / 1000d) + " sec.");
	}

	/**
	 * Test batch performance.
	 * @throws Exception
	 */
	public void testSimple6() throws Exception
	{
		Session session = HibernateHelper.getSession();
		Connection conn = session.connection();
		conn.setAutoCommit(false);
		long begin = System.currentTimeMillis();
		PreparedStatement pstmt = null;
		for(int i = 0; i < 1000; i++)
		{
			pstmt = conn.prepareStatement(
					"insert into GAPS_V (ID, NAME, GELDIG_VANAF, GOEDGEKEURD)"
					+ " values (?, ?, ?, ?)");
			pstmt.setLong(1, new Long(0 - i).longValue());
			pstmt.setString(2, "version_" + i);
			pstmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));
			pstmt.setBoolean(4, true);
			pstmt.executeUpdate();
			pstmt.close();
//			conn.commit();
		}
		conn.commit();
		long end = System.currentTimeMillis();
		log.info("batch/read:6 (1000 entries) kostte: " + ((end - begin) / 1000d) + " sec.");
	}

	/**
	 * Test batch performance.
	 * @throws Exception
	 */
	public void testSimple7() throws Exception
	{
		List ids = new ArrayList();
		Session session = HibernateHelper.getSession();
		Connection conn = session.connection();
		conn.setAutoCommit(false);
		Statement stmt = conn.createStatement();
		dropTestTabel(stmt);
		stmt.execute("create table test(id varchar2(50))");
		conn.commit();
		PreparedStatement insert = conn.prepareStatement("insert into test values (?)");
		for(int i = 0; i < 1000; i++)
		{
			insert.setString(1, "test_" + i);
			insert.executeUpdate();
		}
		PreparedStatement select = conn.prepareStatement("select * from test where id = ?");
		ResultSet rs = null;
		for(int i = 0; i < 1000; i++)
		{
			select.setString(1, "test_" + i);
			rs = select.executeQuery();
			if(!rs.next())
			{
				throw new Exception("object niet gevonden!");
			}
			else
			{
				log.info("gevonden: " + rs.getString(1));
			}
		}
		conn.commit();
		dropTestTabel(stmt);
	}

	/**
	 * @param stmt
	 */
	private void dropTestTabel(Statement stmt)
	{
		try
		{
			stmt.execute("drop table test");
		}
		catch(Exception e)
		{
			// ignore
		}
	}

}