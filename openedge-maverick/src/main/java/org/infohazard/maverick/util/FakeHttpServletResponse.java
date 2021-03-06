/*
 * $Id: FakeHttpServletResponse.java,v 1.8 2004/06/07 20:38:37 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/util/FakeHttpServletResponse.java,v $
 */

package org.infohazard.maverick.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores data written to the response and allows it to be obtained later.
 */
public class FakeHttpServletResponse extends HttpServletResponseWrapper
{
	/**
	 * Logger.
	 */
	private static Logger log = LoggerFactory.getLogger(FakeHttpServletResponse.class);

	/**
	 */
	protected static final String NO_BUFFER_MSG =
		"The previous step never tried to write anything.  Perhaps the document path is incorrect?";

	/**
	 * We need to track the charset which is applied to the response using setContentType
	 * so that we can intelligently convert between streams and readers. If null, the
	 * default encoding is assumed.
	 */
	protected String charset;

	/**
	 * Actually holds all the output data.
	 */
	protected Buffer buffer;

	/**
	 * Creates a response wrapper which buffers the output.
	 */
	public FakeHttpServletResponse(HttpServletResponse wrapMe)
	{
		super(wrapMe);

		if (log.isDebugEnabled())
			log.debug("Creating fake response, original buffer size is " + wrapMe.getBufferSize());
	}

	/**
	 * @return true if it is more efficient to call getOutputAsReader() than
	 *         getOutputAsString(). If false, getOutputAsString() should be used.
	 */
	public boolean prefersReader()
	{
		if (this.buffer == null)
			throw new IllegalStateException(NO_BUFFER_MSG);

		return this.buffer.prefersReader();
	}

	/**
	 * Provides the buffered output as a Reader. If output was written using a
	 * ServletOutputStream, the charset defined in the content-type is used to decode the
	 * data. If no charset is set, the default encoding is assumed.
	 */
	public Reader getOutputAsReader() throws UnsupportedEncodingException
	{
		log.debug("Getting output as Reader");

		if (this.buffer == null)
			throw new IllegalStateException(NO_BUFFER_MSG);

		return this.buffer.getAsReader();
	}

	/**
	 * Provides the buffered output as a String. If output was written using a
	 * ServletOutputStream, the charset defined in the content-type is used to decode the
	 * data. If no charset is set, the default encoding is assumed.
	 */
	public String getOutputAsString() throws UnsupportedEncodingException
	{
		log.debug("Getting output as String");

		if (this.buffer == null)
			throw new IllegalStateException(NO_BUFFER_MSG);

		return this.buffer.getAsString();
	}

	/**
	 * @return the amount of data written to the output stream.
	 */
	public int outputSize()
	{
		if (this.buffer == null)
			return 0;
		else
			return this.buffer.size();
	}

	/**
	 * @see ServletResponse#getOutputStream
	 */
	@Override
	public ServletOutputStream getOutputStream()
	{
		log.debug("View is using ServletOutputStream");

		if (this.buffer == null)
			this.buffer = new ServletOutputStreamBuffer(this.charset);
		else
		{
			if (!(this.buffer instanceof ServletOutputStreamBuffer))
				throw new IllegalStateException("getWriter() already called");
		}

		return (ServletOutputStream) this.buffer;
	}

	/**
	 * @see ServletResponse#getWriter
	 */
	@Override
	public PrintWriter getWriter()
	{
		log.debug("View is using PrintWriter");

		if (this.buffer == null)
			this.buffer = new PrintWriterBuffer();
		else
		{
			if (!(this.buffer instanceof PrintWriterBuffer))
				throw new IllegalStateException("getOutputStream() already called");
		}

		return (PrintWriter) this.buffer;
	}

	/**
	 * @see ServletResponse#setContentType
	 */
	@Override
	public void setContentType(String type)
	{
		if (log.isDebugEnabled())
			log.debug("Setting contentType to " + type);

		// Don't pass through

		// Extract out the charset.
		// type might be something like this: text/html; charset=utf-8
		int semicolonIndex = type.lastIndexOf(';');
		if (semicolonIndex >= 0 && semicolonIndex != (type.length() - 1))
		{
			String working = type.substring(semicolonIndex + 1);
			working = working.trim();

			if (working.toLowerCase().startsWith("charset"))
			{
				working = working.substring(working.indexOf("=") + 1).trim();

				if (working.length() > 0)
					this.charset = working;
			}
		}
	}

	/**
	 * @see ServletResponse#setContentLength
	 */
	@Override
	public void setContentLength(int len)
	{
		log.debug("Someone wanted to set contentLength to " + len);

		// Don't pass through
	}

	/**
	 * @see ServletResponse#flushBuffer
	 */
	@Override
	public void flushBuffer()
	{
		log.debug("Someone wanted to flush the buffer");

		// Don't pass through
	}

	/**
	 * @see ServletResponse#setLocale
	 */
	@Override
	public void setLocale(java.util.Locale loc)
	{
		log.debug("Set locale to " + loc);

		super.setLocale(loc);
	}

	/**
	 * @see HttpServletResponse#addCookie
	 */
	@Override
	public void addCookie(Cookie cookie)
	{
		log.debug("Added cookie " + cookie);

		super.addCookie(cookie);
	}

	/**
	 * @see HttpServletResponse#setDateHeader
	 */
	@Override
	public void setDateHeader(java.lang.String name, long date)
	{
		log.debug("Set date header " + name + " to " + date);

		super.setDateHeader(name, date);
	}

	/**
	 * @see HttpServletResponse#addDateHeader
	 */
	@Override
	public void addDateHeader(java.lang.String name, long date)
	{
		log.debug("Add date header " + name + " to " + date);

		super.addDateHeader(name, date);
	}

	/**
	 * @see HttpServletResponse#setHeader
	 */
	@Override
	public void setHeader(java.lang.String name, java.lang.String value)
	{
		log.debug("Set header " + name + " to " + value);

		super.setHeader(name, value);
	}

	/**
	 * @see HttpServletResponse#addHeader
	 */
	@Override
	public void addHeader(java.lang.String name, java.lang.String value)
	{
		log.debug("Add header " + name + " to " + value);

		super.addHeader(name, value);
	}

	/**
	 * @see HttpServletResponse#setIntHeader
	 */
	@Override
	public void setIntHeader(java.lang.String name, int value)
	{
		log.debug("Set int header " + name + " to " + value);

		super.setIntHeader(name, value);
	}

	/**
	 * @see HttpServletResponse#addIntHeader
	 */
	@Override
	public void addIntHeader(java.lang.String name, int value)
	{
		log.debug("Add int header " + name + " to " + value);

		super.addIntHeader(name, value);
	}

	/**
	 * This actually sends to the real response and flags an error condition.
	 * 
	 * @see HttpServletResponse#addIntHeader
	 */
	@Override
	public void sendError(int sc) throws IOException
	{
		log.warn("Sending error " + sc);

		super.sendError(sc);
	}

	/**
	 * This actually sends to the real response and flags an error condition.
	 * 
	 * @see HttpServletResponse#addIntHeader
	 */
	@Override
	public void sendError(int sc, java.lang.String msg) throws IOException
	{
		log.warn("Sending error " + sc + ", " + msg);

		super.sendError(sc, msg);
	}

	/**
	 * @see HttpServletResponse#setStatus
	 */
	@Override
	public void setStatus(int sc)
	{
		if (log.isDebugEnabled())
			log.debug("Setting status " + sc);

		super.setStatus(sc);
	}

	/**
	 * @see HttpServletResponse#setStatus
	 */
	@Override
	public void setStatus(int sc, java.lang.String sm)
	{
		if (log.isDebugEnabled())
			log.debug("Setting status " + sc + ", " + sm);

		super.setStatus(sc, sm);
		// :FIXME: setStatus is deprecated As of version 2.1 [thusted 2003-10-27]
	}

	/**
	 */
	protected HttpServletResponse getHttpResponse()
	{
		return (HttpServletResponse) this.getResponse();
	}
}
