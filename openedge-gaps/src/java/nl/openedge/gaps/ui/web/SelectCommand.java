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

import java.io.Serializable;

import javax.swing.event.EventListenerList;

import nl.openedge.gaps.core.groups.StructuralGroup;
import nl.openedge.gaps.support.ParameterBrowser;

/**
 * Command voor het doen van een selectie op het parameter framework.
 */
public final class SelectCommand implements Serializable
{
    /** parameter browser. */
    private transient final ParameterBrowser browser = new ParameterBrowser();

    /** list met event listeners. */
    private transient final EventListenerList listenerList = new EventListenerList();

    /**
     * Construct.
     */
    public SelectCommand()
    {
        
    }

    /**
     * Executeert expressie met {@link ParameterBrowser} en geeft resultaat terug.
     * @param expr gaps expressie
     * @return resultaat expressie
     */
    public Object execute(String expr)
    {
        Object result = browser.evaluate(expr);
        dispatchEvents(result);
        return result;
    }

    /**
     * Kijk of er events zijn af te vuren.
     * @param result resultaat execute
     */
    private void dispatchEvents(Object result)
    {
        if(result instanceof StructuralGroup)
        {
            fireSGroupSelectedEvent((StructuralGroup)result);
        }
    }

    /**
     * Vuur event af voor structuurgroep selectie
     * @param group de geselecteerde groep
     */
    protected void fireSGroupSelectedEvent(StructuralGroup group)
    {
        Object[] listeners = listenerList.getListenerList();
        int len = listeners.length;
        SGroupSelectedEvent event = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == SGroupSelectedListener.class)
            {
                // Lazily create the event:
                if (event == null)
                {
                    event = new SGroupSelectedEvent(this, group);
                }
                ((SGroupSelectedListener)listeners[i + 1]).groupSelected(event);
            }
        }
    }
    /**
     * Voeg listener toe.
     * @param listener listener
     */
    public void addSGroupSelectedListener(SGroupSelectedListener listener)
    {
        listenerList.add(SGroupSelectedListener.class, listener);
    }

    /**
     * Verwijder listener.
     * @param listener listener
     */
    public void removeSGroupSelectedListener(SGroupSelectedListener listener)
    {
        listenerList.remove(SGroupSelectedListener.class, listener);
    }
}
