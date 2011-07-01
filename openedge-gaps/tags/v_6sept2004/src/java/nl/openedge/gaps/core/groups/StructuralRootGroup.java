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
}