/*
 * $Id$ $Revision$
 * $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.support.berekeningen.functies;

import java.util.Map;

import nl.openedge.gaps.core.Entity;

/**
 * Functie die kan worden gebruikt in berekeningen.
 */
public abstract class Function extends Entity {

    /**
     * Construct; maakt altijd een instantie aan met de huidige versie.
     * Overervende klasses zijn verplicht deze constructor aan te roepen.
     */
    public Function() {
        super();
    }

    /**
     * Voer de functie uit met de gegeven context en parameters.
     * @param context de context
     * @param arguments de functie parameters
     * @return functieresultaat
     */
    public abstract Object perform(Map context, Object[] arguments);

    /**
     * Geeft de functie descriptor voor deze functie.
     * @return de functie descriptor
     */
    public abstract FunctionDescriptor getDescriptor();
}