/*
 * $Id: ParameterConfigurationException.java,v 1.1 2004/08/12 00:10:50 hillenius
 * Exp $ $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters;

import nl.openedge.gaps.core.UncheckedModelException;

/**
 * Exception die kan worden gegooid indien een parameter niet juist is
 * aangemaakt cq wordt gebruikt.
 */
public class ParameterConfigurationException extends UncheckedModelException {

    /**
     * Construct.
     */
    public ParameterConfigurationException() {
        super();
    }

    /**
     * Construct.
     * @param message message
     */
    public ParameterConfigurationException(String message) {
        super(message);
    }

    /**
     * Construct.
     * @param cause cause
     */
    public ParameterConfigurationException(Throwable cause) {
        super(cause);
    }

    /**
     * Construct.
     * @param message message
     * @param cause cause
     */
    public ParameterConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

}