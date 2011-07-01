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

import junit.framework.TestCase;
import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.JDOMConfigurator;
import nl.openedge.modules.RepositoryFactory;
import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.config.URLHelper;
import nl.openedge.modules.impl.lt.LooselyTypedComponentRepository;
import nl.openedge.modules.types.initcommands.CyclicDependencyException;
import nl.openedge.modules.types.initcommands.DependentTypeWrapper;

/**
 * components related tests
 * 
 * @author E.F. Hillenius
 */
public class LTCyclicDepenencyThrowWayTypesFailTest extends TestCase
{

	/**
	 * construct with name
	 * 
	 * @param name
	 */
	public LTCyclicDepenencyThrowWayTypesFailTest(String name) throws Exception
	{
		super(name);
	}

	public void testLoadCyclicComponentFactory() throws Exception
	{
		try
		{
			DependentTypeWrapper.setFailOnCycle(true);
			URL url = URLHelper.convertToURL("/cyclic-throwaway-oeltmodules.xml",
					AbstractTestBase.class, null);

			RepositoryFactory.setImplementingClass(LooselyTypedComponentRepository.class
					.getName());
			JDOMConfigurator c = new JDOMConfigurator(url);
			ComponentRepository cRepo = RepositoryFactory.getRepository();
			// if we get here, the cycle was not detected
			fail("cycle was not detected!");

		}
		catch (ConfigException e)
		{
			if (e.getCause() instanceof CyclicDependencyException)
			{
				System.err.println("successfully detected cycle during startup\n"
						+ e.getMessage());
			}
			else
			{
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
	}

}
