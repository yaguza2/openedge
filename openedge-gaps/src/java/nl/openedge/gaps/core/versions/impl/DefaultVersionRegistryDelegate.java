/*
 * $Id: DefaultVersionRegistryDelegate.java,v 1.1 2004/08/12 00:10:50 hillenius
 * Exp $ $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.versions.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Transaction;
import nl.openedge.gaps.core.Entity;
import nl.openedge.gaps.core.NotFoundException;
import nl.openedge.gaps.core.RegistryException;
import nl.openedge.gaps.core.groups.ParameterGroup;
import nl.openedge.gaps.core.groups.ParameterRootGroup;
import nl.openedge.gaps.core.groups.StructuralGroup;
import nl.openedge.gaps.core.groups.StructuralRootGroup;
import nl.openedge.gaps.core.groups.impl.GroupDAO;
import nl.openedge.gaps.core.groups.impl.GroupDAOException;
import nl.openedge.gaps.core.parameters.Parameter;
import nl.openedge.gaps.core.parameters.ParameterRegistry;
import nl.openedge.gaps.core.versions.Version;
import nl.openedge.gaps.core.versions.VersionRegistryDelegate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * De standaard delegate die zal worden gebruikt indien er geen andere delegate is
 * geregistreerd bij de {@link nl.openedge.gaps.core.versions.VersionRegistry}.
 */
public final class DefaultVersionRegistryDelegate implements VersionRegistryDelegate
{

	/** Naam van evt dummy versie. */
	private static final String DUMMY_VERSIE_ID = "INITIEEL";

	/** Log. */
	private static Log log = LogFactory.getLog(DefaultVersionRegistryDelegate.class);

	/** data access object voor Version objecten. */
	private VersionDAO versionDao = new VersionDAO();

	/**
	 * Construct; laadt de bekende versies binnen en stopt ze in de cache..
	 */
	public DefaultVersionRegistryDelegate()
	{

		List temp = null;
		try
		{
			temp = versionDao.getAllVersions();
		}
		catch (VersionDAOException e)
		{
			throw new RegistryException(e);
		}
		if ( (temp != null) && (!temp.isEmpty()) )
		{
			for (Iterator i = temp.iterator(); i.hasNext();)
			{
				Version version = (Version) i.next();
			}
		}
		else
		{
			// creeer een dummy versie om het systeem consistent te kunnen laten
			// werken
			Version version = createDummyVersion();
			StructuralRootGroup root = new StructuralRootGroup();
			root.setVersion(version);
			ParameterGroup paramGroup = new ParameterRootGroup(root);
			paramGroup.setVersion(version);
			root.addParameterChild(paramGroup);
			GroupDAO groupDao = new GroupDAO();
			try
			{
				versionDao.saveVersion(version);
				// sla ze zelf op (ipv via de parameter registry)
				groupDao.saveOrUpdateGroup(root);
				groupDao.saveOrUpdateGroup(paramGroup);
				ParameterRegistry.setRootGroup(this, root);
			}
			catch (GroupDAOException e)
			{
				throw new RegistryException(e);
			}
			catch (VersionDAOException e)
			{
				throw new RegistryException(e);
			}
		}
	}

	/**
	 * Het algoritme is als volgt:
	 * <ul>
	 * <li>zoek op in cache, en bepaal of cache entry nog geldig is (of laat dit doen
	 * door een evt cache engine zoals EHCache)</li>
	 * <li>Anders, laad uit tabel op basis van de groep, ingangsdatum en goedgekeurd
	 * flag, en stop uitkomst in cache.</li>
	 * </ul>
	 * @see nl.openedge.gaps.core.versions.VersionRegistryDelegate#getCurrent(Entity)
	 */
	public Version getCurrent(Entity entity) throws NotFoundException
	{

		Version current = null;
		Date now = new Date();
		Set localVersions = getLocalVersionIds(entity);
		for (Iterator i = localVersions.iterator(); i.hasNext();)
		{
			String tempId = (String) i.next();
			Version temp = getVersion(tempId);
			if ((temp.isGoedgekeurd()) && (temp.getGeldigVanaf().before(now)))
			{
				if (current == null)
				{ // de eerst geldige
					current = temp;
				}
				else
				{ // hebben eerder een geldige gevonden; check op betere match
					if (temp.getGeldigVanaf().after(current.getGeldigVanaf()))
					{
						current = temp;
					}
				}
			} // else versie nog niet goedgekeurd of ingangsdatum na vandaag cq
			// nu
		}
		if (current == null)
		{
			throw new NotFoundException("er is geen geldige versie gevonden voor groep "
					+ entity);
		}
		return current;
	}

	/**
	 * @see nl.openedge.gaps.core.versions.VersionRegistryDelegate#createVersion(java.util.Date,
	 *      String, Entity)
	 */
	public Version createVersion(Date ingangsDatum, String naam, Entity entity)
	{

		Version version = new Version(ingangsDatum, naam);
		internalCreateVersion(entity, version);
		return version;
	}

	/**
	 * Creeer de gegeven versie.
	 * @param entity de structuurgroep
	 * @param version de versie
	 * @return mapping object
	 */
	private EntityVersions internalCreateVersion(Entity entity, Version version)
	{
		Entity workEntity = entity;
		if (workEntity == null)
		{
			workEntity = ParameterRegistry.getRootGroup();
		}
		if (log.isDebugEnabled())
		{
			log.debug("creeer nieuwe versie "
					+ version.getName() + " vanaf groep " + workEntity.getId());
		}
		try
		{
			versionDao.saveVersion(version);
		}
		catch (VersionDAOException e)
		{
			throw new RegistryException(e);
		}
		EntityVersions giv = addRegistries(workEntity, version);
		ParameterRegistry.createVersion(workEntity, version);
		return giv;
	}

	/**
	 * Voeg registraties recursief toe.
	 * @param entity huidige groep
	 * @param version toe te voegen versie
	 * @return index object
	 */
	private EntityVersions addRegistries(Entity entity, Version version)
	{

		EntityVersions giv = null;
		try
		{
			giv = versionDao.getVersions(entity);
		}
		catch (VersionDAOException e)
		{
			throw new RegistryException(e);
		}
		if (giv == null)
		{
			giv = new EntityVersions();
			giv.setEntityId(entity.getId());
			giv.setVersionIds(new HashSet());
		}
		Set localVersionIds = giv.getVersionIds();
		localVersionIds.add(version.getName());
		// recurse childs
		if (entity instanceof StructuralGroup)
		{
			StructuralGroup structGroup = (StructuralGroup) entity;
			StructuralGroup[] structChilds = structGroup.getStructuralChilds();
			if (structChilds != null)
			{
				int len = structChilds.length;
				for (int i = 0; i < len; i++)
				{
					addRegistries(structChilds[i], version);
				}
			}
			ParameterGroup[] paramChilds = structGroup.getParameterChilds();
			if (paramChilds != null)
			{
				int len = paramChilds.length;
				for (int i = 0; i < len; i++)
				{
					addRegistries(paramChilds[i], version);
				}
			}
		}
		else if (entity instanceof ParameterGroup)
		{
			ParameterGroup paramGroup = (ParameterGroup) entity;
			Parameter[] parameters = paramGroup.getParameters();
			if (parameters != null)
			{
				int len = parameters.length;
				for (int i = 0; i < len; i++)
				{
					addRegistries(parameters[i], version);
				}
			}
		}
		return giv;
	}

	/**
	 * Geeft de versie met de gegeven naam.
	 * @param name de naam van de versie
	 * @return de versie met de gegeven naam
	 */
	public Version getVersion(String name)
	{

		Version version = null;
		try
		{
			version = versionDao.getVersion(name);
		}
		catch (VersionDAOException e)
		{
			throw new RegistryException(e);
		}
		return version;
	}

	/**
	 * @see nl.openedge.gaps.core.versions.VersionRegistryDelegate#getVersions(Entity)
	 */
	public Version[] getVersions(Entity entity)
	{
		Set localVersionIds = getLocalVersionIds(entity);
		String[] ids = (String[]) localVersionIds.toArray(new String[localVersionIds
				.size()]);
		int len = ids.length;
		Version[] localVersions = new Version[len];
		for (int i = 0; i < len; i++)
		{
			localVersions[i] = getVersion(ids[i]);
		}
		return localVersions;
	}

	/**
	 * Geeft de versies voor een groep & voert null check uit.
	 * @param entity de groep
	 * @return een set met versie objecten
	 */
	private Set getLocalVersionIds(Entity entity)
	{
		Set localVersionIds = null;
		EntityVersions giv = null;
		try
		{
			giv = versionDao.getVersions(entity);
			localVersionIds = giv.getVersionIds();
		}
		catch (VersionDAOException e)
		{
			throw new RegistryException(e);
		}
		return localVersionIds;
	}

	/**
	 * Creeer een dummy versie en registreer de root group.
	 * @return dummy versie
	 */
	private Version createDummyVersion()
	{
		Version dummy = new Version();
		dummy.setName(DUMMY_VERSIE_ID);
		Date epoch = new Date(0);
		dummy.setGeldigVanaf(epoch);
		dummy.setGoedgekeurd(true);
		return dummy;
	}

	/**
	 * @see nl.openedge.gaps.core.versions.VersionRegistryDelegate#updateVersion(nl.openedge.gaps.core.versions.Version)
	 */
	public void updateVersion(Version version)
	{
		try
		{
			versionDao.updateVersion(version);
		}
		catch (VersionDAOException e)
		{
			throw new RegistryException(e);
		}
	}

	/**
	 * @see nl.openedge.gaps.core.versions.VersionRegistryDelegate#deleteVersion(nl.openedge.gaps.core.versions.Version)
	 */
	public void deleteVersion(Version version)
	{
		try
		{
			versionDao.deleteVersion(version);
		}
		catch (VersionDAOException e)
		{
			throw new RegistryException(e);
		}
	}

	/**
	 * Rollback transactie.
	 * @param tx transactie
	 */
	public static void rollback(Transaction tx)
	{
		if (tx != null)
		{
			try
			{
				tx.rollback();
			}
			catch (HibernateException e1)
			{
				log.error(e1.getMessage(), e1);
			}
		}
	}

}