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
package nl.openedge.modules.impl.menumodule.test;

import junit.extensions.TestSetup;
import junit.framework.Test;
import nl.openedge.access.AccessHelper;
import nl.openedge.modules.JDOMConfigurator;

/**
 * Test decorator for MenuTest.
 */
public final class MenuTestDecorator extends TestSetup
{

	/**
	 * Construct.
	 * 
	 * @param test
	 */
	public MenuTestDecorator(Test test)
	{
		super(test);
	}

	/**
	 * @see junit.extensions.TestSetup#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		setUpModules();
		setUpAccessFactory();
	}

	/**
	 * @see junit.extensions.TestSetup#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	/**
	 * loadModules is een helper method die de componenten laad.
	 * 
	 * @throws Exception
	 */
	private void setUpModules() throws Exception
	{
		JDOMConfigurator c = new JDOMConfigurator("test.oemodules.xml");
	}

	/**
	 * laad de access factory
	 * 
	 * @throws Exception
	 */
	private void setUpAccessFactory() throws Exception
	{
		try
		{
			AccessHelper.reload(System.getProperty("configfile", "/test.oeaccess.properties"),
					"test");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}
}
