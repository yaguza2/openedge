/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters.impl;

import nl.openedge.gaps.core.ModelException;

/**
 * Exceptie voor {@link ParameterDAO}.
 */
public final class ParameterDAOException extends ModelException {

    /**
     * Construct.
     */
    public ParameterDAOException() {
        super();
    }

    /**
     * Construct.
     * @param message message
     */
    public ParameterDAOException(String message) {
        super(message);
    }

    /**
     * Construct.
     * @param cause cause
     */
    public ParameterDAOException(Throwable cause) {
        super(cause);
    }

    /**
     * Construct.
     * @param message message
     * @param cause cause
     */
    public ParameterDAOException(String message, Throwable cause) {
        super(message, cause);
    }

}