/*
 * $Id$ $Revision$
 * $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.util;

import nl.openedge.gaps.core.Entity;
import nl.openedge.gaps.core.groups.Group;
import nl.openedge.gaps.core.parameters.Parameter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility klasse voor werken met (parameter-) entiteiten.
 */
public final class EntityUtil
{

	/** Log. */
	private static Log log = LogFactory.getLog(EntityUtil.class);

	/**
	 * Verborgen utility constructor.
	 */
	private EntityUtil()
	{
		//
	}

	/**
	 * Maak een nieuw id voor de gegeven entiteit. NOTE: zorg dat de benodigde properties
	 * (localId etc) zijn gezet voordat deze functie wordt gebruikt
	 * @param entity de entiteit waarvoor een id dient te worden gemaakt
	 * @return een nieuw id
	 */
	public static String createId(Entity entity)
	{

		String id = null;
		if (entity == null)
		{
			throw new IllegalArgumentException("entiteit dient te zijn gegeven!");
		}
		if (entity.getLocalId() == null)
		{
			throw new IllegalArgumentException("entiteit dient een naam te hebben");
		}
		if (entity instanceof Group)
		{
			Group group = (Group) entity;
			String path = group.getPath();
			id = path;

		}
		else if (entity instanceof Parameter)
		{
			Parameter param = (Parameter) entity;
			Group group = param.getParameterGroup();
			String path = group.getPath();
			String localId = param.getLocalId();
			if (param.getParentId() != null)
			{
				// voor nu: 1 niveau diep... mogelijk - ooit - meer niveau's
				// ondersteunen?
				id = path + "/" + param.getParentLocalId() + "|" + localId;
			}
			else
			{
				id = path + "/" + localId;
			}
		}
		if (log.isDebugEnabled())
		{
			if (entity instanceof Group)
			{
				log.debug("GROUP id: " + id);
			}
			else if (entity instanceof Parameter)
			{
				log.debug("PARAMETER id: " + id);
			}
		}
		return id;
	}
}