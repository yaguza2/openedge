/*
 * $Id: Buffer.java,v 1.1 2001/12/21 06:50:10 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/util/Buffer.java,v $
 */

package org.infohazard.maverick.util;

import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * Simple interface to abstract out both ServletOutputStreams and PrintWriters.
 */
public interface Buffer
{
	/**
	 * True if it is more efficient to call toReader() than toString().
	 */
	public boolean prefersReader();

	/**
	 * Produces a reader of the buffered data.
	 */
	public Reader getAsReader() throws UnsupportedEncodingException;

	/**
	 * Produces the buffered data in string form.
	 */
	public String getAsString() throws UnsupportedEncodingException;

	/**
	 * @return the number of bytes or characters in the buffer.
	 */
	public int size();
}
