/*
 * $Id: ServletOutputStreamBuffer.java,v 1.2 2003/10/27 11:00:55 thusted Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/util/ServletOutputStreamBuffer.java,v $
 */

package org.infohazard.maverick.util;

import java.io.*;
import javax.servlet.*;

/**
 */
public class ServletOutputStreamBuffer extends ServletOutputStream implements Buffer
{
	/**
	 */
	protected String charset;

	/**
	 */
	protected FastByteArrayOutputStream holder = new FastByteArrayOutputStream();

	/**
	 * @param charset - if null, default character encoding is assumed.
	 */
	public ServletOutputStreamBuffer(String charset)
	{
		this.charset = charset;
	}

	/**
	 * It's always more efficient to use a reader because the buffer need
	 * not be copied.
	 */
	public boolean prefersReader()
	{
		return true;
	}

	/**
	 */
	public Reader getAsReader() throws UnsupportedEncodingException
	{
		if (this.charset != null)
			return new InputStreamReader(this.holder.getInputStream(), this.charset);
		else
			return new InputStreamReader(this.holder.getInputStream());
	}

	/**
	 */
	public String getAsString() throws UnsupportedEncodingException
	{
		if (this.charset != null)
			return this.holder.toString(this.charset);
		else
			return this.holder.toString();
	}

	/**
	 */
	public int size()
	{
		return this.holder.size();
	}

	/**
	 * Overriden from ServletOutputStream
	 */
	public void write(int b) throws IOException
	{
		holder.write(b);
	}
}
