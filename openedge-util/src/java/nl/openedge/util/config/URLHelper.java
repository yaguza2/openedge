package nl.openedge.util.config;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;

/**
 * Helper class for interpreting url locations
 * @author Eelco Hillenius
 */
public class URLHelper {
	
	/**
	 * Interprets some absolute URLs as external paths or from classpath
	 */
	public static URL convertToURL(String path) 
			throws MalformedURLException {
		
		return convertToURL(path, null);
	}
	
	/**
	 * Interprets some absolute URLs as external paths, otherwise generates URL
	 * appropriate for loading from internal webapp or, servletContext is null,
	 * loading from the classpath.
	 */
	public static URL convertToURL(String path, ServletContext servletContext) 
			throws MalformedURLException {
		
		if (path.startsWith("file:") || path.startsWith("http:") || 
				path.startsWith("https:") || path.startsWith("ftp:")) {
			return new URL(path);
		} else if(servletContext != null) {
			// Quick sanity check
			if (!path.startsWith("/"))
				path = "/" + path;
			return servletContext.getResource(path);
		} else {
			ClassLoader clsLoader = Thread.currentThread().getContextClassLoader();
			if(clsLoader == null) {
				return URLHelper.class.getResource(path);	
			} else {
				return clsLoader.getResource(path);
			}			
		}
	}
}
