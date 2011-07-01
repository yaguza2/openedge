/*
 * $Id$ $Revision$
 * $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.versions.impl;

import java.util.HashSet;
import java.util.List;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import nl.openedge.gaps.core.Entity;
import nl.openedge.gaps.core.RegistryException;
import nl.openedge.gaps.core.groups.Group;
import nl.openedge.gaps.core.groups.StructuralGroup;
import nl.openedge.gaps.core.groups.impl.GroupWrapper;
import nl.openedge.gaps.core.parameters.impl.ParameterWrapper;
import nl.openedge.gaps.core.versions.Version;
import nl.openedge.gaps.util.CacheUtil;
import nl.openedge.gaps.util.TransactionUtil;
import nl.openedge.util.hibernate.HibernateHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DAO voor Version objecten.
 */
public final class VersionDAO
{
	/** naam van entity versions cache. */
	public static final String ENTITY_VERSIONS_CACHE_NAME =
		"nl.openedge.gaps.core.versions.custcache.EntityVersions";

	/** naam van entity versions cache. */
	public static final String VERSION_CACHE_NAME =
		"nl.openedge.gaps.core.versions.custcache.Version";

	/** Log. */
	private static Log log = LogFactory.getLog(VersionDAO.class);

	/** of de cache gebruikt wordt. */
	private boolean useCache = true;

	/**
	 * Construct.
	 */
	public VersionDAO()
	{
		//
	}

	/**
	 * Laad alle bekende versies in.
	 * @return een lijst met alle bekende versies
	 * @throws VersionDAOException bij onverwachte dao fouten
	 */
	public List getAllVersions() throws VersionDAOException
	{
		List versions = null;
		try
		{
			Session session = HibernateHelper.getSession();
			versions = session.find("from " + Version.class.getName());
		}
		catch (HibernateException e)
		{
			throw new VersionDAOException(e);
		}
		return versions;
	}

	/**
	 * Haal versie op naam op (gooit exception indien niet gevonden).
	 * @param versionName versienaam
	 * @return de versie
	 * @throws VersionDAOException bij onverwachte dao fouten
	 */
	public Version getVersion(String versionName) throws VersionDAOException
	{
		Version version = null;
		if(isUseCache())
		{
			version = getVersionFromCache(versionName);
		}
		if(version != null)
		{
			return version;
		}
		try
		{
			Session session = HibernateHelper.getSession();
			List result = session.find("from "
					+ Version.class.getName() + " v where v.name = ?", versionName,
					Hibernate.STRING);
			if (result.isEmpty())
			{
				throw new VersionDAOException("versie " + versionName + " niet gevonden");
			}
			else if (result.size() > 1)
			{
				throw new IllegalStateException(
						"meerdere versie gevonden met dezelfde naam (" + versionName + ")");
			}
			else
			{
				version = (Version) result.get(0);
				if(isUseCache())
				{
					putVersionInCache(version);	
				}
			}
		}
		catch (HibernateException e)
		{
			throw new RegistryException(e);
		}
		return version;
	}

	/**
	 * @see nl.openedge.gaps.core.versions.VersionRegistryDelegate#updateVersion(nl.openedge.gaps.core.Version)
	 */
	public void updateVersion(Version version) throws VersionDAOException
	{
		boolean useTransaction = !TransactionUtil.isTransactionStarted();
		try
		{
			Session session = HibernateHelper.getSession();
			if(useTransaction)
			{
				TransactionUtil.begin(session);
			}
			session.update(version);
			if(useTransaction)
			{
				TransactionUtil.commit();
			}
			if(isUseCache())
			{
				putVersionInCache(version);	
			}
		}
		catch (HibernateException e)
		{
			if(useTransaction)
			{
				TransactionUtil.rollback();
			}
			throw new VersionDAOException(e);
		}
	}

	/**
	 * @see nl.openedge.gaps.core.versions.VersionRegistryDelegate#deleteVersion(nl.openedge.gaps.core.Version)
	 */
	public void deleteVersion(Version version) throws VersionDAOException
	{
		boolean useTransaction = !TransactionUtil.isTransactionStarted();
		// TODO dit dienen we alleen toe te staan voor een versie die nog
		// niet actief is
		try
		{
			Session session = HibernateHelper.getSession();
			if(useTransaction)
			{
				TransactionUtil.begin(session);
			}
			session.delete(version);
			if(useTransaction)
			{
				TransactionUtil.commit();
			}
		}
		catch (HibernateException e)
		{
			if(useTransaction)
			{
				TransactionUtil.rollback();
			}
			throw new VersionDAOException(e);
		}
	}

	/**
	 * @see nl.openedge.gaps.core.versions.VersionRegistryDelegate#saveVersion(nl.openedge.gaps.core.versions.Version,
	 *      StructuralGroup)
	 */
	public void saveVersion(Version version) throws VersionDAOException
	{
		boolean useTransaction = !TransactionUtil.isTransactionStarted();
		try
		{
			Session session = HibernateHelper.getSession();
			if(useTransaction)
			{
				TransactionUtil.begin(session);
			}
			session.save(version);
			if(useTransaction)
			{
				TransactionUtil.commit();
			}
			if(isUseCache())
			{
				CacheUtil.resetCache(ENTITY_VERSIONS_CACHE_NAME);
				putVersionInCache(version);
			}
		}
		catch (HibernateException e)
		{
			if(useTransaction)
			{
				TransactionUtil.rollback();
			}
			throw new VersionDAOException(e);
		}
	}

	/**
	 * Geeft het associatieobject waarmee de versies voor de gegeven entiteit worden
	 * bijgehouden.
	 * @param entity de groep
	 * @return het associatieobject (nooit null)
	 * @throws VersionDAOException bij onverwachte dao fouten
	 */
	public EntityVersions getVersions(Entity entity) throws VersionDAOException
	{
		String entityId = entity.getId();
		EntityVersions entityVersions = null;
		if(isUseCache())
		{
			entityVersions = getEntityVersionsFromCache(entity);
		}
		if (entityVersions != null)
		{
			return entityVersions; // retourneer cache element
		}
		else
		{
			entityVersions = new EntityVersions(); // creeer en voeg straks toe aan cache
		}
		entityVersions.setEntityId(entityId);
		Class clazz = null;
		if (entity instanceof Group)
		{
			clazz = GroupWrapper.class;
		}
		else
		{
			clazz = ParameterWrapper.class;
		}
		try
		{
			Session session = HibernateHelper.getSession();
			Query query = session.createQuery("select w.versionId from "
					+ clazz.getName() + " w where w.path = ?");
			query.setString(0, entityId);
			List result = query.list();
			entityVersions.setVersionIds(new HashSet(result));
			if(isUseCache())
			{
				putEntityVersionsInCache(entity, entityVersions);
			}
		}
		catch (HibernateException e)
		{
			throw new VersionDAOException(e);
		}
		return entityVersions;
	}

	/**
	 * Stopt de versies van entiteit in cache.
	 * @param entity de entiteit
	 * @param entityVersions entiteit versies object
	 */
	private void putVersionInCache(Version version)
	{
		CacheUtil.putObjectInCache(version.getName(), version, VERSION_CACHE_NAME);
	}

	/**
	 * Stopt de versies van entiteit in cache.
	 * @param entity de entiteit
	 * @param entityVersions entiteit versies object
	 */
	private void putEntityVersionsInCache(Entity entity, EntityVersions entityVersions)
	{
		CacheUtil.putObjectInCache(entity.getId(), entityVersions, ENTITY_VERSIONS_CACHE_NAME);
	}

	/**
	 * Haalt de versies van entiteit op uit cache.
	 * @param entity de entiteit
	 * @return EntityVersions object met daarin de versies of null indien niet in
	 *   cache gevonden.
	 */
	private EntityVersions getEntityVersionsFromCache(Entity entity)
	{
		String id = entity.getId();
		EntityVersions entityVersions = (EntityVersions)
			CacheUtil.getObjectFromCache(id, ENTITY_VERSIONS_CACHE_NAME);
		return entityVersions;
	}

	/**
	 * Haalt de versie met de gegeven naam op uit de cache.
	 * @param versionName naam versie
	 * @return het Versie object of null indien niet in cache gevonden
	 */
	private Version getVersionFromCache(String versionName)
	{
		Version version = (Version)CacheUtil.getObjectFromCache(versionName, VERSION_CACHE_NAME);
		return version;
	}

	/**
	 * Leeg versie cache.
	 * @param version versie
	 */
	private void resetVersionCache(Version version)
	{
		CacheUtil.resetCache(VERSION_CACHE_NAME);
	}

	/**
	 * Leeg entiteit-versie cache.
	 */
	public void resetEntityVersionsCache()
	{
		CacheUtil.resetCache(ENTITY_VERSIONS_CACHE_NAME);
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