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

/**
 * De versieregistry houdt versies bij, weet wat de huidige versie is van entiteiten en is
 * in staat om een nieuwe versies aan te maken.
 */
public interface VersionRegistryDelegate
{

	/**
	 * Geeft de huidige versie voor de enteit met het gegeven id.
	 * @param entity de entiteit waarvoor de versie geldt of null voor de versie van root
	 * @return de huidige versie voor de gegeven entiteit
	 * @throws NotFoundException indien de versie niet kan worden gevonden
	 */
	Version getCurrent(Entity entity) throws NotFoundException;

	/**
	 * Geeft de versie met de gegeven naam.
	 * @param name de naam van de versie
	 * @return de versie met de gegeven naam
	 * @throws NotFoundException indien de versie niet kan worden gevonden
	 */
	Version getVersion(String name) throws NotFoundException;

	/**
	 * Geeft alle versies die bekend zijn voor de entiteit.
	 * @param entity de entiteit waarvoor alle versies dienen te worden opgehaald
	 * @return een array van versies
	 */
	Version[] getVersions(Entity entity);

	/**
	 * Maakt een nieuwe versie voor/ vanaf de gegeven groep.
	 * @param ingangsDatum de ingangsdatum van de nieuwe versie
	 * @param naam de logische naam
	 * @param entity de node vanaf waar de nieuwe versie geldig is of null voor de root
	 *            structuurgroep
	 * @return de nieuwe versie met een niet-actieve status
	 */
	Version createVersion(Date ingangsDatum, String naam, Entity entity);

	/**
	 * Sla de gegeven versie op.
	 * @param version versie op te slaan
	 */
	void updateVersion(Version version);

	/**
	 * Verwijderd de gegeven versie indien deze niet actief is, of gooit een unchecked
	 * exception indien de versie al actief is. Ofwel: een versie mag alleen worden
	 * verwijderd indien de versie nog niet actief is.
	 * @param version de te verwijderen versie
	 */
	void deleteVersion(Version version);
}