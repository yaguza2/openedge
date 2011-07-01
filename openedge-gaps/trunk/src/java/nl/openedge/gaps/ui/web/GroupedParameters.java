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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.openedge.gaps.core.parameters.Parameter;
import nl.openedge.gaps.core.parameters.impl.NestedParameter;


/**
 * Groepeert de parameters in geneste/ niet geneste paramers, waarbij
 * de geneste parameter weer worden gegroepeerd naar het aantal kolommen
 * van de nesting.
 */
class GroupedParameters
{
    /** de niet-geneste parameters. */
    private List plainParams = new ArrayList();

    /** de geneste parameters. */
    private Map nestedParams = new HashMap();

    /**
     * Construct en groepeer.
     * @param parameters de te groeperen parameters
     */
    public GroupedParameters(Parameter[] parameters)
    {
        int len = parameters.length;
        for(int i = 0; i < len; i++)
        {
            add(parameters[i]);
        }
    }

    /**
     * Geeft of er helemaal geen parameters zijn gezet.
     * @return true indien er helemaal geen parameters zijn gezet, anders false
     */
    public boolean isEmpty()
    {
        return plainParams.isEmpty() && nestedParams.isEmpty();
    }

    /**
     * Voeg een parameter toe.
     * @param parameter de toe te voegen parameter
     */
    public void add(Parameter parameter)
    {
        if(!(parameter instanceof NestedParameter))
        {
            plainParams.add(parameter);
        }
        else
        {
            NestedParameter nested = (NestedParameter)parameter;
            int size = nested.getChildIds().size();
            NestedParametersWrapper wrapper =
                (NestedParametersWrapper)nestedParams.get(String.valueOf(size));
            if(wrapper == null)
            {
                wrapper = new NestedParametersWrapper(size);
                nestedParams.put(String.valueOf(size), wrapper);
            }
            wrapper.addParameter(nested);
        }
    }

    /**
     * Get nestedParams.
     * @return nestedParams.
     */
    public final Map getNestedParams()
    {
        return nestedParams;
    }
    /**
     * Set nestedParams.
     * @param nestedParams nestedParams.
     */
    public final void setNestedParams(Map nestedParams)
    {
        this.nestedParams = nestedParams;
    }
    /**
     * Get plainParams.
     * @return plainParams.
     */
    public final List getPlainParams()
    {
        return plainParams;
    }
    /**
     * Set plainParams.
     * @param plainParams plainParams.
     */
    public final void setPlainParams(List plainParams)
    {
        this.plainParams = plainParams;
    }
}