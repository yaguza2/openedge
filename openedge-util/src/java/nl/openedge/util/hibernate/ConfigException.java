/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c)
 * All rechten voorbehouden.
 */
package nl.openedge.util.hibernate;


/**
 * Configuration exception.
 */
public final class ConfigException extends Exception
{

    /**
     * Construct.
     */
    public ConfigException()
    {
        super();
    }

    /**
     * Construct.
     * @param message message
     */
    public ConfigException(String message)
    {
        super(message);
    }

    /**
     * Construct.
     * @param cause cause
     */
    public ConfigException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Construct.
     * @param message message
     * @param cause cause
     */
    public ConfigException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
