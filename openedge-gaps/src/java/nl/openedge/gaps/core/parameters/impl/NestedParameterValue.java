/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import nl.openedge.gaps.core.parameters.ParameterValue;

/**
 * Structuur voor het nesten van parameters.
 */
public final class NestedParameterValue extends ParameterValue {

    /** geneste parameter values. */
    private List values;

    /**
     * Construct.
     * @param pvalues te nesten parameter values
     */
    public NestedParameterValue(ParameterValue[] pvalues) {

        super(null);
        if (pvalues != null) {
            values = new ArrayList(pvalues.length);
            values.addAll(Arrays.asList(pvalues));
        }
    }

    /**
     * Construct.
     */
    public NestedParameterValue() {
        this(null);
    }

    /* ================= delegate methoden voor values ==================== */

    /**
     * @see java.util.List.add(Object)
     */
    public boolean add(Object o) {
        return values.add(o);
    }

    /**
     * @see java.util.List.addAll(int, Object)
     */
    public boolean addAll(int index, Collection c) {
        return values.addAll(index, c);
    }

    /**
     * @see java.util.List.addAll(java.util.Collection)
     */
    public boolean addAll(Collection c) {
        return values.addAll(c);
    }

    /**
     * @see java.util.List.clear()
     */
    public void clear() {
        values.clear();
    }

    /**
     * @see java.util.List.contains(Object)
     */
    public boolean contains(Object o) {
        return values.contains(o);
    }

    /**
     * @see java.util.List.get(int)
     */
    public Object get(int index) {
        return values.get(index);
    }

    /**
     * @see java.util.List.indexOf(Object)
     */
    public int indexOf(Object o) {
        return values.indexOf(o);
    }

    /**
     * @see java.util.List.iterator()
     */
    public Iterator iterator() {
        return values.iterator();
    }

    /**
     * @see java.util.List.remove(int)
     */
    public Object remove(int index) {
        return values.remove(index);
    }

    /**
     * @see java.util.List.remove(Object)
     */
    public boolean remove(Object o) {
        return values.remove(o);
    }

    /**
     * @see java.util.List.removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection c) {
        return values.removeAll(c);
    }

    /**
     * @see java.util.List.set(int, Object)
     */
    public Object set(int index, Object element) {
        return values.set(index, element);
    }

    /**
     * @see java.util.List.size()
     */
    public int size() {
        return values.size();
    }

    /**
     * @see java.util.List.toArray(Object[])
     */
    public Object[] toArray(Object[] a) {
        return values.toArray(a);
    }
}