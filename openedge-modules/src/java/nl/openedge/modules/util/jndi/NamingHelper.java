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

package nl.openedge.modules.util.jndi;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Helper for JNDI namings
 * 
 * @author Eelco Hillenius, based on NamingHelper from Hibernate
 */
public final class NamingHelper
{

	/** JNDI initial context class, <tt>Context.INITIAL_CONTEXT_FACTORY</tt> */
	protected static String JNDI_CLASS = "jndi.class";

	/** JNDI provider URL, <tt>Context.PROVIDER_URL</tt> */
	protected static String JNDI_URL = "jndi.url";

	/** prefix for arbitrary JNDI <tt>InitialContext</tt> properties */
	protected static String JNDI_PREFIX = "jndi";

	/* log */
	private static final Log log = LogFactory.getLog(NamingHelper.class);

	protected static final String EMPTY_STRING = "";

	/**
	 * get initial context based on properties and prefix
	 * 
	 * @param props
	 *            properties with jndi config
	 * @return InitialContext jndi context
	 * @throws NamingException
	 */
	public static InitialContext getInitialContext(Properties props) throws NamingException
	{

		Hashtable hash = getJndiProperties(props);
		log.info("JNDI InitialContext properties:" + hash);
		try
		{
			return (hash.size() == 0) ? new InitialContext() : new InitialContext(hash);
		}
		catch (NamingException e)
		{
			log.error("could not obtain initial context", e);
			throw e;
		}
	}

	/**
	 * Bind val to name in ctx, and make sure that all intermediate contexts exist.
	 * 
	 * @param ctx
	 *            the root context
	 * @param name
	 *            the name as a string
	 * @param val
	 *            the object to be bound
	 * @throws NamingException
	 */
	public static void bind(Context ctx, String name, Object val) throws NamingException
	{

		try
		{
			log.trace("binding: " + name);
			ctx.rebind(name, val);
		}
		catch (Exception e)
		{
			Name n = ctx.getNameParser(EMPTY_STRING).parse(name);
			while (n.size() > 1)
			{
				String ctxName = n.get(0);

				Context subctx = null;
				try
				{
					log.trace("lookup: " + ctxName);
					subctx = (Context) ctx.lookup(ctxName);
				}
				catch (NameNotFoundException nfe)
				{
				}

				if (subctx != null)
				{
					log.debug("found subcontext: " + ctxName);
					ctx = subctx;
				}
				else
				{
					log.info("creating subcontext: " + ctxName);
					ctx = ctx.createSubcontext(ctxName);
				}
				n = n.getSuffix(1);
			}
			log.trace("binding: " + n);
			ctx.rebind(n, val);
		}
		log.debug("bound name: " + name);
	}

	/**
	 * Transform JNDI properties passed in the form <tt>${prefix}.jndi.*</tt> to the format
	 * accepted by <tt>InitialContext</tt> by triming the leading "<tt>hibernate.jndi</tt>".
	 */
	public static Properties getJndiProperties(Properties properties)
	{

		HashSet specialProps = new HashSet();
		specialProps.add(JNDI_CLASS);
		specialProps.add(JNDI_URL);

		Iterator iter = properties.keySet().iterator();
		Properties result = new Properties();
		while (iter.hasNext())
		{
			String prop = (String) iter.next();
			if (prop.indexOf(JNDI_PREFIX) > -1 && !specialProps.contains(prop))
			{

				result.setProperty(prop.substring(JNDI_PREFIX.length() + 1), properties
						.getProperty(prop));
			}
		}

		String jndiClass = properties.getProperty(JNDI_CLASS);
		String jndiURL = properties.getProperty(JNDI_URL);
		// we want to be able to just use the defaults,
		// if JNDI environment properties are not supplied
		// so don't put null in anywhere
		if (jndiClass != null)
			result.put(Context.INITIAL_CONTEXT_FACTORY, jndiClass);
		if (jndiURL != null)
			result.put(Context.PROVIDER_URL, jndiURL);

		return result;
	}

}