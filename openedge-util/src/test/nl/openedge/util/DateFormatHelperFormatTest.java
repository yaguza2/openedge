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

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import nl.openedge.util.DateFormatHelper;

import junit.framework.TestCase;

/**
 * Aantal DateFormatHelper gerelateerde tests
 * 
 * @author Eelco Hillenius
 */
public class DateFormatHelperFormatTest extends TestCase
{
	/**
	 * construct
	 * 
	 * @param name
	 */
	public DateFormatHelperFormatTest(String name)
	{
		super(name);
	}

	/**
	 * gaandeweg bleek dat NumberFormatters (die intern worden gebruikt door dateformatters voor het
	 * parsen van onderdelen) parsen tot ze niet meer verder kunnen, en dan - indien ze digits
	 * hebben kunnen parsen - het tot dan gevonden getal terug geven (zonder exception dus)
	 */
	public void testDecimal1()
	{

		try
		{
			DecimalFormat dcf = new DecimalFormat();
			Number n = dcf.parse("45akdl12");
			// dit is toegestaan; er wordt geparsed tot a
			assertEquals(45, n.intValue());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Het eerste karakter moet echter wel een getal zijn
	 */
	public void testDecimal2()
	{

		try
		{
			DecimalFormat dcf = new DecimalFormat();
			Number n = dcf.parse("h45");
			fail("h45 should not have been parsed");
		}
		catch (Exception e)
		{

		}
	}

	/**
	 * Integer (en bijv. Double) zijn echter wel strikt
	 */
	public void testDecimal3()
	{

		try
		{
			int i = Integer.parseInt("45akdl12");
			fail("45akdl12 should not have been parsed");
		}
		catch (Exception e)
		{

		}
	}

	/**
	 * check voor bugfix #2131: 11-12-20la mag niet worden opgevat als een geldige datum
	 */
	public void testInvalidFormat1()
	{

		try
		{
			Date d = DateFormatHelper.fallbackParse("11-12-20la");

			System.err.println("invalid result: 11-12-20la -> " + d);
			fail("date should not have been parsed");
		}
		catch (Exception e)
		{

		}
	}

	/**
	 * check voor bugfix #2131: 111220la mag niet worden opgevat als een geldige datum
	 */
	public void testInvalidFormat2()
	{

		try
		{
			Date d = DateFormatHelper.fallbackParse("111220la");

			System.err.println("invalid result: 11-12-20la -> " + d);
			fail("date should not have been parsed");
		}
		catch (Exception e)
		{

		}
	}

	/**
	 * check voor troep
	 */
	public void testInvalidFormat3()
	{

		try
		{
			Date d = DateFormatHelper.fallbackParse("11!12!2003");

			System.err.println("invalid result: 11!12!2003 -> " + d);
			fail("date should not have been parsed");
		}
		catch (Exception e)
		{

		}
	}

	/**
	 * check voor bugfix #2131: 11122003 is een geldige datum
	 */
	public void testValidFormat1()
	{

		try
		{
			Date d = DateFormatHelper.fallbackParse("11122003");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * check voor bugfix #2131: 11-12-2003 is een geldige datum
	 */
	public void testValidFormat2()
	{

		try
		{
			Date d = DateFormatHelper.fallbackParse("11-12-2003");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	//------------------- test alle configuratie items uit dateformathelper.cfg -------------

	/**
	 * check ddMMyy
	 */
	public void testValidFormatddMMyy()
	{

		try
		{
			Date d = DateFormatHelper.fallbackParse("111203");

			Calendar c = Calendar.getInstance();
			c.setTime(d);
			assertEquals(11, c.get(Calendar.DATE));
			assertEquals(12 - 1, c.get(Calendar.MONTH));
			assertEquals(2003, c.get(Calendar.YEAR));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * check ddMMyy
	 */
	public void testValidFormatddMMyyyy()
	{

		try
		{
			Date d = DateFormatHelper.fallbackParse("11122003");

			Calendar c = Calendar.getInstance();
			c.setTime(d);
			assertEquals(11, c.get(Calendar.DATE));
			assertEquals(12 - 1, c.get(Calendar.MONTH));
			assertEquals(2003, c.get(Calendar.YEAR));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * check d-M-yy
	 */
	public void testValidFormatd_M_yy()
	{

		try
		{
			Date d = DateFormatHelper.fallbackParse("1-2-03");

			Calendar c = Calendar.getInstance();
			c.setTime(d);
			assertEquals(1, c.get(Calendar.DATE));
			assertEquals(2 - 1, c.get(Calendar.MONTH));
			assertEquals(2003, c.get(Calendar.YEAR));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * check d-MM-yy
	 */
	public void testValidFormatd_MM_yy()
	{

		try
		{
			Date d = DateFormatHelper.fallbackParse("1-12-03");

			Calendar c = Calendar.getInstance();
			c.setTime(d);
			assertEquals(1, c.get(Calendar.DATE));
			assertEquals(12 - 1, c.get(Calendar.MONTH));
			assertEquals(2003, c.get(Calendar.YEAR));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * check dd-M-yy
	 */
	public void testValidFormatdd_M_yy()
	{

		try
		{
			Date d = DateFormatHelper.fallbackParse("11-2-03");

			Calendar c = Calendar.getInstance();
			c.setTime(d);
			assertEquals(11, c.get(Calendar.DATE));
			assertEquals(2 - 1, c.get(Calendar.MONTH));
			assertEquals(2003, c.get(Calendar.YEAR));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * check dd-MM-yy
	 */
	public void testValidFormatdd_MM_yy()
	{

		try
		{
			Date d = DateFormatHelper.fallbackParse("11-12-03");

			Calendar c = Calendar.getInstance();
			c.setTime(d);

			System.out.println(d);

			assertEquals(11, c.get(Calendar.DATE));
			assertEquals(12 - 1, c.get(Calendar.MONTH));
			assertEquals(2003, c.get(Calendar.YEAR));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * check d-M-yyyy
	 */
	public void testValidFormatd_M_yyyy()
	{

		try
		{
			Date d = DateFormatHelper.fallbackParse("1-2-2003");

			Calendar c = Calendar.getInstance();
			c.setTime(d);
			assertEquals(1, c.get(Calendar.DATE));
			assertEquals(2 - 1, c.get(Calendar.MONTH));
			assertEquals(2003, c.get(Calendar.YEAR));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * check d-MM-yyyy
	 */
	public void testValidFormatd_MM_yyyy()
	{

		try
		{
			Date d = DateFormatHelper.fallbackParse("1-12-2003");

			Calendar c = Calendar.getInstance();
			c.setTime(d);
			assertEquals(1, c.get(Calendar.DATE));
			assertEquals(12 - 1, c.get(Calendar.MONTH));
			assertEquals(2003, c.get(Calendar.YEAR));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * check dd-M-yyyy
	 */
	public void testValidFormatdd_M_yyyy()
	{

		try
		{
			Date d = DateFormatHelper.fallbackParse("11-2-2003");

			Calendar c = Calendar.getInstance();
			c.setTime(d);
			assertEquals(11, c.get(Calendar.DATE));
			assertEquals(2 - 1, c.get(Calendar.MONTH));
			assertEquals(2003, c.get(Calendar.YEAR));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * check dd-MM-yyyy
	 */
	public void testValidFormatdd_MM_yyyy()
	{

		try
		{
			Date d = DateFormatHelper.fallbackParse("11-12-2003");

			Calendar c = Calendar.getInstance();
			c.setTime(d);
			assertEquals(11, c.get(Calendar.DATE));
			assertEquals(12 - 1, c.get(Calendar.MONTH));
			assertEquals(2003, c.get(Calendar.YEAR));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * check d/M/yy
	 */
	public void testValidFormatd__M__yy()
	{

		try
		{
			Date d = DateFormatHelper.fallbackParse("1/2/03");

			Calendar c = Calendar.getInstance();
			c.setTime(d);
			assertEquals(1, c.get(Calendar.DATE));
			assertEquals(2 - 1, c.get(Calendar.MONTH));
			assertEquals(2003, c.get(Calendar.YEAR));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * check d/MM/yy
	 */
	public void testValidFormatd__MM__yy()
	{

		try
		{
			Date d = DateFormatHelper.fallbackParse("1/12/03");

			Calendar c = Calendar.getInstance();
			c.setTime(d);
			assertEquals(1, c.get(Calendar.DATE));
			assertEquals(12 - 1, c.get(Calendar.MONTH));
			assertEquals(2003, c.get(Calendar.YEAR));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * check dd/M/yy
	 */
	public void testValidFormatdd__M__yy()
	{

		try
		{
			Date d = DateFormatHelper.fallbackParse("11/2/03");

			Calendar c = Calendar.getInstance();
			c.setTime(d);
			assertEquals(11, c.get(Calendar.DATE));
			assertEquals(2 - 1, c.get(Calendar.MONTH));
			assertEquals(2003, c.get(Calendar.YEAR));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * check dd/MM/yy
	 */
	public void testValidFormatdd__MM__yy()
	{

		try
		{
			Date d = DateFormatHelper.fallbackParse("11/12/03");

			Calendar c = Calendar.getInstance();
			c.setTime(d);
			assertEquals(11, c.get(Calendar.DATE));
			assertEquals(12 - 1, c.get(Calendar.MONTH));
			assertEquals(2003, c.get(Calendar.YEAR));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * check d/M/yyyy
	 */
	public void testValidFormatd__M__yyyy()
	{

		try
		{
			Date d = DateFormatHelper.fallbackParse("1/2/2003");

			Calendar c = Calendar.getInstance();
			c.setTime(d);
			assertEquals(1, c.get(Calendar.DATE));
			assertEquals(2 - 1, c.get(Calendar.MONTH));
			assertEquals(2003, c.get(Calendar.YEAR));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * check d/MM/yyyy
	 */
	public void testValidFormatd__MM__yyyy()
	{

		try
		{
			Date d = DateFormatHelper.fallbackParse("1/12/2003");

			Calendar c = Calendar.getInstance();
			c.setTime(d);
			assertEquals(1, c.get(Calendar.DATE));
			assertEquals(12 - 1, c.get(Calendar.MONTH));
			assertEquals(2003, c.get(Calendar.YEAR));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * check dd/M/yyyy
	 */
	public void testValidFormatdd__M__yyyy()
	{

		try
		{
			Date d = DateFormatHelper.fallbackParse("11/2/2003");

			Calendar c = Calendar.getInstance();
			c.setTime(d);
			assertEquals(11, c.get(Calendar.DATE));
			assertEquals(2 - 1, c.get(Calendar.MONTH));
			assertEquals(2003, c.get(Calendar.YEAR));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * check dd/MM/yyyy
	 */
	public void testValidFormatdd__MM__yyyy()
	{

		try
		{
			Date d = DateFormatHelper.fallbackParse("11/12/2003");

			Calendar c = Calendar.getInstance();
			c.setTime(d);
			assertEquals(11, c.get(Calendar.DATE));
			assertEquals(12 - 1, c.get(Calendar.MONTH));
			assertEquals(2003, c.get(Calendar.YEAR));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * check yyyy-MM-ddTHH:mm:ss
	 */
	public void testValidFormat_yyyy_MM_ddTHH_mm_ss()
	{
		boolean org = DateFormatHelper.isCheckForCharacters();
		try
		{
			DateFormatHelper.setCheckForCharacters(false);
			Date d = DateFormatHelper.fallbackParse("2004-02-01T00:00:00");
			DateFormatHelper.setCheckForCharacters(org);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			assertEquals(1, c.get(Calendar.DATE));
			assertEquals(1, c.get(Calendar.MONTH));
			assertEquals(2004, c.get(Calendar.YEAR));
			assertEquals(0, c.get(Calendar.HOUR_OF_DAY));
			assertEquals(0, c.get(Calendar.MINUTE));
			assertEquals(0, c.get(Calendar.SECOND));

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * check yyyy-MM-dd'HH:mm:ss
	 */
	public void testValidFormat_yyyy_MM_dd_HH_mm_ss()
	{

		boolean org = DateFormatHelper.isCheckForCharacters();
		try
		{
			DateFormatHelper.addFormatter("yyyy-MM-dd''HH:mm:ss");
			DateFormatHelper.setCheckForCharacters(false);
			Date d = DateFormatHelper.fallbackParse("2004-02-01'00:00:00");
			DateFormatHelper.setCheckForCharacters(org);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			assertEquals(1, c.get(Calendar.DATE));
			assertEquals(1, c.get(Calendar.MONTH));
			assertEquals(2004, c.get(Calendar.YEAR));
			assertEquals(0, c.get(Calendar.HOUR_OF_DAY));
			assertEquals(0, c.get(Calendar.MINUTE));
			assertEquals(0, c.get(Calendar.SECOND));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * check datum: yyyy-MM-dd tijd: HH:mm:ss
	 */
	public void testValidFormat_datum_yyyy_MM_dd_tijd_HH_mm_ss()
	{

		boolean org = DateFormatHelper.isCheckForCharacters();
		try
		{
			DateFormatHelper.addFormatter("'datum: 'yyyy-MM-dd' tijd: 'HH:mm:ss");
			DateFormatHelper.setCheckForCharacters(false);
			Date d = DateFormatHelper.fallbackParse("datum: 2004-02-01 tijd: 00:00:00");
			DateFormatHelper.setCheckForCharacters(org);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			assertEquals(1, c.get(Calendar.DATE));
			assertEquals(1, c.get(Calendar.MONTH));
			assertEquals(2004, c.get(Calendar.YEAR));
			assertEquals(0, c.get(Calendar.HOUR_OF_DAY));
			assertEquals(0, c.get(Calendar.MINUTE));
			assertEquals(0, c.get(Calendar.SECOND));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Test the keys generated for a pattern
	 */
	public void testKeyGeneration()
	{
		String key = DateFormatHelper.getKeyBasedOnPattern("yyyy-MM-dd'T'HH:mm:ss");
		assertEquals("yyyy-MM-ddTHH:mm:ss", key);
		key = DateFormatHelper.getKeyBasedOnPattern("yyyy-MM-dd''HH:mm:ss");
		assertEquals("yyyy-MM-dd'HH:mm:ss", key);
		key = DateFormatHelper.getKeyBasedOnPattern("yyyy-MM-dd' 'HH:mm:ss");
		assertEquals("yyyy-MM-dd HH:mm:ss", key);
		key = DateFormatHelper.getKeyBasedOnPattern("'datum: 'yyyy-MM-dd' tijd: 'HH:mm:ss");
		assertEquals("datum: yyyy-MM-dd tijd: HH:mm:ss", key);
		key = DateFormatHelper.getKeyBasedOnPattern("yyyy-MM-dd 'a''a'");
		assertEquals("yyyy-MM-dd a'a", key);
	}

}