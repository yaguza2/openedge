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
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

/**
 * Vult alle objecten van de gegeven persistente klasse.
 */
public class VulAlleObjectenCommand extends AbstractQueryCommand
{

	/**
	 * persistente klasse waarvan alle objecten dienen te worden geladen.
	 */
	private Class persistentClass = null;

	/**
	 * Constructor met class arg.
	 * 
	 * @param persistentClassToLoad
	 *            de persistente klasse die dient te worden geladen
	 */
	public VulAlleObjectenCommand(final Class persistentClassToLoad)
	{
		super("unused");
		persistentClass = persistentClassToLoad;
	}

	/**
	 * @see nl.openedge.util.hibernate.AbstractQueryCommand#getQuery(net.sf.hibernate.Session)
	 */
	protected Query getQuery(final Session hibernateSession) throws HibernateException
	{
		Query query = hibernateSession.createQuery("from " + persistentClass.getName());
		return query;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer msg = new StringBuffer();
		msg.append(super.toString());
		msg.append(", pesistente klasse: ");
		if (persistentClass != null)
		{
			msg.append(persistentClass.getName());
		}
		else
		{
			msg.append("-");
		}
		return msg.toString();
	}

}