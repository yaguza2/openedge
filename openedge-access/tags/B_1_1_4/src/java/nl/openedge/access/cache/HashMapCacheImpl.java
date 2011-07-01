/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
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
	 * opslaan in synchronized map om corruptie te voorkomen bij mogelijk
	 * concurrent gebruik.
	 */
	private Map cacheMap = Collections.synchronizedMap(new HashMap<Object, Object>());

	public Object get(Object key) throws CacheException 
	{
		return cacheMap.get(key);
	}

	public void put(Object key, Object value) throws CacheException 
	{
		cacheMap.put(key, value);
	}

	/**
	 * @see Cache#clear()
	 */
	public void clear() 
	{
		cacheMap.clear();
	}

	/**
	 * @see Cache#size()
	 */
	public int size() 
	{
		return cacheMap.size();
	}
	
	
}
