/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.support.berekeningen;

/**
 * Wordt gegooid als zich bij het interpreteren/ vertalen fouten voordoen.
 */
public final class BerekeningSolverException extends UncheckedParserException {

    /**
     * Construct.
     */
    public BerekeningSolverException() {
        super();
    }

    /**
     * Construct.
     * @param message message
     */
    public BerekeningSolverException(String message) {
        super(message);
    }

    /**
     * Construct.
     * @param cause cause
     */
    public BerekeningSolverException(Throwable cause) {
        super(cause);
    }

    /**
     * Construct.
     * @param message message
     * @param cause cause
     */
    public BerekeningSolverException(String message, Throwable cause) {
        super(message, cause);
    }

}