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

import nl.openedge.util.velocity.tools.HtmlTool;
import junit.framework.TestCase;

/**
 * @author shofstee
 *
 * Test de uitvoer van de HtmlTool
 */
public class HtmlToolTest extends TestCase
{
	private String[] input = {"<", ">", "&", "\"", "'"};
	private String[] output = {"&lt;","&gt;","&amp;","&#034;","&#039;"};
	private String start = "Bijvoorbeeld ";
	private String end = " is een special character";
	
	public HtmlToolTest(String name)
	{
		super(name);		
	}
	
	/**
	 * Test of alle tekens goed gereplaced worden.
	 * Deze methode doet dit een voor een.
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
	 *
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
	 *
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
	 *
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
	 * Controleert dat HtmlTool.parseText ook echt null terug geeft
	 * als er null in gestopt wordt.
	 *
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
	 *
	 */
	public void testJavascript()
	{
		String input = "een'twee'";
		String output = "een\\'twee\\'";
		assertEquals(output, HtmlTool.parseJavascipt(input));
	}
}