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
package nl.openedge.modules.config;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

/**
 * Helper class for loading jdom documents
 * @author Eelco Hillenius
 */
public final class DocumentLoader
{

	/* logger */
	private static Log log = LogFactory.getLog(DocumentLoader.class);

	/**
	 * @return a loaded JDOM document containing the configuration information.
	 */
	public static Document loadDocument(String configDocument) throws ConfigException
	{

		try
		{
			URL configURL = URLHelper.convertToURL(
				configDocument, DocumentLoader.class, null);

			if (configURL == null)
				throw new ConfigException(configDocument 
					+ " should be a document but is empty");
			log.info("Loading config from " + configURL);

			return internalLoad(configURL);

		}
		catch (IOException ex)
		{
			throw new ConfigException(ex);
		}
	}

	/**
	 * @return a loaded JDOM document containing the configuration information.
	 */
	public static Document loadDocument(URL configURL) throws ConfigException
	{

		if (configURL == null)
			throw new ConfigException(configURL 
				+ " should be a document but is empty");
		log.info("Loading config from " + configURL);

		return internalLoad(configURL);
	}

	/**
	 * @return a loaded JDOM document containing the configuration information.
	 */
	public static Document loadDocument(String configFile, 
								ServletContext servletContext)
								throws ConfigException
	{

		try
		{

			URL configURL = URLHelper.convertToURL(configFile, 
				DocumentLoader.class, servletContext);

			if (configURL == null)
				throw new ConfigException(configFile 
					+ " should be a document but is empty");
			log.info("Loading config from " + configURL.toString());

			return internalLoad(configURL);

		}
		catch (IOException ex)
		{
			throw new ConfigException(ex);
		}
	}

	/*
	 * @return a loaded JDOM document containing the configuration information.
	 */
	private static Document internalLoad(URL configURL) throws ConfigException
	{

		try
		{
			log.info("Loading config from " + configURL.toString());
			try
			{
				SAXBuilder builder = new SAXBuilder();
				return builder.build(
					configURL.openStream(), configURL.toString());
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
