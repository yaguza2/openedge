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

import ognl.Ognl;
import ognl.OgnlException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.config.ConfigException;

/**
 * Command that populates instances using Ognl.
 * 
 * @author Eelco Hillenius
 */
public final class BeanTypeInitCommand implements InitCommand
{
	
	private Map properties = null;
	
	private static Log log = LogFactory.getLog(BeanTypeInitCommand.class);

	/**
	 * initialize
	 * @see nl.openedge.components.types.decorators.InitCommand#init(java.lang.String, org.jdom.Element, nl.openedge.components.ComponentRepository)
	 */
	public void init(
		String componentName, 
		Element componentNode,
		ComponentRepository componentRepository)
		throws ConfigException
	{
		this.properties = new HashMap();
		List pList = componentNode.getChildren("property");
		if (pList != null)
		{
			for (Iterator j = pList.iterator(); j.hasNext();)
			{
				Element pElement = (Element)j.next();
				properties.put(
					pElement.getAttributeValue("name"), 
					pElement.getAttributeValue("value"));
			}
				
		}
	}

	/**
	 * populate the component instance
	 * @see nl.openedge.components.types.decorators.InitCommand#execute(java.lang.Object)
	 */
	public void execute(Object componentInstance) 
		throws InitCommandException, ConfigException
	{
		if(properties != null)
		{
			try
			{
				populate(componentInstance, this.properties);
			}
			catch (Exception e)
			{
				throw new ConfigException(e);
			}
		}
	}
	
	/**
	 * default populate of form: BeanUtils way; set error if anything goes wrong
	 * @param componentInstance
	 * @param properties
	 * @return true if populate did not have any troubles, false otherwise
	 */
	protected void populate(Object componentInstance, Map properties) 
		throws OgnlException
	{
		for(Iterator i = properties.keySet().iterator(); i.hasNext(); )
		{
			String key = (String)i.next();
			Object value = properties.get(key);
			Ognl.setValue(key, componentInstance, value);
		}
	}

}
