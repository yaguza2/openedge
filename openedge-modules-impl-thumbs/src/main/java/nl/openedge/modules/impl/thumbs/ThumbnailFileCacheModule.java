package nl.openedge.modules.impl.thumbs;

import java.io.File;

import nl.openedge.modules.types.base.SingletonType;
import nl.openedge.modules.types.initcommands.BeanType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Eelco Hillenius simple file cache to use with file systems
 */
public final class ThumbnailFileCacheModule implements SingletonType, BeanType
{

	/* logger */
	private Logger log = LoggerFactory.getLogger(ThumbnailFileCacheModule.class);

	/* suffix to use when caching; max size will be appended */
	private String cacheSuffix;

	/* name of cache dir */
	private String cacheDirName;

	/**
	 * lookup file in cache
	 * 
	 * @param originalFile
	 * @param max
	 *            size indication
	 * @return File; users should query with file.exist or eq; if it does not exist, this
	 *         file should be used as the next cache element
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
		if (log.isDebugEnabled())
		{
			log.debug("cache file found: " + cacheFile.getAbsolutePath());
		}
		return cacheFile;
	}

	/**
	 * removes all cache files
	 * 
	 * @param originalFile
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
				if (log.isDebugEnabled())
				{
					log.debug("removing " + files[i] + " from cache");
				}
				File toDelete = new File(cacheDir, files[i]);
				if (!toDelete.delete()) // delete current file
				{
					// fix for java/win bug
					// see:
					// http://forum.java.sun.com/thread.jsp?forum=4&thread=158689&tstart=0&trange=15
					System.gc();
					try
					{
						Thread.sleep(100);
					}
					catch (InterruptedException e)
					{
					}
					if (!toDelete.delete())
					{
						log.error("deleting " + files[i] + " failed! Clean cache manually");
					}
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
