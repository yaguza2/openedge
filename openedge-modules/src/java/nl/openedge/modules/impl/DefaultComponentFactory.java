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
package nl.openedge.modules.impl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import nl.openedge.modules.ComponentLookupException;
import nl.openedge.modules.ComponentFactory;
import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.config.URLHelper;
import nl.openedge.modules.observers.ChainedEvent;
import nl.openedge.modules.observers.ChainedEventObserver;
import nl.openedge.modules.observers.ComponentFactoryObserver;
import nl.openedge.modules.observers.ComponentsLoadedEvent;
import nl.openedge.modules.observers.ComponentObserver;
import nl.openedge.modules.observers.SchedulerObserver;
import nl.openedge.modules.observers.SchedulerStartedEvent;
import nl.openedge.modules.types.BuilderFactory;
import nl.openedge.modules.types.ComponentBuilder;
import nl.openedge.modules.types.TypesRegistry;
import nl.openedge.modules.types.base.JobTypeBuilder;
import nl.openedge.modules.types.initcommands.InitCommand;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;

/**
 * Default implementation of ComponentFactory
 * 
 * @author Eelco Hillenius
 */
public class DefaultComponentFactory implements ComponentFactory
{

	/** logger */
	private Log log = LogFactory.getLog(this.getClass());

	/** holder for component builders */
	protected Map components = new HashMap();

	/** holder for component adapters that implement job interface */
	protected Map jobs = new HashMap();

	/** holder for triggers */
	protected Map triggers = null;

	/** quartz scheduler */
	protected Scheduler scheduler;

	/** observers for component factory events */
	protected List observers = new ArrayList();
	
	/**
	 * construct
	 */
	public DefaultComponentFactory() 
	{
		// nothing here
	}

	/**
	 * add observer of component factory events
	 * @param observer
	 */
	public void addObserver(ComponentFactoryObserver observer)
	{
		if (observer != null)
			this.observers.add(observer);
	}

	/**
	 * remove observer of component factory events
	 * @param observer
	 */
	public void removeObserver(ComponentFactoryObserver observer)
	{
		if (observer != null)
			this.observers.remove(observer);
	}
	
	/**
	 * initialize the component factory
	 * @param factoryNode
	 * @param servletContext
	 * @throws ConfigException
	 */
	public void start(
			Element factoryNode, 
			ServletContext servletContext) 
			throws ConfigException
	{
		
		internalInit(factoryNode, servletContext);
	}

	//-------------------------------------- INIT METHODS -------------------------//

	/* do 'real' initialisation */
	private void internalInit(
			Element factoryNode, 
			ServletContext servletContext) 
			throws ConfigException
	{

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null)
		{
			classLoader = ComponentFactory.class.getClassLoader();
		}
		// get node for components
		Element componentsNode = factoryNode.getChild("components");
		// load components into components map
		loadModules(componentsNode, classLoader);
		
		// test the components first
		try 
		{
			testModules();
		} 
		catch(ComponentLookupException e)
		{
			e.printStackTrace();
			throw new ConfigException(e);	
		}
		
		// fire components loaded event
		fireModulesLoadedEvent();

		//	get node for quartz scheduler
		Element schedulerNode = factoryNode.getChild("scheduler");
		if (schedulerNode != null)
		{

			// load triggers into trigger map
			this.triggers = getTriggers(schedulerNode, classLoader);

			try
			{ 
				// initialise quartz
				initQuartz(schedulerNode, servletContext);
				
				// notify observers
				fireSchedulerStartedEvent(scheduler);
				
				if (!jobs.isEmpty() && !triggers.isEmpty())
				{
					// finally, schedule the jobs/ triggers
					scheduleJobs(schedulerNode, classLoader);
				}
				else
				{
					log.warn("sceduler was started but " +
						"there are no jobs to scedule");
				}
				
			}
			catch (SchedulerException e)
			{
				throw new ConfigException(e);
			}
		}
		else
		{
			log.info("scheduler node was not found; " +
				"scheduler will not be started");
		}

	}
	
	/**
	 * test all loaded components by getting them
	 */
	protected void testModules() throws ComponentLookupException
	{
		
		String[] names = getModuleNames();
		int size = names.length;
		
		for(int i = 0; i < size; i++)
		{
			Object o = getModule(names[i]);
			
			if(log.isDebugEnabled())
			{
				log.debug("name " + names[i] + " tested (" + o + ")");	
			}
		}
	}

	/**
	 * initialise the quartz scheduler
	 * @throws Exception
	 */
	protected void initQuartz(Element node, ServletContext context) 
		throws ConfigException, SchedulerException
	{

		Properties properties = null;
		if (node != null)
		{
			properties = new Properties();
			String proploc = node.getAttributeValue("properties");
			try
			{
				URL urlproploc = URLHelper.convertToURL(
					proploc, DefaultComponentFactory.class, context);
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
		// and... register shutdownhook to stop it as well
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			public void run()
			{
				try
				{
					scheduler.shutdown(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		log.info("Quartz started");
	}

	/* get components from config 
	 * @return array of maps; first has all components, second has subset of
	 * components that are jobs
	 */
	private Map loadModules(			
			Element componentsNode, 
			ClassLoader classLoader) 
			throws ConfigException
	{

		// iterate components
		List componentNodes = componentsNode.getChildren("component");
		for (Iterator i = componentNodes.iterator(); i.hasNext();)
		{

			Element node = (Element)i.next();
			String name = node.getAttributeValue("name");
			if (components.get(name) != null)
			{
				throw new ConfigException(
						"names of components have to be unique!" 
						+ name + " is used more than once");
			}
			String className = node.getAttributeValue("class");
			Class clazz = null;
			try
			{
				clazz = classLoader.loadClass(className);
			}
			catch (ClassNotFoundException cnfe)
			{
				throw new ConfigException(cnfe);
			}
			
			addModule(name, clazz, node);

		}
		log.info("loading components done");
		return components;
	}
	
	/**
	 * add one component
	 * @param name
	 * @param clazz
	 * @param node
	 * @throws ConfigException
	 */
	public void addModule(
		String name, 
		Class clazz,
		Element node)
		throws ConfigException
	{
		ComponentBuilder adapter = getAdapter(name, clazz, node);
			
		// store component
		components.put(name, adapter);
			
		// special case: see if this is a job
		if(Job.class.isAssignableFrom(clazz))
		{
			jobs.put(name, adapter);
		}
			
		log.info("addedd " + clazz.getName() + " with name: " + name);
	}
	
	/**
	 * get the adapter
	 * @param name component name
	 * @param clazz component class
	 * @param node configuration node
	 * @return ComponentBuilder
	 * @throws ConfigException
	 */
	protected ComponentBuilder getAdapter(
		String name, 
		Class clazz,
		Element node)
		throws ConfigException
	{

		ComponentBuilder adapter = null;
		
		List baseTypes = TypesRegistry.getBaseTypes();
		if(baseTypes == null)
		{
			throw new ConfigException(
				"there are no base types registered!");
		}
			
		boolean wasFoundOnce = false;
		for(Iterator j = baseTypes.iterator(); j.hasNext(); )
		{
			Class baseType = (Class)j.next();
			if(baseType.isAssignableFrom(clazz))
			{
				if(wasFoundOnce) // more than one base type!
				{
					throw new ConfigException(
						"component " + name + 
						" is of more than one registered base type!");
				}
				wasFoundOnce = true;
					
				BuilderFactory adapterFactory =
					TypesRegistry.getAdapterFactory(baseType);
					
				adapter = adapterFactory.constructAdapter(name, node);
					
			}
		}
		if(adapter == null)
		{
			BuilderFactory adapterFactory = 
				TypesRegistry.getDefaultAdapterFactory();
			log.warn(name + " is not of any know type... using " +
				adapterFactory + " to construct an adapter");
					
			adapter = adapterFactory.constructAdapter(name, node);
		}
		adapter.setName(name);
		adapter.setModuleFactory(this);
			
		addInitCommands(adapter, clazz, node);
			
		adapter.setModuleClass(clazz);
		
		return adapter;		
	}
	
	/**
	 * add initialization commands
	 * @param adapter adapter
	 * @param node config node
	 * @param clazz component class
	 * @throws ConfigException
	 */
	protected void addInitCommands(
		ComponentBuilder adapter, 
		Class clazz,
		Element node)
		throws ConfigException
	{
		List initCommands = TypesRegistry.getInitCommandTypes();
		if(initCommands != null)
		{
			List commands = new ArrayList();
			for(Iterator j = initCommands.iterator(); j.hasNext(); )
			{
				Class type = (Class)j.next();
				if(type.isAssignableFrom(clazz))
				{
					// get command for this class
					InitCommand initCommand = 
						TypesRegistry.getInitCommand(type);
					// initialize the command
					initCommand.init(adapter.getName(), node, this);
					// add command to the list
					commands.add(initCommand);
				}
			}
				
			InitCommand[] cmds = (InitCommand[])
				commands.toArray(new InitCommand[commands.size()]);
				
			if(cmds.length > 0)
			{
				adapter.setInitCommands(cmds);	
			}
		}		
	}

	/*
	 * get triggers from config
	 * @return map of jobs
	 */
	protected Map getTriggers(Element schedulerNode, ClassLoader classLoader) 
		throws ConfigException
	{

		Map triggers = new HashMap();
		List trigs = schedulerNode.getChildren("trigger");
		for (Iterator i = trigs.iterator(); i.hasNext();)
		{

			Trigger trigger = null;
			Element triggerNode = (Element)i.next();
			String name = triggerNode.getAttributeValue("name");
			String group = triggerNode.getAttributeValue("group");
			String className = triggerNode.getAttributeValue("class");
			Class clazz = null;
			try
			{
				clazz = classLoader.loadClass(className);
				trigger = (Trigger)clazz.newInstance();
			}
			catch (Exception e)
			{
				throw new ConfigException(e);
			}
			trigger.setName(name); 
			// this is the 'base' name, as another name will be
			// used for the actual sceduling;
			// the name will be a combination of:
			// 'name_jobname', to keep them unique
			trigger.setGroup(group);
			List parameters = triggerNode.getChildren("parameter");
			Map paramMap = new HashMap();
			if (parameters != null)
				for (Iterator j = parameters.iterator(); j.hasNext();)
				{

					Element pNode = (Element)j.next();
					paramMap.put(pNode.getAttributeValue("name"), 
						pNode.getAttributeValue("value"));
				}
			try
			{ // set parameters as properties of trigger
				BeanUtils.populate(trigger, paramMap);
			}
			catch (Exception e)
			{
				throw new ConfigException(e);
			}
			triggers.put(name, trigger);
		}
		return triggers;
	}

	/*
	 * schedule jobs
	 */
	protected void scheduleJobs(Element schedulerNode, ClassLoader classLoader) 
		throws ConfigException
	{

		//get job excecution map from config
		Element execNode = schedulerNode.getChild("jobExecutionMap");
		List execs = execNode.getChildren("job");
		for (Iterator i = execs.iterator(); i.hasNext();)
		{

			Element e = (Element)i.next();
			String triggerName = e.getAttributeValue("trigger");
			String componentName = e.getAttributeValue("component");
			// look up trigger and (job)component
			Trigger trigger = (Trigger) ((Trigger)triggers.get(triggerName)).clone();
			if (trigger == null)
			{
				throw new ConfigException(triggerName + " is not a registered trigger");
			}
			JobTypeBuilder job = (JobTypeBuilder)jobs.get(componentName);
			if (job == null)
			{
				throw new ConfigException(componentName + " is not a component");
			}
			if (!Job.class.isAssignableFrom(job.getModuleClass()))
			{
				throw new ConfigException(
					"component " + componentName + " is not a job (does not implement " 
					+ Job.class.getName() + ")");
			}
			JobDetail jobDetail = new JobDetail(
				job.getName(), job.getGroup(), job.getModuleClass());
			jobDetail.setJobDataMap(job.getJobData());
			try
			{ //start schedule job/trigger combination
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
	 * scedule a Quartz job
	 * @param triggerName name of the trigger
	 * @param trigger the trigger
	 * @param jobDetail the job
	 * @return actual name used to scedule the trigger
	 * 	will be of form: ${triggerName}_${jobDetail.getName()}
	 * @throws SchedulerException
	 */
	public String sceduleJob(Trigger trigger, JobDetail jobDetail) 
		throws SchedulerException
	{
		String triggerName = trigger.getName();
		String sceduleName = triggerName + "_" + jobDetail.getName();
		trigger.setName(sceduleName);
		
		if (trigger.getStartTime() == null)
		{
			log.info("start time not set for trigger " +
				triggerName + "... trying to set to immediate execution");
			try
			{
				// a bit of a hack as the implementors of abstract class 
				// org.quartz.Trigger have method setStartTime(Date), whereas 
				// the base class itself lacks this method
				// set startup to twenty seconds from now
				Class clazz = trigger.getClass();
				Calendar start = new GregorianCalendar();
				start.setLenient(true);
				start.add(Calendar.SECOND, 20);
				Method setMethod = clazz.getMethod(
					"setStartTime", new Class[] { Date.class });
				setMethod.invoke(trigger, new Object[] { start.getTime()});
			}
			catch (Exception e)
			{ // too bad. Let's hope the trigger will fire anyway
				log.error("\tcould not set start time, cause: " + e.getMessage());
			}
		}
		
		log.info("schedule " + jobDetail.getFullName() + 
			" with trigger " + sceduleName);
		
		scheduler.scheduleJob(jobDetail, trigger);
		return sceduleName;
	}

	//--------------------------- NON-INIT METHODS -----------------------------//

	/**
	 * notify observers that scheduler was started
	 * @param scheduler
	 */
	protected void fireSchedulerStartedEvent(Scheduler scheduler)
	{

		SchedulerStartedEvent evt = new SchedulerStartedEvent(this, scheduler);
		for (Iterator i = observers.iterator(); i.hasNext();)
		{

			ComponentFactoryObserver mo = (ComponentFactoryObserver)i.next();
			if (mo instanceof SchedulerObserver)
			{
				((SchedulerObserver)mo).schedulerStarted(evt);
			}
		}
	}
	
	/**
	 * notify observers that all components ware (re)loaded
	 * @param scheduler
	 */
	protected void fireModulesLoadedEvent()
	{

		ComponentsLoadedEvent evt = new ComponentsLoadedEvent(this);
		for (Iterator i = observers.iterator(); i.hasNext();)
		{

			ComponentFactoryObserver mo = (ComponentFactoryObserver)i.next();
			if (mo instanceof ComponentObserver)
			{
				((ComponentObserver)mo).modulesLoaded(evt);
			}
		}
	}

	/**
	 * fired when (according to the implementing component) a critical event occured
	 * @param evt the critical event
	 */
	public void recieveChainedEvent(ChainedEvent evt)
	{
		fireCriticalEvent(evt);
	}

	/**
	 * notify observers that a critical event occured
	 * @param scheduler
	 */
	protected void fireCriticalEvent(ChainedEvent evt)
	{

		for (Iterator i = observers.iterator(); i.hasNext();)
		{

			ComponentFactoryObserver mo = (ComponentFactoryObserver)i.next();
			if (mo instanceof ChainedEventObserver)
			{
				((ChainedEventObserver)mo).recieveChainedEvent(evt);
			}
		}
	}

	/**
	 * @see nl.openedge.components.ComponentFactory#getModule(java.lang.String)
	 */
	public Object getModule(String name)
	{

		ComponentBuilder componentBuilder = (ComponentBuilder)components.get(name);
		if (componentBuilder == null)
		{
			throw new ComponentLookupException(
				"unable to find module with name: " + name);
		}
		return componentBuilder.getModule();
	}

	/**
	 * @see nl.openedge.components.ComponentFactory#getScheduler()
	 */
	public Scheduler getScheduler()
	{
		return scheduler;
	}
	
	/**
	 * @see nl.openedge.components.ComponentFactory#getModuleNames()
	 */
	public String[] getModuleNames()
	{
		return (String[])components.keySet().toArray(new String[components.size()]);
	}
	
	/**
	 * @see nl.openedge.components.ComponentFactory#getModulesByType(java.lang.Class, boolean)
	 */
	public List getModulesByType(Class type, boolean exact)
	{
		List sublist = new ArrayList();
		
		if(type == null)
		{
			return sublist;
		}
		
		if(exact)
		{
			for(Iterator i = components.values().iterator(); i.hasNext(); )
			{
		
				ComponentBuilder adapter = (ComponentBuilder)i.next();
				if(type.equals(adapter.getModuleClass()))
				{
					sublist.add(getModule(adapter.getName()));
				}	
			}			
		}
		else
		{
			for(Iterator i = components.values().iterator(); i.hasNext(); )
			{
		
				ComponentBuilder adapter = (ComponentBuilder)i.next();
				if(type.isAssignableFrom(adapter.getModuleClass()))
				{
					sublist.add(getModule(adapter.getName()));
				}
			}	
		}
		
		return sublist;
	}

}
