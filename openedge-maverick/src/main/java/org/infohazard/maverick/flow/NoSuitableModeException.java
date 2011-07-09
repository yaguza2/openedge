/*
 * $Id: NoSuitableModeException.java,v 1.1 2002/01/03 21:45:03 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/NoSuitableModeException.java,v $
 */

package org.infohazard.maverick.flow;

import javax.servlet.ServletException;

/**
 * Exception which indicates that a Shunt was unable to pick a mode based on current
 * request state.
 */
public class NoSuitableModeException extends ServletException
{
	/**
	 */
	public NoSuitableModeException()
	{
		super();
	}

	/**
	 */
	public NoSuitableModeException(String message)
	{
		super(message);
	}

	/**
	 */
	public NoSuitableModeException(String message, Throwable rootCause)
	{
		super(message, rootCause);
	}

	/**
	 */
	public NoSuitableModeException(Throwable rootCause)
	{
		super(rootCause);
	}
}
