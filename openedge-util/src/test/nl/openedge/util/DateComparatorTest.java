/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003, Open Edge B.V.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions 
 * in binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution. Neither the name of OpenEdge B.V. 
 * nor the names of its contributors may be used to endorse or promote products 
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package nl.openedge.util;

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
