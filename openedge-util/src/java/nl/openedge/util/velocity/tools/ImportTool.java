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
package nl.openedge.util.velocity.tools;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.tools.ViewTool;

/**
 * @author Shawn Bayern
 * @author E.F. Hillenius (just the hack... thanks Shawn Bayern!)
 * $Id$
 * @see org.apache.taglibs.standard.tag.common.core.ImportSupport
 */
public class ImportTool implements ViewTool
{

	/** A reference to the ServletContext */
	protected ServletContext application;

	/** A reference to the HttpServletRequest. */
	protected HttpServletRequest request;

	/** A reference to the HttpServletResponse. */
	protected HttpServletResponse response;

	/** <p>Valid characters in a scheme.</p>
	 *  <p>RFC 1738 says the following:</p>
	 *  <blockquote>
	 *   Scheme names consist of a sequence of characters. The lower
	 *   case letters "a"--"z", digits, and the characters plus ("+"),
	 *   period ("."), and hyphen ("-") are allowed. For resiliency,
	 *   programs interpreting URLs should treat upper case letters as
	 *   equivalent to lower case in scheme names (e.g., allow "HTTP" as
	 *   well as "http").
	 *  </blockquote>
	 * <p>We treat as absolute any URL that begins with such a scheme name,
	 * followed by a colon.</p>
	 */
	public static final String VALID_SCHEME_CHARS = 
		"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789+.-";

	/** Default character encoding for response. */
	public static final String DEFAULT_ENCODING = "ISO-8859-1";

	public String get(String url)
	{

		// check the URL
		if (url == null || url.equals(""))
			return null;

		// Record whether our URL is absolute or relative
		boolean isAbsoluteUrl = isAbsoluteUrl(url);
		try
		{
			return acquireString(url, isAbsoluteUrl);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return e.getMessage();
		}
	}

	/**
	 * Initializes this tool.
	 *
	 * @param obj the current ViewContext
	 * @throws IllegalArgumentException if the param is not a ViewContext
	 */
	public void init(Object obj)
	{

		if (!(obj instanceof ViewContext))
		{

			throw new IllegalArgumentException(
				"Tool can only be wasAdded with a ViewContext");
		}
		ViewContext context = (ViewContext)obj;
		this.request = context.getRequest();
		this.response = context.getResponse();
		this.application = context.getServletContext();
	}

	//*********************************************************************
	// Actual URL importation logic

	/*
	 * Overall strategy:  we have two entry points, acquireString() and
	 * acquireReader().  The latter passes data through unbuffered if
	 * possible (but note that it is not always possible -- specifically
	 * for cases where we must use the RequestDispatcher.  The remaining
	 * methods handle the common.core logic of loading either a URL or a local
	 * resource.
	 *
	 * We consider the 'natural' form of absolute URLs to be Readers and
	 * relative URLs to be Strings.  Thus, to avoid doing extra work,
	 * acquireString() and acquireReader() delegate to one another as
	 * appropriate.  (Perhaps I could have spelled things out more clearly,
	 * but I thought this implementation was instructive, not to mention
	 * somewhat cute...)
	 */

	private String acquireString(String url, boolean isAbsoluteUrl) 
				throws IOException
	{
		if (isAbsoluteUrl)
		{
			// for absolute URLs, delegate to our peer
			BufferedReader r = new BufferedReader(acquireReader(url, isAbsoluteUrl));
			StringBuffer sb = new StringBuffer();
			int i;

			// under JIT, testing seems to show this simple loop is as fast
			// as any of the alternatives
			while ((i = r.read()) != -1)
				sb.append((char)i);

			return sb.toString();
		}
		else
		{

			// URL is relative, so we must be an HTTP request
			if (!(request instanceof HttpServletRequest && 
					response instanceof HttpServletResponse))
				throw new RuntimeException("IMPORT_REL_WITHOUT_HTTP");

			// retrieve an appropriate ServletContext
			ServletContext c = null;
			String targetUrl = url;
			c = application;

			// normalize the URL if we have an HttpServletRequest
			if (!targetUrl.startsWith("/"))
			{
				String sp = request.getServletPath();
				targetUrl = sp.substring(0, sp.lastIndexOf('/')) + '/' + targetUrl;
			}

			// from this context, get a dispatcher
			RequestDispatcher rd = c.getRequestDispatcher(stripSession(targetUrl));
			if (rd == null)
				throw new RuntimeException(stripSession(targetUrl));

			// include the resource, using our custom wrapper
			ImportResponseWrapper irw = new ImportResponseWrapper(response);

			// spec mandates specific error handling form include()
			try
			{
				rd.include(request, irw);
			}
			catch (Exception ex)
			{
				throw new RuntimeException(ex);
			}

			// disallow inappropriate response codes per JSTL spec
			if (irw.getStatus() < 200 || irw.getStatus() > 299)
			{
				throw new RuntimeException(irw.getStatus() + " " 
					+ stripSession(targetUrl));
			}

			// recover the response String from our wrapper
			return irw.getString();
		}
	}

	private Reader acquireReader(String url, boolean isAbsoluteUrl) 
		throws IOException
	{
		if (!isAbsoluteUrl)
		{
			// for relative URLs, delegate to our peer
			return new StringReader(acquireString(url, isAbsoluteUrl));
		}
		else
		{
			try
			{
				// handle absolute URLs ourselves, using java.net.URL
				String target = url;
				URL u = new URL(target);
				URLConnection uc = u.openConnection();
				InputStream i = uc.getInputStream();
				// okay, we've got a stream; encode it appropriately
				Reader r = new InputStreamReader(i, DEFAULT_ENCODING);

				// check response code for HTTP URLs before returning, per spec,
				// before returning
				if (uc instanceof HttpURLConnection)
				{
					int status = ((HttpURLConnection)uc).getResponseCode();
					if (status < 200 || status > 299)
						throw new RuntimeException(status + " " + target);
				}

				return r;
			}
			catch (IOException ex)
			{
				throw new RuntimeException(ex);
			}
			catch (RuntimeException ex)
			{ // because the spec makes us
				throw new RuntimeException(ex);
			}
		}
	}

	//*********************************************************************
	// Public utility methods

	/**
	 * Returns <tt>true</tt> if our current URL is absolute,
	 * <tt>false</tt> otherwise.
	 */
	public static boolean isAbsoluteUrl(String url)
	{
		// a null URL is not absolute, by our definition
		if (url == null)
			return false;

		// do a fast, simple check first
		int colonPos;
		if ((colonPos = url.indexOf(":")) == -1)
			return false;

		// if we DO have a colon, make sure that every character
		// leading up to it is a valid scheme character
		for (int i = 0; i < colonPos; i++)
			if (VALID_SCHEME_CHARS.indexOf(url.charAt(i)) == -1)
				return false;

		// if so, we've got an absolute url
		return true;
	}

	/**
	 * Strips a servlet session ID from <tt>url</tt>.  The session ID
	 * is encoded as a URL "path parameter" beginning with "jsessionid=".
	 * We thus remove anything we find between ";jsessionid=" (inclusive)
	 * and either EOS or a subsequent ';' (exclusive).
	 */
	public static String stripSession(String url)
	{
		StringBuffer u = new StringBuffer(url);
		int sessionStart;
		while ((sessionStart = u.toString().indexOf(";jsessionid=")) != -1)
		{
			int sessionEnd = u.toString().indexOf(";", sessionStart + 1);
			if (sessionEnd == -1)
				sessionEnd = u.toString().indexOf("?", sessionStart + 1);
			if (sessionEnd == -1) // still
				sessionEnd = u.length();
			u.delete(sessionStart, sessionEnd);
		}
		return u.toString();
	}

	/** Wraps responses to allow us to retrieve results as Strings. */
	private class ImportResponseWrapper extends HttpServletResponseWrapper
	{

		//************************************************************
		// Overview

		/*
		 * We provide either a Writer or an OutputStream as requested.
		 * We actually have a true Writer and an OutputStream backing
		 * both, since we don't want to use a character encoding both
		 * ways (Writer -> OutputStream -> Writer).  So we use no
		 * encoding at all (as none is relevant) when the target resource
		 * uses a Writer.  And we decode the OutputStream's bytes
		 * using OUR tag's 'charEncoding' attribute, or ISO-8859-1
		 * as the default.  We thus ignore setLocale() and setContentType()
		 * in this wrapper.
		 *
		 * In other words, the target's asserted encoding is used
		 * to convert from a Writer to an OutputStream, which is typically
		 * the medium through with the target will communicate its
		 * ultimate response.  Since we short-circuit that mechanism
		 * and read the target's characters directly if they're offered
		 * as such, we simply ignore the target's encoding assertion.
		 */

		//************************************************************
		// Data

		/** The Writer we convey. */
		private StringWriter sw = new StringWriter();

		/** A buffer, alternatively, to accumulate bytes. */
		private ByteArrayOutputStream bos = new ByteArrayOutputStream();

		/** A ServletOutputStream we convey, tied to this Writer. */
		private ServletOutputStream sos = new ServletOutputStream()
		{
			public void write(int b) throws IOException
			{
				bos.write(b);
			}
		};

		/** 'True' if getWriter() was called; false otherwise. */
		private boolean isWriterUsed;

		/** 'True if getOutputStream() was called; false otherwise. */
		private boolean isStreamUsed;

		/** The HTTP status set by the target. */
		private int status = 200;

		//************************************************************
		// Constructor and methods

		/** Constructs a new ImportResponseWrapper. */
		public ImportResponseWrapper(HttpServletResponse response)
		{
			super(response);
		}

		/** Returns a Writer designed to buffer the output. */
		public PrintWriter getWriter()
		{
			if (isStreamUsed)
				throw new IllegalStateException("IMPORT_ILLEGAL_STREAM");
			isWriterUsed = true;
			return new PrintWriter(sw);
		}

		/** Returns a ServletOutputStream designed to buffer the output. */
		public ServletOutputStream getOutputStream()
		{
			if (isWriterUsed)
				throw new IllegalStateException("IMPORT_ILLEGAL_WRITER");
			isStreamUsed = true;
			return sos;
		}

		/** Has no effect. */
		public void setContentType(String x)
		{
			// ignore
		}

		/** Has no effect. */
		public void setLocale(Locale x)
		{
			// ignore
		}

		public void setStatus(int status)
		{
			this.status = status;
		}

		public int getStatus()
		{
			return status;
		}

		/** 
		 * Retrieves the buffered output, using the containing tag's 
		 * 'charEncoding' attribute, or the tag's default encoding,
		 * <b>if necessary</b>.
			 */
		// not simply toString() because we need to throw
		// UnsupportedEncodingException
		public String getString() throws UnsupportedEncodingException
		{
			if (isWriterUsed)
			{
				return sw.toString();
			}
			else if (isStreamUsed)
			{
				return bos.toString(DEFAULT_ENCODING);
			}
			else
			{
				return ""; // target didn't write anything
			}
		}
	}

}
