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

import static org.junit.Assert.*;

import java.util.List;

import nl.openedge.modules.types.base.SingletonType;

import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;

/**
 * components related tests
 * 
 * @author E.F. Hillenius
 */
public class LTComponentsTest extends AbstractTestBase
{
	@Test
	public void testThrowAwayComponent()
	{
		ThrowAwayComponentImpl module1 =
			(ThrowAwayComponentImpl) componentFactory.getComponent("ThrowAwayTest");
		assertNotNull(module1);

		ThrowAwayComponentImpl module2 =
			(ThrowAwayComponentImpl) componentFactory.getComponent("ThrowAwayTest");
		assertNotNull(module2);

		assertNotSame(module1, module2);
	}

	@Test
	public void testSingletonComponent()
	{
		SingletonComponentImpl module1 =
			(SingletonComponentImpl) componentFactory.getComponent("SingletonTest");
		assertNotNull(module1);

		SingletonComponentImpl module2 =
			(SingletonComponentImpl) componentFactory.getComponent("SingletonTest");
		assertNotNull(module2);

		assertSame(module1, module2);
	}

	@Test
	public void testConfigurableComponent()
	{
		ConfigurableComponentImpl module =
			(ConfigurableComponentImpl) componentFactory.getComponent("ConfigurableTest");
		assertNotNull(module);

		assertNotNull(module.getMessage());
	}

	@Test
	public void testBeanComponent()
	{
		BeanComponentImpl module = (BeanComponentImpl) componentFactory.getComponent("BeanTest");
		assertNotNull(module);

		assertEquals(module.getMyString(), "test");
		assertEquals(module.getMyInteger(), new Integer(12));

		assertEquals(module.getNested().getAnotherString(), "anotherTest");
	}

	@Test
	public void testChainedComponent()
	{
		ChainedEventCasterComponentImpl module =
			(ChainedEventCasterComponentImpl) componentFactory.getComponent("ChainedEventTest");
		assertNotNull(module);

		// create and add observer
		ChainedEventObserverImpl observer =
			(ChainedEventObserverImpl) componentFactory.getComponent("ChainedEventTestObserver");

		// module.addObserver(observer);
		// call method that fires critical event
		module.doFoo();
		// the observer should now have received a critical event
		assertNotNull(observer.getCriticalEvent());
	}

	@Test
	public void testJobComponent() throws Exception
	{
		Scheduler scheduler = componentFactory.getScheduler();
		assertNotNull(scheduler);

		JobDetail jd = scheduler.getJobDetail("QuartzTest", "DEFAULT");
		assertNotNull(jd);

		Trigger t = scheduler.getTrigger("testTrigger_QuartzTest", "DEFAULT");
		assertNotNull(t);
	}

	@Test
	public void testBlancoComponent()
	{
		BlancoComponentImpl module =
			(BlancoComponentImpl) componentFactory.getComponent("BlancoTest");
		assertNotNull(module);
	}

	@Test
	public void testScedulerObserver()
	{
		SchedulerObserverImpl module =
			(SchedulerObserverImpl) componentFactory.getComponent("SchedulerObserverTest");
		assertNotNull(module);
		assertNotNull(module.getEvt());
	}

	@Test
	public void testComponentsLoadedObserver()
	{
		ComponentsLoadedObserverImpl module =
			(ComponentsLoadedObserverImpl) componentFactory
				.getComponent("ComponentsLoadedObserverTest");
		assertNotNull(module);
		assertNotNull(module.getEvt());
	}

	@Test
	public void testGetComponentsByType()
	{
		List mods1 = componentFactory.getComponentsByType(SingletonType.class, false);

		assertTrue(mods1.size() == 0);

		List mods2 = componentFactory.getComponentsByType(SingletonComponentImpl.class, false);

		assertTrue(mods2.size() == 1);
	}

	@Test
	public void testDependentComponent()
	{
		DependentComponentImpl module =
			(DependentComponentImpl) componentFactory.getComponent("DependendComponentTest");

		assertNotNull(module.getBeanComponent());
		assertNotNull(module.getConfigComponent());
	}

	@Test
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

		@Override
		public void run()
		{
			module1 =
				(ThreadSingletonComponentImpl) componentFactory.getComponent("ThreadSingletonTest");
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
