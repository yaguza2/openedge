/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.support.berekeningen.functies;

import nl.openedge.gaps.core.NotFoundException;
import nl.openedge.gaps.core.RegistryException;
import nl.openedge.gaps.core.parameters.SaveException;

/**
 * Registry voor de beschikbare functie types. Deze registry helpt ons de
 * beheersinterface generiek te kunnen opzetten.
 */
public final class FunctionRegistry {

    /**
     * Delegate die wordt gebruikt indien er geen delegate expliciet is gezet.
     */
    private static final FunctionRegistryDelegate DEFAULT_DELEGATE = new DefaultFunctionRegistryDelegate();

    /**
     * De delegate die het echte werk doet.
     */
    private static FunctionRegistryDelegate delegate;

    /**
     * Verborgen utility constructor.
     */
    private FunctionRegistry() {
        //
    }

    /**
     * Geef de functie die is geregistreerd met het gegeven id.
     * @param id het id van de functie
     * @return de gevonden functie; kan worden gebruikt als werkkopie
     * @throws NotFoundException indien de paramter niet kan worden gevonden
     * @throws RegistryException indien de registry een gevraagde actie niet
     *             naar behoren heeft kunnen uitvoeren vanwege een onbekende/
     *             generieke fout
     */
    public static Function getFunction(String id) throws NotFoundException, RegistryException {

        return getDelegate().getFunction(id);
    }

    /**
     * Sla de gegeven functie op in - evt persistente - store.
     * @param function de te bewaren functie
     * @throws SaveException indien er zich een fout voordoet bij het opslaan
     * @throws RegistryException indien de registry een gevraagde actie niet
     *             naar behoren heeft kunnen uitvoeren vanwege een onbekende/
     *             generieke fout
     */
    public static void saveFunction(Function function) throws SaveException, RegistryException {

        getDelegate().saveFunction(function);
    }

    /**
     * Verwijder de gegeven functie indien mogelijk. Een functie kan alleen
     * worden verwijderd als deze nog niet in gebruik is, cq niet is gekoppeld
     * aan een reeds actieve versie.
     * @param function de te verwijderen functie
     * @throws SaveException indien de functie niet kan worden verwijderd.
     */
    public static void removeFunction(Function function) throws SaveException {

        getDelegate().removeFunction(function);
    }

    /**
     * Geeft delegate; indien een delegate expliciet is gezet wordt deze
     * gegeven, anders de default instantie.
     * @return the delegate.
     * @see FunctionRegistryDelegate
     */
    private static FunctionRegistryDelegate getDelegate() {
        if (delegate != null) {
            return delegate;
        } else {
            return DEFAULT_DELEGATE;
        }
    }

    /**
     * Zet delegate.
     * @param delegate delegate to set.
     * @see FunctionRegistryDelegate
     */
    public static void setDelegate(FunctionRegistryDelegate delegate) {
        FunctionRegistry.delegate = delegate;
    }
}