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
package nl.openedge.modules.test;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;

/**
 * modules related tests
 * 
 * @author E.F. Hillenius
 */
public class ModulesTest extends AbstractTestBase
{

	/**
	 * construct with name
	 * @param name
	 */
	public ModulesTest(String name) throws Exception
	{
		super(name);
	}

	public void testThrowAwayModule()
	{

		try
		{

			ThrowAwayModuleImpl module = (ThrowAwayModuleImpl)
					moduleFactory.getModule("ThrowAwayTest");
			assertNotNull(module);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testSingletonModule()
	{

		try
		{

			SingletonModuleImpl module = (SingletonModuleImpl)
					moduleFactory.getModule("SingletonTest");
			assertNotNull(module);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testConfigurableModule()
	{

		try
		{

			ConfigurableModuleImpl module = (ConfigurableModuleImpl)
					moduleFactory.getModule("ConfigurableTest");
			assertNotNull(module);
			
			assertNotNull(module.getMessage());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testBeanModule()
	{

		try
		{

			BeanModuleImpl module = (BeanModuleImpl)
					moduleFactory.getModule("BeanTest");
			assertNotNull(module);

			assertEquals(module.getMyString(), "test");
			assertEquals(module.getMyInteger(), new Integer(12));

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testCriticalModule()
	{

		try
		{

			CriticalModuleImpl module = (CriticalModuleImpl)
					moduleFactory.getModule("CriticalTest");
			assertNotNull(module);

			// create and add observer
			CriticalEventObserverImpl observer = new CriticalEventObserverImpl();
			module.addObserver(observer);
			// call method that fires critical event
			module.doFoo();
			// the observer should now have received a critical event
			assertNotNull(observer.getCriticalEvent());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testJobModule()
	{

		try
		{

			Scheduler scheduler = moduleFactory.getScheduler();
			assertNotNull(scheduler);			

			JobDetail jd = scheduler.getJobDetail("QuartzTest", "DEFAULT");
			assertNotNull(jd);
			
			Trigger t = scheduler.getTrigger("testTrigger_QuartzTest", "DEFAULT");
			assertNotNull(t);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testBlancoModule()
	{
		
		BlancoTypeImpl module = (BlancoTypeImpl)
				moduleFactory.getModule("BlancoTest");
		assertNotNull(module);
	}

}
