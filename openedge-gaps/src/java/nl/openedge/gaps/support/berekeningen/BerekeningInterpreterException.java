/*
 * $Id: BerekeningInterpreterException.java,v 1.1 2004/08/12 00:10:50 hillenius
 * Exp $ $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.support.berekeningen;

/**
 * Exception die kan worden gegooid indien er zaken misgaan bij het gebruiken
 * van de interpreter.
 */
public final class BerekeningInterpreterException extends ParserException {

    /**
     * Construct.
     */
    public BerekeningInterpreterException() {
        super();
    }

    /**
     * Construct.
     * @param message message
     */
    public BerekeningInterpreterException(String message) {
        super(message);
    }

    /**
     * Construct.
     * @param cause cause
     */
    public BerekeningInterpreterException(Throwable cause) {
        super(cause);
    }

    /**
     * Construct.
     * @param message message
     * @param cause cause
     */
    public BerekeningInterpreterException(String message, Throwable cause) {
        super(message, cause);
    }

}