/*
 * $Id$ $Revision$
 * $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.support.berekeningen;

/**
 * Een berekening is een houder voor een expressie. TODO afmaken
 */
public class Berekening {

    /** Id van de berekening. */
    private String id;

    /** Expressie. */
    private String expression;

    /**
     * Construct.
     * @param id id van de berekening
     * @param expression expressie
     */
    public Berekening(String id, String expression) {
        this.id = id;
        this.expression = expression;
    }

}