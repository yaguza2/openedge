/*
 * $Header$
 * $Revision$
 * $date$
 */
package nl.openedge.modules;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import nl.openedge.util.config.ConfigException;

/**
 * Factory for the module factory.
 * provides singleton access to moduleFactory
 * @author Eelco Hillenius
 */
public class ModuleFactoryFactory
{

	/** class of the default implementation */
	protected static String _implementingClass = ModuleFactoryImpl.class.getName();
	
	/** instance of module factory */
	protected static ModuleFactory moduleFactory = null;

	/** is the factory factory initialized yet? */
	private static boolean initialized = false;
	
	private static Log log = LogFactory.getLog(ModuleFactoryFactory.class);

	/**
	 * get the instance of the module factory
	 * @return ModuleFactory
	 */
	public static ModuleFactory getInstance()
	{
		
		if(!initialized)
		{
			throw new RuntimeException("factory is not yet initialized");
		}
		
		return moduleFactory;
	}

	/**
	 * construct with configuration node with ServletContext
	 * @param factoryNode configuration node
	 * @param servletContext use null if not in servlet environment
	 * @throws ModuleException
	 * @throws ConfigException
	 */
	protected synchronized static void initialize(
				Element factoryNode, ServletContext servletContext) 
				throws ModuleException, ConfigException
	{
		if(!initialized)
		{
			log.info("initializing module factory (" + _implementingClass + ")");
			initialized = true;
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			if (classLoader == null)
			{
				classLoader = ModuleFactoryFactory.class.getClassLoader();
			}
			try
			{
				Class clazz = classLoader.loadClass(_implementingClass);
				moduleFactory = (ModuleFactory)clazz.newInstance();
				moduleFactory.init(factoryNode, servletContext);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new ConfigException(e);
			}			
		}
		else
		{
			log.warn("module factory initialization allready done... skipping");
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
			throws ModuleException
	{
		if(initialized)
		{
			throw new RuntimeException("factory allready initialized");
		}
		_implementingClass = implementingClass;
	}

}
