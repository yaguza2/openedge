/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters;

import nl.openedge.gaps.core.Entity;
import nl.openedge.gaps.core.NotFoundException;
import nl.openedge.gaps.core.RegistryException;
import nl.openedge.gaps.core.groups.ParameterGroup;
import nl.openedge.gaps.core.groups.StructuralGroup;
import nl.openedge.gaps.core.groups.StructuralRootGroup;
import nl.openedge.gaps.core.parameters.impl.DefaultParameterRegistryDelegate;
import nl.openedge.gaps.core.versions.Version;
import nl.openedge.gaps.core.versions.VersionRegistryDelegate;

/**
 * Registry voor parameters die de daadwerkelijke persistency stategie verbergt, en
 * die, door static methodes, de toegang makkelijker/ efficienter maakt.
 */
public final class ParameterRegistry {

    /**
     * Delegate die wordt gebruikt indien er geen delegate expliciet is gezet.
     */
    private static ParameterRegistryDelegate defaultDelegate;

    /**
     * De delegate die het echte werk doet.
     */
    private static ParameterRegistryDelegate delegate;

    /**
     * Verborgen utility constructor.
     */
    private ParameterRegistry() {
        //
    }

    /**
     * Geeft de prototypes van alle geregistreerde parameters.
     * @return werkkopieen van alle geregistreerde parameters
     * @throws RegistryException indien de registry een gevraagde actie niet
     *             naar behoren heeft kunnen uitvoeren vanwege een onbekende/
     *             generieke fout
     */
    public static Parameter[] getParameterPrototypes() throws RegistryException {

        return getDelegate().getParameterPrototypes();
    }

    /**
     * Geeft prototype van de parameter van het gegeven type.
     * @param clazz het type waarvan het prototype dient te worden gegeven
     * @return een werkkopie van de parameter
     * @throws RegistryException indien de registry een gevraagde actie niet
     *             naar behoren heeft kunnen uitvoeren vanwege een onbekende/
     *             generieke fout
     */
    public static Parameter getParameterPrototype(Class clazz) throws RegistryException {

        return getDelegate().getParameterPrototype(clazz);
    }

    /**
     * Geef de parameter die is geregistreerd met het gegeven id.
     * @param id het id van de parameter
     * @return de gevonden parameter; kan worden gebruikt als werkkopie
     * @throws NotFoundException indien de parameter niet kan worden gevonden
     * @throws RegistryException indien de registry een gevraagde actie niet
     *             naar behoren heeft kunnen uitvoeren vanwege een onbekende/
     *             generieke fout
     */
    public static Parameter getParameter(String id) throws NotFoundException, RegistryException {

        return getDelegate().getParameter(id);
    }

    /**
     * Geef de parameter die is geregistreerd met het gegeven id en de gegeven
     * versie.
     * @param id het id van de parameter
     * @param version de versie van de parameter
     * @return de gevonden parameter; kan worden gebruikt als werkkopie
     * @throws NotFoundException indien de parameter niet kan worden gevonden
     * @throws RegistryException indien de registry een gevraagde actie niet
     *             naar behoren heeft kunnen uitvoeren vanwege een onbekende/
     *             generieke fout
     */
    public static Parameter getParameter(String id, Version version) throws NotFoundException,
            RegistryException {

        return getDelegate().getParameter(id, version);
    }

    /**
     * Sla de gegeven parameter op in - evt persistente - store.
     * @param parameter de te bewaren parameter
     * @throws SaveException indien er zich een fout voordoet bij het opslaan
     * @throws RegistryException indien de registry een gevraagde actie niet
     *             naar behoren heeft kunnen uitvoeren vanwege een onbekende/
     *             generieke fout
     */
    public static void saveParameter(Parameter parameter) throws SaveException, RegistryException {

        getDelegate().saveParameter(parameter);
    }

    /**
     * Verwijder de gegeven parameter indien mogelijk. Een parameter kan alleen
     * worden verwijderd als deze nog niet in gebruik is, cq niet is gekoppeld
     * aan een reeds actieve versie.
     * @param parameter de te verwijderen parameter
     * @throws SaveException TODO
     * @throws SaveException indien de parameter niet kan worden verwijderd.
     */
    public static void removeParameter(Parameter parameter) throws SaveException {

        getDelegate().removeParameter(parameter);
    }

    /**
     * Geeft de root parameter groep.
     * @return de root parameter groep
     * @throws RegistryException bij onverwachte registry fouten
     */
    public static StructuralGroup getRootGroup() throws RegistryException {
        return getDelegate().getRootGroup();
    }

    /**
     * Zet de root group; mag alleen gebeuren door version registry.
     * @param callee aanroeper (implementaties dienen additioneel een null check uit te voeren.
     * @param root de te zetten root
     */
    public static void setRootGroup(VersionRegistryDelegate callee, StructuralRootGroup root) {
        getDelegate().setRootGroup(callee, root);
    }

    /**
     * Geeft de groep met het gegeven id voor de gegeven versie.
     * @param groupId het id van de parametergroep
     * @param version de versie
     * @return instantie van groep of null
     * @throws RegistryException bij onverwachte registry fouten
     * @throws NotFoundException indien de parameter niet kan worden gevonden
     */
    public static StructuralGroup getStructuralGroup(String groupId, Version version)
            throws RegistryException, NotFoundException {

        return getDelegate().getStructuralGroup(groupId, version);
    }

    /**
     * Geeft de groep met het gegeven id.
     * @param groupId het id van de parametergroep
     * @return instantie van groep of null
     * @throws RegistryException bij onverwachte registry fouten
     * @throws NotFoundException indien de parameter niet kan worden gevonden
     */
    public static StructuralGroup getStructuralGroup(String groupId) throws RegistryException,
            NotFoundException {

        return getDelegate().getStructuralGroup(groupId);
    }

    /**
     * Geeft de groep met het gegeven id voor de gegeven versie.
     * @param groupId het id van de parametergroep
     * @param version de versie
     * @return instantie van groep of null
     * @throws RegistryException bij onverwachte registry fouten
     * @throws NotFoundException indien de parameter niet kan worden gevonden
     */
    public static ParameterGroup getParameterGroup(String groupId, Version version)
            throws RegistryException, NotFoundException {

        return getDelegate().getParameterGroup(groupId, version);
    }

    /**
     * Geeft de groep met het gegeven id.
     * @param groupId het id van de parametergroep
     * @return instantie van groep of null
     * @throws RegistryException bij onverwachte registry fouten
     * @throws NotFoundException indien de parameter niet kan worden gevonden
     */
    public static ParameterGroup getParameterGroup(String groupId) throws RegistryException,
            NotFoundException {

        return getDelegate().getParameterGroup(groupId);
    }

    /**
     * Sla de gegeven structuurgroep op in - evt persistente - store. LET OP:
     * het contract is dat er direct een kopie wordt gemaakt, en dat deze kopie
     * wordt opgeslagen ipv de gegeven referentie. Maw de snapshot van het
     * object zoals de wordt aangeboden wordt bewaard, en evt bewerkingen op de
     * meegegeven instantie zullen geen effect hebben op de opgeslagen
     * parametergroep.
     * @param group de structuurgroep die dient te worden opgeslagen
     * @throws RegistryException bij onverwachte registry fouten
     */
    public static void saveGroup(StructuralGroup group) throws RegistryException {
        getDelegate().saveGroup(group);
    }

    /**
     * Creeer een kopie van de gegeven groep (of root indien group null is)
     * en alle groepen en parameters die er onder hangen voor de gegeven versie.
     * @param entity de entiteit vanaf waar de nieuwe versie geldt
     * @param version de nieuwe versie
     * @throws RegistryException bij onverwachte fouten
     */
    public static void createVersion(Entity entity, Version version)
    	throws RegistryException {

        getDelegate().createVersion(entity, version);
    }
 
    /**
     * Sla de gegeven parametergroep op in - evt persistente - store. LET OP:
     * het contract is dat er direct een kopie wordt gemaakt, en dat deze kopie
     * wordt opgeslagen ipv de gegeven referentie. Maw de snapshot van het
     * object zoals de wordt aangeboden wordt bewaard, en evt bewerkingen op de
     * meegegeven instantie zullen geen effect hebben op de opgeslagen
     * parametergroep.
     * @param group de parametergroep die dient te worden opgeslagen
     * @throws RegistryException bij onverwachte registry fouten
     */
    public static void saveGroup(ParameterGroup group) throws RegistryException {
        getDelegate().saveGroup(group);
    }

    /**
     * Verwijder de gegeven groep.
     * @param group de te verwijderen structuur groep
     * @throws RegistryException bij onverwachte registry fouten
     * @throws NotFoundException indien de parameter niet kan worden gevonden
     */
    public static void removeGroup(StructuralGroup group) throws NotFoundException,
            RegistryException {

        getDelegate().removeGroup(group);
    }

    /**
     * Geeft delegate; indien een delegate expliciet is gezet wordt deze
     * gegeven, anders de default instantie.
     * @return the delegate.
     * @see ParameterRegistryDelegate
     */
    private static ParameterRegistryDelegate getDelegate() {
        if (delegate != null) {
            return delegate;
        } else {
            if(defaultDelegate == null) {
                defaultDelegate = new DefaultParameterRegistryDelegate();
            }
            return defaultDelegate;
        }
    }

    /**
     * Zet delegate.
     * @param delegate delegate to set.
     * @see ParameterRegistryDelegate
     */
    public static void setDelegate(ParameterRegistryDelegate delegate) {
        ParameterRegistry.delegate = delegate;
    }

}