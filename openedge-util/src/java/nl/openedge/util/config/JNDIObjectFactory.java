/*
 * $Header$
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
package nl.openedge.util.config;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.event.EventContext;
import javax.naming.event.NamespaceChangeListener;
import javax.naming.event.NamingEvent;
import javax.naming.event.NamingExceptionEvent;
import javax.naming.event.NamingListener;
import javax.naming.spi.ObjectFactory;

import nl.openedge.util.jndi.NamingHelper;

import org.apache.commons.collections.FastHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Resolves JNDI lookups and deserialization
 * @author Eelco Hillenius and 'someone' from Hibernate
 */
public class JNDIObjectFactory implements ObjectFactory {

	/*	to stop the class from being unloaded */
	private static final JNDIObjectFactory INSTANCE;
	/* logger */
	private static final Log log;

	static {
		log = LogFactory.getLog(JNDIObjectFactory.class);
		INSTANCE = new JNDIObjectFactory();
		log.debug("initializing");
	}

	private static final FastHashMap instances = new FastHashMap();
	private static final FastHashMap namedInstances = new FastHashMap();

	private static final NamingListener listener = new NamespaceChangeListener() {
		
		public void objectAdded(NamingEvent evt) {
			log.debug( "a object was successfully bound to name: " + 
						evt.getNewBinding().getName() );
		}
		public void objectRemoved(NamingEvent evt) {
			String name = evt.getOldBinding().getName();
			log.info("a object was unbound from name: " + name);
			Object instance = namedInstances.remove(name);
			Iterator iter = instances.values().iterator();
			while ( iter.hasNext() ) {
				if ( iter.next()==instance ) iter.remove();
			}
		}
		public void objectRenamed(NamingEvent evt) {
			String name = evt.getOldBinding().getName();
			log.info("a object was renamed from name: " + name);
			namedInstances.put( evt.getNewBinding().getName(), 
								namedInstances.remove(name) );
		}
		public void namingExceptionThrown(NamingExceptionEvent evt) {
			log.warn( "naming exception occurred accessing object: " + 
						evt.getException() );
		}
	};

	/**
	 * @see ObjectFactory#getObjectInstance(Object, Name, Context, Hashtable) throws Exception
	 */
	public Object getObjectInstance(Object reference, 
									Name name, 
									Context ctx, 
									Hashtable env) 
									throws Exception {
										
		log.debug("JNDI lookup: " + name);
		String uid = (String)((Reference)reference).get(0).getContent();
		return getInstance(uid);
	}

	/**
	 * adds an instance
	 * @param uid
	 * @param name
	 * @param instance
	 * @param properties
	 */
	public static void addInstance(String uid, String name, 
				Object instance, Properties properties) {
	
		log.info("registered: " + uid + " (" + ( (name==null) ? "unnamed" : name ) + ')');
		instances.put(uid, instance);
		if (name!=null) namedInstances.put(name, instance);
	
		//must add to JNDI _after_ adding to HashMaps, because some JNDI servers use serialization
		if (name==null) {
			log.info("no JDNI name configured");
		}
		else {
		
			log.info("registered name: " + name);
		
			try {
				Context ctx = NamingHelper.getInitialContext(properties);
				NamingHelper.bind(ctx, name, instance);
				log.info("bound " + instance + " to JNDI name: " + name);
				( (EventContext) ctx ).addNamingListener(name, EventContext.OBJECT_SCOPE, listener);
			}
			catch (InvalidNameException ine) {
				log.error("invalid JNDI name: " + name, ine);
			}
			catch (NamingException ne) {
				log.warn("could not bind factory to JNDI", ne);
			}
			catch(ClassCastException cce) {
				log.warn("initialContext did not implement EventContext");
			}
		}
	}

	/**
	 * removes an instance
	 * @param uid
	 * @param name
	 * @param properties
	 */
	public static void removeInstance(String uid, String name, Properties properties) {
		//TODO: theoretically non-threadsafe...

		if (name!=null) {
			log.info("unbinding object: " + name);
		
			try {
				Context ctx = NamingHelper.getInitialContext(properties);
				ctx.unbind(name);
				log.info("unbound object from JNDI name: " + name);
			}
			catch (InvalidNameException ine) {
				log.error("invalid JNDI name: " + name, ine);
			}
			catch (NamingException ne) {
				log.warn("could not unbind object from JNDI", ne);
			}
		
			namedInstances.remove(name);
		}

		instances.remove(uid);
	}

	/**
	 * get a named instance
	 * @param name
	 * @return Object
	 */
	public static Object getNamedInstance(String name) {
		log.debug("lookup: name=" + name);
		Object result = namedInstances.get(name);
		if (result==null) {
			log.warn("not found: " + name);
			log.debug(namedInstances);
		}
		return result;
	}

	/** get an instance */
	public static Object getInstance(String uid) {
		log.debug("lookup: uid=" + uid);
		Object result = instances.get(uid);
		if (result==null) {
			log.warn("not found: " + uid);
			log.debug(instances);
		}
		return result;
	}

}

