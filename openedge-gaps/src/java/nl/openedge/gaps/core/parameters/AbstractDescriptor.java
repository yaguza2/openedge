/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters;

import java.io.Serializable;

/**
 * Baseclass voor descriptors.
 */
public abstract class AbstractDescriptor implements Cloneable, Serializable {

    /**
     * Construct.
     */
    public AbstractDescriptor() {
        //
    }

    /** Postfix voor gebruik in resources voor de description. */
    private static final String DESCRIPTION_POSTFIX = ".descr";

    /** Postfix voor gebruik in resources voor de display name. */
    private static final String NAME_POSTFIX = ".name";

    /**
     * @see nl.openedge.gaps.core.parameters.ParameterDescriptor#getDescription()
     */
    public String getDescription() {
        return ParameterResources.getText(getDescriptionKey());
    }

    /**
     * @see nl.openedge.gaps.core.parameters.ParameterDescriptor#getDisplayName()
     */
    public String getDisplayName() {
        return ParameterResources.getText(getNameKey());
    }

    /**
     * Geeft de resource bundle key voor description.
     * @return de resource bundle key voor description
     */
    protected String getDescriptionKey() {
        return getPrefix() + DESCRIPTION_POSTFIX;
    }

    /**
     * Geeft de resource bundle key voor display name.
     * @return de resource bundle key voor display name
     */
    protected String getNameKey() {
        return getPrefix() + NAME_POSTFIX;
    }

    /**
     * Geeft prefix voor resource bundle key.
     * @return prefix voor resource bundle key
     */
    private String getPrefix() {
        return getClass().getName();
    }
}