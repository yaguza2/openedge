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
package nl.openedge.modules.impl.hibernate.usermanager.test;

import java.net.URL;
import java.util.Collection;

import junit.framework.TestCase;

import net.sf.hibernate.FlushMode;
import nl.openedge.access.AccessHelper;
import nl.openedge.modules.JDOMConfigurator;
import nl.openedge.modules.RepositoryFactory;
import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.config.URLHelper;
import nl.openedge.util.hibernate.HibernateHelper;

/**
 * This is the baseclass for testcases.
 * It does some initialisation and provides additional test methods
 * 
 * @author E.F. Hillenius
 */
public abstract class AbstractTestBase extends TestCase
{

	/** access factory */
	protected AccessHelper accessHelper;

	/** access factory */
	protected static ComponentRepository moduleFactory;
	private static boolean initialised = false;

	/** construct */
	public AbstractTestBase(String name) throws Exception
	{
		super(name);
		if (!initialised)
		{
			HibernateHelper.init();
			HibernateHelper.setFlushMode(FlushMode.COMMIT);
			init();
			initialised = true;
		}
	}

	/** 
	 * initialise
	 */
	protected void init() throws Exception
	{

		loadModuleFactory();
		loadAccessFactory();
	}

	/**
	 * load the access factory
	 * @throws Exception
	 */
	protected void loadAccessFactory() throws Exception
	{
		try
		{
			AccessHelper.reload(
				System.getProperty("configfile", "/oeaccess.properties"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * load the module factory
	 * @throws Exception
	 */
	protected void loadModuleFactory() throws Exception
	{

		if (!initialised)
		{
			try
			{

				URL url =
					URLHelper.convertToURL(
						System.getProperty("configfile", "/oemodules.xml"),
						AbstractTestBase.class,
						null);

				new JDOMConfigurator(url);
				moduleFactory = RepositoryFactory.getRepository();

			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw e;
			}
			initialised = true;
		}
	}

	/**
	 * Asserts that two collections contain the same elements. 
	 * If they do not it throws an AssertionFailedError with the given message.
	 */
	static public void assertSameContents(String message, Collection c1, Collection c2)
	{
		if (c1 == null || c2 == null || (!c1.containsAll(c2)))
			fail(message);
	}

	/**
	 * Asserts that two collections do not contain the same elements. 
	 * If they do not, it throws an AssertionFailedError with the given message.
	 */
	static public void assertNotSameContents(String message, Collection c1, Collection c2)
	{
		if (c1 == null && c2 == null)
			return;
		if (c1 == null || c2 == null || (!c1.containsAll(c2)))
			fail(message);
	}
}
