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

import nl.openedge.modules.ModuleLookpupException;
import nl.openedge.modules.ModuleFactory;
import nl.openedge.modules.config.*;
import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.config.URLHelper;
import nl.openedge.modules.observers.*;
import nl.openedge.modules.types.*;
import nl.openedge.modules.types.base.*;
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
 * The ModuleFactory constructs and initialises objects.
 * 
 * @author Eelco Hillenius
 */
public class DefaultModuleFactory implements ModuleFactory
{

	/**
	 * Default location of the xml configuration file.
	 */
	public static final String DEFAULT_CONFIG_FILE = "/WEB-INF/oemodules.xml";

	/**
	 * If a value is set in the application attribute context with this key,
	 * the value is used to override the setting of the configFile.
	 */
	public static final String KEY_CONFIG_FILE = "oemodules.configFile";

	/**
	 * Name of the servlet init parameter which defines the path to the
	 * OpenEdge Modules configuration file.  Defaults to DEFAULT_CONFIG_FILE.
	 */
	protected static final String INITPARAM_CONFIG_FILE = "oemodules.configFile";

	/** logger */
	private Log log = LogFactory.getLog(this.getClass());

	/** holder for module adapters */
	protected Map modules = null;

	/** holder for module adapters that implement job interface */
	protected Map jobs = null;
	/** counter for jobs with the same name */

	/** holder for triggers */
	protected Map triggers = null;

	/** quartz scheduler */
	protected Scheduler scheduler;

	/** observers for module factory events */
	protected List observers = new ArrayList();
	
	/**
	 * construct
	 */
	public DefaultModuleFactory() 
	{
		// nothing here
	}

	/**
	 * add observer of module factory events
	 * @param observer
	 */
	public void addObserver(ModuleFactoryObserver observer)
	{
		if (observer != null)
			this.observers.add(observer);
	}

	/**
	 * remove observer of module factory events
	 * @param observer
	 */
	public void removeObserver(ModuleFactoryObserver observer)
	{
		if (observer != null)
			this.observers.remove(observer);
	}
	
	/**
	 * initialize the module factory
	 * @param factoryNode
	 * @param servletContext
	 * @throws ConfigException
	 */
	public void init(
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
			classLoader = ModuleFactory.class.getClassLoader();
		}
		// get node for modules
		Element modulesNode = factoryNode.getChild("modules");
		// load modules into modules map
		Map[] mods = getModules(modulesNode, classLoader);
		this.modules = mods[0];
		this.jobs = mods[1];

		//	get node for quartz scheduler
		Element schedulerNode = factoryNode.getChild("scheduler");
		if (schedulerNode != null)
		{

			// load triggers into trigger map
			this.triggers = getTriggers(schedulerNode, classLoader);
			if (!jobs.isEmpty() && !triggers.isEmpty())
			{
				log.info("schedule jobs & triggers");
				try
				{ // initialise quartz
					initQuartz(schedulerNode, servletContext);
					// notify observers
					fireSchedulerStartedEvent(scheduler);
					// finally, schedule the jobs/ triggers
					scheduleJobs(schedulerNode, classLoader);
				}
				catch (SchedulerException e)
				{
					throw new ConfigException(e);
				}
			}
			else
			{
				log.warn(
					"allthough a scheduler node was found in the " + 
					"configuration, there is nothing to schedule!");
			}
		}
		else
		{
			log.info("scheduler node was not found; scheduler will not be started");
		}

	}

	/**
	 * initialise the quartz scheduler
	 * @throws Exception
	 */
	private void initQuartz(Element node, ServletContext context) throws ConfigException, SchedulerException
	{

		Properties properties = null;
		if (node != null)
		{
			properties = new Properties();
			String proploc = node.getAttributeValue("properties");
			try
			{
				URL urlproploc = URLHelper.convertToURL(proploc, DefaultModuleFactory.class, context);
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

	/* get modules from config 
	 * @return array of maps; first has all modules, second has subset of
	 * modules that are jobs
	 */
	private Map[] getModules(			
			Element modulesNode, 
			ClassLoader classLoader) 
			throws ConfigException
	{

		Map modules = new HashMap();
		Map jobs = new HashMap();
		// iterate modules
		List moduleNodes = modulesNode.getChildren("module");
		for (Iterator i = moduleNodes.iterator(); i.hasNext();)
		{

			Element node = (Element)i.next();
			String name = node.getAttributeValue("name");
			if (modules.get(name) != null)
			{
				throw new ConfigException(
						"names of modules have to be unique!" 
						+ name + " is used more than once");
			}
			String className = node.getAttributeValue("class");
			ModuleAdapter adapter = null;
			Class clazz = null;
			try
			{
				clazz = classLoader.loadClass(className);
			}
			catch (ClassNotFoundException cnfe)
			{
				throw new ConfigException(cnfe);
			}
			
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
					
					AdapterFactory adapterFactory =
						TypesRegistry.getAdapterFactory(baseType);
					
					adapter = adapterFactory.constructAdapter(name, node);
				}
			}
			if(adapter == null)
			{
				throw new ConfigException(
					"no adapter could be created for " + name + 
					", class " + clazz);
			}
			adapter.setName(name);
			adapter.setModuleFactory(this);
			
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
						initCommand.init(name, node, this);
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
			
			adapter.setModuleClass(clazz);
			// store component
			modules.put(name, adapter);
			log.info("stored " + className + " with name: " + name);

		}
		log.info("loading modules done");
		return new Map[] { modules, jobs };
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
			//trigger.setName(name); postpone setting the name;
			// the name will be a combination of:
			// 'name_jobname', to keep them unique
			trigger.setGroup(group);
			List parameters = triggerNode.getChildren("parameter");
			Map paramMap = new HashMap();
			if (parameters != null)
				for (Iterator j = parameters.iterator(); j.hasNext();)
				{

					Element pNode = (Element)j.next();
					paramMap.put(pNode.getAttributeValue("name"), pNode.getAttributeValue("value"));
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
	protected void scheduleJobs(Element schedulerNode, ClassLoader classLoader) throws ConfigException
	{

		//get job excecution map from config
		Element execNode = schedulerNode.getChild("jobExecutionMap");
		List execs = execNode.getChildren("job");
		for (Iterator i = execs.iterator(); i.hasNext();)
		{

			Element e = (Element)i.next();
			String triggerName = e.getAttributeValue("trigger");
			String moduleName = e.getAttributeValue("module");
			// look up trigger and (job)module
			Trigger trigger = (Trigger) ((Trigger)triggers.get(triggerName)).clone();
			if (trigger == null)
			{
				throw new ConfigException(triggerName + " is not a registered trigger");
			}
			JobTypeAdapter job = (JobTypeAdapter)jobs.get(moduleName);
			if (job == null)
			{
				throw new ConfigException(moduleName + " is not a module");
			}
			if (!Job.class.isAssignableFrom(job.getModuleClass()))
			{
				throw new ConfigException(
					"module " + moduleName + " is not a job (does not implement " 
					+ Job.class.getName() + ")");
			}
			JobDetail jobDetail = new JobDetail(
				job.getName(), job.getGroup(), job.getModuleClass());
			jobDetail.setJobDataMap(job.getJobData());
			try
			{ //start schedule job/trigger combination
				sceduleJob(triggerName, trigger, jobDetail);
			}
			catch (SchedulerException ex)
			{
				ex.printStackTrace();
				throw new ConfigException(ex);
			}
		}
	}

	public String sceduleJob(String triggerName, Trigger trigger, JobDetail jobDetail) 
		throws SchedulerException
	{
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

			ModuleFactoryObserver mo = (ModuleFactoryObserver)i.next();
			if (mo instanceof SchedulerObserver)
			{
				((SchedulerObserver)mo).schedulerStarted(evt);
			}
		}
	}

	/**
	 * fired when (according to the implementing module) a critical event occured
	 * @param evt the critical event
	 */
	public void criticalEventOccured(CriticalEvent evt)
	{
		fireCriticalEvent(evt);
	}

	/**
	 * notify observers that a critical event occured
	 * @param scheduler
	 */
	protected void fireCriticalEvent(CriticalEvent evt)
	{

		for (Iterator i = observers.iterator(); i.hasNext();)
		{

			ModuleFactoryObserver mo = (CriticalEventObserver)i.next();
			if (mo instanceof SchedulerObserver)
			{
				((CriticalEventObserver)mo).criticalEventOccured(evt);
			}
		}
	}

	/**
	 * @see nl.openedge.modules.ModuleFactory#getModule(java.lang.String)
	 */
	public Object getModule(String name)
	{

		ModuleAdapter moduleAdapter = (ModuleAdapter)modules.get(name);
		if (moduleAdapter == null)
		{
			throw new ModuleLookpupException("unable to find module with name: " + name);
		}
		return moduleAdapter.getModule();
	}

	/*
	 * @see nl.openedge.modules.ModuleFactory#getScheduler()
	 */
	public Scheduler getScheduler()
	{
		return scheduler;
	}

}
