package nl.openedge.util;

import junit.framework.TestCase;
import nl.openedge.util.velocity.tools.HtmlTool;

/**
 * @author shofstee Test de uitvoer van de HtmlTool
 */
public class HtmlToolTest extends TestCase
{
	private String[] input = {"<", ">", "&", "\"", "'"};

	private String[] output = {"&lt;", "&gt;", "&amp;", "&#034;", "&#039;"};

	private String start = "Bijvoorbeeld ";

	private String end = " is een special character";

	public HtmlToolTest(String name)
	{
		super(name);
	}

	/**
	 * Test of alle tekens goed gereplaced worden. Deze methode doet dit een voor een.
	 */
	public void testHtmlTool()
	{
		String out = null;
		for (int i = 0; i < input.length; i++)
		{
			out = HtmlTool.parseText(input[i]);
			assertEquals("i = " + i, output[i], out);
		}
	}

	/**
	 * Test als de special chars in een string zitten.
	 */
	public void testCharsInFrontString()
	{
		String inputString = null;
		String outputString = null;
		String out = null;
		for (int i = 0; i < input.length; i++)
		{
			inputString = start + input[i];
			out = HtmlTool.parseText(inputString);
			outputString = start + output[i];
			assertEquals("i = " + i, outputString, out);

		}
	}

	/**
	 * Test als de special chars in een string zitten.
	 */
	public void testCharsInEndString()
	{
		String inputString = null;
		String outputString = null;
		String out = null;
		for (int i = 0; i < input.length; i++)
		{
			inputString = input[i] + end;
			out = HtmlTool.parseText(inputString);
			outputString = output[i] + end;
			assertEquals("i = " + i, outputString, out);

		}
	}

	/**
	 * Test als de special chars in een string zitten.
	 */
	public void testCharsInString()
	{
		String inputString = null;
		String outputString = null;
		String out = null;
		for (int i = 0; i < input.length; i++)
		{
			inputString = start + input[i] + end;
			out = HtmlTool.parseText(inputString);
			outputString = start + output[i] + end;
			assertEquals("i = " + i, outputString, out);

		}
	}

	/**
	 * Controleert dat HtmlTool.parseText ook echt null terug geeft als er null in gestopt
	 * wordt.
	 */
	public void testNull()
	{
		try
		{
			String output = HtmlTool.parseText(null);
			assertNull(output);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 * Test dat '-characters goed worden vervangen.
	 */
	public void testJavascript()
	{
		String input = "een'twee'";
		String output = "een\\'twee\\'";
		assertEquals(output, HtmlTool.parseJavascipt(input));
	}
}
