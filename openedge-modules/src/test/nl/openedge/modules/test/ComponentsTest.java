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
 * components related tests
 * 
 * @author E.F. Hillenius
 */
public class ComponentsTest extends AbstractTestBase
{

	/**
	 * construct with name
	 * @param name
	 */
	public ComponentsTest(String name) throws Exception
	{
		super(name);
	}

	public void testThrowAwayComponent()
	{

		try
		{

			ThrowAwayComponentImpl module1 = (ThrowAwayComponentImpl)
					componentFactory.getModule("ThrowAwayTest");
			assertNotNull(module1);
			
			ThrowAwayComponentImpl module2 = (ThrowAwayComponentImpl)
					componentFactory.getModule("ThrowAwayTest");
			assertNotNull(module2);
			
			assertNotSame(module1, module2);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testSingletonComponent()
	{

		try
		{

			SingletonComponentImpl module1 = (SingletonComponentImpl)
					componentFactory.getModule("SingletonTest");
			assertNotNull(module1);
			
			SingletonComponentImpl module2 = (SingletonComponentImpl)
					componentFactory.getModule("SingletonTest");
			assertNotNull(module2);
			
			assertSame(module1, module2);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testConfigurableComponent()
	{

		try
		{

			ConfigurableComponentImpl module = (ConfigurableComponentImpl)
					componentFactory.getModule("ConfigurableTest");
			assertNotNull(module);
			
			assertNotNull(module.getMessage());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testBeanComponent()
	{

		try
		{

			BeanComponentImpl module = (BeanComponentImpl)
					componentFactory.getModule("BeanTest");
			assertNotNull(module);

			assertEquals(module.getMyString(), "test");
			assertEquals(module.getMyInteger(), new Integer(12));
			
			assertEquals(module.getNested().getAnotherString(), "anotherTest");

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testChainedComponent()
	{

		try
		{

			ChainedEventCasterComponentImpl module = (ChainedEventCasterComponentImpl)
					componentFactory.getModule("ChainedEventTest");
			assertNotNull(module);

			// create and add observer
			ChainedEventObserverImpl observer = (ChainedEventObserverImpl)
					componentFactory.getModule("ChainedEventTestObserver");
			
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

	public void testJobComponent()
	{

		try
		{

			Scheduler scheduler = componentFactory.getScheduler();
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
	
	public void testBlancoComponent()
	{
		
		BlancoComponentImpl module = (BlancoComponentImpl)
				componentFactory.getModule("BlancoTest");
		assertNotNull(module);
	}
	
	public void testScedulerObserver()
	{

		try
		{

			SchedulerObserverImpl module = (SchedulerObserverImpl)
					componentFactory.getModule("SchedulerObserverTest");
			assertNotNull(module);
			assertNotNull(module.getEvt());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testComponentsLoadedObserver()
	{

		try
		{

			ComponentsLoadedObserverImpl module = (ComponentsLoadedObserverImpl)
					componentFactory.getModule("ComponentsLoadedObserverTest");
			assertNotNull(module);
			assertNotNull(module.getEvt());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testGetComponentsByType()
	{

		try
		{

			List mods1 = 
				componentFactory.getModulesByType(SingletonType.class, false);
			
			assertTrue( mods1.size() > 1 );
			
			List mods2 =
				componentFactory.getModulesByType(SingletonComponentImpl.class, false);
				
			assertTrue( mods2.size() == 1 );

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testDependentComponent()
	{

		try
		{

			DependentComponentImpl module = (DependentComponentImpl)
				componentFactory.getModule("DependendComponentTest");
				
			assertNotNull(module.getBeanComponent());
			
			assertNotNull(module.getConfigComponent());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testThreadSingletonComponent()
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
		private ThreadSingletonComponentImpl module1 = null;
		
		private ThreadSingletonComponentImpl module2 = null;
	
		public void run()
		{
			module1 = (ThreadSingletonComponentImpl)
				componentFactory.getModule("ThreadSingletonTest");
		}
		
		public ThreadSingletonComponentImpl getModule1()
		{
			return module1;
		}
		
		public ThreadSingletonComponentImpl getModule2()
		{
			return module1;
		}
	}

}

