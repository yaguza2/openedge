/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.groups;

import nl.openedge.gaps.core.Constants;

/**
 * standaard root parametergroep.
 */
public final class ParameterRootGroup extends ParameterGroup
{
    /** serial UUID. */
	private static final long serialVersionUID = 6322963661459651574L;

    /**
	 * Construct.
	 * @param parent de structural group.
	 */
	public ParameterRootGroup(StructuralGroup parent)
	{
		setId("/:" + Constants.ROOT_ID);
		setLocalId(Constants.ROOT_ID);
		setDescription("- group voor parameters die niet onder een structuurgroep vallen -");
		setParent(parent);
	}

}