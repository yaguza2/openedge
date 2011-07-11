package nl.openedge.access.cache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Super-naieve cache implementatie
 */
public class HashMapCacheImpl implements Cache
{
	private ConcurrentHashMap<Object, Object> cacheMap = new ConcurrentHashMap<Object, Object>();

	@Override
	public Object get(Object key)
	{
		return cacheMap.get(key);
	}

	@Override
	public void put(Object key, Object value)
	{
		cacheMap.put(key, value);
	}
}
