/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c)
 * All rechten voorbehouden.
 */
package nl.openedge.gaps.util;

import java.sql.Types;

import net.sf.hibernate.dialect.Oracle9Dialect;


/**
 * Override op het Oracle9Dialect voor databasecreatie.
 */
public class CustomOracle9Dialect extends Oracle9Dialect {

    /**
     * Construct.
     */
    public CustomOracle9Dialect() {
        super();
        registerColumnType( Types.VARBINARY, 255, "long raw" );
    }

}
