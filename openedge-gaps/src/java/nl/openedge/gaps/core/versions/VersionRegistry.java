/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.versions;


import java.util.Date;

import nl.openedge.gaps.core.Entity;
import nl.openedge.gaps.core.NotFoundException;
import nl.openedge.gaps.core.groups.StructuralGroup;
import nl.openedge.gaps.core.versions.impl.DefaultVersionRegistryDelegate;

/**
 * De versieregistry houdt versies bij, weet wat de huidige versie is en is in
 * staat om een nieuwe versie aan te maken.
 */
public final class VersionRegistry {

    /**
     * Delegate die wordt gebruikt indien er geen delegate expliciet is gezet.
     */
    private static VersionRegistryDelegate defaultDelegate;

    /**
     * De delegate die het echte werk doet.
     */
    private static VersionRegistryDelegate delegate;

    /**
     * Verborgen utility constructor.
     */
    private VersionRegistry() {
        //
    }

    /**
     * Geeft de huidige versie voor de entiteit met het gegeven id.
     * @param entity de groep waarvoor de versie geldt of null voor de versie van root
     * @return de huidige versie voor de gegeven entiteit
     * @throws NotFoundException indien de versie niet kan worden gevonden
     */
    public static Version getCurrent(Entity entity) throws NotFoundException {
        return getDelegate().getCurrent(entity);
    }

    /**
     * Geeft de versie met de gegeven naam.
     * @param name de naam van de versie
     * @return de versie met de gegeven naam
     * @throws NotFoundException indien de versie niet kan worden gevonden
     */
    public static Version getVersion(String name) throws NotFoundException {
        return getDelegate().getVersion(name);
    }

    /**
     * Geeft alle versies die bekend zijn voor de groep.
     * @param group de groep waarvoor alle versies dienen te worden opgehaald
     * @return een array van versies
     */
    public static Version[] getVersions(StructuralGroup group) {
        return getDelegate().getVersions(group);
    }

    /**
     * Maakt een nieuwe versie.
     * @param ingangsDatum de ingangsdatum van de nieuwe versie
     * @param name de logische naam
     * @param entity de groep node vanaf waar de nieuwe versie geldig is of null voor de root
     * @return de nieuwe versie met een niet-actieve status
     */
    public static Version createVersion(
            Date ingangsDatum, String name, Entity entity) {

        return getDelegate().createVersion(ingangsDatum, name, entity);
    }

    /**
     * Verwijderd de gegeven versie indien deze niet actief is, of gooit een
     * unchecked exception indien de versie al actief is. Ofwel: een versie mag
     * alleen worden verwijderd indien de versie nog niet actief is.
     * TODO ALS verwijderen wordt toegestaan, dienen ook alle samenhangende groepen
     * en parameters te worden verwijdered
     * @param version de te verwijderen versie
     */
    public static void deleteVersion(Version version) {
        getDelegate().deleteVersion(version);
    }

    /**
     * Sla de gegeven versie op.
     * @param version de te verwijderen versie
     */
    public static void updateVersion(Version version) {
       getDelegate().updateVersion(version); 
    }

    /**
     * Geeft delegate; indien een delegate expliciet is gezet wordt deze
     * gegeven, anders de default instantie.
     * @return de delegate.
     * @see VersionRegistryDelegate
     */
    private static VersionRegistryDelegate getDelegate() {
        if (delegate != null) {
            return delegate;
        } else {
            if(defaultDelegate == null) {
                defaultDelegate = new DefaultVersionRegistryDelegate();
            }
            return defaultDelegate;
        }
    }

    /**
     * Zet delegate.
     * @param delegate delegate to set.
     * @see VersionRegistryDelegate
     */
    public static void setDelegate(VersionRegistryDelegate delegate) {
        VersionRegistry.delegate = delegate;
    }
}