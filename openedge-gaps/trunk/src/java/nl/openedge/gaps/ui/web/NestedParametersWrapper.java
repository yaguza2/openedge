/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c)
 * All rechten voorbehouden.
 */
package nl.openedge.gaps.ui.web;

import java.util.ArrayList;

import nl.openedge.gaps.core.parameters.impl.NestedParameter;


/**
 * Wrapper klasse voor geneste parameters. De wrapper groepeert
 * de geneste parameters met hetzelfde aantal kolommen.
 */
public class NestedParametersWrapper extends ArrayList
{
    /** het aantal kolommen. */
    private int cols;

    /**
     * Construct.
     * @param cols het aantal kolommen
     */
    public NestedParametersWrapper(int cols)
    {
        this.cols = cols;
    }

    /**
     * voeg een geneste parameter toe.
     * @param param de toe te voegen geneste parameter
     */
    public void addParameter(NestedParameter param)
    {
        add(param);
    }
}