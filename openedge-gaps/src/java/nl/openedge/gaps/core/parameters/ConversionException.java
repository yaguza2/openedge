/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters;

/**
 * Exception die wordt gegooid indien aangeboden string waarde niet kan worden
 * geconverteerd naar het gewenste doel type.
 */
public class ConversionException extends InputException {

    /**
     * Construct.
     */
    public ConversionException() {
        super();
    }

    /**
     * Construct.
     * @param message message
     */
    public ConversionException(String message) {
        super(message);
    }

    /**
     * Construct.
     * @param cause cause
     */
    public ConversionException(Throwable cause) {
        super(cause);
    }

    /**
     * Construct.
     * @param message message
     * @param cause cause
     */
    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

}