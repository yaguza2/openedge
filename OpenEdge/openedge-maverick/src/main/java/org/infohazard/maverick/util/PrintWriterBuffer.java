/*
 * $Id: PrintWriterBuffer.java,v 1.4 2004/06/07 20:38:42 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/util/PrintWriterBuffer.java,v $
 */

package org.infohazard.maverick.util;

import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class PrintWriterBuffer extends PrintWriter implements Buffer
{
	private static Logger log = LoggerFactory.getLogger(PrintWriterBuffer.class);

	public PrintWriterBuffer()
	{
		super(new StringWriter());
	}

	/**
	 * It's more efficient to simply go directly to String.
	 * 
	 * @see org.infohazard.maverick.util.Buffer#prefersReader()
	 */
	@Override
	public boolean prefersReader()
	{
		return false;
	}

	/**
	 * @see org.infohazard.maverick.util.Buffer#getAsReader()
	 */
	@Override
	public Reader getAsReader()
	{
		return new StringReader(this.getAsString());
	}

	/**
	 * @see org.infohazard.maverick.util.Buffer#getAsString()
	 */
	@Override
	public String getAsString()
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
	@Override
	public int size()
	{
		return ((StringWriter) this.out).getBuffer().length();
	}

	/**
	 * @see java.io.Writer#close()
	 */
	@Override
	public void close()
	{
		// Actually, do nothing... the default close sets this.out to null.
	}
}
