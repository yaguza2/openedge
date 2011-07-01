/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.util;

import java.sql.Types;

import net.sf.hibernate.dialect.HSQLDialect;

/**
 * Override op het Oracle9Dialect voor databasecreatie.
 */
public class CustomHSQLDialect extends HSQLDialect
{

	/**
	 * Construct.
	 */
	public CustomHSQLDialect()
	{
		super();
		registerColumnType(Types.VARBINARY, "varbinary");
	}

}