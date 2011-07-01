/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.groups;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import nl.openedge.gaps.core.NotFoundException;
import nl.openedge.gaps.core.RegistryException;
import nl.openedge.gaps.core.parameters.ParameterRegistry;
import nl.openedge.gaps.core.versions.Version;
import nl.openedge.gaps.util.NotNullArrayList;
import nl.openedge.gaps.util.NotNullHashMap;

/**
 * Een StructuralGroup wordt gebruikt om de hierarchie van groepen vast te leggen.
 * Bijvoorbeeld: groepen die producten representeren en een groep voor algemene,
 * productonafhankelijke parameters.
 */
public class StructuralGroup extends Group
{
    /** serial UUID. */
	private static final long serialVersionUID = -1268659553007139260L;

    /** Structural (sub)groepen. */
	private List structuralChildIds = new NotNullArrayList();

	/** map voor snel opzoeken. */
	private Map mapIdStructChildIds = new NotNullHashMap();

	/** Parametergroepen. */
	private List parameterChildIds = new NotNullArrayList();

	/** map voor snel opzoeken. */
	private Map mapIdParamChildIds = new NotNullHashMap();

	/**
	 * Construct.
	 */
	public StructuralGroup()
	{
		// niets
	}

	/**
	 * Zet parent; zet direct properties parentId en parentLocalId, en voegt deze
	 * instantie toe als child aan de parent.
	 * @param parent parent.
	 */
	public final void setParent(StructuralGroup parent)
	{

		if (parent != null)
		{
			setParentId(parent.getId());
			setParentLocalId(parent.getLocalId());
		}
	}

	/**
	 * Geeft de structural (sub)groepen.
	 * @return de subgroepen (mogelijk array length == 0)
	 */
	public StructuralGroup[] getStructuralChilds()
	{

		StructuralGroup[] groups = null;
		int len = structuralChildIds.size();
		String[] ids = (String[]) structuralChildIds
				.toArray(new String[structuralChildIds.size()]);
		groups = new StructuralGroup[len];
		Version version = getVersion();
		try
		{
			for (int i = 0; i < len; i++)
			{
				groups[i] = ParameterRegistry.getStructuralGroup(ids[i], version);
			}
		}
		catch (NotFoundException e)
		{
			throw new RegistryException(e);
		}
		return groups;
	}

	/**
	 * Geeft child van type StructuralGroup met het gegeven id indien deze bestaat.
	 * @param localChildId localIdd van kind
	 * @return group of null indien niet gevonden
	 */
	public StructuralGroup getStructuralChild(String localChildId)
	{

		StructuralGroup group = null;
		String id = (String) mapIdStructChildIds.get(localChildId);
		if (id == null)
		{
			throw new RegistryException(localChildId
					+ " is niet geregistreerd als parametergroep van "
					+ "structuurgroep " + getId());
		}
		try
		{
			group = ParameterRegistry.getStructuralGroup(id, getVersion());
		}
		catch (NotFoundException e)
		{
			throw new RegistryException(e);
		}
		return group;
	}

	/**
	 * Geeft de parametergroepen.
	 * @return de parametergroepen (mogelijk array length == 0)
	 */
	public ParameterGroup[] getParameterChilds()
	{

		int len = parameterChildIds.size();
		String[] ids = (String[]) parameterChildIds.toArray(new String[parameterChildIds
				.size()]);
		ParameterGroup[] groups = new ParameterGroup[len];
		Version version = getVersion();
		try
		{
			for (int i = 0; i < len; i++)
			{
				groups[i] = ParameterRegistry.getParameterGroup(ids[i], version);
			}
		}
		catch (NotFoundException e)
		{
			throw new RegistryException(e);
		}
		return groups;
	}

	/**
	 * Geeft child van type ParameterGroup met het gegeven id indien deze bestaat.
	 * @param localChildId localId van kind
	 * @return group of null indien niet gevonden
	 */
	public ParameterGroup getParameterChild(String localChildId)
	{

		String id = (String) mapIdParamChildIds.get(localChildId);
		if (id == null)
		{
			throw new RegistryException(localChildId
					+ " is niet geregistreerd als parametergroep van "
					+ "structuurgroep " + getId());
		}
		ParameterGroup group;
		try
		{
			group = ParameterRegistry.getParameterGroup(id, getVersion());
		}
		catch (NotFoundException e)
		{
			throw new RegistryException(e);
		}
		return group;
	}

	/**
	 * Voegt een kind cq structural subgroep toe.
	 * @param group toe te voegen (sub)groep
	 */
	public void addStructuralChild(StructuralGroup group)
	{

		String id = group.getId();
		String localId = group.getLocalId();
		if (!structuralChildIds.contains(id))
		{
			structuralChildIds.add(id);
			mapIdStructChildIds.put(localId, id);
		}
	}

	/**
	 * Voegt een kind cq parameter groep toe.
	 * @param group toe te voegen parametergroep
	 */
	public void addParameterChild(ParameterGroup group)
	{

		String id = group.getId();
		String localId = group.getLocalId();
		if (!parameterChildIds.contains(id))
		{
			parameterChildIds.add(id);
			mapIdParamChildIds.put(localId, id);
		}
	}

	/**
	 * Zet de structural (sub)groepen.
	 * @param groups de (sub)groepen
	 */
	public void setStructuralChilds(StructuralGroup[] groups)
	{

		structuralChildIds.clear();
		mapIdStructChildIds.clear();
		addStructuralChilds(groups);
	}

	/**
	 * Zet de parametergroepen.
	 * @param groups de parametergroepen
	 */
	public void setParameterChilds(ParameterGroup[] groups)
	{

		parameterChildIds.clear();
		mapIdParamChildIds.clear();
		addParameterChilds(groups);
	}

	/**
	 * Voeg de gegeven structural (sub)groepen toe aan de huidige (sub)groepen.
	 * @param groups de structural (sub)groepen
	 */

	public void addStructuralChilds(StructuralGroup[] groups)
	{

		for (int i = 0; i < groups.length; i++)
		{
			addStructuralChild(groups[i]);
		}
	}

	/**
	 * Voeg de gegeven parametergroepen toe aan de huidige parametergroepen.
	 * @param groups de parametergroepen.
	 */
	public void addParameterChilds(ParameterGroup[] groups)
	{

		for (int i = 0; i < groups.length; i++)
		{
			addParameterChild(groups[i]);
		}
	}

	/**
	 * @see nl.openedge.gaps.core.parameters.groups.Group#getPath()
	 */
	public String getPath()
	{

		String parentPath = getParentPath();
		String path = parentPath;
		if (!path.endsWith("/"))
		{
			path = path + "/";
		}
		path = path + getLocalId();
		return path;
	}

	/**
	 * Geeft het huidige pad als een array van strings waarbij ieder array element
	 * in feite een lokaal id is van een van de parents of dit element.
	 * @return huidige pad als een array van strings
	 */
	public String[] getPathParts()
	{
	    String theId = getId();
	    if(theId == null)
	    {
	        return new String[0];
	    }
	    StringTokenizer tk = new StringTokenizer(theId, "/");
	    String[] parts = new String[tk.countTokens()];
	    int i = 0;
	    while(tk.hasMoreTokens())
	    {
	        parts[i++] = tk.nextToken();
	    }
	    return parts;
	}

	/**
	 * Geeft pad tot en met de root.
	 * @return pad tot en met de root
	 */
	public StructuralGroup[] getPathToRoot()
	{
	    StructuralGroup[] parentPathToRoot = getParent().getPathToRoot();
	    StructuralGroup[] path = new StructuralGroup[parentPathToRoot.length + 1];
	    System.arraycopy(parentPathToRoot, 0, path, 0, parentPathToRoot.length);
	    path[path.length - 1] = this;
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
		} else if (this.getParentId() != null) {
		    /*
		     * Komt voor indien de versie van de structuurgroep
		     * niet overeenkomt met de versie van de parent.
		     */
		    parentPath = this.getParentId();
		}
		return parentPath;
	}

	/**
	 * Get mapIdParamChildIds.
	 * @return mapIdParamChildIds.
	 */
	public Map getMapIdParamChildIds()
	{
		return mapIdParamChildIds;
	}

	/**
	 * Set mapIdParamChildIds.
	 * @param mapIdParamChildIds mapIdParamChildIds.
	 */
	public void setMapIdParamChildIds(Map mapIdParamChildIds)
	{
		this.mapIdParamChildIds = mapIdParamChildIds;
	}

	/**
	 * Get mapIdStructChildIds.
	 * @return mapIdStructChildIds.
	 */
	public Map getMapIdStructChildIds()
	{
		return mapIdStructChildIds;
	}

	/**
	 * Set mapIdStructChildIds.
	 * @param mapIdStructChildIds mapIdStructChildIds.
	 */
	public void setMapIdStructChildIds(Map mapIdStructChildIds)
	{
		this.mapIdStructChildIds = mapIdStructChildIds;
	}

	/**
	 * Get parameterChildIds.
	 * @return parameterChildIds.
	 */
	public List getParameterChildIds()
	{
		return parameterChildIds;
	}

	/**
	 * Set parameterChildIds.
	 * @param parameterChildIds parameterChildIds.
	 */
	public void setParameterChildIds(List parameterChildIds)
	{
		this.parameterChildIds = parameterChildIds;
	}

	/**
	 * Get structuralChildIds.
	 * @return structuralChildIds.
	 */
	public List getStructuralChildIds()
	{
		return structuralChildIds;
	}

	/**
	 * Set structuralChildIds.
	 * @param structuralChildIds structuralChildIds.
	 */
	public void setStructuralChildIds(List structuralChildIds)
	{
		this.structuralChildIds = structuralChildIds;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer b = new StringBuffer(super.toString());
		b.append("[structChilds{");
		for(Iterator i = structuralChildIds.iterator(); i.hasNext();)
		{
		    String id = (String)i.next();
		    b.append(id);
		    if(i.hasNext()) b.append(",");
		}
		b.append("}]");
		b.append("[paramChilds{");
		for(Iterator i = parameterChildIds.iterator(); i.hasNext();)
		{
		    String id = (String)i.next();
		    b.append(id);
		    if(i.hasNext()) b.append(",");
		}
		b.append("}]");
		return b.toString();
	}
}