/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003, Promedico ICT B.V.
 * All rights reserved.
 */
package nl.openedge.maverick.framework.velocity.tools;

/**
 * Interface for objects that can do formatting on other objects
 * @author Eelco Hillenius
 */
public interface Formatter
{
	/**
	 * format object with pattern
	 * @param object
	 * @param pattern
	 * @return
	 */
	public String format(Object object, String  pattern);
}
