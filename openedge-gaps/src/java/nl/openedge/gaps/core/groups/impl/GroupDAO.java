/*
 * $Id$ $Revision$
 * $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.groups.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.zip.DataFormatException;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.type.Type;
import nl.openedge.gaps.core.groups.Group;
import nl.openedge.gaps.core.versions.Version;
import nl.openedge.util.hibernate.HibernateHelper;
import nl.openedge.util.ser.SerializeAndZipHelper;
import nl.openedge.util.ser.SerializedAndZipped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DAO/ utility klasse voor groepen.
 */
public final class GroupDAO
{

	/** Log. */
	private static Log log = LogFactory.getLog(GroupDAO.class);

	/**
	 * Construct.
	 */
	public GroupDAO()
	{
		//
	}

	/**
	 * Zoekt groep op gegeven pad.
	 * @param path zoekpad
	 * @param version de versie voor de zoektocht
	 * @return de groep of null indien niet gevonden
	 * @throws GroupDAOException bij onverwachte fouten
	 */
	public Group findGroup(String path, Version version) throws GroupDAOException
	{

		GroupWrapper wrapper = findGroupWrapper(path, version);
		Group group = unpackGroup(wrapper);
		return group;
	}

	/**
	 * Pak representatie object uit naar {@link Group}.
	 * @param wrapper representatie object.
	 * @return de uitgepakte groep of null indien de wrapper null was
	 * @throws GroupDAOException bij onverwachte fouten
	 */
	public Group unpackGroup(GroupWrapper wrapper) throws GroupDAOException
	{

		Group group = null;
		if (wrapper != null)
		{
			SerializedAndZipped data = wrapper.getData();
			try
			{
				group = (Group) SerializeAndZipHelper.unzipAndDeserialize(data);
			}
			catch (IOException e)
			{
				log.error(e.getMessage(), e);
				throw new GroupDAOException(e);
			}
			catch (DataFormatException e)
			{
				log.error(e.getMessage(), e);
				throw new GroupDAOException(e);
			}
			catch (ClassNotFoundException e)
			{
				log.error(e.getMessage(), e);
				throw new GroupDAOException(e);
			}
		}
		return group;
	}

	/**
	 * Pak de gegeven groep in.
	 * @param group groep
	 * @return de ingepakte groep of null indien de groep null was
	 * @throws GroupDAOException bij onverwachte fouten
	 */
	public GroupWrapper packGroup(Group group) throws GroupDAOException
	{

		GroupWrapper wrapper = null;
		if (group != null)
		{
			try
			{
				SerializedAndZipped data = SerializeAndZipHelper.serializeAndZip(group);
				wrapper = new GroupWrapper();
				wrapper.setData(data);
				wrapper.setPath(group.getId());
				wrapper.setVersionId(group.getVersion().getName());
			}
			catch (IOException e)
			{
				log.error(e.getMessage(), e);
				throw new GroupDAOException(e);
			}
		}
		return wrapper;
	}

	/**
	 * Zoekt wrapper (opslagformaat Group) op gegeven groep.
	 * @param group de groep
	 * @return de wrapper of null indien niet gevonden
	 * @throws GroupDAOException bij onverwachte fouten
	 */
	public GroupWrapper findGroupWrapper(Group group) throws GroupDAOException
	{

		return findGroupWrapper(group.getId(), group.getVersion());
	}

	/**
	 * Zoekt wrapper (opslagformaat Group) op gegeven pad/ versie.
	 * @param path zoekpad
	 * @param version de versie voor de zoektocht
	 * @return de wrapper of null indien niet gevonden
	 * @throws GroupDAOException bij onverwachte fouten
	 */
	public GroupWrapper findGroupWrapper(String path, Version version)
			throws GroupDAOException
	{

		GroupWrapper wrapper = null;
		Session session = null;
		Transaction tx = null;
		try
		{
			session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			List results = session.find("from "
					+ GroupWrapper.class.getName()
					+ " gw where gw.path = ? and gw.versionId = ?", new Object[] {path,
					version.getName()}, new Type[] {Hibernate.STRING, Hibernate.STRING});
			tx.commit();
			if ((results != null) && (!results.isEmpty()))
			{
				if (results.size() > 1)
				{
					throw new GroupDAOException(
							"meer dan 1 resultaat gevonden; database state is ambigu!");
				}
				wrapper = (GroupWrapper) results.get(0);
			}
		}
		catch (HibernateException e)
		{
			log.error(e.getMessage(), e);
			rollback(tx);
			throw new GroupDAOException(e);
		}
		return wrapper;
	}

	/**
	 * Slaat de gegeven groep op in de database.
	 * @param group de groep
	 * @return de evt bijgewerkte groep
	 * @throws GroupDAOException bij onverwachte fouten
	 */
	public Group saveOrUpdateGroup(Group group) throws GroupDAOException
	{

		if (group == null)
		{
			return group;
		}
		Session session = null;
		Transaction tx = null;
		GroupWrapper wrapper = findGroupWrapper(group);
		try
		{
			session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			if (wrapper != null)
			{ // update; groep bestaat reeds
				Long wrapperId = wrapper.getId();
				session.evict(wrapper);
				wrapper = packGroup(group); // zip/ serialiseer de groep
				wrapper.setId(wrapperId);
				session.update(wrapper);
				if (log.isDebugEnabled())
				{
					log.debug("groep " + group + " bijgewerkt");
				}
			}
			else
			{ // create; groep niet gevonden
				wrapper = packGroup(group); // zip/ serialiseer de groep
				Serializable id = session.save(wrapper);
				id = session.save(wrapper);
				if (log.isDebugEnabled())
				{
					log.debug("groep " + group + " toegevoegd (intern id = " + id + ")");
				}
			}
			tx.commit();
		}
		catch (HibernateException e)
		{
			log.error(e.getMessage(), e);
			rollback(tx);
		}
		return group;
	}

	/**
	 * Verwijderd de gegeven groep uit de database.
	 * @param group de groep
	 * @throws GroupDAOException bij onverwachte fouten
	 */
	public void deleteGroup(Group group) throws GroupDAOException
	{

		//TODO check op actieve versie
		// het verwijderen van een groep zal in
		// praktijk slechts worden toegestaan indien de groep nog niet met
		// een actieve versie was verbonden
		if (group == null)
		{
			return;
		}
		Session session = null;
		Transaction tx = null;
		GroupWrapper wrapper = findGroupWrapper(group);
		try
		{
			session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			if (wrapper != null)
			{ // verwijder
				session.delete(wrapper);
			}
			tx.commit();
		}
		catch (HibernateException e)
		{
			log.error(e.getMessage(), e);
			rollback(tx);
		}
	}

	/**
	 * Rollback transactie.
	 * @param tx transactie
	 */
	private void rollback(Transaction tx)
	{
		if (tx != null)
		{
			try
			{
				tx.rollback();
			}
			catch (HibernateException e1)
			{
				log.error(e1.getMessage(), e1);
			}
		}
	}
}