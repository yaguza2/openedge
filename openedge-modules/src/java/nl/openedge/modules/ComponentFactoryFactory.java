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
package nl.openedge.modules;

import javax.servlet.ServletContext;

import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.impl.DefaultComponentFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

/**
 * Factory for the module factory.
 * provides singleton access to moduleFactory and stores the available
 * module types
 * @author Eelco Hillenius
 */
public class ComponentFactoryFactory
{

	/** class of the default implementation */
	protected static String _implementingClass = DefaultComponentFactory.class.getName();
	
	/** instance of module factory */
	protected static ComponentFactory moduleFactory = null;

	/** is the factory factory wasAdded yet? */
	private static boolean initialized = false;
	
	/* logger */
	private static Log log = LogFactory.getLog(ComponentFactoryFactory.class);

	/**
	 * get the instance of the module factory
	 * @return ComponentFactory
	 */
	public static ComponentFactory getInstance()
	{
		
		if(!initialized)
		{
			throw new RuntimeException("factory is not yet wasAdded");
		}
		
		return moduleFactory;
	}

	/**
	 * construct with configuration node and ServletContext
	 * @param factoryNode configuration node
	 * @param servletContext use null if not in servlet environment
	 * @throws ComponentLookupException
	 * @throws ConfigException
	 */
	protected synchronized static void initialize(
				Element factoryNode, ServletContext servletContext) 
				throws ComponentLookupException, ConfigException
	{
		if(!initialized)
		{
			load(factoryNode, servletContext);		
		}
		else
		{
			log.warn("module factory initialization allready done... skipping");
		}
	}
	
	/**
	 * load/ instantiate the module factory
	 * @param factoryNode configuration node
	 * @param servletContext use null if not in servlet environment
	 * @throws ComponentLookupException
	 * @throws ConfigException
	 */
	protected static void load(
					Element factoryNode, ServletContext servletContext) 
					throws ComponentLookupException, ConfigException
	{
		log.info("initializing module factory (" + _implementingClass + ")");
		initialized = true;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null)
		{
			classLoader = ComponentFactoryFactory.class.getClassLoader();
		}
		try
		{
			Class clazz = classLoader.loadClass(_implementingClass);
			moduleFactory = (ComponentFactory)clazz.newInstance();
			moduleFactory.start(factoryNode, servletContext);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new ConfigException(e);
		}			
	}
	
	/**
	 * @return String
	 */
	public static String getImplementingClass()
	{
		return _implementingClass;
	}

	/**
	 * @param implementingClass
	 */
	public static void setImplementingClass(String implementingClass)
			throws ComponentLookupException
	{
		if(initialized)
		{
			throw new RuntimeException("factory allready wasAdded");
		}
		_implementingClass = implementingClass;
	}

	/**
	 * reset the wasAdded flag
	 */
	protected static void reset()
	{
		initialized = false;
	}

}
