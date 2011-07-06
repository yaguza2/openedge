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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;

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
	private static Logger log = LoggerFactory.getLogger(VerversObjectenCommand.class);

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

			// TODO: ongetest - aangepast voor hibernate3
			ClassMetadata meta = sessionFactoryImpl.getClassMetadata(clazz);
			Serializable id = meta.getIdentifier( object, (SessionImplementor) hibernateSession);			
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
