/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c) 2004, Open Edge B.V.,  All Rights Reserved.
 */
package nl.openedge.util.hibernate;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * De HibernateInvoker wordt gebruikt om HibernateCommands uit te voeren.
 * <p>
 * Gebruik als volgt (bijvoorbeeld):
 * </p>
 * <p>
 * 
 * <pre>
 * VerversObjectenCommand verversCommand = new VerversObjectenCommand();
 * verversCommand.addObject(polis);
 * HibernateInvoker invoker = new HibernateInvoker();
 * invoker.execute(verversCommand);
 * </pre>
 * 
 * </p>
 */
public class HibernateInvoker
{
	/**
	 * Logger.
	 */
	private static Log log = LogFactory.getLog(HibernateInvoker.class);

	/**
	 * Voert het <code>command</code> uit binnen een Hibernate sessie. Gooit een
	 * HibernateCommandException bij onverwachte fouten (HibernateCommandException is een
	 * RuntimeException, dus HOEFT niet te worden
	 * 
	 * @param command
	 *            het commando dat uitgevoerd moet worden. afgevangen)
	 */
	public void execute(final HibernateCommand command)
	{

		log.trace("Enter");
		Session session;
		try
		{
			session = getSession();
			command.execute(session);
		}
		catch (Throwable e)
		{
			log.error("Fout bij het uitvoeren van HibernateCommand " + command, e);
			if (e instanceof HibernateCommandException)
			{
				throw ((HibernateCommandException) e); // gooi origineel
			}
			else
			{
				throw new HibernateCommandException(e); // wrap exceptie
			}
		}
		finally
		{
			try
			{
				closeResources();
			}
			catch (HibernateException e)
			{
				log.fatal("Kan resources niet sluiten: ", e);
			}
		}
		log.trace("Leave");
	}

	/**
	 * Haalt een Hibernate session object op.
	 * 
	 * @return de Hibernate session
	 * @throws HibernateException
	 *             als er geen sessie verkregen kan worden.
	 */
	protected Session getSession() throws HibernateException
	{
		return HibernateHelper.getSession();
	}

	/**
	 * Sluit resources indien huidige HibernateHelper delegate instantie is van
	 * HibernateHelperReloadConfigImpl, sluit dan resources; anders: ignore.
	 * 
	 * @throws HibernateException
	 *             indien resources niet kunnen worden gesloten door Hibernate
	 */
	protected void closeResources() throws HibernateException
	{
		HibernateHelperDelegate delegate = HibernateHelper.getDelegate();
		if (delegate instanceof HibernateHelperReloadConfigImpl)
		{
			((HibernateHelperReloadConfigImpl) delegate).closeResources();
		}
	}

}