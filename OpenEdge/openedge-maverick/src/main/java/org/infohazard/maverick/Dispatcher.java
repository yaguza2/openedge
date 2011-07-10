/*
 * $Id: Dispatcher.java,v 1.26 2005/10/03 21:45:58 prophecyslides Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/Dispatcher.java,v $
 */

package org.infohazard.maverick;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.infohazard.maverick.flow.Command;
import org.infohazard.maverick.flow.ConfigException;
import org.infohazard.maverick.flow.Loader;
import org.infohazard.maverick.flow.MaverickContext;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dispatcher is the central command processor of the Maverick framework. All commands are
 * routed to this servlet by way of extension mapping (say, *.m). From here requests are
 * routed through the "workflow" tree of {@link Command},
 * {@link org.infohazard.maverick.flow.View View}, and
 * {@link org.infohazard.maverick.flow.Transform Transform} (or "Pipeline") objects built
 * from the Maverick configuration file. </p>
 * 
 * <p>
 * Commands can be gracefully chained together; if a view references another Maverick
 * Command, the same {@link MaverickContext} object is used.
 * </p>
 * 
 * <p>
 * The Dispatcher object is made available to
 * {@link org.infohazard.maverick.flow.Controller Controllers} (or anyone else) as an
 * object in the application-scope (aka {@link ServletContext} attribute) collection. The
 * attribute key is the value of the {@link #MAVERICK_APPLICATION_KEY
 * MAVERICK_APPLICATION_KEY} constant.
 * </p>
 * 
 * <p>
 * Note that there is are two special pseudocommands defined by this servlet: "
 * <code>*</code>" and "<code>reload<code>".
 * </p>
 * 
 * <p>
 * "<code>reload</code>" triggers a reload of the maverick config file. This can safely be
 * done on running system; all commands currently being processed will complete using the
 * old data. New command requests will use the new data as soon as it is finished loading.
 * Note that the actual command name used for "<code>reload</code>" is determined by the
 * <code>reload</code> Servlet init parameter.
 * </p>
 * 
 * <p>
 * "<code>*</code>" is a special command which can be defined in the configuration file.
 * If a command request cannot be mapped to a command (because the requested Command was
 * not defined), the "*" Command will be used instead. If there is no "*" command defined
 * in the configuration file, unmatched requests return 404.
 * </p>
 */
public class Dispatcher extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * <p>
	 * The key in the application context ({@link ServletContext}) under which the
	 * <code>Dispatcher</code> will be made available ["mav.dispatcher"].
	 * </p>
	 */
	public static final String MAVERICK_APPLICATION_KEY = "mav.dispatcher";

	/**
	 * <p>
	 * If a value is set as an application attribute with this key, the value is used to
	 * override the setting of the <code>configFile</code> Servlet init parameter
	 * ["mav.configFile"].
	 * </p>
	 */
	public static final String KEY_CONFIG_FILE = "mav.configFile";

	/**
	 * <p>
	 * Name of the Servlet init parameter which defines the path to the Maverick
	 * configuration file ["configFile"]. This parameter is used when
	 * {@link #KEY_CONFIG_FILE} is not set, otherwise the path defaults to
	 * {@link #DEFAULT_CONFIG_FILE DEFAULT_CONFIG_FILE}.
	 * </p>
	 */
	public static final String INITPARAM_CONFIG_FILE = "configFile";

	/**
	 * <p>
	 * If a value is set as an application attribute with this key, the value is used to
	 * override the setting of the <code>configTransform</code> Servlet init parameter
	 * ["mav.configTransform"].
	 * </p>
	 */
	public static final String KEY_CONFIG_TRANSFORM = "mav.configTransform";

	/**
	 * <p>
	 * Name of the Servlet init parameter which defines the path to a transform which will
	 * be applied to the Maverick configuration XML document before loading
	 * ["configTransform"]. Defaults to null, which means perform no transformation.
	 * </p>
	 */
	public static final String INITPARAM_CONFIG_TRANSFORM = "configTransform";

	/**
	 * <p>
	 * Name of the Servlet init parameter which defines the name of the
	 * <code>reload</code> Command ["reloadCommand"]. The value will typically be
	 * something like "reload".
	 * </p>
	 */
	public static final String INITPARAM_RELOAD_COMMAND = "reloadCommand";

	/**
	 * <p>
	 * Name of the Servlet init parameter which defines the name of the Command which
	 * displays the current configuration ["currentConfigCommand"]. The value will
	 * typically be something like "currentConfig".
	 * </p>
	 */
	public static final String INITPARAM_CURRENT_CONFIG_COMMAND = "currentConfigCommand";

	/**
	 * <p>
	 * Name of the Serlvet init parameter used to set the {@link #defaultRequestCharset
	 * defaultRequestCharset} property ["defaultRequestCharset"].
	 * </p>
	 */
	public static final String INITPARAM_DEFAULT_REQUEST_CHARSET = "defaultRequestCharset";

	/**
	 * <p>
	 * Name of the Serlvet init parameter used to set the {@link #limitTransformsParam
	 * limitTransformsParam} property ["limitTransformsParam"].
	 * </p>
	 */
	public static final String INITPARAM_LIMIT_TRANSFORMS_PARAM = "limitTransformsParam";

	/**
	 * <p>
	 * Name of the Serlvet init parameter used to set the {@link #reuseMaverickContext
	 * reuseMaverickContext} property ["reuseMaverickContext"].
	 * </p>
	 */
	public static final String INITPARAM_REUSE_CONTEXT = "reuseMaverickContext";

	/**
	 * <p>
	 * Default, context-relative, location of the Maverick XML configuration file
	 * ["/WEB-INF/maverick.xml"].
	 * </p>
	 * <p>
	 * Used if {@link #KEY_CONFIG_FILE} and {@link #INITPARAM_CONFIG_FILE} are not set.
	 * </p>
	 */
	protected static final String DEFAULT_CONFIG_FILE = "/WEB-INF/maverick.xml";

	/**
	 * <p>
	 * The {@link MaverickContext} object is stored in the request context with this key
	 * so that it can be recovered for recursive maverick execution ["mav.context"].
	 * </p>
	 */
	protected static final String SAVED_MAVCTX_KEY = "mav.context";

	/**
	 * Dispatcher logger.
	 */
	private static Logger log = LoggerFactory.getLogger(Dispatcher.class);

	/**
	 * Maps command names to Command objects.
	 */
	protected Map<String, Command> commands;

	/**
	 * The current configuration document.
	 */
	protected Document configDocument;

	/**
	 * The charset to use by default for request parameter decoding [<code>null</code>].
	 * If not set, the default charset will be whatever the servlet container chooses
	 * (probably ISO-8859-1 aka Latin-1). If set, this String is used as the character
	 * encoding for HTTP requests. Leaving the property unset means do nothing special.
	 * <p>
	 * This property may be set through the {@link #INITPARAM_DEFAULT_REQUEST_CHARSET
	 * INITPARAM_DEFAULT_REQUEST_CHARSET} Serlvet init parameter.
	 */
	protected String defaultRequestCharset;

	/**
	 * The number of transformations to run before stopping, regardless of whether the
	 * final step has been reached. If this property is not set, all transforms will run
	 * to completion.
	 * <p>
	 * This property may be set through the {@link #INITPARAM_LIMIT_TRANSFORMS_PARAM
	 * INITPARAM_LIMIT_TRANSFORMS_PARAM} Serlvet init parameter.
	 */
	protected String limitTransformsParam;

	/**
	 * If set to <code>true</code>, the {@link MaverickContext} is reused between Commands
	 * invoked within the same request. This allows Maverick Controllers to be "chained"
	 * by forwarding context attributes from one Maverick Command to another.
	 * <p>
	 * The Context is <b>not</b> preserved in the case of a redirected request, since
	 * redirection creates a new HTTP request.
	 * <p>
	 * This property may be set through the {@link #INITPARAM_REUSE_CONTEXT
	 * INITPARAM_REUSE_CONTEXT} Serlvet init parameter. Set the parameter to "true" or
	 * leave it undefined ["false"].
	 */
	protected boolean reuseMaverickContext;

	/**
	 * Initializes the Dispatcher by loading the configuration file.
	 */
	@Override
	public void init() throws ServletException
	{
		// Make us available in the application attribute collection
		this.getServletContext().setAttribute(MAVERICK_APPLICATION_KEY, this);

		// Get defaultRequestCharset from init parameter, null is ok
		this.defaultRequestCharset = this.getInitParameter(INITPARAM_DEFAULT_REQUEST_CHARSET);

		// Get limitTransformsParam from init parameter, null is ok
		this.limitTransformsParam = this.getInitParameter(INITPARAM_LIMIT_TRANSFORMS_PARAM);

		// Get reuseMaverickContext from init parameter, null is ok
		this.reuseMaverickContext = "true".equals(this.getInitParameter(INITPARAM_REUSE_CONTEXT));

		try
		{
			reloadConfig();
		}
		catch (ConfigException e)
		{
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * The main entry point of the servlet; this processes an HTTP request.
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		// identify the command
		String commandName = extractCommandName(request);

		// get config for the command
		Command cmd = this.getCommand(commandName);

		if (cmd == null)
		{
			log.warn("No such command " + commandName);

			// return 404
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "There is no such command \""
				+ commandName + "\".");
		}
		else
		{
			log.debug("Servicing command:  {}", commandName);

			// This must be done before any parameters are read
			if (this.defaultRequestCharset != null)
				request.setCharacterEncoding(this.defaultRequestCharset);

			// Maybe we want to use the same context object if we were recursively called
			// from a
			// Maverick view (say, somebody forwarded to "command.m")
			MaverickContext ctx;

			if (this.reuseMaverickContext)
			{
				ctx = (MaverickContext) request.getAttribute(SAVED_MAVCTX_KEY);

				if (ctx == null)
				{
					ctx = new MaverickContext(this, request, response);
					request.setAttribute(SAVED_MAVCTX_KEY, ctx);
				}
			}
			else
			{
				ctx = new MaverickContext(this, request, response);
			}

			cmd.go(ctx);
		}
	}

	/**
	 * Extracts the command name from the request. Extension and leading / will be
	 * removed.
	 */
	protected String extractCommandName(HttpServletRequest request)
	{
		// If we are include()ed from a RequestDispatcher, the real request
		// path will be obtained from this special attribute. If we are
		// produced by a forward() or a normal request, we can use the
		// getServletPath() method. See section 8.3 of the Servlet 2.3 API.
		String path = (String) request.getAttribute("javax.servlet.include.servlet_path");
		if (path == null)
			path = request.getServletPath();

		if (log.isDebugEnabled())
		{
			log.debug("Command servlet path is:  " + path);
			log.debug("Command context path is:  " + request.getContextPath());
		}

		int firstChar = 0;
		if (path.startsWith("/"))
			firstChar = 1;

		int period = path.lastIndexOf(".");

		path = path.substring(firstChar, period);

		return path;
	}

	/**
	 * Reloads the XML configuration file. Can be done on-the-fly. Any requests being
	 * serviced are allowed to complete with the old data.
	 */
	protected void reloadConfig() throws ConfigException
	{
		log.info("Starting configuration load");

		Document replacementConfigDocument = this.loadConfigDocument();
		Loader loader = new Loader(replacementConfigDocument, this.getServletConfig());
		Map<String, Command> replacementCommands = loader.getCommands();

		//
		// Add a simple reload command if the user defined one.
		//
		String reloadStr = this.getInitParameter(INITPARAM_RELOAD_COMMAND);
		if (reloadStr != null)
		{
			Command reload = new Command()
			{
				@Override
				public void go(MaverickContext mctx) throws ServletException
				{
					try
					{
						reloadConfig();
					}
					catch (ConfigException e)
					{
						log.error(e.getMessage(), e);
						throw e;
					}
				}
			};

			replacementCommands.put(reloadStr, reload);
		}

		//
		// Add the current config display command if the user defined one.
		//
		String currentConfigStr = this.getInitParameter(INITPARAM_CURRENT_CONFIG_COMMAND);
		if (currentConfigStr != null)
		{
			Command currentConfig = new Command()
			{
				@Override
				public void go(MaverickContext mctx) throws IOException
				{
					XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

					mctx.getRealResponse().setContentType("text/xml; charset=UTF-8");
					outputter.output(configDocument, mctx.getRealResponse().getOutputStream());
				}
			};

			replacementCommands.put(currentConfigStr, currentConfig);
		}

		// Replace the commands map in place as the *LAST* step. This makes this
		// operation thread-safe, since all existing threads continue working with
		// the old data until they are finished.
		this.commands = replacementCommands;
		this.configDocument = replacementConfigDocument;

		log.info("Finished configuration load");
	}

	/**
	 * <p>
	 * Returns the command object associated with the specified name. If the command is
	 * not found, a command with name "*" is returned. If there is no command with id "*",
	 * null is returned.
	 * </p>
	 */
	protected Command getCommand(String name)
	{
		Command cmd = this.commands.get(name);

		if (cmd == null)
		{
			cmd = this.commands.get("*");
			if (cmd != null)
				log.warn("Unknown command " + name + ", using *.");
		}

		return cmd;
	}

	/**
	 * <p>
	 * Returns a loaded JDOM document containing the configuration information.
	 * 
	 * @return a loaded JDOM document containing the configuration information
	 *         </p>
	 */
	protected Document loadConfigDocument() throws ConfigException
	{
		try
		{
			// Figure out the config file
			String configFile = (String) this.getServletContext().getAttribute(KEY_CONFIG_FILE);

			if (configFile == null)
				configFile = this.getInitParameter(INITPARAM_CONFIG_FILE);

			if (configFile == null)
				configFile = DEFAULT_CONFIG_FILE;

			java.net.URL configURL = this.convertToURL(configFile);
			log.info("Loading config from " + configURL.toString());

			// Figure out the config transform (if appropriate)
			String configTransform =
				(String) this.getServletContext().getAttribute(KEY_CONFIG_TRANSFORM);

			if (configTransform == null)
				configTransform = this.getInitParameter(INITPARAM_CONFIG_TRANSFORM);

			// Now load the document, maybe performing a transform
			if (configTransform == null)
			{
				try
				{
					SAXBuilder builder = new SAXBuilder();
					return builder.build(configURL.openStream(), configURL.toString());
				}
				catch (org.jdom.JDOMException jde)
				{
					throw new ConfigException(jde);
				}
			}
			else
			// must perform a transformation
			{
				java.net.URL transURL = this.convertToURL(configTransform);
				log.info("Transforming config with " + transURL.toString());

				try
				{
					Transformer transformer =
						TransformerFactory.newInstance().newTransformer(
							new StreamSource(transURL.openStream(), transURL.toString()));

					Source in = new StreamSource(configURL.openStream(), configURL.toString());
					JDOMResult out = new JDOMResult();

					transformer.transform(in, out);
					return out.getDocument();
				}
				catch (TransformerException ex)
				{
					throw new ConfigException(ex);
				}
			}
		}
		catch (IOException ex)
		{
			throw new ConfigException(ex);
		}
	}

	/**
	 * <p>
	 * Returns the current configuration as a JDOM Document.
	 * 
	 * @return the current configuration as a JDOM Document.
	 *         </p>
	 */
	public Document getConfigDocument()
	{
		return this.configDocument;
	}

	/**
	 * <p>
	 * Returns the {@link #limitTransformsParam} property. <code>null</code> null
	 * indicates the feature is disabled.
	 * </p>
	 */
	public String getLimitTransformsParam()
	{
		return this.limitTransformsParam;
	}

	/**
	 * <p>
	 * Interprets some absolute URLs as external paths, otherwise generates URL
	 * appropriate for loading from internal webapp.
	 * </p>
	 */
	protected URL convertToURL(String path) throws MalformedURLException
	{
		if (path.startsWith("file:") || path.startsWith("http:") || path.startsWith("https:")
			|| path.startsWith("ftp:") || path.startsWith("jar:"))
			return new URL(path);
		else
		{
			// Quick sanity check
			if (!path.startsWith("/"))
				path = "/" + path;

			return this.getServletContext().getResource(path);
		}
	}
}
