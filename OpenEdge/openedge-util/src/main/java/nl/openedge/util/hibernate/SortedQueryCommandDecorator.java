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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * SortedQueryCommandDecorator is een sortering decorator voor het uitvoeren van
 * <code>QueryCommands</code>. Deze decorator zet de <code>order by</code> van de query.
 * <h3>Gebruikte Patterns</h3>
 * Deze klasse implementeert of is onderdeel van de volgende patterns:
 * <ul>
 * <li>Decorator (implementatie)</li>
 * <li>Template method (onderdeel)</li>
 * <li>Command (onderdeel)</li>
 * </ul>
 */
public class SortedQueryCommandDecorator extends AbstractQueryCommand
{
	/**
	 * Gebruikt voor logging.
	 */
	private static Logger log = LoggerFactory.getLogger(SortedQueryCommandDecorator.class);

	/**
	 * Het gedecoreerde query command.
	 */
	private final AbstractQueryCommand decorated;

	/**
	 * De lijst waarin de sorteringen opgeslagen worden.
	 */
	private List sorteringen = new ArrayList();

	/**
	 * Construeert een <strong>Decorator </strong> voor een <code>AbstractQueryCommand</code>.
	 * 
	 * @param deQuery
	 *            de query die gedecoreerd moet worden.
	 */
	public SortedQueryCommandDecorator(final AbstractQueryCommand deQuery)
	{
		super(deQuery.getQueryNaam());
		log.trace("Enter");
		decorated = deQuery;
		log.trace("Leave");
	}

	/**
	 * Voegt een sorteringscriterium toe aan de query.
	 * 
	 * @param kolom
	 *            de kolom waarop gesorteerd moet worden.
	 * @param richting
	 *            de richting (asc, desc)
	 * @return het huidige object voor statement-chaining.
	 */
	public SortedQueryCommandDecorator addOrderBy(final String kolom, final String richting)
	{
		sorteringen.add(kolom + " " + richting);
		return this;
	}

	/**
	 * Haalt de query op uit het gedecoreerde commando, en past de query aan met de sorteringen.
	 * 
	 * @see nl.openedge.medischevaria.util.AbstractQueryCommand#getQuery(net.sf.hibernate.Session)
	 */
	@Override
	protected Query getQuery(final Session hibernateSession) throws HibernateException
	{
		log.trace("Enter");
		Query query = decorated.getQuery(hibernateSession);
		String queryTekst = query.getQueryString();
		queryTekst = queryTekst + " order by ";
		for (Iterator i = sorteringen.iterator(); i.hasNext();)
		{
			queryTekst = queryTekst + " " + i.next();
			if (i.hasNext())
			{
				queryTekst += ", ";
			}
		}
		query = hibernateSession.createQuery(queryTekst);
		log.trace("Leave");
		return query;
	}

	/**
	 * Delegeert het verzoek om de resultaten naar het gedecoreerde
	 * <code>AbstractQueryCommand</code>.
	 * 
	 * @see nl.openedge.medischevaria.util.AbstractQueryCommand#getResultaat()
	 */
	@Override
	public List getResultaat()
	{
		log.trace("Enter");
		List resultaat = decorated.getResultaat();
		log.trace("Leave");
		return resultaat;
	}

	/**
	 * Delegeert het verzoek om de parameters te zetten op de query naar het gedecoreerde
	 * <code>AbstractQueryCommand</code>, en zet zelf de parameters voor het pagineren. Dit is de
	 * eigenlijke implementatie van het <strong>Decorator </strong> pattern.
	 * 
	 * @see nl.openedge.medischevaria.util.AbstractQueryCommand#setParameters(net.sf.hibernate.Query)
	 */
	@Override
	protected void setParameters(final Query query) throws HibernateException
	{
		log.trace("Enter");
		decorated.setParameters(query);
		log.trace("Leave");
	}

	/**
	 * Delegeert het verzoek om de resultaten te zetten naar het gedecoreerde
	 * <code>AbstractQueryCommand</code>.
	 * 
	 * @see nl.openedge.medischevaria.util.AbstractQueryCommand#setResultaat(net.sf.hibernate.Query)
	 */
	@Override
	protected void setResultaat(final Query query) throws HibernateException
	{
		log.trace("Enter");
		decorated.setResultaat(query);
		log.trace("Leave");
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuffer msg = new StringBuffer();
		msg.append(decorated.toString());
		msg.append(", sorteringen: [");
		if (sorteringen != null)
		{
			for (Iterator i = sorteringen.iterator(); i.hasNext();)
			{
				String sortering = (String) i.next();
				msg.append(sortering);
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
