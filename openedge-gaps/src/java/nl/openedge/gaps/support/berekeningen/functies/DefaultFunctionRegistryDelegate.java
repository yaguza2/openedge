/*
 * $Id: DefaultFunctionRegistryDelegate.java,v 1.1 2004/08/12 00:10:50 hillenius
 * Exp $ $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.support.berekeningen.functies;

import nl.openedge.gaps.core.NotFoundException;
import nl.openedge.gaps.core.RegistryException;
import nl.openedge.gaps.core.parameters.SaveException;
import nl.openedge.gaps.core.versions.Version;

/**
 * De standaard delegate die zal worden gebruikt indien er geen andere delegate
 * is geregistreerd bij de
 * {@link nl.openedge.gaps.support.berekeningen.functies.FunctionRegistry}. TODO
 * implementeer
 */
public class DefaultFunctionRegistryDelegate implements FunctionRegistryDelegate {

    /**
     * @see nl.openedge.gaps.support.berekeningen.functies.FunctionRegistryDelegate#getFunction(java.lang.String)
     */
    public Function getFunction(String id) throws NotFoundException, RegistryException {
        throw new IllegalStateException("methode nog niet geimplementeerd!");
    }

    /**
     * @see nl.openedge.gaps.support.berekeningen.functies.FunctionRegistryDelegate#getFunction(java.lang.String,
     *      nl.openedge.gaps.core.Version)
     */
    public Function getFunction(String id, Version version) throws NotFoundException,
            RegistryException {
        throw new IllegalStateException("methode nog niet geimplementeerd!");
    }

    /**
     * @see nl.openedge.gaps.support.berekeningen.functies.FunctionRegistryDelegate#saveFunction(nl.openedge.gaps.core.parameters.Function)
     */
    public void saveFunction(Function function) throws SaveException, RegistryException {
        throw new IllegalStateException("methode nog niet geimplementeerd!");
    }

    /**
     * @see nl.openedge.gaps.support.berekeningen.functies.FunctionRegistryDelegate#removeFunction(nl.openedge.gaps.core.parameters.Function)
     */
    public void removeFunction(Function function) throws SaveException {
        throw new IllegalStateException("methode nog niet geimplementeerd!");
    }

}