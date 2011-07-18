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
package nl.openedge.modules.test.lt;

import java.net.URL;

import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.JDOMConfigurator;
import nl.openedge.modules.RepositoryFactory;
import nl.openedge.modules.impl.lt.LooselyTypedComponentRepository;

import org.junit.AfterClass;
import org.junit.Before;

/**
 * This is the baseclass for testcases. It does some initialization and provides
 * additional test methods
 * 
 * @author E.F. Hillenius
 */
public abstract class AbstractTestBase
{
	protected static ComponentRepository componentFactory;

	protected JDOMConfigurator configurator;

	protected static boolean initialised = false;

	@Before
	public void init() throws Exception
	{
		loadComponentFactory();
	}

	@AfterClass
	public static void reset()
	{
		initialised = false;
	}

	protected void loadComponentFactory() throws Exception
	{
		if (!initialised)
		{
			initialised = true;

			RepositoryFactory.setImplementingClass(LooselyTypedComponentRepository.class.getName());

			URL url = getClass().getResource("/oeltmodules.xml");

			configurator = new JDOMConfigurator(url);
			componentFactory = RepositoryFactory.getRepository();
		}
	}
}