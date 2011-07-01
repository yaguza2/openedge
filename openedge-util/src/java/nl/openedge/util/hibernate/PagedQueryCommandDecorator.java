/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c) 2004, Open Edge B.V.,  All Rights Reserved.
 */
package nl.openedge.util.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * PagedQueryCommandDecorator is een paginerings decorator voor het uitvoeren van
 * <code>QueryCommands</code>. Deze decorator zet het maximale aantal resultaten van de query en
 * de eerste start rij van het resultaat. Hierdoor wordt het mogelijk om bijvoorbeeld door een
 * resultset van 100 resultaten met pagina's van 10 objecten te lopen.
 * <h3>Gebruikte Patterns</h3>
 * Deze klasse implementeert of is onderdeel van de volgende patterns:
 * <ul>
 * <li>Decorator (implementatie)</li>
 * <li>Template method (onderdeel)</li>
 * <li>Command (onderdeel)</li>
 * </ul>
 */
public class PagedQueryCommandDecorator extends AbstractQueryCommand
{
	/**
	 * Gebruikt voor logging.
	 */
	private static Log log = LogFactory.getLog(PagedQueryCommandDecorator.class);

	/**
	 * Het gedecoreerde query command.
	 */
	private final AbstractQueryCommand decorated;

	/**
	 * De eerste rij die in het resultaat terecht moet komen.
	 */
	private final int eersteRij;

	/**
	 * Het maximaal aantal rijen dat in het resultaat terecht moet komen.
	 */
	private final int maxAantalRijen;

	/**
	 * Construeert een <strong>Decorator </strong> voor een <code>AbstractQueryCommand</code>.
	 * 
	 * @param deQuery
	 *            de query die gedecoreerd moet worden.
	 * @param deEersteRij
	 *            de eerste rij die in het resultaat terecht moet komen.
	 * @param hetMaxAantal
	 *            het maximaal aantal rijen dat in het resultaat terecht moet komen.
	 */
	public PagedQueryCommandDecorator(final AbstractQueryCommand deQuery, final int deEersteRij,
			final int hetMaxAantal)
	{
		super(deQuery.getQueryNaam());
		log.trace("Enter");
		decorated = deQuery;
		eersteRij = deEersteRij;
		maxAantalRijen = hetMaxAantal;
		log.trace("Leave");
	}

	/**
	 * Delegeert het verzoek om een query naar het gedecoreerde <code>AbstractQueryCommand</code>.
	 * 
	 * @see nl.openedge.medischevaria.util.AbstractQueryCommand#getQuery(net.sf.hibernate.Session)
	 */
	protected Query getQuery(final Session hibernateSession) throws HibernateException
	{
		log.trace("Enter");
		Query query = decorated.getQuery(hibernateSession);
		log.trace("Leave");
		return query;
	}

	/**
	 * Delegeert het verzoek om de resultaten naar het gedecoreerde
	 * <code>AbstractQueryCommand</code>.
	 * 
	 * @see nl.openedge.medischevaria.util.AbstractQueryCommand#getResultaat()
	 */
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
	protected void setParameters(final Query query) throws HibernateException
	{
		log.trace("Enter");
		decorated.setParameters(query);
		query.setMaxResults(maxAantalRijen);
		query.setFirstResult(eersteRij);
		log.trace("Leave");
	}

	/**
	 * Delegeert het verzoek om de resultaten te zetten naar het gedecoreerde
	 * <code>AbstractQueryCommand</code>.
	 * 
	 * @see nl.openedge.medischevaria.util.AbstractQueryCommand#setResultaat(net.sf.hibernate.Query)
	 */
	protected void setResultaat(final Query query) throws HibernateException
	{
		log.trace("Enter");
		decorated.setResultaat(query);
		log.trace("Leave");
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer msg = new StringBuffer();
		msg.append(decorated.toString());
		msg.append(", eerste rij: ");
		msg.append(eersteRij);
		msg.append(", max aantal rijen: ");
		msg.append(maxAantalRijen);
		return msg.toString();
	}
}