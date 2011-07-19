/*
 * $Id: Loader.java,v 1.17 2004/06/27 17:42:14 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/Loader.java,v $
 */
package org.infohazard.maverick.flow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;

import org.infohazard.maverick.util.XML;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builds the tree of flow objects which process Maverick commands.
 * 
 * created January 27, 2002
 * 
 * @author Jeff Schnitzer
 * @version $Revision: 1.17 $ $Date: 2004/06/27 17:42:14 $
 */
public class Loader
{
	/** xml tag for modules, value = 'modules'. */
	protected final static String TAG_MODULES = "modules";

	/** xml tag for view factories, value = 'view-factory'. */
	protected final static String TAG_VIEWFACTORY = "view-factory";

	/** xml tag for controller factories, value = 'controller-facotry'. */
	protected final static String TAG_CONTROLLERFACTORY = "controller-factory";

	/** xml tag for transform factories, value = 'transform-factory'. */
	protected final static String TAG_TRANSFORMFACTORY = "transform-factory";

	/** xml tag for shunt factories, value = 'shunt-factory'. */
	protected final static String TAG_SHUNTFACTORY = "shunt-factory";

	/** xml tag for shunt provider, value = 'provider'. */
	protected final static String ATTR_PROVIDER = "provider";

	/** xml tag for commands, value = 'commands'. */
	protected final static String TAG_COMMANDS = "commands";

	/** xml tag for a command, value = 'command'. */
	protected final static String TAG_COMMAND = "command";

	/** xml tag for a command name, value = 'name'. */
	protected final static String ATTR_COMMAND_NAME = "name";

	/** xml tag for views, value = 'views'. */
	protected final static String TAG_VIEWS = "views";

	/** xml tag for the default view type, value = 'default-view-type'. */
	protected final static String PARAM_DEFAULT_VIEW_TYPE = "default-view-type";

	/** xml tag for the default transform type, value = 'default-transform-type'. */
	protected final static String PARAM_DEFAULT_TRANSFORM_TYPE = "default-transform-type";

	/** Logger. */
	private static Logger log = LoggerFactory.getLogger(Loader.class);

	/** Commands get built into this map. */
	protected Map<String, Command> commands = new HashMap<String, Command>();

	/** Servlet config. */
	protected ServletConfig servletCfg;

	/** Encapsulates view and transform factories and handles decoration */
	protected MasterFactory masterFact;

	/** For tracking global views and managing view lists */
	protected ViewRegistry viewReg;

	/** For creating commands */
	protected CommandFactory commandFact;

	/**
	 * @param doc
	 *            An already parsed JDOM Document of the config file.
	 * @param dispatcherConfig
	 *            servlet configuration of the dispatcher servlet
	 * @exception ConfigException
	 */
	public Loader(Document doc, ServletConfig dispatcherConfig) throws ConfigException
	{
		this.servletCfg = dispatcherConfig;
		this.masterFact = new MasterFactory(dispatcherConfig);

		// Assume simple, non-shunted system. These can be replaced later if the
		// user loads a ShuntFactory module.
		this.viewReg = new ViewRegistrySimple(this.masterFact);
		this.commandFact = new CommandFactory(this.viewReg);

		this.setupCoreModules();

		this.loadDocument(doc);
	}

	/**
	 * Get the commands.
	 * 
	 * @return Commands map
	 */
	public Map<String, Command> getCommands()
	{
		return this.commands;
	}

	/**
	 * setup the core modules.
	 * 
	 * @exception ConfigException
	 */
	protected void setupCoreModules() throws ConfigException
	{
		// Set up the basic transforms
		TransformFactory docTrans =
			new org.infohazard.maverick.transform.DocumentTransformFactory();
		docTrans.init(null, this.servletCfg);
		this.masterFact.defineTransformFactory("document", docTrans);

		TransformFactory xsltTrans = new org.infohazard.maverick.transform.XSLTransformFactory();
		xsltTrans.init(null, this.servletCfg);
		this.masterFact.defineTransformFactory("xslt", xsltTrans);

		// Set up the basic views
		ViewFactory document = new org.infohazard.maverick.view.DocumentViewFactory();
		document.init(null, this.servletCfg);
		this.masterFact.defineViewFactory("document", document);

		ViewFactory redirect = new org.infohazard.maverick.view.RedirectViewFactory();
		redirect.init(null, this.servletCfg);
		this.masterFact.defineViewFactory("redirect", redirect);

		ViewFactory trivial = new org.infohazard.maverick.view.TrivialViewFactory();
		trivial.init(null, this.servletCfg);
		this.masterFact.defineViewFactory("trivial", trivial);

		ViewFactory nullViewFact = new org.infohazard.maverick.view.NullViewFactory();
		nullViewFact.init(null, this.servletCfg);
		this.masterFact.defineViewFactory("null", nullViewFact);
	}

	/**
	 * load the configuration document.
	 * 
	 * @param doc
	 *            the configuration document
	 * @exception ConfigException
	 */
	@SuppressWarnings("unchecked")
	protected void loadDocument(Document doc) throws ConfigException
	{
		Element root = doc.getRootElement();

		// Maybe default transform type was specified
		String defaultTransformType = XML.getValue(root, PARAM_DEFAULT_TRANSFORM_TYPE);
		if (defaultTransformType != null)
			this.masterFact.setDefaultTransformType(defaultTransformType);

		// Maybe default view type was specified
		String defaultViewType = XML.getValue(root, PARAM_DEFAULT_VIEW_TYPE);
		if (defaultViewType != null)
			this.masterFact.setDefaultViewType(defaultViewType);

		// load modules. probably won't be more than one element, but who knows.
		// don't need to worry about default type being reset because it won't be.
		Iterator<Element> allModulesIt = root.getChildren(TAG_MODULES).iterator();
		while (allModulesIt.hasNext())
		{
			Element modules = allModulesIt.next();
			this.loadModules(modules);
		}

		// load global views; can be many elements
		Iterator<Element> allViewsIt = root.getChildren(TAG_VIEWS).iterator();
		while (allViewsIt.hasNext())
		{
			Element viewsNode = allViewsIt.next();
			this.viewReg.defineGlobalViews(viewsNode);
		}

		// load commands; can be many elements
		Iterator<Element> allCommandsIt = root.getChildren(TAG_COMMANDS).iterator();
		while (allCommandsIt.hasNext())
		{
			Element commandsNode = allCommandsIt.next();

			Iterator<Element> commandIt = commandsNode.getChildren(TAG_COMMAND).iterator();
			while (commandIt.hasNext())
			{
				Element commandNode = commandIt.next();

				String commandName = commandNode.getAttributeValue(ATTR_COMMAND_NAME);

				log.info("Creating command:  " + commandName);

				Command cmd = this.commandFact.createCommand(commandNode);

				this.commands.put(commandName, cmd);
			}
		}
	}

	/**
	 * load the modules.
	 * 
	 * @param modulesNode
	 *            the xml node of the modules
	 * @exception ConfigException
	 */
	@SuppressWarnings("unchecked")
	protected void loadModules(Element modulesNode) throws ConfigException
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null)
		{
			classLoader = DefaultControllerFactory.class.getClassLoader();
		}

		// can override the core modules already installed
		masterFact.defineTransformFactories(modulesNode.getChildren(TAG_TRANSFORMFACTORY));
		masterFact.defineViewFactories(modulesNode.getChildren(TAG_VIEWFACTORY));

		Element shuntFactoryNode = modulesNode.getChild(TAG_SHUNTFACTORY);
		if (shuntFactoryNode != null)
		{
			String providerName = shuntFactoryNode.getAttributeValue(ATTR_PROVIDER);

			if (providerName == null)
				throw new ConfigException("Not a valid shunt factory node:  "
					+ XML.toString(shuntFactoryNode));

			Class< ? > providerClass;
			ShuntFactory instance;
			try
			{
				providerClass = classLoader.loadClass(providerName);
				instance = (ShuntFactory) providerClass.newInstance();
			}
			catch (Exception ex)
			{
				throw new ConfigException("Unable to define shunt factory " + providerName, ex);
			}

			log.info("Using shunt factory " + providerName);
			// Give the factory an opportunity to initialize itself from any subnodes
			instance.init(shuntFactoryNode, this.servletCfg);

			// Replace the RendererRegistry and set view registry property on the cmd
			// factory
			this.viewReg = new ViewRegistryShunted(this.masterFact, instance);
			this.commandFact.setViewRegistry(this.viewReg);
		}
		Element controllerFactoryNode = modulesNode.getChild(TAG_CONTROLLERFACTORY);
		if (controllerFactoryNode != null)
		{
			String providerName = controllerFactoryNode.getAttributeValue(ATTR_PROVIDER);

			if (providerName == null)
				throw new ConfigException("Not a valid controller factory node: "
					+ XML.toString(controllerFactoryNode));

			Class< ? > providerClass;
			ControllerFactory instance;
			try
			{
				providerClass = classLoader.loadClass(providerName);
				instance = (ControllerFactory) providerClass.newInstance();
			}
			catch (Exception ex)
			{
				throw new ConfigException("Unable to define controller factory " + providerName, ex);
			}

			log.info("Using controller factory " + providerName);

			// Give the factory an opportunity to initialize itself from any subnodes
			instance.init(controllerFactoryNode, this.servletCfg);

			this.commandFact.setControllerFactory(instance);
		}
	}

}
