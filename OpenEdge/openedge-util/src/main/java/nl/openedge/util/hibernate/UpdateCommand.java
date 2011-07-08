/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c) 2004, Open Edge B.V.,  All Rights Reserved.
 */
package nl.openedge.util.hibernate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UpdateCommand voert updates uit via Hibernate. UpdateCommand kan een collectie van
 * objecten in 1 transactie persisteren in database.
 */
public class UpdateCommand implements HibernateCommand
{

	/** Logger. */
	private static Logger log = LoggerFactory.getLogger(UpdateCommand.class);

	/**
	 * Collectie van te persisteren objecten.
	 */
	private final List objectsToSave = new ArrayList();

	/**
	 * Methode die object toevoegt aan collectie van te persisteren objecten.
	 * 
	 * @param saveObject
	 *            Het object dat aan de collectie moet worden toegevoegd.
	 */
	public void add(final Object saveObject)
	{
		objectsToSave.add(saveObject);
	}

	/**
	 * Voert saveOrUpdate uit voor de collectie te persisteren objecten binnen 1
	 * transactie.
	 * 
	 * @see nl.openedge.medischevaria.util.AbstractHibernateCommand#execute(net.sf.hibernate.Session)
	 */
	@Override
	public void execute(final Session hibernateSession) throws HibernateException
	{
		Transaction transaction = hibernateSession.beginTransaction();
		try
		{
			for (int i = 0; i < objectsToSave.size(); i++)
			{
				hibernateSession.saveOrUpdate(objectsToSave.get(i));
			}
			transaction.commit();
		}
		catch (HibernateException e)
		{
			log.error("Kan objecten niet opslaan in database.");
			try
			{
				transaction.rollback();
			}
			catch (HibernateException e1)
			{
				log.error("Kan geen rollback uitvoeren");
			}
			throw (e);
		}

	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuffer msg = new StringBuffer();
		msg.append(super.toString());
		msg.append(", objecten op te slaan: [");
		if (objectsToSave != null)
		{
			for (Iterator i = objectsToSave.iterator(); i.hasNext();)
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
