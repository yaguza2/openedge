/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003, Promedico ICT B.V.
 * All rights reserved.
 */
package nl.openedge.util.test;

import java.util.Calendar;

import nl.openedge.util.baritus.validators.AfterValidator;
import nl.openedge.util.baritus.validators.BeforeValidator;
import junit.framework.TestCase;

/**
 * Testen van before en after validators
 * 
 * @author kbrand
 */
public class ValidatorTest extends TestCase
{
	public ValidatorTest(String name)
	{
		super(name);		
	}

	public void testBeforeValidator() throws Exception
	{
		BeforeValidator bv = new BeforeValidator();
		Calendar cal = Calendar.getInstance();

		// gisteren
		cal.roll( Calendar.DAY_OF_YEAR, -1);
		assertTrue( bv.isValid( null, null, "datum", cal));

		// vandaag
		cal.roll( Calendar.DAY_OF_YEAR, 1);
		assertTrue( bv.isValid( null, null, "datum", cal));

		// morgen
		cal.roll( Calendar.DAY_OF_YEAR, 1);
		assertFalse( bv.isValid( null, null, "datum", cal));
	}

	public void testAfterValidator() throws Exception
	{
		AfterValidator av = new AfterValidator();
		Calendar cal = Calendar.getInstance();

		// gisteren
		cal.roll( Calendar.DAY_OF_YEAR, -1);
		assertFalse( av.isValid( null, null, "datum", cal));

		// vandaag
		cal.roll( Calendar.DAY_OF_YEAR, 1);
		assertTrue( av.isValid( null, null, "datum", cal));

		// morgen
		cal.roll( Calendar.DAY_OF_YEAR, 1);
		assertTrue( av.isValid( null, null, "datum", cal));
	}
}
