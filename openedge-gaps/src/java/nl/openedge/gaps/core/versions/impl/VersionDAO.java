/*
 * $Id$ $Revision$
 * $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.versions.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import nl.openedge.gaps.core.Entity;
import nl.openedge.gaps.core.RegistryException;
import nl.openedge.gaps.core.groups.Group;
import nl.openedge.gaps.core.groups.StructuralGroup;
import nl.openedge.gaps.core.groups.impl.GroupWrapper;
import nl.openedge.gaps.core.parameters.impl.ParameterWrapper;
import nl.openedge.gaps.core.versions.Version;
import nl.openedge.util.hibernate.HibernateHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DAO voor Version objecten.
 */
public final class VersionDAO
{
	/** naam van entity versions cache. */
	private static final String ENTITY_VERSIONS_CACHE_NAME =
		"nl.openedge.gaps.core.versions.custcache.EntityVersions";

	/** naam van entity versions cache. */
	private static final String VERSION_CACHE_NAME =
		"nl.openedge.gaps.core.versions.custcache.Version";

	/** of de cache gebruikt wordt. */
	private boolean useCache = false;

	/** Log. */
	private static Log log = LogFactory.getLog(VersionDAO.class);

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
		if(useCache)
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
				if(useCache)
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
		Transaction tx = null;
		try
		{
			Session session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			session.update(version);
			tx.commit();
			if(useCache)
			{
				putVersionInCache(version);	
			}
		}
		catch (HibernateException e)
		{
			rollback(tx);
			throw new VersionDAOException(e);
		}
	}

	/**
	 * @see nl.openedge.gaps.core.versions.VersionRegistryDelegate#deleteVersion(nl.openedge.gaps.core.Version)
	 */
	public void deleteVersion(Version version) throws VersionDAOException
	{
		// TODO dit dienen we alleen toe te staan voor een versie die nog
		// niet actief is
		Transaction tx = null;
		try
		{
			Session session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			session.delete(version);
			tx.commit();
		}
		catch (HibernateException e)
		{
			rollback(tx);
			throw new VersionDAOException(e);
		}
	}

	/**
	 * @see nl.openedge.gaps.core.versions.VersionRegistryDelegate#saveVersion(nl.openedge.gaps.core.versions.Version,
	 *      StructuralGroup)
	 */
	public void saveVersion(Version version) throws VersionDAOException
	{
		Transaction tx = null;
		try
		{
			Session session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			session.save(version);
			tx.commit();
			if(useCache)
			{
				resetVersionCache();
				putVersionInCache(version);
			}
		}
		catch (HibernateException e)
		{
			rollback(tx);
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
		if(useCache)
		{
			getEntityVersionsFromCache(entity);
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
			if(useCache)
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
		putObjectInCache(version.getName(), version, VERSION_CACHE_NAME);
	}

	/**
	 * Stopt de versies van entiteit in cache.
	 * @param entity de entiteit
	 * @param entityVersions entiteit versies object
	 */
	private void putEntityVersionsInCache(Entity entity, EntityVersions entityVersions)
	{
		putObjectInCache(entity.getId(), entityVersions, ENTITY_VERSIONS_CACHE_NAME);
	}

	/**
	 * Stopt object in gegeven cache.
	 * @param id cache id
	 * @param toCache te cachen object
	 * @param cacheName naam cache
	 */
	private void putObjectInCache(String id, Serializable toCache, String cacheName)
	{
		try
		{
			CacheManager cacheManager = CacheManager.getInstance();
			Cache cache = cacheManager.getCache(cacheName);
			if (cache != null)
			{
				Element el = new Element(id, toCache);
				cache.put(el);	
			}
		}
		catch (CacheException e)
		{
			log.error("cache " + cacheName + "is niet beschikbaar: ", e);
		}
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
			getObjectFromCache(id, ENTITY_VERSIONS_CACHE_NAME);
		return entityVersions;
	}

	/**
	 * Haalt de versie met de gegeven naam op uit de cache.
	 * @param versionName naam versie
	 * @return het Versie object of null indien niet in cache gevonden
	 */
	private Version getVersionFromCache(String versionName)
	{
		Version version = (Version)getObjectFromCache(versionName, VERSION_CACHE_NAME);
		return version;
	}

	/**
	 * Haalt de versie met de gegeven naam op uit de cache.
	 * @param id cache id
	 * @param cacheName naam cache
	 * @return het Versie object of null indien niet in cache gevonden
	 */
	private Object getObjectFromCache(String id, String cacheName)
	{
		Object result = null;
		try
		{
			CacheManager cacheManager = CacheManager.getInstance();
			Cache cache = cacheManager.getCache(cacheName);
			if (cache != null)
			{
				Element el = cache.get(id);
				if (el != null)
				{
					result = el.getValue();	
				}	
			}
		}
		catch (CacheException e)
		{
			log.error("cache " + cacheName + "is niet beschikbaar: ", e);
		}
		return result;
	}

	/**
	 * Leeg versie cache.
	 * @param version versie
	 */
	private void resetVersionCache(Version version)
	{
		resetCache(VERSION_CACHE_NAME);
	}

	/**
	 * Leeg entiteit-versie cache.
	 */
	public void resetEntityVersionsCache()
	{
		resetCache(ENTITY_VERSIONS_CACHE_NAME);
	}

	/**
	 * Schoon cache met gegeven naam.
	 * @param cacheName te schonen cache
	 */
	private void resetCache(String cacheName)
	{
		try
		{
			CacheManager cacheManager = CacheManager.getInstance();
			Cache cache = cacheManager.getCache(cacheName);
			if (cache != null)
			{
				cache.removeAll();
			}
		}
		catch (CacheException e)
		{
			log.error("cache " + cacheName + "is niet beschikbaar: ", e);
		}
		catch (IOException e)
		{
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * Leeg versie cache.
	 */
	public void resetVersionCache()
	{
		try
		{
			CacheManager cacheManager = CacheManager.getInstance();
			Cache cache = cacheManager.getCache(ENTITY_VERSIONS_CACHE_NAME);
			if (cache != null)
			{
				cache.removeAll();
			}
		}
		catch (CacheException e)
		{
			log.error("cache " + VERSION_CACHE_NAME + "is niet beschikbaar: ", e);
		}
		catch (IOException e)
		{
			log.error(e.getMessage(), e);
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

	/**
	 * Get useCache.
	 * @return useCache.
	 */
	public boolean isUseCache()
	{
		return useCache;
	}

	/**
	 * Set useCache.
	 * @param useCache useCache.
	 */
	public void setUseCache(boolean useCache)
	{
		this.useCache = useCache;
	}
}