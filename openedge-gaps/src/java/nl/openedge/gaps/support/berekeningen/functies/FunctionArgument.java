/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.support.berekeningen.functies;

/**
 * Struct klasse met informatie over een functie argument.
 */
public final class FunctionArgument {

    /**
     * Naam argument.
     */
    private String name;

    /**
     * Type van het argument.
     */
    private Class type;

    /**
     * Construct.
     */
    public FunctionArgument() {
        // niets
    }

    /**
     * Construct.
     * @param name naam argument
     * @param type type argument
     */
    public FunctionArgument(String name, Class type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Get name.
     * @return name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set name.
     * @param name name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get type.
     * @return type.
     */
    public Class getType() {
        return type;
    }

    /**
     * Set type.
     * @param type type.
     */
    public void setType(Class type) {
        this.type = type;
    }
}