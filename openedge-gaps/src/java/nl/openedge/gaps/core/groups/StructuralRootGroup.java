/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.groups;

/**
 * standaard root parametergroep.
 */
public final class StructuralRootGroup extends StructuralGroup
{
    /** serial UUID. */
	private static final long serialVersionUID = 6637125885899564646L;

    /**
	 * Construct.
	 */
	public StructuralRootGroup()
	{
		super();
		setId("/");
		setLocalId("/");
		setDescription("ROOT Structuur Groep");
	}

	/**
	 * @see nl.openedge.gaps.core.parameters.groups.Group#getPath()
	 */
	public String getPath()
	{
		return "/";
	}

	/**
	 * Get parent; altijd null voor de root.
	 * @return parent.
	 */
	public StructuralGroup getParent()
	{
		return null;
	}

	/**
	 * Geeft pad tot en met de root.
	 * @return pad tot en met de root
	 */
	public StructuralGroup[] getPathToRoot()
	{
	    return new StructuralGroup[]{this};
	}
}