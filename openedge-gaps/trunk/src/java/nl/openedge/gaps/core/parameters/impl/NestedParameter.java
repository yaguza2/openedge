/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import nl.openedge.gaps.core.RegistryException;
import nl.openedge.gaps.core.parameters.InputException;
import nl.openedge.gaps.core.parameters.Parameter;
import nl.openedge.gaps.core.parameters.ParameterConfigurationException;
import nl.openedge.gaps.core.parameters.ParameterDescriptor;
import nl.openedge.gaps.core.parameters.ParameterValue;
import nl.openedge.gaps.core.versions.Version;

/**
 * Parameter die een rij (met kolommen) representeert; dit is dus een nesting van
 * parameters.
 */
public final class NestedParameter extends Parameter implements Externalizable
{
    /** serial UUID. */
	private static final long serialVersionUID = 2826628444683510616L;

    /**
	 * Tokens die de kolommen van elkaar scheiden bij input in de vorm van een enkele
	 * string.
	 */
	public static final String COLUMN_SEP_TOKENS = "\t\n\r\f";

	/** DAO parameters. */
	private static ParameterDAO parameterDao = new ParameterDAO();

	/** id's kind-parameters. */
	private List childIds = new ArrayList();

	/** Map met local id's kind-parameters voor snelle toegang. */
	private Map mapChildLocalIds = new HashMap();

	/**
	 * Construct.
	 */
	public NestedParameter()
	{
		super();
	}

	/**
	 * Construct.
	 * @param parameters parameters
	 */
	public NestedParameter(Parameter[] parameters)
	{
		this();
		addAll(parameters);
	}

	/**
	 * @see nl.openedge.gaps.core.parameters.Parameter#getDescriptor()
	 */
	public ParameterDescriptor getDescriptor()
	{
		return new Descriptor(childIds, getVersion());
	}

	/**
	 * @see nl.openedge.gaps.core.parameters.Parameter#createValue(java.util.Map,
	 *      java.lang.String)
	 */
	public ParameterValue createValue(Map context, String valueAsString)
			throws InputException
	{

		StringTokenizer tk = new StringTokenizer(valueAsString, COLUMN_SEP_TOKENS, false);
		String[] values = new String[tk.countTokens()];
		int i = 0;
		while (tk.hasMoreTokens())
		{
			values[i++] = tk.nextToken();
		}
		return createValue(context, values);
	}

	/**
	 * @see nl.openedge.gaps.core.parameters.Parameter#createValue(java.util.Map,
	 *      java.lang.String[])
	 */
	public ParameterValue createValue(Map context, String[] valueAsString)
			throws InputException
	{

		int size = valueAsString.length;
		if (size != childIds.size())
		{
			throw new InputException("het aantal gegeven waarden ("
					+ size + ") komt niet overeen met "
					+ " het aantal verwachte waarden (" + childIds.size() + ")");
		}
		ParameterValue[] values = new ParameterValue[size];
		Parameter[] parameters = getNested();
		for (int i = 0; i < size; i++)
		{
			values[i] = parameters[i].createValue(context, valueAsString[i]);
		}

		ParameterValue nested = new NestedParameterValue(values);
		return nested;
	}

	/* ================= delegate methoden voor row ==================== */

	/**
	 * Voeg parameter toe.
	 * @param parameter toe te voegen parameter
	 * @return altijd true (contract Collection.add)
	 */
	public boolean add(Parameter parameter)
	{
		if (parameter != null)
		{
			childIds.add(parameter.getId());
			mapChildLocalIds.put(parameter.getLocalId(), parameter.getId());
		}
		return true;
	}

	/**
	 * Voeg array parameters toe.
	 * @param parameters de parameters
	 * @return altijd true (contract Collection.addAll
	 */
	public boolean addAll(Parameter[] parameters)
	{
		if (parameters != null)
		{
			int len = parameters.length;
			for (int i = 0; i < len; i++)
			{
				add(parameters[i]);
			}
		}
		return true;
	}

	/**
	 * Verwijder geneste parameters.
	 */
	public void clear()
	{
		childIds.clear();
		mapChildLocalIds.clear();
	}

	/**
	 * Check of de gegeven parameter in deze parameter is genest.
	 * @param parameter de parameter
	 * @return true indien gevonden, anders false
	 */
	public boolean contains(Parameter parameter)
	{
		return childIds.contains(parameter.getId());
	}

	/**
	 * Geeft de parameter die met de gegeven naam (localId van de parameter) is genest.
	 * @param localId naam van de geneste parameter
	 * @return de parameter of null indien niet gevonden
	 */
	public Parameter get(String localId)
	{
		String id = (String) mapChildLocalIds.get(localId);
		Parameter param = null;
		try
		{
			param = parameterDao.findParameter(id, getVersion());
		}
		catch (ParameterDAOException e)
		{
			throw new RegistryException(e);
		}
		return param;
	}

	/**
	 * Geeft alle geneste parameters als een array.
	 * @return alle geneste parameters als een array
	 */
	public Parameter[] getNested()
	{
		int len = childIds.size();
		Version version = getVersion();
		Parameter[] params = new Parameter[len];
		try
		{
			for (int i = 0; i < len; i++)
			{
				String id = (String) childIds.get(i);
				params[i] = parameterDao.findParameter(id, version);
			}
		}
		catch (ParameterDAOException e)
		{
			throw new RegistryException(e);
		}
		return params;
	}

	/**
	 * Verwijder de gegeven parameter.
	 * @param parameter de te verwijderen parameter
	 * @return true indien de parameter was gevonden
	 */
	public boolean removeNested(Parameter parameter)
	{
		if (parameter == null)
		{
			return false;
		}
		boolean result = childIds.remove(parameter.getId());
		if (result)
		{
			mapChildLocalIds.remove(parameter.getLocalId());
		}
		return result;
	}

	/**
	 * Geeft het aantal geneste parameters (alleen dit directe niveau).
	 * @return het aantal geneste parameters
	 */
	public int sizeNested()
	{
		return childIds.size();
	}

	/* ================= private klassen =============================== */

	/**
	 * Descriptor klasse.
	 */
	private static final class Descriptor extends ParameterDescriptor
	{
	    /** serial UUID. */
		private static final long serialVersionUID = 2688804199414583212L;

        /** toegestane input waarden. */
		private Object[][] possibleValues = null;

		/**
		 * Construct.
		 * @param childIds de rij
		 * @param version de versie
		 */
		public Descriptor(List childIds, Version version)
		{
			int len = childIds.size();
			Parameter[] params = new Parameter[len];
			try
			{
				for (int i = 0; i < len; i++)
				{
					String id = (String) childIds.get(i);
					params[i] = parameterDao.findParameter(id, version);
				}
			}
			catch (ParameterDAOException e)
			{
				throw new RegistryException(e);
			}
			possibleValues = new Object[len][];
			for (int i = 0; i < len; i++)
			{
				Parameter param = params[i];
				Object childsPossibleValues = param.getDescriptor().getPossibleValues();
				if (childsPossibleValues instanceof Object[][])
				{
					throw new ParameterConfigurationException(
							"een NestedParameter mag geen andere RowParameters bevatten");
				}
				else if (childsPossibleValues instanceof Object[])
				{
					possibleValues[i] = (Object[]) childsPossibleValues;
				}
				else
				{
					throw new ParameterConfigurationException(
							"onjuist type voor possibleValues gegeven ("
									+ childsPossibleValues.getClass().getName() + ")");
				}
			}
		}

		/**
		 * @see nl.openedge.gaps.core.parameters.ParameterDescriptor#getPossibleValues()
		 */
		public Object getPossibleValues()
		{
			return possibleValues;
		}
	}

	/**
	 * Get childIds.
	 * @return childIds.
	 */
	public List getChildIds()
	{
		return childIds;
	}

	/**
	 * Set childIds.
	 * @param childIds childIds.
	 */
	public void setChildIds(List childIds)
	{
		this.childIds = childIds;
	}

	/**
	 * Get mapChildLocalIds.
	 * @return mapChildLocalIds.
	 */
	public Map getMapChildLocalIds()
	{
		return mapChildLocalIds;
	}

	/**
	 * Set mapChildLocalIds.
	 * @param mapChildLocalIds mapChildLocalIds.
	 */
	public void setMapChildLocalIds(Map mapChildLocalIds)
	{
		this.mapChildLocalIds = mapChildLocalIds;
	}

	/**
	 * Custom schrijfmethode.
	 * @serialData cusom
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException
	{

		super.writeExternal(out);
		out.writeObject(getChildIds());
		out.writeObject(getMapChildLocalIds());
	}

	/**
	 * Custom leesmethode.
	 * @serialData custom
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{

		super.readExternal(in);
		setChildIds((List) in.readObject());
		setMapChildLocalIds((Map) in.readObject());
	}
}