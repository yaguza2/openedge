package nl.openedge.modules.config;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletContext;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for loading jdom documents.
 * 
 * @author Eelco Hillenius
 */
public final class DocumentLoader
{
	/** logger. */
	private static Logger log = LoggerFactory.getLogger(DocumentLoader.class);

	/**
	 * Load configuration document.
	 * 
	 * @param configDocument
	 *            configuration document
	 * @return a loaded JDOM document containing the configuration information.
	 * @throws ConfigException
	 *             when an configuration error occurs
	 */
	public static Document loadDocument(String configDocument) throws ConfigException
	{
		try
		{
			URL configURL = URLHelper.convertToURL(configDocument, DocumentLoader.class, null);

			if (configURL == null)
				throw new ConfigException(configDocument + " should be a document but is empty");
			log.info("Loading config from " + configURL);

			return internalLoad(configURL);

		}
		catch (IOException ex)
		{
			throw new ConfigException(ex);
		}
	}

	/**
	 * Load configuration document.
	 * 
	 * @param configURL
	 *            URL of configuration document
	 * @return a loaded JDOM document containing the configuration information.
	 * @throws ConfigException
	 *             when an configuration error occurs
	 */
	public static Document loadDocument(URL configURL) throws ConfigException
	{
		if (configURL == null)
			throw new ConfigException(configURL + " should be a document but is empty");
		log.info("Loading config from " + configURL);

		return internalLoad(configURL);
	}

	/**
	 * Load configuration document.
	 * 
	 * @param configDocument
	 *            configuration document
	 * @param servletContext
	 *            servlet context
	 * @return a loaded JDOM document containing the configuration information.
	 * @throws ConfigException
	 *             when an configuration error occurs
	 */
	public static Document loadDocument(String configDocument, ServletContext servletContext)
			throws ConfigException
	{
		try
		{
			URL configURL =
				URLHelper.convertToURL(configDocument, DocumentLoader.class, servletContext);

			if (configURL == null)
				throw new ConfigException(configDocument + " should be a document but is empty");
			log.info("Loading config from " + configURL.toString());
			return internalLoad(configURL);
		}
		catch (IOException ex)
		{
			throw new ConfigException(ex);
		}
	}

	/**
	 * Do the real loading.
	 * 
	 * @param configURL
	 *            URL of configuration document
	 * @return a loaded JDOM document containing the configuration information.
	 * @throws ConfigException
	 *             when an configuration error occurs
	 */
	private static Document internalLoad(URL configURL) throws ConfigException
	{

		try
		{
			log.info("Loading config from " + configURL.toString());
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
		catch (IOException ex)
		{

			throw new ConfigException(ex);
		}
	}
}
