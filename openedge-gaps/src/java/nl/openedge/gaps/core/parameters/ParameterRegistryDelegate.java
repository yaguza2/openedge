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
import nl.openedge.gaps.core.versions.Version;
import nl.openedge.gaps.core.versions.VersionRegistryDelegate;

/**
 * Interface voor ondersteuning van strategy pattern voor {@link ParameterRegistry}.<br>
 * <p>
 * <b>LET OP: het interne contract is dat een id uniek gemaakt moet worden over meerdere
 * versie heen. Voor clients dient deze vertaling transparant te zijn. Bijvoorbeeld: stel
 * de versie == 22-12-2005, het id == 'mijnparam', het interne id kan dan zijn
 * '22122005|mijnparam'. </b>
 * </p>
 */
public interface ParameterRegistryDelegate
{

	/**
	 * Geeft de prototypes van alle geregistreerde parameters.
	 * @return werkkopieen van alle geregistreerde parameters
	 * @throws RegistryException indien de registry een gevraagde actie niet naar behoren
	 *             heeft kunnen uitvoeren vanwege een onbekende/ generieke fout
	 */
	Parameter[] getParameterPrototypes() throws RegistryException;

	/**
	 * Geeft prototype van de parameter van het gegeven type.
	 * @param clazz het type waarvan het prototype dient te worden gegeven
	 * @return een werkkopie van de parameter
	 * @throws RegistryException indien de registry een gevraagde actie niet naar behoren
	 *             heeft kunnen uitvoeren vanwege een onbekende/ generieke fout
	 */
	Parameter getParameterPrototype(Class clazz) throws RegistryException;

	/**
	 * Geef de parameter die is geregistreerd met het gegeven id voor de huidige versie.
	 * @param id het id van de parameter
	 * @return de gevonden parameter; kan worden gebruikt als werkkopie
	 * @throws NotFoundException indien de paramter niet kan worden gevonden
	 * @throws RegistryException indien de registry een gevraagde actie niet naar behoren
	 *             heeft kunnen uitvoeren vanwege een onbekende/ generieke fout
	 */
	Parameter getParameter(String id) throws NotFoundException, RegistryException;

	/**
	 * Geef de parameter die is geregistreerd met het gegeven id voor de gegeven versie.
	 * @param id het id van de parameter
	 * @param version de te gebruiken versie
	 * @return de gevonden parameter; kan worden gebruikt als werkkopie
	 * @throws NotFoundException indien de paramter niet kan worden gevonden
	 * @throws RegistryException indien de registry een gevraagde actie niet naar behoren
	 *             heeft kunnen uitvoeren vanwege een onbekende/ generieke fout
	 */
	Parameter getParameter(String id, Version version) throws NotFoundException,
			RegistryException;

	/**
	 * Sla de gegeven parameter op in - evt persistente - store. LET OP: het contract is
	 * dat er direct een kopie wordt gemaakt, en dat deze kopie wordt opgeslagen ipv de
	 * gegeven referentie. Maw de snapshot van het object zoals de wordt aangeboden wordt
	 * bewaard, en evt bewerkingen op de meegegeven instantie zullen geen effect hebben op
	 * de opgeslagen parameter.
	 * @param parameter de te bewaren parameter
	 * @throws SaveException indien er zich een fout voordoet bij het opslaan
	 * @throws RegistryException indien de registry een gevraagde actie niet naar behoren
	 *             heeft kunnen uitvoeren vanwege een onbekende/ generieke fout
	 */
	void saveParameter(Parameter parameter)
		throws SaveException, RegistryException;

	/**
	 * Verwijder de gegeven parameter indien mogelijk. Een parameter kan alleen worden
	 * verwijderd als deze nog niet in gebruik is, cq niet is gekoppeld aan een reeds
	 * actieve versie.
	 * @param parameter de te verwijderen parameter
	 * @throws SaveException indien de parameter niet kan worden verwijderd.
	 */
	void removeParameter(Parameter parameter) throws SaveException;

	/**
	 * Geeft de root parameter groep.
	 * @return de root parameter groep
	 * @throws RegistryException bij onverwachte registry fouten
	 */
	StructuralGroup getRootGroup() throws RegistryException;

	/**
	 * Zet de root group; mag alleen gebeuren door version registry.
	 * @param callee aanroeper (implementaties dienen additioneel een null check uit te
	 *            voeren.
	 * @param root de te zetten root
	 */
	void setRootGroup(VersionRegistryDelegate callee, StructuralRootGroup root);

	/**
	 * Geeft de groep met het gegeven id voor de gegeven versie.
	 * @param groupId het id van de parametergroep
	 * @param version de versie
	 * @return groep of null
	 * @throws RegistryException bij onverwachte registry fouten
	 * @throws NotFoundException indien de parameter niet kan worden gevonden
	 */
	StructuralGroup getStructuralGroup(String groupId, Version version)
			throws RegistryException, NotFoundException;

	/**
	 * Geeft de structuur of parametergroep met het gegeven id.
	 * @param groupId het id van de parametergroep
	 * @return instantie van groep of null
	 * @throws RegistryException bij onverwachte registry fouten
	 * @throws NotFoundException indien de parameter niet kan worden gevonden
	 */
	StructuralGroup getStructuralGroup(String groupId)
			throws RegistryException, NotFoundException;

	/**
	 * Geeft de groep met het gegeven id voor de gegeven versie.
	 * @param groupId het id van de parametergroep
	 * @param version de versie
	 * @return groep of null
	 * @throws RegistryException bij onverwachte registry fouten
	 * @throws NotFoundException indien de parameter niet kan worden gevonden
	 */
	ParameterGroup getParameterGroup(String groupId, Version version)
			throws RegistryException, NotFoundException;

	/**
	 * Geeft de structuur of parametergroep met het gegeven id.
	 * @param groupId het id van de parametergroep
	 * @return instantie van groep of null
	 * @throws RegistryException bij onverwachte registry fouten
	 * @throws NotFoundException indien de parameter niet kan worden gevonden
	 */
	ParameterGroup getParameterGroup(String groupId)
		throws RegistryException, NotFoundException;

	/**
	 * Sla de gegeven structuur groep op in - evt persistente - store. LET OP: het
	 * contract is dat er direct een kopie wordt gemaakt, en dat deze kopie wordt
	 * opgeslagen ipv de gegeven referentie. Maw de snapshot van het object zoals de wordt
	 * aangeboden wordt bewaard, en evt bewerkingen op de meegegeven instantie zullen geen
	 * effect hebben op de opgeslagen groep.
	 * @param group de groep die dient te worden opgeslagen
	 * @throws RegistryException bij onverwachte registry fouten
	 */
	void saveGroup(StructuralGroup group)
		throws RegistryException;

	/**
	 * Sla de gegeven parameter groep op in - evt persistente - store. LET OP: het
	 * contract is dat er direct een kopie wordt gemaakt, en dat deze kopie wordt
	 * opgeslagen ipv de gegeven referentie. Maw de snapshot van het object zoals de wordt
	 * aangeboden wordt bewaard, en evt bewerkingen op de meegegeven instantie zullen geen
	 * effect hebben op de opgeslagen groep.
	 * @param group de groep die dient te worden opgeslagen
	 * @throws RegistryException bij onverwachte registry fouten
	 */
	void saveGroup(ParameterGroup group)
		throws RegistryException;

	/**
	 * Creeer een kopie van de gegeven groep (of root indien group null is) en alle
	 * groepen en parameters die er onder hangen voor de gegeven versie.
	 * @param entity de entiteit vanaf waar de nieuwe versie geldt
	 * @param version de nieuwe versie
	 * @throws RegistryException bij onverwachte fouten
	 */
	void createVersion(Entity entity, Version version) throws RegistryException;

	/**
	 * Verwijder de gegeven structuur groep.
	 * @param group de te verwijderen groep
	 * @throws RegistryException bij onverwachte registry fouten
	 * @throws NotFoundException indien de parameter niet kan worden gevonden
	 */
	void removeGroup(StructuralGroup group)
		throws NotFoundException, RegistryException;

}