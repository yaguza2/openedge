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
import nl.openedge.gaps.core.versions.Version;

/**
 * Interface voor ondersteuning van strategy pattern voor {@link FunctieRegistry}.<br>
 * <p>
 * <b>LET OP: het interne contract is dat een id uniek gemaakt moet worden over meerdere
 * versie heen. Voor clients dient deze vertaling transparant te zijn. Bijvoorbeeld: stel
 * de versie == 22-12-2005, het id == 'mijnparam', het interne id kan dan zijn
 * '22122005|mijnparam'. </b>
 * </p>
 */
public interface FunctionRegistryDelegate
{

	/**
	 * Geef de functie die is geregistreerd met het gegeven id voor de huidige versie.
	 * @param id het id van de functie
	 * @return de gevonden functie; kan worden gebruikt als werkkopie
	 * @throws NotFoundException indien de paramter niet kan worden gevonden
	 * @throws RegistryException indien de registry een gevraagde actie niet naar behoren
	 *             heeft kunnen uitvoeren vanwege een onbekende/ generieke fout
	 */
	Function getFunction(String id) throws NotFoundException, RegistryException;

	/**
	 * Geef de functie die is geregistreerd met het gegeven id voor de gegeven versie.
	 * @param id het id van de functie
	 * @param version de te gebruiken versie
	 * @return de gevonden functie; kan worden gebruikt als werkkopie
	 * @throws NotFoundException indien de paramter niet kan worden gevonden
	 * @throws RegistryException indien de registry een gevraagde actie niet naar behoren
	 *             heeft kunnen uitvoeren vanwege een onbekende/ generieke fout
	 */
	Function getFunction(String id, Version version) throws NotFoundException,
			RegistryException;

	/**
	 * Sla de gegeven functie op in - evt persistente - store. LET OP: het contract is dat
	 * er direct een kopie wordt gemaakt, en dat deze kopie wordt opgeslagen ipv de
	 * gegeven referentie. Maw de snapshot van het object zoals de wordt aangeboden wordt
	 * bewaard, en evt bewerkingen op de meegegeven instantie zullen geen effect hebben op
	 * de opgeslagen functie.
	 * @param function de te bewaren functie
	 * @throws SaveException indien er zich een fout voordoet bij het opslaan
	 * @throws RegistryException indien de registry een gevraagde actie niet naar behoren
	 *             heeft kunnen uitvoeren vanwege een onbekende/ generieke fout
	 */
	void saveFunction(Function function) throws SaveException, RegistryException;

	/**
	 * Verwijder de gegeven functie indien mogelijk. Een functie kan alleen worden
	 * verwijderd als deze nog niet in gebruik is, cq niet is gekoppeld aan een reeds
	 * actieve versie.
	 * @param function de te verwijderen functie
	 * @throws SaveException indien de functie niet kan worden verwijderd.
	 */
	void removeFunction(Function function) throws SaveException;
}