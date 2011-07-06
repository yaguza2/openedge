/*
 * $Id: FastByteArrayOutputStream.java,v 1.1 2001/12/21 06:50:10 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/util/FastByteArrayOutputStream.java,v $
 */

package org.infohazard.maverick.util;

import java.io.*;

/**
 * Allows us to convert this into a ByteArrayInputStream without copying the
 * buffer.  Just make sure you are finished writing first.
 */
public class FastByteArrayOutputStream extends ByteArrayOutputStream
{
	/** You should be done writing before you call this method. */
	public ByteArrayInputStream getInputStream()
	{
		// Be careful to limit the size of the buffer otherwise you get
		// nulls at the end of the input stream.
		return new ByteArrayInputStream(this.buf, 0, this.size());
	}
}
