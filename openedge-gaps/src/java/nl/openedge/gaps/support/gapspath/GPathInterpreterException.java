/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.support.gapspath;

/**
 * Exception die kan worden gegooid indien er zaken misgaan bij het gebruiken
 * van de interpreter.
 */
public final class GPathInterpreterException extends ParserException {

    /**
     * Construct.
     */
    public GPathInterpreterException() {
        super();
    }

    /**
     * Construct.
     * @param message message
     */
    public GPathInterpreterException(String message) {
        super(message);
    }

    /**
     * Construct.
     * @param cause cause
     */
    public GPathInterpreterException(Throwable cause) {
        super(cause);
    }

    /**
     * Construct.
     * @param message message
     * @param cause cause
     */
    public GPathInterpreterException(String message, Throwable cause) {
        super(message, cause);
    }

}