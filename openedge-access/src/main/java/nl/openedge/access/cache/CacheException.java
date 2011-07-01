/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2005, Topicus B.V.
 * All rights reserved.
 */
package nl.openedge.access.cache;


/**
 * @author marrink
 *
 */
public class CacheException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public CacheException()
	{
		super();
	}

	/**
	 * @param message
	 */
	public CacheException(String message)
	{
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CacheException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public CacheException(Throwable cause)
	{
		super(cause);
	}

}
