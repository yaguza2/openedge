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

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.event.EventContext;
import javax.naming.event.NamespaceChangeListener;
import javax.naming.event.NamingEvent;
import javax.naming.event.NamingExceptionEvent;
import javax.naming.event.NamingListener;
import javax.naming.spi.ObjectFactory;
import javax.servlet.ServletContext;

import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.impl.DefaultComponentRepository;
import nl.openedge.util.jndi.NamingHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

/**
 * Factory that creates instances of the component repository.
 * Provides access to ComponentRepository. Optionally it 
 * can register the component repository with JNDI.
 *  
 * @author Eelco Hillenius
 */
public class RepositoryFactory implements  ObjectFactory
{

	/** class of the default implementation */
	protected static String _implementingClass = 
		DefaultComponentRepository.class.getName();
	
	/** instance of component repository */
	protected static ComponentRepository _componentRepository = null;

	private static boolean initialized = false;
	
	/* logger */
	private static Log log = LogFactory.getLog(RepositoryFactory.class);

	/**
	 * get the instance of the component repository
	 * @return ComponentRepository
	 */
	public static ComponentRepository getRepository()
	{	
		if(!initialized)
		{
			throw new RuntimeException("factory is not yet initialized");
		}
		
		return _componentRepository;
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
			log.warn("component repository initialization allready done... skipping");
		}
	}
	
	/**
	 * load/ instantiate the component repository
	 * @param factoryNode configuration node
	 * @param servletContext use null if not in servlet environment
	 * @throws ComponentLookupException
	 * @throws ConfigException
	 */
	protected static void load(
					Element factoryNode, ServletContext servletContext) 
					throws ComponentLookupException, ConfigException
	{
		log.info("initializing component repository (" + _implementingClass + ")");
		initialized = true;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null)
		{
			classLoader = RepositoryFactory.class.getClassLoader();
		}
		try
		{
			Class clazz = classLoader.loadClass(_implementingClass);
			_componentRepository = (ComponentRepository)clazz.newInstance();
			_componentRepository.start(factoryNode, servletContext);
			
			// if a name was defined, register with JNDI
			Element jndiNode = factoryNode.getChild("jndi");
			String name = null;
			if(jndiNode != null)
			{
				name = jndiNode.getAttributeValue("name");	
			}

			if(name != null)
			{
				try
				{
					Context ctx = NamingHelper.getInitialContext(new Properties());
					NamingHelper.bind(ctx, name, _componentRepository);
					log.info("bound " + _componentRepository + " to JNDI name: " + name 
						+ " to context " + ctx);
					((EventContext)ctx).addNamingListener(
						name, EventContext.OBJECT_SCOPE, listener);
				}
				catch (InvalidNameException ine)
				{
					log.info("invalid JNDI name: " + name + "... " + ine);
				}
				catch (NamingException ne)
				{
					log.info("could not bind factory to JNDI" + "... " + ne);
				}
				catch (ClassCastException cce)
				{
					log.info("initialContext did not implement EventContext");
				}	
			}
			
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
			throw new RuntimeException("factory allready initialized");
		}
		_implementingClass = implementingClass;
	}

	/**
	 * reset the initialized flag
	 */
	protected static void reset()
	{
		initialized = false;
	}
	
	/**
	 * @see ObjectFactory#getObjectInstance(Object, Name, Context, Hashtable) throws Exception
	 */
	public Object getObjectInstance(
		Object reference, 
		Name name, 
		Context ctx, 
		Hashtable env) 
		throws Exception
	{	
		log.info("ref: " + reference + ", name: " + name + ", ctx: " + ctx + ", env: " + env);
		
		log.debug("JNDI lookup: " + name);
		return _componentRepository;
	}

	private static final NamingListener listener = new NamespaceChangeListener()
	{

		public void objectAdded(NamingEvent evt)
		{
			log.info("a object was successfully bound to name: " + 
				evt.getNewBinding().getName());
		}
		public void objectRemoved(NamingEvent evt)
		{
			String name = evt.getOldBinding().getName();
			log.info("a object was unbound from name: " + name);
		}
		public void objectRenamed(NamingEvent evt)
		{
			String name = evt.getOldBinding().getName();
			log.info("a object was renamed from name: " + name);
		}
		public void namingExceptionThrown(NamingExceptionEvent evt)
		{
			log.info("naming exception occurred accessing object: " + 
				evt.getException());
		}
	};

}
