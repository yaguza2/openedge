package nl.openedge.util.jndi;

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
 * @author Eelco Hillenius, based on NamingHelper from Hibernate
 */
public class NamingHelper {
	
	/** JNDI initial context class, <tt>Context.INITIAL_CONTEXT_FACTORY</tt>*/
	protected static String JNDI_CLASS ="jndi.class";
	/** JNDI provider URL, <tt>Context.PROVIDER_URL</tt> */
	protected static String JNDI_URL ="jndi.url";
	/** prefix for arbitrary JNDI <tt>InitialContext</tt> properties */
	protected static String JNDI_PREFIX = "jndi";
	/* log */
	private static final Log log = LogFactory.getLog(NamingHelper.class);
	protected static final String EMPTY_STRING="";
	
	/**
	 * get initial context based on properties and prefix
	 * @param props properties with jndi config
	 * @return InitialContext jndi context
	 * @throws NamingException
	 */
	public static InitialContext getInitialContext(
						Properties props) 
						throws NamingException {
		
		Hashtable hash = getJndiProperties(props);
		log.info("JNDI InitialContext properties:" + hash);
		try {
			return ( hash.size()==0 ) ?
			new InitialContext() :
			new InitialContext(hash);
		}
		catch (NamingException e) {
			log.error("could not obtain initial context", e);
			throw e;
		}
	}
	
	/**
	 * Bind val to name in ctx, and make sure that all intermediate contexts exist.
	 *
	 * @param ctx the root context
	 * @param name the name as a string
	 * @param val the object to be bound
	 * @throws NamingException
	 */
	public static void bind(Context ctx, String name, Object val) 
						throws NamingException {
		
		try {
			log.trace("binding: " + name);
			ctx.rebind(name, val);
		}
		catch (Exception e) {
			Name n = ctx.getNameParser(EMPTY_STRING).parse(name);
			while ( n.size() > 1 ) {
				String ctxName = n.get(0);
				
				Context subctx=null;
				try {
					log.trace("lookup: " + ctxName);
					subctx = (Context) ctx.lookup(ctxName);
				}
				catch (NameNotFoundException nfe) {}
				
				if (subctx!=null) {
					log.debug("found subcontext: " + ctxName);
					ctx = subctx;
				}
				else {
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
	 * Transform JNDI properties passed in the form 
	 * <tt>${prefix}.jndi.*</tt> to the format accepted 
	 * by <tt>InitialContext</tt> by triming the leading "<tt>hibernate.jndi</tt>".
	 */
	public static Properties getJndiProperties(Properties properties) {
		
		HashSet specialProps = new HashSet();
		specialProps.add(JNDI_CLASS);
		specialProps.add(JNDI_URL);
		
		Iterator iter = properties.keySet().iterator();
		Properties result = new Properties();
		while ( iter.hasNext() ) 
		{
			String prop = (String) iter.next();
			if ( prop.indexOf(JNDI_PREFIX) > -1 && 
				 !specialProps.contains(prop) ) {
				 	
				result.setProperty(
					prop.substring(JNDI_PREFIX.length()+1),
					properties.getProperty(prop)
				);
			}
		}
		
		String jndiClass = properties.getProperty(JNDI_CLASS);
		String jndiURL = properties.getProperty(JNDI_URL);
		// we want to be able to just use the defaults,
		// if JNDI environment properties are not supplied
		// so don't put null in anywhere
		if (jndiClass != null) result.put(
				Context.INITIAL_CONTEXT_FACTORY, jndiClass);
		if (jndiURL != null) result.put(Context.PROVIDER_URL, jndiURL);
		
		return result;
	}
	
}