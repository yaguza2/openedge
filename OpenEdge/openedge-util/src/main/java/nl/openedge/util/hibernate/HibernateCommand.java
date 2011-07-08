/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c) 2004, Open Edge B.V.,  All Rights Reserved.
 */
package nl.openedge.util.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 * Interface voor het uitvoeren van database commando's via Hibernate.
 * <h3>Gebruikte Patterns</h3>
 * Deze klasse vervult de rol van <em>Command</em> uit het <strong>Command </strong> pattern.
 */
public interface HibernateCommand 
{
	/**
	 * Declaratie voor de interface voor het uitvoeren van een database commando. Deze wordt in
	 * subklassen geimplementeerd.
	 * 
	 * @param hibernateSession
	 *            de Hibernate sessie waarop het commando uitgevoerd moet worden.
	 * @throws HibernateException
	 *             als er bij het uitvoeren van het commando een fout optreedt.
	 */
	void execute(final Session hibernateSession) throws HibernateException;
}
