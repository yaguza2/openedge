/*
 * $Id$ $Revision:
 * 1.5 $ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.groups;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.openedge.gaps.core.NotFoundException;
import nl.openedge.gaps.core.RegistryException;
import nl.openedge.gaps.core.parameters.Parameter;
import nl.openedge.gaps.core.parameters.ParameterRegistry;
import nl.openedge.gaps.core.parameters.impl.ParameterDAOException;
import nl.openedge.gaps.core.versions.Version;
import nl.openedge.gaps.util.NotNullArrayList;
import nl.openedge.gaps.util.NotNullHashMap;

/**
 * Een ParameterGroup wordt gebruikt om parameters te groeperen. De groep houdt alleen de
 * alleen de parameter id's bij, en weet hoe de daadwerkelijke instanties zijn op te
 * halen. De parent is altijd een StructuralGroup.
 */
public class ParameterGroup extends Group
{
	/** Parameters. */
	private transient List parameterIds = new NotNullArrayList();

	/** map voor snel opzoeken. */
	private transient Map mapIdParameterIds = new NotNullHashMap();

	/**
	 * Eventuele super; als deze is ingevuld, overerft deze groep van de super groep.
	 */
	private String superGroupId;

	/**
	 * Construct.
	 */
	public ParameterGroup()
	{
		//
	}

	/**
	 * Set parent.
	 * @param parent parent.
	 */
	public void setParent(StructuralGroup parent)
	{
		if (parent != null)
		{
			setParentId(parent.getId());
			setParentLocalId(parent.getLocalId());
		}
	}

	/**
	 * Geeft de parameters binnen deze groep.
	 * @return de parametergroepen (mogelijk array length == 0)
	 */
	public Parameter[] getParameters()
	{
		List allParams = new ArrayList(internalGetParameters());
		ParameterGroup sGroup = getSuperGroup();
		if (sGroup != null)
		{
			Parameter[] sParams = sGroup.getParameters();
			int len = sParams.length;
			for (int i = 0; i < len; i++)
			{
				if (mapIdParameterIds.get(sParams[i].getLocalId()) == null)
				{
					// alleen toevoegen indien niet in de huidige goep
					// gedefinieerd
					allParams.add(sParams[i]);
				}
			}
		}
		return (Parameter[]) allParams.toArray(new Parameter[allParams.size()]);
	}

	/**
	 * Haal de instanties op van de parameters op basis van de ids.
	 * @return de instanties van de parameters op basis van de ids
	 */
	protected List internalGetParameters()
	{

		List params = new ArrayList(parameterIds.size());
		Version version = getVersion();
		try
		{
			for (Iterator i = parameterIds.iterator(); i.hasNext();)
			{
				String id = (String) i.next();
				Parameter param = ParameterRegistry.getParameter(id, version);
				params.add(param);
			}
		}
		catch (NotFoundException e)
		{
			throw new RegistryException(e);
		}
		return params;
	}

	/**
	 * Zet de parameters.
	 * @param params de parameters
	 */
	public void setParameters(Parameter[] params)
	{
		parameterIds.clear();
		mapIdParameterIds.clear();
		addParameters(params);
	}

	/**
	 * Voeg de gegeven parameters toe.
	 * @param params de parameters
	 */

	public void addParameters(Parameter[] params)
	{
		for (int i = 0; i < params.length; i++)
		{
			addParameter(params[i]);
		}
	}

	/**
	 * Voegt een parameter toe.
	 * @param parameter toe te voegen parameter
	 */
	public void addParameter(Parameter parameter)
	{
		String id = parameter.getId();
		if (id == null)
		{
			throw new RegistryException("kan parameter "
					+ parameter + " niet toevoegen; geen id gegeven");
		}
		String localId = parameter.getLocalId();
		if (!parameterIds.contains(id))
		{
			parameterIds.add(id);
			mapIdParameterIds.put(localId, id);
		}
	}

	/**
	 * Geef een parameter van deze groep met het gegeven local id.
	 * @param localId local id van de parameter
	 * @return de parameter of null
	 */
	public Parameter getParameter(String localId)
	{

		String id = (String) mapIdParameterIds.get(localId);
		Parameter param;
		try
		{
			param = ParameterRegistry.getParameter(id, getVersion());
		}
		catch (NotFoundException e)
		{
			throw new RegistryException(e);
		}
		if ((param == null) && (superGroupId != null))
		{
			// vraag aan super
			ParameterGroup superGroup = getSuperGroup();
			param = superGroup.getParameter(localId);
		}
		return param;
	}

	/**
	 * @see nl.openedge.gaps.core.parameters.groups.Group#getPath()
	 */
	public String getPath()
	{

		String parentPath = getParentPath();
		String path = parentPath + ":" + getLocalId();
		return path;
	}

	/**
	 * @see nl.openedge.gaps.core.parameters.groups.Group#getPath()
	 */
	private String getParentPath()
	{

		String parentPath = "";
		StructuralGroup parent = getParent();
		if (parent != null)
		{
			parentPath = parent.getPath();
		}
		return parentPath;
	}

	/**
	 * Geeft superGroup; deze groep overerft van de super groep.
	 * @return superGroup de supergroep of null indien deze groep niet van een andere
	 *         groep overerft
	 */
	public ParameterGroup getSuperGroup()
	{
		ParameterGroup superGroup = null;
		if (superGroupId != null)
		{
			try
			{
				superGroup = ParameterRegistry.getParameterGroup(superGroupId,
						getVersion());
			}
			catch (NotFoundException e)
			{
				throw new RegistryException(e);
			}
		}
		return superGroup;
	}

	/**
	 * Zet superGroup; deze groep overerft van de gegeven super groep.
	 * @param superGroup superGroup de supergroep of null indien deze groep niet van een
	 *            andere groep overerft
	 */
	public void setSuperGroup(ParameterGroup superGroup)
	{
		if (superGroup != null)
		{
			this.superGroupId = superGroup.getId();
		}
	}

	/**
	 * Get mapIdParameterIds.
	 * @return mapIdParameterIds.
	 */
	public Map getMapIdParameterIds()
	{
		return mapIdParameterIds;
	}

	/**
	 * Set mapIdParameterIds.
	 * @param mapIdParameterIds mapIdParameterIds.
	 */
	protected void setMapIdParameterIds(Map mapIdParameterIds)
	{
		this.mapIdParameterIds = mapIdParameterIds;
	}

	/**
	 * Get parameterIds.
	 * @return parameterIds.
	 */
	public List getParameterIds()
	{
		return parameterIds;
	}

	/**
	 * Set parameterIds.
	 * @param parameterIds parameterIds.
	 */
	protected void setParameterIds(List parameterIds)
	{
		this.parameterIds = parameterIds;
	}

	/**
	 * Get superGroupId.
	 * @return superGroupId.
	 */
	public String getSuperGroupId()
	{
		return superGroupId;
	}

	/**
	 * Set superGroupId.
	 * @param superGroupId superGroupId.
	 */
	public void setSuperGroupId(String superGroupId)
	{
		this.superGroupId = superGroupId;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		String repr = super.toString();
		String superGID = getSuperGroupId();
		if (superGID != null)
		{
			repr = repr + " -> " + superGID;
		}
		return repr;
	}

    /** 
	 * Schrijft standaard velden weg.
	 * @param s object output stream
	 * @serialData Schrijf de default serializable velden weg
	 * @throws IOException bij schrijffouten
	 */
	private void writeObject(ObjectOutputStream s) throws IOException
	{
		s.defaultWriteObject();
	}

    /** 
	 * Schrijft standaard velden weg.
	 * @param s object input stream
	 * @serialData Lees de default serializable velden in, en vul de parameter ids
	 * @throws IOException bij leesfouten
	 */
	private void readObject(ObjectInputStream s) throws IOException,
			ClassNotFoundException
	{
		s.defaultReadObject();
		List ids;
		Map idmap;
		try
		{
			List all = parameterDao.findParameterIdsForParameterGroup(getId(), getVersion());
			ids = new ArrayList(all.size());
			idmap = new HashMap(all.size());
			for(Iterator i = all.iterator(); i.hasNext();)
			{
				Object[] struct = (Object[])i.next();
				ids.add((String)struct[0]);
				idmap.put((String)struct[1], (String)struct[0]);
			}
			setParameterIds(ids);
			setMapIdParameterIds(idmap);
		}
		catch (ParameterDAOException e)
		{
			throw new RegistryException(e);
		}
	}
}