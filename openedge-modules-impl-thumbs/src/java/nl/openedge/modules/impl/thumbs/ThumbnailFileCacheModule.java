/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003, Open Edge B.V.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions 
 * in binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution. Neither the name of OpenEdge B.V. 
 * nor the names of its contributors may be used to endorse or promote products 
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package nl.openedge.modules.impl.thumbs;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.openedge.modules.types.base.SingletonType;
import nl.openedge.modules.types.initcommands.BeanType;

/**
 * @author Eelco Hillenius
 * simple file cache to use with file systems
 */
public final class ThumbnailFileCacheModule implements SingletonType, BeanType
{

	/* logger */
	private Log log = LogFactory.getLog(ThumbnailFileCacheModule.class);
	/* suffix to use when caching; max size will be appended */
	private String cacheSuffix;
	/* name of cache dir */
	private String cacheDirName;

	/**
	 * lookup file in cache
	 * @param originalFile
	 * @param max size indication
	 * @return File; users should query with file.exist or eq; if it does
	 * 		not exist, this file should be used as the next cache element
	 */
	public File getFromCache(File originalFile, int max)
	{

		String originalName = originalFile.getName();
		int extPos = originalName.lastIndexOf('.');
		String name = originalName.substring(0, extPos);
		String ext = originalName.substring((extPos + 1));
		File dir = originalFile.getParentFile();
		File cacheDir = new File(dir, cacheDirName);
		if (!cacheDir.exists())
		{ // cache dir does not exist yet... create now
			log.info("creating cachedir " + cacheDir.getAbsolutePath());
			cacheDir.mkdir();
		}
		File cacheFile = new File(cacheDir, name + cacheSuffix + max + "." + ext);
		if(log.isDebugEnabled())
		{
			log.debug("cache file found: " + cacheFile.getAbsolutePath());
		} 
		return cacheFile;
	}

	/**
	 * removes all cache files
	 * @param originalFile
	 * @param max size indication
	 * @return File; users should query with file.exist or eq; if it does
	 * 		not exist, this file should be used as the next cache element
	 */
	public void removeFromCache(File originalFile)
	{

		String originalName = originalFile.getName();
		int extPos = originalName.lastIndexOf('.');
		String name = originalName.substring(0, extPos);
		String ext = originalName.substring((extPos + 1));

		String scanFor = name + cacheSuffix;
		File dir = originalFile.getParentFile();
		File cacheDir = new File(dir, cacheDirName);
		if (!cacheDir.exists())
		{ // cache dir does not exist yet... nothing to clear!
			return;
		}
		String[] files = cacheDir.list();
		int size = files.length;
		for (int i = 0; i < size; i++)
		{

			if ((files[i].startsWith(scanFor)) && (files[i].endsWith(ext)))
			{
				if(log.isDebugEnabled())
				{
					log.debug("removing " + files[i] + " from cache");
				}
				File toDelete = new File(cacheDir, files[i]);
				boolean success = toDelete.delete();
				if (!success)
				{
					log.error("deleting " + files[i] + 
						" failed! Clean cache manually");
				}
			}
		}
	}

	/**
	 * @return String
	 */
	public String getCacheDirName()
	{
		return cacheDirName;
	}

	/**
	 * @return String
	 */
	public String getCacheSuffix()
	{
		return cacheSuffix;
	}

	/**
	 * @param string
	 */
	public void setCacheDirName(String string)
	{
		cacheDirName = string;
	}

	/**
	 * @param string
	 */
	public void setCacheSuffix(String string)
	{
		cacheSuffix = string;
	}

}
