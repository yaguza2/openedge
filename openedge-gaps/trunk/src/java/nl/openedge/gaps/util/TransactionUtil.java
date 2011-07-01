/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c)
 * All rechten voorbehouden.
 */
package nl.openedge.gaps.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;

/**
 * Utility klasse voor transacties.
 */
public final class TransactionUtil
{
	/** Log. */
	private static Log log = LogFactory.getLog(TransactionUtil.class);

	/** houder thread local voor huidige transactie/ huidige thread. */
	private static ThreadLocal txHolder = new ThreadLocal();

	/**
	 * Hidden construct.
	 */
	private TransactionUtil()
	{
		super();
	}

	/**
	 * Start een transactie voor de huidige thread.
	 * @param session de sessie
	 * @throws HibernateException bij onverwachte Hibernate fouten
	 * @throws TransactionException als er reeds een transactie bezig is
	 */
	public static void begin(Session session) throws HibernateException
	{
		Transaction tx = getCurrentTransaction();
		if(tx != null)
		{
			throw new TransactionException("er is reeds een transactie bezig");
		}
		tx = session.beginTransaction();
		txHolder.set(tx);
	}

	/**
	 * Commit de transactie voor de huidige thread.
	 * @throws HibernateException bij onverwachte Hibernate fouten
	 * @throws TransactionException als er geen transactie bezig is
	 */
	public static void commit() throws HibernateException
	{
		Transaction tx = getCurrentTransaction();
		if(tx == null)
		{
			throw new TransactionException("er is geen transactie bezig");
		}
		tx.commit();
		clearCurrentTransaction();
	}

	/**
	 * Rollback de transactie voor de huidige thread. Een eventuele exceptie wordt
	 * alleen maar gelogged.
	 * @throws TransactionException als er geen transactie bezig is
	 */
	public static void rollback()
	{
		Transaction tx = getCurrentTransaction();
		if(tx == null)
		{
			throw new TransactionException("er is geen transactie bezig");
		}
		try
		{
			tx.rollback();
		}
		catch (HibernateException e)
		{
			log.error(e.getMessage(), e);
		}
		clearCurrentTransaction();
	}

	/**
	 * Geeft of er een transactie bezig is voor de huidige thread.
	 * @return of er een transactie bezig is voor de huidige thread
	 */
	public static boolean isTransactionStarted()
	{
		return (getCurrentTransaction() != null);
	}

	/**
	 * Geeft de transactie voor de huidige thread of null indien er geen bezig is.
	 * @return de transactie voor de huidige thread of null indien er geen bezig is
	 */
	private static Transaction getCurrentTransaction()
	{
		return (Transaction)txHolder.get();
	}

	/**
	 * Schoon de transactie voor de huidige thread.
	 */
	private static void clearCurrentTransaction()
	{
		txHolder.set(null);
		CacheUtil.flushTransactionCaches();
	}
}
