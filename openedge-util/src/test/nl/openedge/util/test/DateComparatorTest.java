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

import java.util.Date;

import nl.openedge.util.DateComparator;
import nl.openedge.util.DateHelper;
import junit.framework.TestCase;

/**
 * Test the DateComparator in different situations
 * 
 * @author hofstee
 */
public class DateComparatorTest extends TestCase
{
	private DateComparator comparator = null;
	
	/**
	 * Create a DateComparatorTest with name.
	 * @param name the name of the test.
	 */
	public DateComparatorTest(String name)
	{
		super(name);
	}
	
	/**
	 * Creates a new DateComparator.
	 */
	protected void setUp()
	{
		comparator = new DateComparator();
	}
	
	/**
	 * Test comparing dates when the Dates are equal.
	 */
	public void testEquals()
	{
		Date date1 = new Date();
		Date date2 = (Date)date1.clone();
		assertEquals(0, comparator.compare(date1, date2));
		date2 = DateHelper.addHours(date2, -3, true);
		assertEquals(0, comparator.compare(date1, date2));
	}
	
	/**
	 * Test comparing dates when date1 is bigger than date2
	 */
	public void testBiggerThan()
	{
		Date date1 = new Date();
		Date date2 = (Date)date1.clone();
		date1 = DateHelper.addDaysInMonth(date1, 1, true);
		
		assertEquals(-1, comparator.compare(date1, date2));
		
		date1 = new Date();
		date1 = DateHelper.addMonths(date1, 1, true);
		assertEquals(-1, comparator.compare(date1, date2));
		
		date1 = new Date();
		date1 = DateHelper.addYears(date1, 1, true);
		assertEquals(-1, comparator.compare(date1, date2));
		
		date1 = new Date();
		date1 = DateHelper.addYears(date1, 1, true);
		date1 = DateHelper.addMonths(date1, 1, true);
		assertEquals(-1, comparator.compare(date1, date2));
		
	}
	
	/**
	 * Test comparing dates when date1 is lesser than date2
	 */
	public void testLesserThan()
	{
		Date date1 = new Date();
		Date date2 = (Date)date1.clone();
		date1 = DateHelper.addDaysInMonth(date1, -1, true);
		
		assertEquals(1, comparator.compare(date1, date2));
		
		date1 = new Date();
		date1 = DateHelper.addMonths(date1, -1, true);
		assertEquals(1, comparator.compare(date1, date2));
		
		date1 = new Date();
		date1 = DateHelper.addYears(date1, -1, true);
		assertEquals(1, comparator.compare(date1, date2));
		
		date1 = new Date();
		date1 = DateHelper.addYears(date1, -1, true);
		date1 = DateHelper.addMonths(date1, -1, true);
		assertEquals(1, comparator.compare(date1, date2));
		
	}
		
}
