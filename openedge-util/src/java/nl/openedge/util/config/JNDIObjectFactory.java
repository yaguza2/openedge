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
 * @author unknown, got it from Hibernate
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
			log.debug( "A factory was successfully bound to name: " + 
						evt.getNewBinding().getName() );
		}
		public void objectRemoved(NamingEvent evt) {
			String name = evt.getOldBinding().getName();
			log.info("A factory was unbound from name: " + name);
			Object instance = namedInstances.remove(name);
			Iterator iter = instances.values().iterator();
			while ( iter.hasNext() ) {
				if ( iter.next()==instance ) iter.remove();
			}
		}
		public void objectRenamed(NamingEvent evt) {
			String name = evt.getOldBinding().getName();
			log.info("A factory was renamed from name: " + name);
			namedInstances.put( evt.getNewBinding().getName(), 
								namedInstances.remove(name) );
		}
		public void namingExceptionThrown(NamingExceptionEvent evt) {
			log.warn( "Naming exception occurred accessing factory: " + 
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
	
		log.debug("registered: " + uid + " (" + ( (name==null) ? "unnamed" : name ) + ')');
		instances.put(uid, instance);
		if (name!=null) namedInstances.put(name, instance);
	
		//must add to JNDI _after_ adding to HashMaps, because some JNDI servers use serialization
		if (name==null) {
			log.info("no JDNI name configured");
		}
		else {
		
			log.info("Factory name: " + name);
		
			try {
				Context ctx = NamingHelper.getInitialContext(properties);
				NamingHelper.bind(ctx, name, instance);
				log.info("Bound factory to JNDI name: " + name);
				( (EventContext) ctx ).addNamingListener(name, EventContext.OBJECT_SCOPE, listener);
			}
			catch (InvalidNameException ine) {
				log.error("Invalid JNDI name: " + name, ine);
			}
			catch (NamingException ne) {
				log.warn("Could not bind factory to JNDI", ne);
			}
			catch(ClassCastException cce) {
				log.warn("InitialContext did not implement EventContext");
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
			log.info("Unbinding factory: " + name);
		
			try {
				Context ctx = NamingHelper.getInitialContext(properties);
				ctx.unbind(name);
				log.info("Unbound factory from JNDI name: " + name);
			}
			catch (InvalidNameException ine) {
				log.error("Invalid JNDI name: " + name, ine);
			}
			catch (NamingException ne) {
				log.warn("Could not unbind factory from JNDI", ne);
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
			log.warn("Not found: " + name);
			log.debug(namedInstances);
		}
		return result;
	}

	/** get an instance */
	public static Object getInstance(String uid) {
		log.debug("lookup: uid=" + uid);
		Object result = instances.get(uid);
		if (result==null) {
			log.warn("Not found: " + uid);
			log.debug(instances);
		}
		return result;
	}

}

