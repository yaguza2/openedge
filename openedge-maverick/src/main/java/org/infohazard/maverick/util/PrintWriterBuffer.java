/*
 * $Id: PrintWriterBuffer.java,v 1.4 2004/06/07 20:38:42 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/util/PrintWriterBuffer.java,v $
 */

package org.infohazard.maverick.util;

import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class PrintWriterBuffer extends PrintWriter implements Buffer
{
	/**
	 * Logger.
	 */
	private static Logger log = LoggerFactory.getLogger(PrintWriterBuffer.class);

	/**
	 * Construct.
	 */
	public PrintWriterBuffer()
	{
		super(new StringWriter());
	}

	/**
	 * It's more efficient to simply go directly to String.
	 * 
	 * @see org.infohazard.maverick.util.Buffer#prefersReader()
	 */
	public boolean prefersReader()
	{
		return false;
	}

	/**
	 * @see org.infohazard.maverick.util.Buffer#getAsReader()
	 */
	public Reader getAsReader() throws UnsupportedEncodingException
	{
		return new StringReader(this.getAsString());
	}

	/**
	 * @see org.infohazard.maverick.util.Buffer#getAsString()
	 */
	public String getAsString() throws UnsupportedEncodingException
	{
		if (this.size() == 0)
		{
			log.debug("size() was 0, returning empty string");
			return "";
		}
		else
			return this.out.toString();
	}

	/**
	 * @see org.infohazard.maverick.util.Buffer#size()
	 */
	public int size()
	{
		return ((StringWriter) this.out).getBuffer().length();
	}

	/**
	 * @see java.io.Writer#close()
	 */
	public void close()
	{
		// Actually, do nothing... the default close sets this.out to null.
	}
}
