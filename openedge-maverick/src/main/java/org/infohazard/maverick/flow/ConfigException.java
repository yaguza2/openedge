/*
 * $Id: ConfigException.java,v 1.1 2001/11/30 02:03:39 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/ConfigException.java,v $
 */

package org.infohazard.maverick.flow;

import javax.servlet.ServletException;

/**
 * Exception which indicates a failure during configuration of Maverick.
 */
public class ConfigException extends ServletException
{
	/**
	 */
	public ConfigException()
	{
		super();
	}

	/**
	 */
	public ConfigException(String message)
	{
		super(message);
	}

	/**
	 */
	public ConfigException(String message, Throwable rootCause)
	{
		super(message, rootCause);
	}

	/**
	 */
	public ConfigException(Throwable rootCause)
	{
		super(rootCause);
	}
}
