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
	/** the remote address. */
	private String remoteAddr;

	/** remote host. */
	private String remoteHost;

	/** hit count. */
	private int hitCount = 0;

	/** extra attributes. */
	private HashMap attributes = new HashMap();

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
	 * @param key attribute key
	 * @param value attribute value
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
	public HashMap getAttributes()
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
	public void setAttributes(HashMap attributes)
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
