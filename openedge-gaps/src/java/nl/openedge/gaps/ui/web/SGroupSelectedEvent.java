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

import java.util.EventObject;

import nl.openedge.gaps.core.groups.StructuralGroup;

/**
 * Event dat aangeeft dat een (nieuwe) structuurgroep is geselecteerd.
 */
public final class SGroupSelectedEvent extends EventObject
{
    /** structuurgroep. */
    private StructuralGroup group;

    /**
     * Construct.
     * @param source bron event
     * @param group de geselecteerde groep
     */
    public SGroupSelectedEvent(Object source, StructuralGroup group)
    {
        super(source);
        this.group = group;
    }

    /**
     * Get group.
     * @return group.
     */
    public final StructuralGroup getGroup()
    {
        return group;
    }
}
