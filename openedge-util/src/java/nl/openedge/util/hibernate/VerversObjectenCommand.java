/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c) 2004, Open Edge B.V.,  All Rights Reserved.
 */
package nl.openedge.util.hibernate;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.persister.ClassPersister;

/**
 * Ververst de opgegeven objecten met data uit de database.
 * {@linkplain #addObject(Object) Voeg objecten toe}die ververst moeten worden. Voer daarna dit
 * commando uit met de <code>HibernateHelper</code>.
 */
public class VerversObjectenCommand implements HibernateCommand
{
	/**
	 * Logger.
	 */
	private static Log log = LogFactory.getLog(VerversObjectenCommand.class);

	/**
	 * De lijst van te verversen objecten.
	 */
	private List objecten = Collections.synchronizedList(new ArrayList());

	/**
	 * Voegt een object toe aan de lijst van te verversen objecten.
	 * 
	 * @param object
	 *            het te verversen object.
	 */
	public void addObject(final Object object)
	{
		objecten.add(object);
	}

	/**
	 * Ververst de <code>objecten</code> vanuit de database.
	 * 
	 * @see nl.openedge.medischevaria.util.AbstractHibernateCommand#execute(net.sf.hibernate.Session)
	 */
	public void execute(final Session hibernateSession) throws HibernateException
	{
		if (objecten == null || objecten.size() == 0)
		{ // doe alleen wat als we objecten hebben
			return;
		}

		// haal de session factory op en cast naar het interne contract om de identifier te kunnen
		// bepalen
		SessionFactoryImplementor sessionFactoryImpl = (SessionFactoryImplementor) hibernateSession
				.getSessionFactory();
		for (Iterator i = objecten.iterator(); i.hasNext();)
		{
			// loop door objecten; Iterator werkt hier niet aangezien we de objecten
			// vervangen door 'ververste' objecten
			Object object = i.next();
			Class clazz = object.getClass();
			ClassPersister persister = sessionFactoryImpl.getPersister(clazz); // haal persister op
			Serializable id = persister.getIdentifier(object); // bepaal id property(/ies) van dit
			// object
			Object copy = hibernateSession.load(clazz, id); // laat Hibernate object laden
			try
			{
				BeanUtils.copyProperties(object, copy);
			}
			catch (IllegalAccessException e)
			{
				log.error(e.getMessage(), e);
				throw new HibernateCommandException(e);
			}
			catch (InvocationTargetException e)
			{
				log.error(e.getMessage(), e);
				throw new HibernateCommandException(e);
			}
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer msg = new StringBuffer();
		msg.append(super.toString());
		msg.append(", te verversen objecten: [");
		if (objecten != null)
		{
			for (Iterator i = objecten.iterator(); i.hasNext();)
			{
				Object object = i.next();
				msg.append(object);
				if (i.hasNext())
				{
					msg.append(",");
				}
			}
		}
		msg.append("]");
		return msg.toString();
	}
}