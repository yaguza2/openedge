package nl.openedge.modules;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

import javax.servlet.ServletContext;

import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.config.URLHelper;
import nl.openedge.modules.observers.ChainedEvent;
import nl.openedge.modules.observers.ChainedEventObserver;
import nl.openedge.modules.observers.ComponentObserver;
import nl.openedge.modules.observers.ComponentRepositoryObserver;
import nl.openedge.modules.observers.ComponentsLoadedEvent;
import nl.openedge.modules.observers.SchedulerObserver;
import nl.openedge.modules.observers.SchedulerStartedEvent;
import nl.openedge.modules.types.ComponentFactory;
import nl.openedge.modules.types.base.JobTypeFactory;
import ognl.Ognl;
import ognl.OgnlException;

import org.jdom.Element;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base for implementations of ComponentRepository.
 * 
 * @author Eelco Hillenius
 */
public abstract class AbstractComponentRepository implements ComponentRepository
{
	private static final long serialVersionUID = 1L;

	/** default number of seconds to wait until the scheduler starts up. */
	private static final int DEFAULT_WAIT_UNTIL_SCHEDULER_STARTUP = 20;

	/** logger. */
	private static Logger log = LoggerFactory.getLogger(AbstractComponentRepository.class);

	/** holder for component builders. */
	private Map<String, Object> components = Collections
		.synchronizedMap(new HashMap<String, Object>());

	/** holder for component component factories that implement job interface. */
	private Map<String, Object> jobs = Collections.synchronizedMap(new HashMap<String, Object>());

	/** holder for triggers. */
	private Map< ? , ? > triggers = null;

	/** quartz scheduler. */
	private Scheduler scheduler;

	/** observers for component factory events. */
	private List<ComponentRepositoryObserver> observers = Collections
		.synchronizedList(new ArrayList<ComponentRepositoryObserver>());

	/** servlet context if provided. */
	private ServletContext servletContext = null;

	/**
	 * construct.
	 */
	public AbstractComponentRepository()
	{
		// nothing here
	}

	/**
	 * add observer of component factory events.
	 * 
	 * @param observer
	 *            event observer
	 */
	@Override
	public void addObserver(ComponentRepositoryObserver observer)
	{
		if (observer != null)
		{
			synchronized (observers)
			{
				if (!observers.contains(observer))
				{
					this.observers.add(observer);
				}
			}
		}
	}

	/**
	 * remove observer of component factory events.
	 * 
	 * @param observer
	 *            event observer
	 */
	@Override
	public void removeObserver(ComponentRepositoryObserver observer)
	{
		if (observer != null)
			this.observers.remove(observer);
	}

	/**
	 * initialize the component factory.
	 * 
	 * @param rootNode
	 *            root node
	 * @param theServletContext
	 *            servlet context
	 * @throws ConfigException
	 *             when an configuration error occurs
	 */
	@Override
	public void start(Element rootNode, ServletContext theServletContext) throws ConfigException
	{
		this.servletContext = theServletContext;
		internalInit(rootNode, theServletContext);
	}

	// -------------------------------------- INIT METHODS -------------------------//

	/**
	 * Initialize.
	 * 
	 * @param rootNode
	 *            root node
	 * @param theServletContext
	 *            servlet context
	 * @throws ConfigException
	 *             when an configuration error occurs
	 */
	private void internalInit(Element rootNode, ServletContext theServletContext)
			throws ConfigException
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null)
		{
			classLoader = ComponentRepository.class.getClassLoader();
		}
		// get node for components
		Element componentsNode = rootNode.getChild("components");
		// load components into components map
		addComponents(componentsNode, classLoader);

		// test the components first
		try
		{
			testModules();
		}
		catch (ComponentLookupException e)
		{
			e.printStackTrace();
			throw new ConfigException(e);
		}

		// fire components loaded event
		fireModulesLoadedEvent();

		// get node for quartz scheduler
		Element schedulerNode = rootNode.getChild("scheduler");
		if (schedulerNode != null)
		{
			// load triggers into trigger map
			this.triggers = addTriggers(schedulerNode, classLoader);

			try
			{
				// initialise quartz
				initQuartz(schedulerNode, theServletContext);

				// notify observers
				fireSchedulerStartedEvent(scheduler);

				if (!jobs.isEmpty() && !triggers.isEmpty())
				{
					// finally, schedule the jobs/ triggers
					scheduleJobs(schedulerNode, classLoader);
				}
				else
				{
					log.warn("scheduler was started but " + "there are no jobs to schedule");
				}

			}
			catch (SchedulerException e)
			{
				e.printStackTrace();
				throw new ConfigException(e);
			}
		}
		else
		{
			log.info("scheduler node was not found; " + "scheduler will not be started");
		}
	}

	/**
	 * test all loaded components by getting them.
	 * 
	 * @throws ComponentLookupException
	 *             when the component could not be found
	 */
	protected void testModules()
	{
		String[] names = getComponentNames();
		int size = names.length;

		for (int i = 0; i < size; i++)
		{
			Object o = getComponent(names[i]);

			if (log.isDebugEnabled())
			{
				log.debug("name " + names[i] + " tested (" + o + ")");
			}
		}
	}

	/**
	 * initialise the quartz scheduler.
	 * 
	 * @param node
	 *            config node
	 * @param context
	 *            servlet context
	 * @throws ConfigException
	 *             when an configuration error occurs
	 * @throws SchedulerException
	 *             on quartz errors
	 */
	protected void initQuartz(Element node, ServletContext context) throws ConfigException,
			SchedulerException
	{
		Properties properties = null;
		if (node != null)
		{
			properties = new Properties();
			String proploc = node.getAttributeValue("properties");
			try
			{
				URL urlproploc =
					URLHelper.convertToURL(proploc, AbstractComponentRepository.class, context);
				log.info("will use " + urlproploc + " to initialise Quartz");
				properties.load(urlproploc.openStream());
			}
			catch (IOException ioe)
			{
				throw new ConfigException(ioe);
			}
		}
		SchedulerFactory schedFact = null;
		if (properties != null)
		{
			schedFact = new org.quartz.impl.StdSchedulerFactory(properties);
		}
		else
		{
			schedFact = new org.quartz.impl.StdSchedulerFactory();
		}
		scheduler = schedFact.getScheduler();
		log.info("initialised Quartz... starting up...");
		// start it
		scheduler.start();
		log.info("Quartz started");
	}

	/**
	 * get components from config.
	 * 
	 * @param componentsNode
	 *            config node of components
	 * @param classLoader
	 *            Class<?> loader
	 * @throws ConfigException
	 *             when an configuration error occurs
	 * @return components
	 */
	protected Map< ? , ? > addComponents(Element componentsNode, ClassLoader classLoader)
			throws ConfigException
	{
		// iterate components
		List< ? > componentNodes = componentsNode.getChildren("component");
		for (Iterator< ? > i = componentNodes.iterator(); i.hasNext();)
		{
			Element node = (Element) i.next();
			String name = node.getAttributeValue("name");
			if (components.get(name) != null)
			{
				throw new ConfigException("names of components have to be unique!" + name
					+ " is used more than once");
			}
			String className = node.getAttributeValue("class");
			Class< ? > clazz = null;
			try
			{
				clazz = classLoader.loadClass(className);
			}
			catch (ClassNotFoundException cnfe)
			{
				throw new ConfigException(cnfe);
			}

			addComponent(name, clazz, node);
		}
		log.info("loading components done");
		return components;
	}

	/**
	 * add one component.
	 * 
	 * @param name
	 *            component name
	 * @param clazz
	 *            component class
	 * @param node
	 *            component config node
	 * @throws ConfigException
	 *             when an configuration error occurs
	 */
	protected abstract void addComponent(String name, Class< ? > clazz, Element node)
			throws ConfigException;

	/**
	 * get the component factory.
	 * 
	 * @param name
	 *            component name
	 * @param clazz
	 *            component class
	 * @param node
	 *            configuration node
	 * @return ComponentFactory
	 * @throws ConfigException
	 *             when an configuration error occurs
	 */
	protected abstract ComponentFactory getComponentFactory(String name, Class< ? > clazz,
			Element node) throws ConfigException;

	/**
	 * add initialization commands.
	 * 
	 * @param factory
	 *            factory
	 * @param node
	 *            config node
	 * @param clazz
	 *            component class
	 * @throws ConfigException
	 *             when an configuration error occurs
	 */
	protected abstract void addInitCommands(ComponentFactory factory, Class< ? > clazz, Element node)
			throws ConfigException;

	/**
	 * get triggers from config.
	 * 
	 * @param schedulerNode
	 *            scheduler config node
	 * @param classLoader
	 *            Class<?> loader
	 * @return map of jobs
	 * @throws ConfigException
	 *             when an configuration error occurs
	 */
	protected Map< ? , ? > addTriggers(Element schedulerNode, ClassLoader classLoader)
			throws ConfigException
	{
		Map<Object, Object> workTriggers =
			Collections.synchronizedMap(new HashMap<Object, Object>());
		List< ? > trigs = schedulerNode.getChildren("trigger");
		for (Iterator< ? > i = trigs.iterator(); i.hasNext();)
		{
			Trigger trigger = null;
			Element triggerNode = (Element) i.next();
			String name = triggerNode.getAttributeValue("name");
			String group = triggerNode.getAttributeValue("group");
			String className = triggerNode.getAttributeValue("class");
			Class< ? > clazz = null;

			try
			{
				clazz = classLoader.loadClass(className);
				trigger = (Trigger) clazz.newInstance();
			}
			catch (ClassNotFoundException e)
			{
				log.error(e.getMessage(), e);
				throw new ConfigException(e);
			}
			catch (InstantiationException e)
			{
				log.error(e.getMessage(), e);
				throw new ConfigException(e);
			}
			catch (IllegalAccessException e)
			{
				log.error(e.getMessage(), e);
				throw new ConfigException(e);
			}

			trigger.setName(name);
			// this is the 'base' name, as another name will be
			// used for the actual sceduling;
			// the name will be a combination of:
			// 'name_jobname', to keep them unique
			trigger.setGroup(group);
			List< ? > parameters = triggerNode.getChildren("property");
			Map<String, String> paramMap = new HashMap<String, String>();
			if (parameters != null)
				for (Iterator< ? > j = parameters.iterator(); j.hasNext();)
				{
					Element pNode = (Element) j.next();
					paramMap.put(pNode.getAttributeValue("name"), pNode.getAttributeValue("value"));
				}
			try
			{
				// set parameters as properties of trigger
				populate(trigger, paramMap);
			}
			catch (OgnlException e)
			{
				log.error(e.getMessage(), e);
				throw new ConfigException(e);
			}
			workTriggers.put(name, trigger);
		}
		return workTriggers;
	}

	/**
	 * populate bean.
	 * 
	 * @param bean
	 *            the bean
	 * @param parameters
	 *            the parameters
	 * @throws OgnlException
	 *             on population errors
	 */
	private void populate(Object bean, Map<String, String> parameters) throws OgnlException
	{
		for (Iterator<String> i = parameters.keySet().iterator(); i.hasNext();)
		{
			String key = i.next();
			Object value = parameters.get(key);
			Ognl.setValue(key, bean, value);
		}
	}

	/**
	 * schedule jobs.
	 * 
	 * @param schedulerNode
	 *            config node of scheduler
	 * @param classLoader
	 *            Class<?> loader
	 * @throws ConfigException
	 *             when an configuration error occurs
	 */
	protected void scheduleJobs(Element schedulerNode, ClassLoader classLoader)
			throws ConfigException
	{
		// get job excecution map from config
		Element execNode = schedulerNode.getChild("jobExecutionMap");
		List< ? > execs = execNode.getChildren("job");
		for (Iterator< ? > i = execs.iterator(); i.hasNext();)
		{
			Element e = (Element) i.next();
			String triggerName = e.getAttributeValue("trigger");
			String componentName = e.getAttributeValue("component");
			// look up trigger and (job)component
			Trigger trigger = (Trigger) ((Trigger) triggers.get(triggerName)).clone();
			if (trigger == null)
			{
				throw new ConfigException(triggerName + " is not a registered trigger");
			}
			JobTypeFactory job = (JobTypeFactory) jobs.get(componentName);
			if (job == null)
			{
				throw new ConfigException(componentName + " is not a component");
			}
			if (!Job.class.isAssignableFrom(job.getComponentClass()))
			{
				throw new ConfigException("component " + componentName
					+ " is not a job (does not implement " + Job.class.getName() + ")");
			}
			JobDetail jobDetail =
				new JobDetail(job.getName(), job.getGroup(), job.getComponentClass());
			jobDetail.setJobDataMap(job.getJobData());
			try
			{ // start schedule job/trigger combination
				sceduleJob(trigger, jobDetail);
			}
			catch (SchedulerException ex)
			{
				ex.printStackTrace();
				throw new ConfigException(ex);
			}
		}
	}

	/**
	 * scedule a Quartz job.
	 * 
	 * @param trigger
	 *            the trigger
	 * @param jobDetail
	 *            the job
	 * @return actual name used to scedule the trigger will be of form:
	 *         ${triggerName}_${jobDetail.getName()}
	 * @throws SchedulerException
	 *             on Quartz exceptions
	 */
	protected String sceduleJob(Trigger trigger, JobDetail jobDetail) throws SchedulerException
	{
		String triggerName = trigger.getName();
		String sceduleName = triggerName + "_" + jobDetail.getName();
		trigger.setName(sceduleName);

		if (trigger.getStartTime() == null)
		{
			log.info("start time not set for trigger " + triggerName
				+ "... trying to set to immediate execution");

			try
			{
				// a bit of a hack as the implementors of abstract class
				// org.quartz.Trigger have method setStartTime(Date), whereas
				// the base Class<?> itself lacks this method
				// set startup to twenty seconds from now
				Class< ? extends Trigger> triggerClass = trigger.getClass();
				Calendar start = new GregorianCalendar();
				start.setLenient(true);
				start.add(Calendar.SECOND, DEFAULT_WAIT_UNTIL_SCHEDULER_STARTUP);
				Method setMethod = triggerClass.getMethod("setStartTime", new Class[] {Date.class});
				setMethod.invoke(trigger, new Object[] {start.getTime()});
			}
			catch (NoSuchMethodException e)
			{
				log.error("\trigger does not support setting the startTime"
					+ "(method setStartTime not found)");
			}
			catch (IllegalAccessException e)
			{
				log.error("unable to set start time: " + e.getMessage(), e);
			}
			catch (InvocationTargetException e)
			{
				log.error("unable to set start time: " + e.getMessage(), e);
			}
		}

		log.info("schedule " + jobDetail.getFullName() + " with trigger " + sceduleName);

		scheduler.scheduleJob(jobDetail, trigger);
		return sceduleName;
	}

	// --------------------------- NON-INIT METHODS -----------------------------//

	/**
	 * notify observers that scheduler was started.
	 * 
	 * @param theScheduler
	 *            the scheduler
	 */
	protected void fireSchedulerStartedEvent(Scheduler theScheduler)
	{
		SchedulerStartedEvent evt = new SchedulerStartedEvent(this, theScheduler);
		synchronized (observers)
		{
			for (Iterator<ComponentRepositoryObserver> i = observers.iterator(); i.hasNext();)
			{
				ComponentRepositoryObserver mo = i.next();
				if (mo instanceof SchedulerObserver)
				{
					((SchedulerObserver) mo).schedulerStarted(evt);
				}
			}
		}
	}

	/**
	 * notify observers that all components ware (re)loaded.
	 */
	protected void fireModulesLoadedEvent()
	{
		ComponentsLoadedEvent evt = new ComponentsLoadedEvent(this);
		synchronized (observers)
		{
			for (Iterator<ComponentRepositoryObserver> i = observers.iterator(); i.hasNext();)
			{
				ComponentRepositoryObserver mo = i.next();
				if (mo instanceof ComponentObserver)
				{
					((ComponentObserver) mo).modulesLoaded(evt);
				}
			}
		}
	}

	/**
	 * fired when (according to the implementing component) a critical event occured.
	 * 
	 * @param evt
	 *            the critical event
	 */
	@Override
	public void recieveChainedEvent(ChainedEvent evt)
	{
		fireCriticalEvent(evt);
	}

	/**
	 * notify observers that a critical event occured.
	 * 
	 * @param evt
	 *            the event
	 */
	protected void fireCriticalEvent(ChainedEvent evt)
	{
		synchronized (observers)
		{
			for (Iterator<ComponentRepositoryObserver> i = observers.iterator(); i.hasNext();)
			{
				ComponentRepositoryObserver mo = i.next();
				if (mo instanceof ChainedEventObserver)
				{
					((ChainedEventObserver) mo).recieveChainedEvent(evt);
				}
			}
		}
	}

	@Override
	public Object getComponent(String name)
	{
		log.info("looking for component " + name);
		ComponentFactory componentFactory = (ComponentFactory) components.get(name);
		if (componentFactory == null)
		{
			throw new ComponentLookupException("unable to find module with name: " + name);
		}
		return componentFactory.getComponent();
	}

	@Override
	public Scheduler getScheduler()
	{
		return scheduler;
	}

	@Override
	public ServletContext getServletContext()
	{
		return servletContext;
	}

	@Override
	public String[] getComponentNames()
	{
		return components.keySet().toArray(new String[components.size()]);
	}

	public Map<String, Object> getComponents()
	{
		return components;
	}

	public Map<String, Object> getJobs()
	{
		return jobs;
	}

	public List<ComponentRepositoryObserver> getObservers()
	{
		return observers;
	}

	public Map< ? , ? > getTriggers()
	{
		return triggers;
	}
}
