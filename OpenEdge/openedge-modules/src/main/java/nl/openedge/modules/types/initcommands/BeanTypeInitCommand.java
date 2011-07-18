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
package nl.openedge.modules.types.initcommands;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.config.ConfigException;
import ognl.Ognl;
import ognl.OgnlException;

import org.jdom.Element;

/**
 * Command that populates instances using Ognl.
 * 
 * @author Eelco Hillenius
 */
public final class BeanTypeInitCommand implements InitCommand
{
	/** bean properties for population. */
	private Map properties = null;

	/**
	 * Prefix voor waarden die in JNDI env. opgezocht moeten worden
	 */
	private static final String JNDI_PREFIX = "JNDI:";

	/**
	 * @see nl.openedge.components.types.decorators.InitCommand#init(java.lang.String,
	 *      org.jdom.Element, nl.openedge.components.ComponentRepository)
	 */
	@Override
	public void init(String componentName, Element componentNode,
			ComponentRepository componentRepository) throws ConfigException
	{
		this.properties = new HashMap();
		List pList = componentNode.getChildren("property");
		if (pList != null)
		{
			for (Iterator j = pList.iterator(); j.hasNext();)
			{
				Element pElement = (Element) j.next();
				String value = pElement.getAttributeValue("value");

				if (value != null && value.length() > (JNDI_PREFIX.length() + 1)
					&& JNDI_PREFIX.equals(value.substring(0, JNDI_PREFIX.length()).toUpperCase()))
				{
					try
					{
						Context env = (Context) new InitialContext().lookup("java:comp/env");
						if (env != null)
						{
							value = (String) env.lookup(value.substring(JNDI_PREFIX.length()));
						}
					}
					catch (NamingException e)
					{
					}
				}
				properties.put(pElement.getAttributeValue("name"), value);
			}
		}
	}

	/**
	 * populate the component instance.
	 * 
	 * @see nl.openedge.components.types.decorators.InitCommand#execute(java.lang.Object)
	 */
	@Override
	public void execute(Object componentInstance) throws InitCommandException, ConfigException
	{
		if (properties != null)
		{
			try
			{
				populate(componentInstance, this.properties);
			}
			catch (OgnlException e)
			{
				throw new ConfigException(e);
			}
		}
	}

	/**
	 * default populate of form: BeanUtils way; set error if anything goes wrong.
	 * 
	 * @param instance
	 *            the component instance
	 * @param propertiesToPopulate
	 *            properties for population
	 * @throws OgnlException
	 *             on population errors
	 */
	private void populate(Object instance, Map propertiesToPopulate) throws OgnlException
	{
		for (Iterator i = propertiesToPopulate.keySet().iterator(); i.hasNext();)
		{
			String key = (String) i.next();
			Object value = propertiesToPopulate.get(key);
			Ognl.setValue(key, instance, value);
		}
	}

}
