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
package nl.openedge.modules.impl.thumbs;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.RepositoryFactory;
import nl.openedge.util.hibernate.ConfigException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Image download servlet that gets images from filesystem and optionally gets it as a
 * resized image
 * 
 * @author Eelco Hillenius
 */
public final class FileImageServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	protected Logger log = LoggerFactory.getLogger(FileImageServlet.class);

	protected String imageModuleAlias = null;

	protected String cacheModuleAlias = null;

	private ServletContext sctx = null;

	private int bufferSize = 8192;

	private boolean useDirectBuffers = true;

	private static ThreadLocal<ByteBuffer> bufferHolder = new ThreadLocal<ByteBuffer>();

	private String notFoundImage = "images/smiley.jpg";

	private File notFoundImageFile = null;

	private boolean failOnNotFound = true;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		String sMax = request.getParameter("max");
		int max = 0;
		if (sMax != null)
		{
			try
			{
				max = Integer.parseInt(sMax);
			}
			catch (NumberFormatException e)
			{
				System.err.println(e.getMessage());
				throw new ServletException(e);
			}
		}

		String src = request.getParameter("src");
		String realFile = sctx.getRealPath(src);
		if (realFile == null)
		{
			if (failOnNotFound)
			{
				response.getOutputStream().close();
				log.error(src + " not found!");
				return;
			}
		}
		File concreteFile = null;
		if (realFile != null)
		{
			concreteFile = new File(realFile);
			if (!concreteFile.isFile())
			{
				if (failOnNotFound)
				{
					response.getOutputStream().close();
					log.error(src + " not found!");
					return;
				}
				else
				{
					concreteFile = null;
				}
			}
		}

		if (concreteFile == null) // fallthrough
		{
			concreteFile = new File(notFoundImageFile.getAbsolutePath());
		}

		response.setContentType("images/jpeg"); // always as a jpeg
		ImageModule iModule = null;
		ThumbnailFileCacheModule cacheModule = null;

		ComponentRepository mf = RepositoryFactory.getRepository();
		iModule = (ImageModule) mf.getComponent(imageModuleAlias);
		cacheModule = (ThumbnailFileCacheModule) mf.getComponent(cacheModuleAlias);

		// lookup or create file in cache direcory with prefix
		File cacheFile = cacheModule.getFromCache(concreteFile, max);
		if (!cacheFile.isFile())
		{
			// image was not cached yet
			log.debug("did not find image in cache, create new image now...");
			FileInputStream fis = null;
			FileOutputStream fos = null;
			try
			{
				fis = new FileInputStream(concreteFile);
				fos = new FileOutputStream(cacheFile);
				// first create a cached image
				iModule.writeImage(fis, fos, max);
			}
			catch (Exception e)
			{
				log.error(e.getMessage(), e);
				throw new ServletException(e);
			}
			finally
			{
				closeQuietly(fos);
				closeQuietly(fis);
			}
		}
		FileInputStream cacheFo = null;
		OutputStream servletOs = null;
		int readBytes = 0;
		int bytes = 0;
		ReadableByteChannel rbc = null;
		WritableByteChannel wbc = null;
		try
		{
			// send image to servlet.out
			cacheFo = new FileInputStream(cacheFile);
			rbc = cacheFo.getChannel();
			servletOs = response.getOutputStream();
			wbc = Channels.newChannel(servletOs);

			ByteBuffer byteBuffer = bufferHolder.get();
			if (byteBuffer == null)
			{
				if (useDirectBuffers)
				{
					byteBuffer = ByteBuffer.allocateDirect(bufferSize);
				}
				else
				{
					byteBuffer = ByteBuffer.allocate(bufferSize);
				}
			}
			byteBuffer.clear(); // Prepare buffer for use
			for (;;)
			{
				if ((bytes = rbc.read(byteBuffer)) < 0)
				{
					break; // No more bytes to transfer
				}
				readBytes = readBytes + bytes;
				byteBuffer.flip();
				wbc.write(byteBuffer);
				byteBuffer.compact();
			}
		}
		catch (FileNotFoundException e)
		{
			// e.printStackTrace();
			throw new ServletException(e);
		}
		catch (IOException e)
		{
			// e.printStackTrace();
			throw new ServletException(e);
		}
		finally
		{
			closeQuietly(rbc);
			closeQuietly(wbc);
			closeQuietly(cacheFo);
			closeQuietly(servletOs);
		}
	}

	private void closeQuietly(Closeable closeable)
	{
		if (closeable != null)
		{
			try
			{
				closeable.close();
			}
			catch (Exception e)
			{
				log.error(
					"Error during closing of " + closeable.getClass().getName() + ": "
						+ e.getMessage(), e);
			}
		}
	}

	@Override
	public String getServletInfo()
	{
		return FileImageServlet.class.getName();
	}

	@Override
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);

		try
		{
			this.imageModuleAlias = config.getInitParameter("imageModule");
			this.cacheModuleAlias = config.getInitParameter("cacheModule");
			this.notFoundImage = config.getInitParameter("notFoundImage");

			// test if the modules are available
			ComponentRepository mf = RepositoryFactory.getRepository();
			mf.getComponent(imageModuleAlias);
			mf.getComponent(cacheModuleAlias);

			this.sctx = config.getServletContext();

			String useDB = config.getInitParameter("useDirectBuffers");
			if (useDB != null)
			{
				try
				{
					this.useDirectBuffers = Boolean.valueOf(useDB).booleanValue();
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
				}
			}
			String fonf = config.getInitParameter("failOnNotFound");
			if (fonf != null)
			{
				try
				{
					this.failOnNotFound = Boolean.valueOf(fonf).booleanValue();
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
				}
			}

			if (!failOnNotFound) // check
			{
				if (notFoundImage == null)
				{
					throw new ConfigException("if failOnNotFound == false, parameter notFoundImage"
						+ " must be provided");
				}
				this.notFoundImageFile =
					new File(config.getServletContext().getRealPath(notFoundImage));
				if (!notFoundImageFile.isFile())
				{
					throw new ConfigException(notFoundImage
						+ " (parameter notFoundImage) is not a file");
				}
				else
				{
					log.info("using " + notFoundImageFile.getAbsolutePath()
						+ " as the image for unknown files");
				}
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
			throw new ServletException(e);
		}
	}
}
