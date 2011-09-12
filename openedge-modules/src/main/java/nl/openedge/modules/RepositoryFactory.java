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
import nl.openedge.modules.util.jndi.NamingHelper;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory that creates instances of the component repository. Provides access to
 * ComponentRepository. Optionally it can register the component repository with JNDI.
 * 
 * @author Eelco Hillenius
 */
public final class RepositoryFactory implements ObjectFactory
{
	/** Listener instance. */
	private static final NamingListener NAMING_LISTENER = new NamespaceChangeListener()
	{
		/**
		 * @see javax.naming.event.NamespaceChangeListener#objectAdded(javax.naming.event.NamingEvent)
		 */
		@Override
		public void objectAdded(NamingEvent evt)
		{
			log.info("a object was successfully bound to name: " + evt.getNewBinding().getName());
		}

		/**
		 * @see javax.naming.event.NamespaceChangeListener#objectRemoved(javax.naming.event.NamingEvent)
		 */
		@Override
		public void objectRemoved(NamingEvent evt)
		{
			String name = evt.getOldBinding().getName();
			log.info("a object was unbound from name: " + name);
		}

		/**
		 * @see javax.naming.event.NamespaceChangeListener#objectRenamed(javax.naming.event.NamingEvent)
		 */
		@Override
		public void objectRenamed(NamingEvent evt)
		{
			String name = evt.getOldBinding().getName();
			log.info("a object was renamed from name: " + name);
		}

		/**
		 * @see javax.naming.event.NamingListener#namingExceptionThrown(javax.naming.event.NamingExceptionEvent)
		 */
		@Override
		public void namingExceptionThrown(NamingExceptionEvent evt)
		{
			log.info("naming exception occurred accessing object: " + evt.getException());
		}
	};

	/** class of the default implementation. */
	private static String implementingClass = DefaultComponentRepository.class.getName();

	/** instance of component repository. */
	private static ComponentRepository componentRepository = null;

	/** whether the repository factory was initiliazed. */
	private static boolean initialized = false;

	/** logger. */
	private static Logger log = LoggerFactory.getLogger(RepositoryFactory.class);

	/**
	 * get the instance of the component repository.
	 * 
	 * @return ComponentRepository
	 */
	public static ComponentRepository getRepository()
	{
		if (!initialized)
		{
			throw new RuntimeException("factory is not yet initialized");
		}

		return componentRepository;
	}

	/**
	 * construct with configuration node and ServletContext.
	 * 
	 * @param factoryNode
	 *            configuration node
	 * @param servletContext
	 *            use null if not in servlet environment
	 * @throws ComponentLookupException
	 * @throws ConfigException
	 *             when an configuration error occurs
	 */
	protected static synchronized void initialize(Element factoryNode, ServletContext servletContext)
			throws ConfigException
	{
		if (!initialized)
		{
			load(factoryNode, servletContext);
		}
		else
		{
			log.warn("component repository initialization allready done... skipping");
		}
	}

	/**
	 * load/ instantiate the component repository.
	 * 
	 * @param factoryNode
	 *            configuration node
	 * @param servletContext
	 *            use null if not in servlet environment
	 * @throws ComponentLookupException
	 * @throws ConfigException
	 *             when an configuration error occurs
	 */
	protected static void load(Element factoryNode, ServletContext servletContext)
			throws ConfigException
	{
		log.info("initializing component repository (" + implementingClass + ")");
		initialized = true;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null)
		{
			classLoader = RepositoryFactory.class.getClassLoader();
		}

		try
		{
			Class< ? > clazz = classLoader.loadClass(implementingClass);
			componentRepository = (ComponentRepository) clazz.newInstance();
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
			throw new ConfigException(e);
		}
		componentRepository.start(factoryNode, servletContext);

		// if a name was defined, register with JNDI
		Element jndiNode = factoryNode.getChild("jndi");
		String name = null;
		if (jndiNode != null)
		{
			name = jndiNode.getAttributeValue("name");
		}

		if (name != null)
		{
			try
			{
				Context ctx = NamingHelper.getInitialContext(new Properties());
				NamingHelper.bind(ctx, name, componentRepository);
				log.info("bound " + componentRepository + " to JNDI name: " + name + " to context "
					+ ctx);
				((EventContext) ctx).addNamingListener(name, EventContext.OBJECT_SCOPE,
					NAMING_LISTENER);
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

	/**
	 * @return String
	 */
	public static String getImplementingClass()
	{
		return implementingClass;
	}

	/**
	 * set the implementing class.
	 * 
	 * @param theImplementingClass
	 *            the implementing class
	 */
	public static void setImplementingClass(String theImplementingClass)
	{
		if (initialized)
		{
			throw new RuntimeException("factory allready initialized");
		}
		implementingClass = theImplementingClass;
	}

	/**
	 * reset the initialized flag.
	 */
	protected static void reset()
	{
		initialized = false;
	}

	/**
	 * @see ObjectFactory#getObjectInstance(Object, Name, Context, Hashtable) throws
	 *      Exception
	 */
	@Override
	public Object getObjectInstance(Object reference, Name name, Context ctx, Hashtable< ? , ? > env)
	{
		log.info("ref: " + reference + ", name: " + name + ", ctx: " + ctx + ", env: " + env);

		log.debug("JNDI lookup: " + name);
		return componentRepository;
	}

}
