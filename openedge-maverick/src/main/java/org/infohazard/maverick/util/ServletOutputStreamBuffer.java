/*
 * $Id: ServletOutputStreamBuffer.java,v 1.2 2003/10/27 11:00:55 thusted Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/util/ServletOutputStreamBuffer.java,v $
 */

package org.infohazard.maverick.util;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;

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
	 * @param charset
	 *            - if null, default character encoding is assumed.
	 */
	public ServletOutputStreamBuffer(String charset)
	{
		this.charset = charset;
	}

	/**
	 * It's always more efficient to use a reader because the buffer need not be copied.
	 */
	@Override
	public boolean prefersReader()
	{
		return true;
	}

	/**
	 */
	@Override
	public Reader getAsReader() throws UnsupportedEncodingException
	{
		if (this.charset != null)
			return new InputStreamReader(this.holder.getInputStream(), this.charset);
		else
			return new InputStreamReader(this.holder.getInputStream());
	}

	/**
	 */
	@Override
	public String getAsString() throws UnsupportedEncodingException
	{
		if (this.charset != null)
			return this.holder.toString(this.charset);
		else
			return this.holder.toString();
	}

	/**
	 */
	@Override
	public int size()
	{
		return this.holder.size();
	}

	/**
	 * Overriden from ServletOutputStream
	 */
	@Override
	public void write(int b)
	{
		holder.write(b);
	}
}
