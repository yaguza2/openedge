package nl.openedge.util.web;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Records extra information about a session.
 * 
 * @author Eelco Hillenius
 */
public class SessionStats implements Serializable
{
	private static final long serialVersionUID = 1L;

	/** the remote address. */
	private String remoteAddr;

	/** remote host. */
	private String remoteHost;

	/** hit count. */
	private int hitCount = 0;

	/** extra attributes. */
	private HashMap<String, Object> attributes = new HashMap<String, Object>();

	/**
	 * default constructor.
	 */
	public SessionStats()
	{

		hitCount++;
	}

	/**
	 * increase hitcount.
	 */
	public void hit()
	{
		hitCount++;
	}

	/**
	 * Returns the hitCount.
	 * 
	 * @return int
	 */
	public int getHitCount()
	{
		return hitCount;
	}

	/**
	 * Returns the remoteAddr.
	 * 
	 * @return String
	 */
	public String getRemoteAddr()
	{
		return remoteAddr;
	}

	/**
	 * Sets the remoteAddr.
	 * 
	 * @param remoteAddr
	 *            The remoteAddr to set
	 */
	public void setRemoteAddr(String remoteAddr)
	{
		this.remoteAddr = new String(remoteAddr);
	}

	/**
	 * Set attribute.
	 * 
	 * @param key
	 *            attribute key
	 * @param value
	 *            attribute value
	 */
	public void setAttribute(String key, String value)
	{
		attributes.put(key, value);
	}

	/**
	 * Returns the attributes.
	 * 
	 * @return HashMap
	 */
	public HashMap<String, Object> getAttributes()
	{
		return attributes;
	}

	/**
	 * Get named attribute.
	 * 
	 * @param key
	 *            key (name) of attribute
	 * @return attribute or null if nothing is stored under the given key
	 */
	public Object getAttribute(String key)
	{
		return attributes.get(key);
	}

	/**
	 * put (possibly overwrite) named attribute.
	 * 
	 * @param key
	 *            key (name) of attribute
	 * @param attribute
	 *            attribute to store
	 */
	public void putAttribute(String key, Object attribute)
	{
		attributes.put(key, attribute);
	}

	/**
	 * Sets the attributes.
	 * 
	 * @param attributes
	 *            The attributes to set
	 */
	public void setAttributes(HashMap<String, Object> attributes)
	{
		this.attributes = attributes;
	}

	/**
	 * Returns the remoteHost.
	 * 
	 * @return String
	 */
	public String getRemoteHost()
	{
		return remoteHost;
	}

	/**
	 * Sets the remoteHost.
	 * 
	 * @param remoteHost
	 *            The remoteHost to set
	 */
	public void setRemoteHost(String remoteHost)
	{
		this.remoteHost = remoteHost;
	}

}
