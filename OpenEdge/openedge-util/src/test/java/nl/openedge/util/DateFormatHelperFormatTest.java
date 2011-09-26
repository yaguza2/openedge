package nl.openedge.util;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

/**
 * Aantal DateFormatHelper gerelateerde tests
 * 
 * @author Eelco Hillenius
 */
public class DateFormatHelperFormatTest
{
	/**
	 * check voor bugfix #2131: 11-12-20la mag niet worden opgevat als een geldige datum
	 * 
	 * @throws ParseException
	 */
	@Test(expected = ParseException.class)
	public void testInvalidFormat1() throws ParseException
	{
		DateFormatHelper.fallbackParse("11-12-20la");
	}

	/**
	 * check voor bugfix #2131: 111220la mag niet worden opgevat als een geldige datum
	 * 
	 * @throws ParseException
	 */
	@Test(expected = ParseException.class)
	public void testInvalidFormat2() throws ParseException
	{
		DateFormatHelper.fallbackParse("111220la");
	}

	/**
	 * check voor troep
	 * 
	 * @throws ParseException
	 */
	@Test(expected = ParseException.class)
	public void testInvalidFormat3() throws ParseException
	{
		DateFormatHelper.fallbackParse("11!12!2003");
	}

	/**
	 * check voor bugfix #2131: 11122003 is een geldige datum
	 * 
	 * @throws ParseException
	 */
	@Test
	public void testValidFormat1() throws ParseException
	{
		DateFormatHelper.fallbackParse("11122003");
	}

	/**
	 * check voor bugfix #2131: 11-12-2003 is een geldige datum
	 * 
	 * @throws ParseException
	 */
	@Test
	public void testValidFormat2() throws ParseException
	{
		DateFormatHelper.fallbackParse("11-12-2003");
	}

	/**
	 * check ddMMyy
	 * 
	 * @throws ParseException
	 */
	@Test
	public void testValidFormatddMMyy() throws ParseException
	{
		Date d = DateFormatHelper.fallbackParse("111203");

		Calendar c = Calendar.getInstance();
		c.setTime(d);
		assertEquals(11, c.get(Calendar.DATE));
		assertEquals(12 - 1, c.get(Calendar.MONTH));
		assertEquals(2003, c.get(Calendar.YEAR));
	}

	/**
	 * check ddMMyy
	 * 
	 * @throws ParseException
	 */
	@Test
	public void testValidFormatddMMyyyy() throws ParseException
	{
		Date d = DateFormatHelper.fallbackParse("11122003");

		Calendar c = Calendar.getInstance();
		c.setTime(d);
		assertEquals(11, c.get(Calendar.DATE));
		assertEquals(12 - 1, c.get(Calendar.MONTH));
		assertEquals(2003, c.get(Calendar.YEAR));
	}

	/**
	 * check d-M-yy
	 */
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
