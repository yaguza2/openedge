/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters;

/**
 * Metadata over een parameter.
 */
public abstract class ParameterDescriptor extends AbstractDescriptor {

    /**
     * Construct.
     */
    public ParameterDescriptor() {
        super();
    }

    /**
     * Geeft de mogelijke waarden voor deze parameter.
     * @return de mogelijke waarden voor deze parameter als een array ([]) van
     *         {@link ParameterInput}bij een enkelvoudige waarde of als een
     *         dubbele array ([][]) bij een rij waarde.
     * @see ParameterInput
     */
    public abstract Object getPossibleValues();
}