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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Deletes the given objects in the order they were added from the database.
 */
public class DeleteObjectsCommand implements HibernateCommand
{
	/** Logger. */
	private static Log log = LogFactory.getLog(DeleteObjectsCommand.class);

	/**
	 * Collection of objects to delete.
	 */
	private final List objectsToDelete = new ArrayList();

	/**
	 * Adds object to the list of objects to be deleted. The order in which the objects are added,
	 * is the same order in which they are deleted, i.e. first in, first out.
	 * 
	 * @param object
	 *            the object to be deleted
	 */
	public void add(final Object object)
	{
		objectsToDelete.add(object);
	}

	/**
	 * Voert saveOrUpdate uit voor de collectie te persisteren objecten binnen 1 transactie.
	 * 
	 * @see nl.openedge.medischevaria.util.AbstractHibernateCommand#execute(net.sf.hibernate.Session)
	 */
	public void execute(final Session hibernateSession) throws HibernateException
	{
		if (log.isTraceEnabled())
		{
			log.trace("enter execute");
		}
		if (log.isDebugEnabled())
		{
			log.debug(this.toString());
		}
		Transaction transaction = hibernateSession.beginTransaction();
		try
		{
			for (int i = 0; i < objectsToDelete.size(); i++)
			{
				hibernateSession.delete(objectsToDelete.get(i));
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
				log.fatal("Kan geen rollback uitvoeren");
			}
			throw (e);
		}
		log.trace("leave execute");
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer msg = new StringBuffer();
		msg.append(super.toString());
		msg.append(", objects to delete: [");
		if (objectsToDelete != null)
		{
			for (Iterator i = objectsToDelete.iterator(); i.hasNext();)
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