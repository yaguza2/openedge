/*
 * $Id$ $Revision:
 * 1.1 $ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Een ParameterValue houdt de werkelijke waarde van een parameter vast, en kan
 * tevens een vrije set attributen bijhouden (bijvoorbeeld voor logging
 * doeleinden).
 */
public class ParameterValue implements Cloneable, Serializable {

    /**
     * De waarde.
     */
    protected Object value;

    /**
     * Attributen.
     */
    protected Map attributes = new HashMap(0);

    /**
     * Construct.
     * @param value de waarde
     */
    public ParameterValue(Object value) {
        this.value = value;
    }

    /**
     * Geeft de werkelijke (onderliggende) waarde.
     * @return de werkelijke (onderliggende) waarde
     */
    public Object getValue() {
        return value;
    }

    /**
     * Zet een attribuut met de gegeven key en value.
     * @param key key attribuut
     * @param value waarde attribuut
     * @return de vorige waarde bij de gegeven key of null indien er geen was
     */
    public Object put(Object key, Object value) {
        return attributes.put(key, value);
    }

    /**
     * Voeg de gegeven attributen toe aan de huidige attributen.
     * @param attributes toe te voegen attributen
     */
    public void putAll(Map attributes) {
        attributes.putAll(attributes);
    }

    /**
     * Geef attribuut waarde.
     * @param key key attribuut
     * @return waarde attribuut
     */
    public Object get(Object key) {
        return attributes.get(key);
    }

    /**
     * Geef attributen.
     * @return attributen
     */
    public Map getAttributes() {
        return attributes;
    }

    /**
     * Vervang de huidige attributen met de gegeven attributen.
     * @param attributes de nieuwe attributen
     */
    public void setAttributes(Map attributes) {

    }

    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}