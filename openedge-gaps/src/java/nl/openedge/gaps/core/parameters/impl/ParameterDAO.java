/*
 * $Id$ $Revision:
 * 1.1 $ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.zip.DataFormatException;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.type.Type;
import nl.openedge.gaps.core.parameters.Parameter;
import nl.openedge.gaps.core.versions.Version;
import nl.openedge.util.hibernate.HibernateHelper;
import nl.openedge.util.ser.SerializeAndZipHelper;
import nl.openedge.util.ser.SerializedAndZipped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DAO/ utility klasse voor parameters.
 */
public final class ParameterDAO
{

	/** Log. */
	private static Log log = LogFactory.getLog(ParameterDAO.class);

	/**
	 * Construct.
	 */
	public ParameterDAO()
	{
		//
	}

	/**
	 * Zoekt parameter op gegeven pad.
	 * @param path zoekpad
	 * @param version de versie voor de zoektocht
	 * @return de parameter of null indien niet gevonden
	 * @throws ParameterDAOException bij onverwachte fouten
	 */
	public Parameter findParameter(String path, Version version)
			throws ParameterDAOException
	{

		ParameterWrapper wrapper = findParameterWrapper(path, version);
		Parameter param = unpackParameter(wrapper);
		return param;
	}

	/**
	 * Zoekt parameter wrapper (opslagformaat Parameter) op gegeven parameter.
	 * @param parameter de parameter
	 * @return de parameter wrapper of null indien niet gevonden
	 * @throws ParameterDAOException bij onverwachte fouten
	 */
	public ParameterWrapper findParameterWrapper(Parameter parameter)
			throws ParameterDAOException
	{

		return findParameterWrapper(parameter.getId(), parameter.getVersion());
	}

	/**
	 * Zoekt parameter wrapper (opslagformaat Parameter) op gegeven pad/ version.
	 * @param path zoekpad
	 * @param version de versie voor de zoektocht
	 * @return de parameter wrapper of null indien niet gevonden
	 * @throws ParameterDAOException bij onverwachte fouten
	 */
	public ParameterWrapper findParameterWrapper(String path, Version version)
			throws ParameterDAOException
	{

		ParameterWrapper wrapper = null;
		Session session = null;
		try
		{
			session = HibernateHelper.getSession();
			List results = session.find(
					"from " + ParameterWrapper.class.getName()
					+ " pw where pw.path = ? and pw.versionId = ?",
					new Object[] {path, version.getName()},
					new Type[] {Hibernate.STRING, Hibernate.STRING});
			if ((results != null) && (!results.isEmpty()))
			{
				if (results.size() > 1)
				{
					throw new ParameterDAOException(
							"meer dan 1 resultaat gevonden; database state is ambigu!");
				}
				wrapper = (ParameterWrapper) results.get(0);
			}
		}
		catch (HibernateException e)
		{
			log.error(e.getMessage(), e);
			throw new ParameterDAOException(e);
		}
		return wrapper;
	}

	/**
	 * Geeft de parameter-ids voor de gegeven parametergroep-id
	 * @param parameterGroupId id van de parametergroep
	 * @param version de versie waarvoor de parameters dienen te worden bepaald
	 * @return de parameter-ids (mogelijk lege lijst, maar niet null) met elementen
	 *  vh type String[2]; pos 0 == id, pos 1 == localId
	 * @throws ParameterDAOException bij onverwachte fouten
	 */
	public List findParameterIdsForParameterGroup(
			String parameterGroupId, Version version)
		throws ParameterDAOException
	{
		List results = null;
		Session session = null;
		try
		{
			session = HibernateHelper.getSession();
			results = session.find(
					"select pw.path, pw.localId from " + ParameterWrapper.class.getName()
					+ " pw where pw.parameterGroupId = ? and pw.versionId = ?",
					new Object[] {parameterGroupId, version.getName()},
					new Type[] {Hibernate.STRING, Hibernate.STRING});
		}
		catch (HibernateException e)
		{
			log.error(e.getMessage(), e);
			throw new ParameterDAOException(e);
		}
		return results;
	}

	/**
	 * Pak representatie object uit naar {@link Parameter}.
	 * @param wrapper representatie object.
	 * @return de uitgepakte parameter of null indien de wrapper null was
	 * @throws ParameterDAOException bij onverwachte fouten
	 */
	public Parameter unpackParameter(ParameterWrapper wrapper)
			throws ParameterDAOException
	{

		Parameter param = null;
		if (wrapper != null)
		{
			SerializedAndZipped data = wrapper.getData();
			try
			{
				param = (Parameter) SerializeAndZipHelper.unzipAndDeserialize(data);
			}
			catch (IOException e)
			{
				log.error(e.getMessage(), e);
				throw new ParameterDAOException(e);
			}
			catch (DataFormatException e)
			{
				log.error(e.getMessage(), e);
				throw new ParameterDAOException(e);
			}
			catch (ClassNotFoundException e)
			{
				log.error(e.getMessage(), e);
				throw new ParameterDAOException(e);
			}
		}
		return param;
	}

	/**
	 * Pak de gegeven parameter in.
	 * @param parameter parameter.
	 * @return de ingepakte parameter of null indien de parameter null was
	 * @throws ParameterDAOException bij onverwachte fouten
	 */
	public ParameterWrapper packParameter(Parameter parameter)
			throws ParameterDAOException
	{

		ParameterWrapper wrapper = null;
		if (parameter != null)
		{
			try
			{
				SerializedAndZipped data = SerializeAndZipHelper
						.serializeAndZip(parameter);
				wrapper = new ParameterWrapper();
				wrapper.setData(data);
				wrapper.setPath(parameter.getId());
				wrapper.setLocalId(parameter.getLocalId());
				wrapper.setParameterGroupId(parameter.getParameterGroupId());
				wrapper.setVersionId(parameter.getVersion().getName());
			}
			catch (IOException e)
			{
				log.error(e.getMessage(), e);
				throw new ParameterDAOException(e);
			}
		}
		return wrapper;
	}

	/**
	 * Slaat de gegeven parameter op in de database.
	 * @param param de parameter
	 * @return de evt bijgewerkte parameter
	 * @throws ParameterDAOException bij onverwachte fouten
	 */
	public Parameter saveOrUpdateParameter(Parameter param) throws ParameterDAOException
	{

		if (param == null)
		{
			return param;
		}
		Session session = null;
		Transaction tx = null;
		ParameterWrapper wrapper = findParameterWrapper(param);
		try
		{
			session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			if (wrapper != null)
			{ // update; parameter bestaat reeds
				Long wrapperId = wrapper.getId();
				session.evict(wrapper);
				wrapper = packParameter(param); // zip/ serialiseer de groep
				wrapper.setId(wrapperId);
				session.update(wrapper);
				if (log.isDebugEnabled())
				{
					log.debug("parameter " + param + " bijgewerkt");
				}
			}
			else
			{ // create; parameter niet gevonden
				wrapper = packParameter(param); // zip/ serialiseer de parameter
				Serializable id = session.save(wrapper);
				id = session.save(wrapper);
				if (log.isDebugEnabled())
				{
					log.debug("parameter "
							+ param + " toegevoegd (intern id = " + id + ")");
				}
			}
			tx.commit();
		}
		catch (HibernateException e)
		{
			log.error(e.getMessage(), e);
			rollback(tx);
		}
		return param;
	}

	/**
	 * Verwijderd de gegeven parameter uit de database.
	 * @param param de parameter
	 * @throws ParameterDAOException bij onverwachte fouten
	 */
	public void deleteParameter(Parameter param) throws ParameterDAOException
	{
		//TODO check op actieve versie
		// het verwijderen van een parameter zal in
		// praktijk slechts worden toegestaan indien de parameter nog niet met
		// een actieve versie was verbonden
		if (param == null)
		{
			return;
		}
		Session session = null;
		Transaction tx = null;
		ParameterWrapper wrapper = findParameterWrapper(param);
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