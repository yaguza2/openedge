/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c)
 * All rechten voorbehouden.
 */
package nl.openedge.gaps.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import nl.openedge.gaps.core.Entity;
import nl.openedge.gaps.core.groups.impl.GroupWrapper;
import nl.openedge.gaps.core.parameters.impl.ParameterWrapper;
import nl.openedge.gaps.core.versions.Version;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility voor EHCache.
 */
public final class CacheUtil
{
	/** log. */
	private static Log log = LogFactory.getLog(CacheUtil.class);

	/** lijst namen van caches die bedoeld zijn voor transaction caches. */
	private static Set transactionCacheNames = new HashSet();

	/** houder voor transactie caches. */
	private static ThreadLocal txCacheHolder = new ThreadLocal();
	static
	{
		txCacheHolder.set(new HashMap());
	}

	/**
	 * Verborgen utiltiy constructor.
	 */
	private CacheUtil()
	{
		super();
	}

	/**
	 * Haalt object met gegeven id op uit de cache met gegeven naam.
	 * @param cacheKey cache id
	 * @param cacheName naam cache
	 * @return het object of null indien niet in cache gevonden
	 */
	public static Object getObjectFromCache(String cacheKey, String cacheName)
	{
		Object result = null;
		if(transactionCacheNames.contains(cacheName))
		{
			Map txCache = (Map)txCacheHolder.get();
			if(txCache != null)
			{
				result = txCache.get(cacheKey);
				if(log.isDebugEnabled())
				{
					if(result != null)
					{
						log.debug(result + " gevonden onder key " + cacheKey
								+ " in TRANSACTIE cache " + cacheName);
					}
					else
					{
						log.debug("geen object gevonden onder key " + cacheKey
								+ " in TRANSACTIE cache " + cacheName);
					}
				}
			}
			else
			{
				log.warn("TRANSACTIE ThreadLocale nog niet aangemaakt voor Thread "
						+ Thread.currentThread().getName());
			}
		}
		else
		{
			try
			{
				CacheManager cacheManager = CacheManager.getInstance();
				Cache cache = cacheManager.getCache(cacheName);
				if (cache != null)
				{
					Element el = cache.get(cacheKey);
					if (el != null)
					{
						result = el.getValue();
						if(log.isDebugEnabled())
						{
							log.debug(result + " gevonden onder key " + cacheKey
									+ " in cache " + cacheName);
						}
					}
					else if(log.isDebugEnabled())
					{
						log.debug("geen object gevonden onder key " + cacheKey
								+ " in cache " + cacheName);
					}
				}
				else
				{
					throw new CacheException("cache " + cacheName + " niet gevonden");
				}
			}
			catch (CacheException e)
			{
				log.error("cache " + cacheName + "is niet beschikbaar: ", e);
			}
		}
		return result;
	}

	/**
	 * Haalt object met gegeven id op uit de cache met gegeven naam.
	 * @param cacheKey cache id
	 * @param cacheName naam cache
	 * @return het object of null indien niet in cache gevonden
	 */
	public static Object removeObjectFromCache(String cacheKey, String cacheName)
	{
		Object result = null;
		if(transactionCacheNames.contains(cacheName))
		{
			Map txCache = (Map)txCacheHolder.get();
			if(txCache != null)
			{
				result = txCache.remove(cacheKey);
				if(log.isDebugEnabled())
				{
					log.debug("object met key " + cacheKey
							+ " verwijderd uit TRANSACTIE cache " + cacheName);
				}
			}
			else
			{
				log.error("TRANSACTIE ThreadLocale nog niet aangemaakt voor Thread "
						+ Thread.currentThread().getName());
			}
		}
		else
		{
			try
			{
				CacheManager cacheManager = CacheManager.getInstance();
				Cache cache = cacheManager.getCache(cacheName);
				if (cache != null)
				{
					cache.remove(cacheKey);
					if(log.isDebugEnabled())
					{
						log.debug("object met key " + cacheKey
								+ " verwijderd uit cache " + cacheName);
					}
				}
				else
				{
					throw new CacheException("cache " + cacheName + " niet gevonden");
				}
			}
			catch (CacheException e)
			{
				log.error("cache " + cacheName + "is niet beschikbaar: ", e);
			}
		}
		return result;
	}

	/**
	 * Schoon cache met gegeven naam.
	 * @param cacheName te schonen cache
	 */
	public static void resetCache(String cacheName)
	{
		if(transactionCacheNames.contains(cacheName))
		{
			Map txCache = (Map)txCacheHolder.get();
			if(txCache != null)
			{
				txCache.clear();
				txCacheHolder.set(null);
				if(log.isDebugEnabled())
				{
					log.debug("TRANSACTIE cache " + cacheName + " geschoond");
				}
			}
			else
			{
				log.error("TRANSACTIE ThreadLocale nog niet aangemaakt voor Thread "
						+ Thread.currentThread().getName());
			}
		}
		else
		{
			try
			{
				CacheManager cacheManager = CacheManager.getInstance();
				Cache cache = cacheManager.getCache(cacheName);
				if (cache != null)
				{
					cache.removeAll();
					if(log.isDebugEnabled())
					{
						log.debug("cache " + cacheName + " geschoond");
					}
				}
				else
				{
					throw new CacheException("cache " + cacheName + " niet gevonden");
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
	}

	/**
	 * Stopt object in gegeven cache.
	 * @param cacheKey cache id
	 * @param toCache te cachen object
	 * @param cacheName naam cache
	 */
	public static void putObjectInCache(
			String cacheKey, Serializable toCache, String cacheName)
	{
		if(transactionCacheNames.contains(cacheName))
		{
			Map txCache = (Map)txCacheHolder.get();
			if(txCache == null)
			{
				txCache = new HashMap();
				txCacheHolder.set(txCache);
			}
			txCache.put(cacheKey, toCache);
			if(log.isDebugEnabled())
			{
				log.debug(toCache + " opgeslagen in TRANSACTIE cache "
						+ cacheName + " met key " + cacheKey);
			}
		}
		else
		{
			try
			{
				CacheManager cacheManager = CacheManager.getInstance();
				Cache cache = cacheManager.getCache(cacheName);
				if (cache != null)
				{
					Element el = new Element(cacheKey, toCache);
					cache.put(el);
					if(log.isDebugEnabled())
					{
						log.debug(toCache + " opgeslagen in cache "
								+ cacheName + " met key " + cacheKey);
					}
				}
				else
				{
					throw new CacheException("cache " + cacheName + " niet gevonden");
				}
			}
			catch (CacheException e)
			{
				log.error("cache " + cacheName + "is niet beschikbaar: ", e);
			}
		}
	}

	/**
	 * Schoon alle transactie caches.
	 */
	public static void flushTransactionCaches()
	{
		String cacheName = null;
		for(Iterator i = transactionCacheNames.iterator(); i.hasNext();)
		{
			cacheName = (String)i.next();
			resetCache(cacheName);
		}
	}

	/**
	 * voeg een cachenaam toe van een transactie cache.
	 * @param transactionCacheName naam transactie cache
	 */
	public static void addTransactionCacheName(String transactionCacheName)
	{
		transactionCacheNames.add(transactionCacheName);
		log.info(transactionCacheName + " toegevoegd als een TRANSACTIE cache");
	}

	/**
	 * verwijder een cachenaam van een transactie cache.
	 * @param transactionCacheName naam transactie cache
	 */
	public static void removeTransactionCacheName(String transactionCacheName)
	{
		transactionCacheNames.remove(transactionCacheName);
		log.info(transactionCacheName + " verwijderd als een TRANSACTIE cache");
	}

	/**
	 * Geef namen transactie caches.
	 * @return lijst transactie cache namen
	 */
	public static Set getTransactionCacheName()
	{
		return transactionCacheNames;
	}
}
