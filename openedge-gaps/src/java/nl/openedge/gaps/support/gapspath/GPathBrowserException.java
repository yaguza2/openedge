/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.support.gapspath;

/**
 * Wordt gegooid als zich bij het interpreteren/ vertalen fouten voordoen.
 */
public final class GPathBrowserException extends UncheckedParserException {

    /**
     * Construct.
     */
    public GPathBrowserException() {
        super();
    }

    /**
     * Construct.
     * @param message message
     */
    public GPathBrowserException(String message) {
        super(message);
    }

    /**
     * Construct.
     * @param cause cause
     */
    public GPathBrowserException(Throwable cause) {
        super(cause);
    }

    /**
     * Construct.
     * @param message message
     * @param cause cause
     */
    public GPathBrowserException(String message, Throwable cause) {
        super(message, cause);
    }

}