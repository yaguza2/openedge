/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.util;

import java.sql.Types;

import net.sf.hibernate.dialect.Oracle9Dialect;

/**
 * Override op het Oracle9Dialect voor databasecreatie.
 */
public class CustomOracle9Dialect extends Oracle9Dialect
{
	/** lang leve checkstyle. */
	private static final int FIELD_LENGTH = 255;

	/**
	 * Construct.
	 */
	public CustomOracle9Dialect()
	{
		super();
		registerColumnType(Types.VARBINARY, FIELD_LENGTH, "long raw");
	}

}