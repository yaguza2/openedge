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

import java.util.List;

import nl.openedge.modules.types.base.SingletonType;

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

			ThrowAwayModuleImpl module1 = (ThrowAwayModuleImpl)
					moduleFactory.getModule("ThrowAwayTest");
			assertNotNull(module1);
			
			ThrowAwayModuleImpl module2 = (ThrowAwayModuleImpl)
					moduleFactory.getModule("ThrowAwayTest");
			assertNotNull(module2);
			
			assertNotSame(module1, module2);

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

			SingletonModuleImpl module1 = (SingletonModuleImpl)
					moduleFactory.getModule("SingletonTest");
			assertNotNull(module1);
			
			SingletonModuleImpl module2 = (SingletonModuleImpl)
					moduleFactory.getModule("SingletonTest");
			assertNotNull(module2);
			
			assertSame(module1, module2);

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
			CriticalEventObserverImpl observer = (CriticalEventObserverImpl)
					moduleFactory.getModule("CriticalTestObserver");
			
			//module.addObserver(observer);
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
	
	public void testScedulerObserver()
	{

		try
		{

			SchedulerObserverImpl module = (SchedulerObserverImpl)
					moduleFactory.getModule("SchedulerObserverTest");
			assertNotNull(module);
			assertNotNull(module.getEvt());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testModulesLoadedObserver()
	{

		try
		{

			ModulesLoadedObserverImpl module = (ModulesLoadedObserverImpl)
					moduleFactory.getModule("ModulesLoadedObserverTest");
			assertNotNull(module);
			assertNotNull(module.getEvt());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testGetModulesByType()
	{

		try
		{

			List mods1 = 
				moduleFactory.getModulesByType(SingletonType.class, false);
			
			assertTrue( mods1.size() > 1 );
			
			List mods2 =
				moduleFactory.getModulesByType(SingletonModuleImpl.class, false);
				
			assertTrue( mods2.size() == 1 );

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testDependentModule()
	{

		try
		{

			DependendModuleImpl module = (DependendModuleImpl)
				moduleFactory.getModule("DependendModuleTest");
				
			assertNotNull(module.getBeanModule());
			
			assertNotNull(module.getConfigModule());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testThreadSingletonModule()
	{
		
		ThreadSingletonThread t1 = new ThreadSingletonThread();
		ThreadSingletonThread t2 = new ThreadSingletonThread();
		
		t1.start();
		t2.start();
		
		try
		{
			t1.join();
			t2.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		assertNotNull(t1.getModule1());
		assertNotNull(t1.getModule2());
		
		assertNotNull(t2.getModule1());
		assertNotNull(t2.getModule2());

		assertSame(t1.getModule1(), t1.getModule2());

		assertNotSame(t1.getModule1(), t2.getModule1());
		
	}


	class ThreadSingletonThread extends Thread
	{
		private ThreadSingletonModuleImpl module1 = null;
		
		private ThreadSingletonModuleImpl module2 = null;
	
		public void run()
		{
			module1 = (ThreadSingletonModuleImpl)
				moduleFactory.getModule("ThreadSingletonTest");
		}
		
		public ThreadSingletonModuleImpl getModule1()
		{
			return module1;
		}
		
		public ThreadSingletonModuleImpl getModule2()
		{
			return module1;
		}
	}

}

