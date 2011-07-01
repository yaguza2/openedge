/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2005, Topicus B.V.
 * All rights reserved.
 */
package nl.openedge.access.cache.impl;

// $Id$
/*
 * ==================================================================== The Apache
 * Software License, Version 1.1 Copyright (c) 2003 - 2004 Greg Luck. All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met: 1. Redistributions of source
 * code must retain the above copyright notice, this list of conditions and the following
 * disclaimer. 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following acknowlegement:
 * "This product includes software developed by Greg Luck
 * (http://sourceforge.net/users/gregluck) and contributors. See
 * http://sourceforge.net/project/memberlist.php?group_id=93232 for a list of
 * contributors" Alternately, this acknowledgement may appear in the software itself, if
 * and wherever such third-party acknowlegements normally appear. 4. The names "EHCache"
 * must not be used to endorse or promote products derived from this software without
 * prior written permission. For written permission, please contact Greg Luck (gregluck at
 * users.sourceforge.net). 5. Products derived from this software may not be called
 * "EHCache" nor may "EHCache" appear in their names without prior written permission of
 * Greg Luck. THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL GREG LUCK OR OTHER
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ==================================================================== This software
 * consists of voluntary contributions made by contributors individuals on behalf of the
 * EHCache project. For more information on EHCache, please see
 * <http://ehcache.sourceforge.net/>.
 */
import java.util.Properties;

import net.sf.ehcache.CacheManager;
import nl.openedge.access.cache.Cache;
import nl.openedge.access.cache.CacheException;
import nl.openedge.access.cache.CacheProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * Cache Provider plugin for Hibernate Use
 * <code>hibernate.cache.provider_class=org.hibernate.cache.EhCacheProvider</code> in
 * Hibernate 3.x or later Taken from EhCache 0.9 distribution
 * @author Greg Luck
 * @author Emmanuel Bernard
 */
public class EhCacheProvider implements CacheProvider
{

	private static final Log log = LogFactory.getLog(EhCacheProvider.class);

	// CacheManager.create() actually returns a singleton reference, which is causing
	// problems with users attempting to use multiple SessionFactories all using the
	// EhCacheProvider in the same classloader. The work-around is to use simple reference
	// counting here...
	private static int referenceCount = 0;

	private CacheManager manager;

	/**
	 * Builds a Cache.
	 * <p>
	 * Even though this method provides properties, they are not used. Properties for
	 * EHCache are specified in the ehcache.xml file. Configuration will be read from
	 * ehcache.xml for a cache declaration where the name attribute matches the name
	 * parameter in this builder.
	 * @param name the name of the cache.
	 * @param properties not used
	 * @return a newly built cache will be built and initialised
	 * @throws CacheException inter alia, if a cache of the same name already exists
	 */
	public Cache buildCache(String name, Properties properties) throws CacheException
	{
		try
		{
			net.sf.ehcache.Cache cache = manager.getCache(name);
			if (cache == null)
			{
				if (properties == null || properties.isEmpty())
				{
					log.warn("Could not find configuration [" + name + "]; using defaults.");
					manager.addCache(name);
					cache = manager.getCache(name);
				}
				else
				{
					int maxElementsInMemory=Integer.parseInt(properties.getProperty("maxElementsInMemory","100"));
					boolean overflowToDisk=Boolean.parseBoolean(properties.getProperty("overflowToDisk","false"));
					boolean eternal =Boolean.parseBoolean(properties.getProperty("eternal","false"));
					long timeToLiveSeconds=Long.parseLong(properties.getProperty("timeToLiveSeconds","0"));
					long timeToIdleSeconds=Long.parseLong(properties.getProperty("timeToIdleSeconds","0"));
					cache=new net.sf.ehcache.Cache(name,maxElementsInMemory,overflowToDisk,eternal,timeToLiveSeconds,timeToIdleSeconds);
				}
				log.debug("started EHCache region: " + name);
			}
			return new EhCache(cache);
		}
		catch (net.sf.ehcache.CacheException e)
		{
			throw new CacheException(e);
		}
	}

	/**
	 * Returns the next timestamp.
	 */
	public long nextTimestamp()
	{
		return Timestamper.next();
	}

	/**
	 * Callback to perform any necessary initialization of the underlying cache
	 * implementation during SessionFactory construction.
	 * @param properties current configuration settings.
	 */
	@SuppressWarnings("unused")
	public void start(Properties properties) throws CacheException
	{
		try
		{
			manager = CacheManager.create();
			referenceCount++;
		}
		catch (net.sf.ehcache.CacheException e)
		{
			throw new CacheException(e);
		}
	}

	/**
	 * Callback to perform any necessary cleanup of the underlying cache implementation
	 * during SessionFactory.close().
	 */
	public void stop()
	{
		if (manager != null)
		{
			if (--referenceCount == 0)
			{
				manager.shutdown();
			}
			manager = null;
		}
	}

	public boolean isMinimalPutsEnabledByDefault()
	{
		return false;
	}

}
