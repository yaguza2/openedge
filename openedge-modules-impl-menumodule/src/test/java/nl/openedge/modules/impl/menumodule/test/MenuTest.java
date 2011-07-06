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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.security.auth.Subject;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import nl.openedge.access.UserPrincipal;
import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.RepositoryFactory;
import nl.openedge.modules.impl.menumodule.MenuItem;
import nl.openedge.modules.impl.menumodule.MenuModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit tests for menu component.
 * 
 * @author Eelco Hillenius
 */
public class MenuTest extends TestCase
{
	/** Log. */
	private static Logger log = LoggerFactory.getLogger(MenuTest.class);

	/**
	 * contstruct.
	 * 
	 * @param name
	 *            naam test
	 */
	public MenuTest(String name)
	{
		super(name);
	}

	/**
	 * Create suite.
	 * 
	 * @return suite
	 */
	public static Test suite()
	{
		TestSuite suite = new TestSuite(MenuTest.class);
		MenuTestDecorator deco = new MenuTestDecorator(suite);
		return deco;
	}

	/**
	 * test whether the menu module was loaded.
	 * 
	 * @throws Exception
	 */
	public void testMenuComponentLoaded() throws Exception
	{
		ComponentRepository cRepo = RepositoryFactory.getRepository();
		MenuModule menuModule = (MenuModule) cRepo.getComponent("MenuModule");
		assertNotNull(menuModule);
	}

	/**
	 * test menu voor user chef.
	 * 
	 * @throws Exception
	 */
	public void testMenuForChef() throws Exception
	{
		ComponentRepository cRepo = RepositoryFactory.getRepository();
		MenuModule menuModule = (MenuModule) cRepo.getComponent("MenuModule");
		log.info("");
		Subject subject = new Subject();
		Set principals = new HashSet();
		principals.add(new UserPrincipal("chef"));

		subject.getPrincipals().addAll(principals);

		List[] items = menuModule.getMenuItems(subject);
		List linkChecks = new ArrayList();
		linkChecks.add("/admin.onderhoud.m");
		linkChecks.add("/medischdossier.m");
		linkChecks.add("/zoeken.m");
		log.info("level 1 ***********************************");
		int i = 0;
		assertEquals(1, items.length); // 1 niveau diep
		int size = items[0].size();
		assertEquals(3, size); // drie items
		for (; i < size; i++)
		{
			MenuItem item = (MenuItem) items[0].get(i);
			log.info("menuitem: " + item);
			assertEquals(linkChecks.get(i), item.getLink());
		}

		linkChecks.clear();
		linkChecks.add("/admin.onderhoud.functie.m");
		linkChecks.add("/admin.onderhoud.praktijk.m");
		items = menuModule.getMenuItems(subject, "/admin.onderhoud.m");
		log.info("level 2 ***********************************");
		i = 0;
		assertEquals(2, items.length); // 2 niveaus diep
		size = items[1].size();
		assertEquals(2, size); // 2 items
		for (; i < size; i++)
		{
			MenuItem item = (MenuItem) items[1].get(i);
			log.info("menuitem: " + item);
			assertEquals(linkChecks.get(i), item.getLink());
		}

		linkChecks.clear();
		linkChecks.add("/admin.onderhoud.functie.recursetest.m");
		items = menuModule.getMenuItems(subject, "/admin.onderhoud.functie.m");
		log.info("level 3 ***********************************");
		i = 0;
		assertEquals(3, items.length); // 3 niveaus diep
		size = items[2].size();
		assertEquals(1, size); // 1 item op dit niveau
		for (; i < size; i++)
		{
			MenuItem item = (MenuItem) items[2].get(i);
			log.info("menuitem: " + item);
			assertEquals(linkChecks.get(i), item.getLink());
		}
	}

	/**
	 * test menu voor user admin.
	 * 
	 * @throws Exception
	 */
	public void testMenuForAdmin() throws Exception
	{
		ComponentRepository cRepo = RepositoryFactory.getRepository();
		MenuModule menuModule = (MenuModule) cRepo.getComponent("MenuModule");
		log.info("");
		Subject subject = new Subject();
		Set principals = new HashSet();
		principals.add(new UserPrincipal("admin"));

		subject.getPrincipals().addAll(principals);

		List[] items = menuModule.getMenuItems(subject);
		List linkChecks = new ArrayList();
		linkChecks.add("/medischdossier.m");
		linkChecks.add("/zoeken.m");
		log.info("level 1 ***********************************");
		int i = 0;
		assertEquals(1, items.length);
		int size = items[0].size();
		assertEquals(2, size);
		for (; i < size; i++)
		{
			MenuItem item = (MenuItem) items[0].get(i);
			log.info("menuitem: " + item);
			assertEquals(linkChecks.get(i), item.getLink());
		}

		linkChecks.clear();
		linkChecks.add("/medischdossier.journaal.m");
		linkChecks.add("/medischdossier.actuelemedicatie.m");
		linkChecks.add("/medischdossier.contraindicaties.m");
		linkChecks.add("/medischdossier.allergieen.m");
		linkChecks.add("/medischdossier.probleemlijst.m");
		items = menuModule.getMenuItems(subject, "/medischdossier.m");
		log.info("level 2 ***********************************");
		i = 0;
		assertEquals(2, items.length);
		size = items[1].size();
		assertEquals(5, size);
		for (; i < size; i++)
		{
			MenuItem item = (MenuItem) items[1].get(i);
			log.info("menuitem: " + item);
			assertEquals(linkChecks.get(i), item.getLink());
		}

		items = menuModule.getMenuItems(subject, "/medischdossier.allergieen.m");
		assertEquals(2, items.length);
		// we horen nog steeds twee niveaus te krijgen
		// omdat deze selectie geen childs heeft
	}

	/**
	 * test attributes.
	 * 
	 * @throws Exception
	 */
	public void testAttribute() throws Exception
	{
		ComponentRepository cRepo = RepositoryFactory.getRepository();
		MenuModule menuModule = (MenuModule) cRepo.getComponent("MenuModule");
		log.info("");
		Subject subject = new Subject();
		Set principals = new HashSet();
		principals.add(new UserPrincipal("admin"));

		subject.getPrincipals().addAll(principals);

		List[] items = menuModule.getMenuItems(subject);
		int size = items[0].size();

		MenuItem item = (MenuItem) items[0].get(1);
		log.info("menuitem: " + item);

		// check:
		// <attribute name="test1">value1</attribute>
		// <attribute name="test2"><![CDATA[ test with CDATA: &'"<> ]]></attribute>
		Object test1Val = item.getAttribute("test1");
		Object test2Val = item.getAttribute("test2");
		log.info("test1: " + test1Val + ", test2: " + test2Val);

		assertEquals("value1", test1Val);
		assertEquals("test with CDATA: &'\"<>", test2Val);
	}

	/**
	 * test 'active' marker.
	 * 
	 * @throws Exception
	 */
	public void testActief() throws Exception
	{
		ComponentRepository cRepo = RepositoryFactory.getRepository();
		MenuModule menuModule = (MenuModule) cRepo.getComponent("MenuModule");
		log.info("");
		Subject subject = new Subject();
		Set principals = new HashSet();
		principals.add(new UserPrincipal("admin"));

		subject.getPrincipals().addAll(principals);

		List[] items = null;
		int i = 0;
		items = menuModule.getMenuItems(subject, "/medischdossier.allergieen.m");

		// level 0
		int size = items[0].size();
		for (; i < size; i++)
		{
			MenuItem item = (MenuItem) items[0].get(i);
			if ("/medischdossier.m".equals(item.getLink()))
			{
				assertTrue(item.isActive());
			}
		}

		// level 1
		size = items[1].size();
		for (; i < size; i++)
		{
			MenuItem item = (MenuItem) items[1].get(i);
			if ("/medischdossier.allergieen.m".equals(item.getLink()))
			{
				assertTrue(item.isActive());
			}
		}
	}

	/**
	 * test alias.
	 * 
	 * @throws Exception
	 */
	public void testAlias() throws Exception
	{
		ComponentRepository cRepo = RepositoryFactory.getRepository();
		MenuModule menuModule = (MenuModule) cRepo.getComponent("MenuModule");
		log.info("");
		Subject subject = new Subject();
		Set principals = new HashSet();
		principals.add(new UserPrincipal("admin"));

		subject.getPrincipals().addAll(principals);

		List[] items = null;
		int i = 0;
		items = menuModule.getMenuItems(subject, "/medischdossier.journaaldetail.m");

		// level 0
		int size = items[0].size();
		for (; i < size; i++)
		{
			MenuItem item = (MenuItem) items[0].get(i);
			if ("/medischdossier.m".equals(item.getLink()))
			{
				assertTrue(item.isActive());
			}
		}

		// level 1
		size = items[1].size();
		for (; i < size; i++)
		{
			MenuItem item = (MenuItem) items[1].get(i);
			if ("/medischdossier.journaal.m".equals(item.getLink()))
			{
				assertTrue(item.isActive());
			}
		}
	}

	/**
	 * Test request scope filter.
	 * 
	 * @throws Exception
	 */
	public void testRequestScopeFilter() throws Exception
	{
		ComponentRepository cRepo = RepositoryFactory.getRepository();
		MenuModule menuModule = (MenuModule) cRepo.getComponent("MenuModule");
		Subject subject = new Subject();
		Set principals = new HashSet();
		principals.add(new UserPrincipal("admin"));

		subject.getPrincipals().addAll(principals);

		// zet indicator variable
		menuModule.putFilterContextVariable(RequestScopeTestFilter.TEST_CONTEXT_KEY, new Object());

		List[] items = menuModule.getMenuItems(subject);
		List linkChecks = new ArrayList();
		linkChecks.add("/medischdossier.m");
		int i = 0;
		assertEquals(1, items.length);
		int size = items[0].size();
		assertEquals(1, size);
		for (; i < size; i++)
		{
			MenuItem item = (MenuItem) items[0].get(i);
			assertEquals(linkChecks.get(i), item.getLink());
		}

		// verwijder indicator variable
		menuModule.resetContextForCurrentThread();

		items = menuModule.getMenuItems(subject);
		linkChecks = new ArrayList();
		linkChecks.add("/medischdossier.m");
		linkChecks.add("/zoeken.m");
		i = 0;
		assertEquals(1, items.length);
		size = items[0].size();
		assertEquals(2, size);
		for (; i < size; i++)
		{
			MenuItem item = (MenuItem) items[0].get(i);
			assertEquals(linkChecks.get(i), item.getLink());
		}
	}

	/**
	 * Test node scoped filter.
	 * 
	 * @throws Exception
	 */
	public void testNodeScopeFilter() throws Exception
	{
		ComponentRepository cRepo = RepositoryFactory.getRepository();
		MenuModule menuModule = (MenuModule) cRepo.getComponent("MenuModule");
		log.info("");
		Subject subject = new Subject();
		Set principals = new HashSet();
		principals.add(new UserPrincipal("admin"));

		subject.getPrincipals().addAll(principals);

		Object test =
			menuModule.getFilterContextVariable(RequestScopeTestFilterForOneItem.TEST_CONTEXT_KEY);

		List[] items = menuModule.getMenuItems(subject);

		test =
			menuModule.getFilterContextVariable(RequestScopeTestFilterForOneItem.TEST_CONTEXT_KEY);

		assertNotNull(test);
	}

	/**
	 * Test childs.
	 * 
	 * @throws Exception
	 */
	public void testChildListFiltering() throws Exception
	{
		ComponentRepository cRepo = RepositoryFactory.getRepository();
		MenuModule menuModule = (MenuModule) cRepo.getComponent("MenuModule");
		Subject subject = new Subject();
		Set principals = new HashSet();
		principals.add(new UserPrincipal("admin"));
		subject.getPrincipals().addAll(principals);
		List[] items = menuModule.getMenuItems(subject, "/zoeken.m");
		Iterator it = items[0].iterator();
		MenuItem actief = null;
		while (it.hasNext())
		{
			actief = (MenuItem) it.next();
			if (actief.isActive())
				break;
		}
		// the menuitems in items[1] should be exactly the same as those in
		// actief.children
		assertEquals("Mismatch in children.", items[1], actief.getChildren());
	}

	/**
	 * test a second instance of the menu module (with a different config/ menu tree).
	 * 
	 * @throws Exception
	 */
	public void testSecondModuleInstance() throws Exception
	{
		ComponentRepository cRepo = RepositoryFactory.getRepository();
		MenuModule menuModule2 = (MenuModule) cRepo.getComponent("MenuModule2");
		assertNotNull(menuModule2);
		List[] items = menuModule2.getMenuItems(new Subject());
		assertNotNull(items);
		assertEquals(1, items.length);
		List level0 = items[0];
		assertNotNull(level0);
		MenuItem item = (MenuItem) level0.get(0);
		assertEquals(item.getTag(), "Test");
		assertEquals(item.getLink(), "/test.m");
	}

	/**
	 * test module property useRootForNullPath.
	 * 
	 * @throws Exception
	 */
	public void testUseRootForNullPath() throws Exception
	{
		ComponentRepository cRepo = RepositoryFactory.getRepository();
		MenuModule menuModule2 = (MenuModule) cRepo.getComponent("MenuModule2");
		assertNotNull(menuModule2);
		assertTrue(menuModule2.isUseRootForNullPath());
		// as useRootForNullPath == true, we should get the first level of items
		List[] items = menuModule2.getMenuItems(new Subject(), "/admin.onderhoud.m");
		assertNotNull(items);
		assertFalse(items.length == 0);
		menuModule2.setUseRootForNullPath(false);
		// as useRootForNullPath == false, we should not get any items
		items = menuModule2.getMenuItems(new Subject(), "/admin.onderhoud.m");
		assertNotNull(items);
		assertTrue(items.length == 0);
	}
}
