package nl.openedge.access.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Super-naieve cache implementatie
 */
public class HashMapCacheImpl implements Cache
{
    /**
     * opslaan in synchronized map om corruptie te voorkomen bij mogelijk concurrent gebruik.
     */
    private Map cacheMap = Collections.synchronizedMap( new HashMap<Object,Object>());

    public Object get(Object key) throws CacheException
    {
	return cacheMap.get(key);
    }

    public void put(Object key, Object value) throws CacheException
    {
	cacheMap.put(key, value);
    }
}
