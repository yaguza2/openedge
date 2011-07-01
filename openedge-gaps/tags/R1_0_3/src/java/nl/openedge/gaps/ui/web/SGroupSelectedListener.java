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

import java.util.EventListener;

/**
 * Interface voor het luisteren naar nieuwe selecties van een nieuwe structuurgroep.
 */
public interface SGroupSelectedListener extends EventListener
{
    /**
     * Listener methode die wordt afgevuurd als er een (nieuwe)
     * structuur groep is geselecteerd.
     * @param event selectie event
     */
    public void groupSelected(SGroupSelectedEvent event);
}
