/*
 * $Id$ $Revision$
 * $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.support.gapspath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Structuur voor het opbouwen van een select; een select ziet er bijvoorbeeld
 * zo uit. <br/>
 * <pre>
 *  [@id='paramid' and @version='theversion']
 * </pre>
 */
public class Select {

    /**
     * Onderdelen van de select.
     */
    private List parts = new ArrayList();

    /**
     * Construct.
     */
    public Select() {
        //
    }

    /**
     * Voeg part toe.
     * @param o part
     * @return altijd true
     */
    public boolean add(Object o) {
        return parts.add(o);
    }

    /**
     * Voeg parts toe.
     * @param index op index
     * @param c parts
     * @return altijd true
     */
    public boolean addAll(int index, Collection c) {
        return parts.addAll(index, c);
    }

    /**
     * Geef part op index.
     * @param index index
     * @return Part
     */
    public Object get(int index) {
        return parts.get(index);
    }

    /**
     * Geeft index van part.
     * @param o part
     * @return index
     */
    public int indexOf(Object o) {
        return parts.indexOf(o);
    }

    /**
     * Geeft iterator.
     * @return iterator
     */
    public Iterator iterator() {
        return parts.iterator();
    }

    /**
     * Geeft list iterator.
     * @return list iterator
     */
    public ListIterator listIterator() {
        return parts.listIterator();
    }

    /**
     * Verwijder van index.
     * @param index index
     * @return verwijderde part
     */
    public Object remove(int index) {
        return parts.remove(index);
    }

    /**
     * Verwijder part.
     * @param o part
     * @return true indien part was gevonden
     */
    public boolean remove(Object o) {
        return parts.remove(o);
    }

    /**
     * Get parts.
     * @return parts.
     */
    public List getParts() {
        return parts;
    }

    /**
     * Set parts.
     * @param parts parts.
     */
    public void setParts(List parts) {
        this.parts = parts;
    }
}