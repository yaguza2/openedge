/*
 * $Id$ $Revision$
 * $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;

import nl.openedge.gaps.core.Entity;
import nl.openedge.gaps.core.NotFoundException;
import nl.openedge.gaps.core.RegistryException;
import nl.openedge.gaps.core.groups.ParameterGroup;
import nl.openedge.gaps.core.parameters.impl.NestedParameter;
import nl.openedge.gaps.core.versions.VersionRegistry;

/**
 * Een parameter binnen bijvoorbeeld een berekening of model element (zoals een
 * fonds). Iedere parameter heeft een version die overeenkomt met de version van
 * het gebruikte model. Aan de version is de geldigheidsdatum en de status
 * gekoppeld. Deze abstracte klasse wordt nooit direct gebruikt, maar altijd een
 * van de overervende klassen zoals {@link BooleanParameter},
 * {@link DateParameter}of {@link FixedSetParameter}.
 */
public abstract class Parameter extends Entity implements Externalizable {

    /** de parameter groep waartoe deze parameter behoort. */
    private ParameterGroup group;

    /** de waarde. */
    private ParameterValue value;

    /** id van parent indien dit een geneste parameter is. */
    private String parentId;

    /**
     * Construct; maakt altijd een instantie aan met de huidige versie.
     * Overervende klasses zijn verplicht deze constructor aan te roepen.
     */
    public Parameter() {
        super();
    }

    /**
     * Geeft de huidige waarde van de parameter.
     * @return de huidige waarde van de parameter
     */
    public ParameterValue getValue() {
        return value;
    }

    /**
     * Zet de huidige waarde van de parameter.
     * @param value de huidige waarde van de parameter
     */
    public void setValue(ParameterValue value) {
        this.value = value;
    }

    /**
     * Geeft de groep waartoe deze parameter behoort.
     * @return de groep waartoe deze parameter behoort
     */
    public ParameterGroup getGroup() {
        return group;
    }

    /**
     * Zet de groep waartoe deze parameter behoort, en voeg de parameter toe aan
     * de gegeven groep.
     * @param group de groep waartoe deze parameter behoort
     */
    public void setGroup(ParameterGroup group) {
        this.group = group;
    }

    /**
     * Geeft de parameter descriptor voor deze parameter.
     * @return de parameter descriptor
     */
    public abstract ParameterDescriptor getDescriptor();

    /**
     * Bouwt een {@link ParameterValue}op basis van de gegeven string.
     * @param context de context
     * @param valueAsString de string op basis waarvan de {@link ParameterValue}
     *            dient te worden gebouwd
     * @return de instantie van {@link ParameterValue}die is gemaakt op basis
     *         van de gegeven string
     * @throws InputException als de waarde niet correct kan worden
     *             geconverteerd
     */
    public abstract ParameterValue createValue(Map context, String valueAsString)
            throws InputException;

    /**
     * Bouwt een {@link ParameterValue}op basis van de gegeven string array.
     * NOTE: de twee varianten van createValue dienen beide te worden
     * geimplementeerd; in een webapplicatie is het niet altijd te voorzien of
     * een parameter als een enkelvoudige string of een string array wordt
     * aangeboden. Daarnaast hebben we op deze wijze een eenvoudige manier om
     * rij/ kolom invoer te kunnen ondersteunen.
     * @param context de context
     * @param valueAsString de string op basis waarvan de {@link ParameterValue}
     *            dient te worden gebouwd
     * @return de instantie van {@link ParameterValue}die is gemaakt op basis
     *         van de gegeven string
     * @throws InputException als de waarde niet correct kan worden
     *             geconverteerd
     */
    public abstract ParameterValue createValue(Map context, String[] valueAsString)
            throws InputException;

    /**
     * Doet aanroep createValue met context met elemen van string array.
     * @param context context
     * @param valueAsString te string array met string element
     * @return ParameterValue de geconverteerde waarde als ParameterValue object
     * @throws InputException indien de input niet kan worden geconverteerd
     */
    protected ParameterValue createValueWithSingleString(Map context, String[] valueAsString)
            throws InputException {
        int length = valueAsString.length;
        if (length == 1) {
            return createValue(context, valueAsString[0]);
        } else if (length == 0) {
            return createValue(context, (String) null);
        } else {
            throw new InputException("onjuist aantal argumenten");
        }
    }

    /**
     * Get parentId.
     * @return parentId.
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * Set parentId.
     * @param parentId parentId.
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * Zet parent.
     * @param parent de parent
     */
    public void setParent(NestedParameter parent) {
        if(parent != null) {
            if(parent instanceof NestedParameter) {
                this.parentId = parent.getId();
            } else {
                throw new RegistryException(
                        "parameters van het type "
                        + parent.getClass().getName()
                        + " kunnen niet worden gebruikt voor nestingen");
            }
        }
    }

    /**
     * Geeft parent indien dit een geneste parameter is.
     * @return de parent of null indien deze parameter niet is genest
     */
    public NestedParameter getParent() {
        NestedParameter parent = null;
        String pid = getParentId();
        if(pid != null) {
            try {
                parent = (NestedParameter)ParameterRegistry.getParameter(pid);
            } catch (NotFoundException e) {
                throw new RegistryException(e);
            }
        }
        return parent;
    }

    /**
     * Custom schrijfmethode.
     * @serialData cusom
     * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal(ObjectOutput out) throws IOException {

        out.writeObject(getId());
        out.writeObject(getVersion().getName());
        out.writeObject(getGroup().getId());
        out.writeObject(getValue());
        out.writeObject(getParentId());
    }

    /**
     * Custom leesmethode.
     * @serialData custom
     * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

        setId((String) in.readObject());
        String versionId = (String) in.readObject();
        String groupId = (String) in.readObject();
        try {
            setVersion(VersionRegistry.getVersion(versionId));
            setGroup(ParameterRegistry.getParameterGroup(groupId));
            setValue((ParameterValue) in.readObject());
            setParentId((String)in.readObject());
        } catch (RegistryException e) {
            throw new RuntimeException(e);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}