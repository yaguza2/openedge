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
import net.sf.hibernate.type.Type;
import nl.openedge.gaps.core.groups.Group;
import nl.openedge.gaps.core.versions.Version;
import nl.openedge.gaps.util.CacheUtil;
import nl.openedge.gaps.util.TransactionUtil;
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
	/** naam van group cache. */
	public static final String GROUPS_CACHE_NAME =
		"nl.openedge.gaps.core.groups.custcache.Groups";

	/**
	 * naam van groupwrapper cache; LET OP: dit dient een
	 * cache voor 'slechts' transactiegebruik te zijn.
	 */
	private static final String GROUP_WRAPPERS_CACHE_NAME =
		"nl.openedge.gaps.core.groups.custcache.GroupWrappers";

	static
	{
		// registreer als een speciale transactie cache
		CacheUtil.addTransactionCacheName(GROUP_WRAPPERS_CACHE_NAME);
	}

	/** Log. */
	private static Log log = LogFactory.getLog(GroupDAO.class);

	/** of de cache gebruikt wordt. */
	private boolean useCache = true;

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
		Group group = null;
		String cacheKey = null;
		if(isUseCache())
		{
			cacheKey = getCacheKeyForGroups(path, version);
			group = (Group)CacheUtil.getObjectFromCache(cacheKey, GROUPS_CACHE_NAME);
		}
		if(group != null)
		{
			return group;
		}
		GroupWrapper wrapper = findGroupWrapper(path, version);
		group = unpackGroup(wrapper);
		if(isUseCache())
		{
			CacheUtil.putObjectInCache(cacheKey, group, GROUPS_CACHE_NAME);
		}
		return group;
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
		String cacheKey = null;
		if(isUseCache())
		{
			cacheKey = getCacheKeyForGroups(path, version);
			wrapper = (GroupWrapper)CacheUtil.getObjectFromCache(
					cacheKey, GROUP_WRAPPERS_CACHE_NAME);
		}
		if(wrapper != null)
		{
			return wrapper;
		}
		Session session = null;
		try
		{
			session = HibernateHelper.getSession();
			List results = session.find(
					"from " + GroupWrapper.class.getName()
					+ " gw where gw.path = ? and gw.versionId = ?",
					new Object[] {path, version.getName()},
					new Type[] {Hibernate.STRING, Hibernate.STRING});
			if ((results != null) && (!results.isEmpty()))
			{
				if (results.size() > 1)
				{
					throw new GroupDAOException(
							"meer dan 1 resultaat gevonden; database state is ambigu!");
				}
				wrapper = (GroupWrapper) results.get(0);
				if(isUseCache())
				{
					CacheUtil.putObjectInCache(
							cacheKey, wrapper, GROUP_WRAPPERS_CACHE_NAME);
				}
			}
		}
		catch (HibernateException e)
		{
			log.error(e.getMessage(), e);
			throw new GroupDAOException(e);
		}
		return wrapper;
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
	 * Slaat de gegeven groep op in de database.
	 * @param group de groep
	 * @return de evt bijgewerkte groep
	 * @throws GroupDAOException bij onverwachte fouten
	 */
	public Group saveOrUpdateGroup(Group group)
		throws GroupDAOException
	{
		boolean useTransaction = !TransactionUtil.isTransactionStarted();
		if (group == null)
		{
			return group;
		}
		Session session = null;
		GroupWrapper wrapper = findGroupWrapper(group);
		try
		{
			session = HibernateHelper.getSession();
			if(useTransaction)
			{
				TransactionUtil.begin(session);
			}
			if (wrapper != null)
			{ // update; groep bestaat reeds
				GroupWrapper newWrapper = packGroup(group); // zip/ serialiseer de groep
				wrapper.setData(newWrapper.getData());
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
			if(useTransaction)
			{
				TransactionUtil.commit();
			}
			if(isUseCache())
			{
				String cacheKey;
				cacheKey = getCacheKeyForGroups(group.getId(), group.getVersion());
				CacheUtil.putObjectInCache(cacheKey, group, GROUPS_CACHE_NAME);
				CacheUtil.putObjectInCache(cacheKey, wrapper, GROUP_WRAPPERS_CACHE_NAME);
			}
		}
		catch (HibernateException e)
		{
			log.error(e.getMessage(), e);
			if(useTransaction)
			{
				TransactionUtil.rollback();
			}
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
		boolean useTransaction = !TransactionUtil.isTransactionStarted();
		//TODO check op actieve versie
		// het verwijderen van een groep zal in
		// praktijk slechts worden toegestaan indien de groep nog niet met
		// een actieve versie was verbonden
		if (group == null)
		{
			return;
		}
		Session session = null;
		GroupWrapper wrapper = findGroupWrapper(group);
		try
		{
			session = HibernateHelper.getSession();
			if(useTransaction)
			{
				TransactionUtil.begin(session);
			}
			if (wrapper != null)
			{ // verwijder
				session.delete(wrapper);
			}
			if(useTransaction)
			{
				TransactionUtil.commit();
			}
			if(isUseCache())
			{
				String cacheKey;
				cacheKey = getCacheKeyForGroups(group.getId(), group.getVersion());
				CacheUtil.removeObjectFromCache(cacheKey, GROUPS_CACHE_NAME);
				CacheUtil.removeObjectFromCache(cacheKey, GROUP_WRAPPERS_CACHE_NAME);
			}
		}
		catch (HibernateException e)
		{
			log.error(e.getMessage(), e);
			if(useTransaction)
			{
				TransactionUtil.rollback();
			}
		}
	}

	/**
	 * Geeft cachekey voor groepen.
	 * @param path pad
	 * @param version versie
	 * @return de cache key voor het gegeven pad/ groep
	 */
	private String getCacheKeyForGroups(String path, Version version)
	{
		String cacheKey;
		cacheKey = path + "|" + version.getName();
		return cacheKey;
	}

	/**
	 * Geeft of caching wordt gebruikt.
	 * @return of caching wordt gebruikt.
	 */
	public boolean isUseCache()
	{
		return useCache;
	}

	/**
	 * Zet of caching wordt gebruikt.
	 * @param useCache of caching wordt gebruikt.
	 */
	public void setUseCache(boolean useCache)
	{
		this.useCache = useCache;
	}
}