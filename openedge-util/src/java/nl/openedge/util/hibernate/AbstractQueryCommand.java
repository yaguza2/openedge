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

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AbstractQueryCommand voert een zogeheten named query uit via Hibernate.
 * <h3>Gebruikte Patterns</h3>
 * Deze klasse implementeert of is onderdeel van de volgende patterns:
 * <ul>
 * <li>Template method (implementeert <em>AbstractClass</em>)</li>
 * <li>Command (implementeert <em>ConcreteCommand</em>)</li>
 * </ul>
 */
public abstract class AbstractQueryCommand implements HibernateCommand
{
	/**
	 * Gebruikt voor logging.
	 */
	private static Log log = LogFactory.getLog(AbstractQueryCommand.class);

	/**
	 * De naam van de uit te voeren query.
	 */
	private final String queryNaam;

	/**
	 * Het resultaat van deze query als de query uitgevoerd is.
	 */
	private List resultaat;

	/**
	 * Construeert een AbstractQueryCommand die een Hibernate 'named query uitvoert met de naam
	 * 'deQuery'.
	 * 
	 * @param deQuery
	 *            de naam van de named query.
	 */
	public AbstractQueryCommand(final String deQuery)
	{
		log.trace("Enter");
		queryNaam = deQuery;
		log.trace("Leave");
	}

	/**
	 * Implementatie van het concrete commando in de vorm van een <em>template method</em>.
	 * Vervult de rol van <em>TemplateMethod</em> uit het pattern.
	 * 
	 * @see nl.openedge.medischevaria.util.AbstractHibernateCommand#execute(net.sf.hibernate.Session)
	 */
	public final void execute(final Session hibernateSession) throws HibernateException
	{
		log.trace("Enter");
		Query query = getQuery(hibernateSession);
		setParameters(query);
		setResultaat(query);
		log.trace("Leave");
	}

	/**
	 * Haalt het resultaat uit de query op. Vervult de rol van <em>primitive operation</em> uit
	 * het pattern <em>template method</em>.
	 * 
	 * @param query
	 *            de Hibernate <code>Query</code> waarvan het resultaat bepaald moet worden.
	 * @throws HibernateException
	 *             als er een probleem optreedt bij het uitvoeren.
	 */
	protected void setResultaat(final Query query) throws HibernateException
	{
		log.trace("Enter");
		resultaat = query.list();
		log.trace("Leave");
	}

	/**
	 * Geeft een Hibernate Query terug op basis van de <code>queryNaam</code>. Vervult de rol van
	 * <em>primitive operation</em> uit het pattern <em>template method</em>.
	 * 
	 * @param hibernateSession
	 *            de sessie waarop de query moet draaien.
	 * @return een Hibernate Query object.
	 * @throws HibernateException
	 *             als de query niet gevonden kan worden.
	 */
	protected Query getQuery(final Session hibernateSession) throws HibernateException
	{
		log.trace("Enter");
		Query query = hibernateSession.getNamedQuery(queryNaam);
		log.trace("Leave");
		return query;
	}

	/**
	 * Zet de parameters op de Hibernate Query, indien de query parameters heeft, moet deze method
	 * overriden worden. Vervult de rol van <em>primitive operation</em> uit het pattern
	 * <em>template method</em>.
	 * 
	 * @param query
	 *            de Hibernate <code>Query</code> waarop de parameters gezet moeten worden.
	 * @throws HibernateException
	 *             als het zetten van parameters fout gaat.
	 */
	protected void setParameters(final Query query) throws HibernateException
	{
		log.trace("Enter");

		if (true)
		{
			log.debug(query);
		}
		// deze methode is opzettelijk leeg gelaten zodat query commands zonder parameters
		// deze methode niet verplicht hoeven te implementeren.
		// De checkstyle waarschuwing mag dus genegeerd worden.
		log.trace("Leave");
	}

	/**
	 * Geeft het resultaat van de uitgevoerde query.
	 * 
	 * @return het resultaat.
	 */
	public List getResultaat()
	{
		return resultaat;
	}

	/**
	 * Geeft de naam van de named query terug.
	 * 
	 * @return de naam van de named query.
	 */
	protected String getQueryNaam()
	{
		return queryNaam;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer msg = new StringBuffer();
		msg.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode()))
				.append(", querynaam: ").append(queryNaam);
		if (resultaat != null)
		{
			msg.append(", ").append(resultaat.size()).append(" resultaten");
		}
		return msg.toString();
	}
}