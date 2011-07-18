package nl.openedge.modules.impl.menumodule.test;

import java.security.Principal;
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

/**
 * Unit tests for menu component.
 * 
 * @author Eelco Hillenius
 */
public class MenuTest extends TestCase
{
	public MenuTest(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite(MenuTest.class);
		MenuTestDecorator deco = new MenuTestDecorator(suite);
		return deco;
	}

	public void testMenuComponentLoaded()
	{
		ComponentRepository cRepo = RepositoryFactory.getRepository();
		MenuModule menuModule = (MenuModule) cRepo.getComponent("MenuModule");
		assertNotNull(menuModule);
	}

	public void testMenuForChef()
	{
		ComponentRepository cRepo = RepositoryFactory.getRepository();
		MenuModule menuModule = (MenuModule) cRepo.getComponent("MenuModule");
		Subject subject = new Subject();
		Set<Principal> principals = new HashSet<Principal>();
		principals.add(new UserPrincipal("chef"));

		subject.getPrincipals().addAll(principals);

		List<MenuItem>[] items = menuModule.getMenuItems(subject);
		List<String> linkChecks = new ArrayList<String>();
		linkChecks.add("/admin.onderhoud.m");
		linkChecks.add("/medischdossier.m");
		linkChecks.add("/zoeken.m");
		int i = 0;
		assertEquals(1, items.length); // 1 niveau diep
		int size = items[0].size();
		assertEquals(3, size); // drie items
		for (; i < size; i++)
		{
			MenuItem item = items[0].get(i);
			assertEquals(linkChecks.get(i), item.getLink());
		}

		linkChecks.clear();
		linkChecks.add("/admin.onderhoud.functie.m");
		linkChecks.add("/admin.onderhoud.praktijk.m");
		items = menuModule.getMenuItems(subject, "/admin.onderhoud.m");
		i = 0;
		assertEquals(2, items.length); // 2 niveaus diep
		size = items[1].size();
		assertEquals(2, size); // 2 items
		for (; i < size; i++)
		{
			MenuItem item = items[1].get(i);
			assertEquals(linkChecks.get(i), item.getLink());
		}

		linkChecks.clear();
		linkChecks.add("/admin.onderhoud.functie.recursetest.m");
		items = menuModule.getMenuItems(subject, "/admin.onderhoud.functie.m");
		i = 0;
		assertEquals(3, items.length); // 3 niveaus diep
		size = items[2].size();
		assertEquals(1, size); // 1 item op dit niveau
		for (; i < size; i++)
		{
			MenuItem item = items[2].get(i);
			assertEquals(linkChecks.get(i), item.getLink());
		}
	}

	public void testMenuForAdmin()
	{
		ComponentRepository cRepo = RepositoryFactory.getRepository();
		MenuModule menuModule = (MenuModule) cRepo.getComponent("MenuModule");
		Subject subject = new Subject();
		Set<UserPrincipal> principals = new HashSet<UserPrincipal>();
		principals.add(new UserPrincipal("admin"));

		subject.getPrincipals().addAll(principals);

		List<MenuItem>[] items = menuModule.getMenuItems(subject);
		List<String> linkChecks = new ArrayList<String>();
		linkChecks.add("/medischdossier.m");
		linkChecks.add("/zoeken.m");
		int i = 0;
		assertEquals(1, items.length);
		int size = items[0].size();
		assertEquals(2, size);
		for (; i < size; i++)
		{
			MenuItem item = items[0].get(i);
			assertEquals(linkChecks.get(i), item.getLink());
		}

		linkChecks.clear();
		linkChecks.add("/medischdossier.journaal.m");
		linkChecks.add("/medischdossier.actuelemedicatie.m");
		linkChecks.add("/medischdossier.contraindicaties.m");
		linkChecks.add("/medischdossier.allergieen.m");
		linkChecks.add("/medischdossier.probleemlijst.m");
		items = menuModule.getMenuItems(subject, "/medischdossier.m");
		i = 0;
		assertEquals(2, items.length);
		size = items[1].size();
		assertEquals(5, size);
		for (; i < size; i++)
		{
			MenuItem item = items[1].get(i);
			assertEquals(linkChecks.get(i), item.getLink());
		}

		items = menuModule.getMenuItems(subject, "/medischdossier.allergieen.m");
		assertEquals(2, items.length);
		// we horen nog steeds twee niveaus te krijgen
		// omdat deze selectie geen childs heeft
	}

	public void testAttribute()
	{
		ComponentRepository cRepo = RepositoryFactory.getRepository();
		MenuModule menuModule = (MenuModule) cRepo.getComponent("MenuModule");
		Subject subject = new Subject();
		Set<UserPrincipal> principals = new HashSet<UserPrincipal>();
		principals.add(new UserPrincipal("admin"));

		subject.getPrincipals().addAll(principals);

		List<MenuItem>[] items = menuModule.getMenuItems(subject);

		MenuItem item = items[0].get(1);

		// check:
		// <attribute name="test1">value1</attribute>
		// <attribute name="test2"><![CDATA[ test with CDATA: &'"<> ]]></attribute>
		Object test1Val = item.getAttribute("test1");
		Object test2Val = item.getAttribute("test2");

		assertEquals("value1", test1Val);
		assertEquals("test with CDATA: &'\"<>", test2Val);
	}

	public void testActief()
	{
		ComponentRepository cRepo = RepositoryFactory.getRepository();
		MenuModule menuModule = (MenuModule) cRepo.getComponent("MenuModule");
		Subject subject = new Subject();
		Set<UserPrincipal> principals = new HashSet<UserPrincipal>();
		principals.add(new UserPrincipal("admin"));

		subject.getPrincipals().addAll(principals);

		List<MenuItem>[] items = null;
		int i = 0;
		items = menuModule.getMenuItems(subject, "/medischdossier.allergieen.m");

		// level 0
		int size = items[0].size();
		for (; i < size; i++)
		{
			MenuItem item = items[0].get(i);
			if ("/medischdossier.m".equals(item.getLink()))
			{
				assertTrue(item.isActive());
			}
		}

		// level 1
		size = items[1].size();
		for (; i < size; i++)
		{
			MenuItem item = items[1].get(i);
			if ("/medischdossier.allergieen.m".equals(item.getLink()))
			{
				assertTrue(item.isActive());
			}
		}
	}

	public void testAlias()
	{
		ComponentRepository cRepo = RepositoryFactory.getRepository();
		MenuModule menuModule = (MenuModule) cRepo.getComponent("MenuModule");
		Subject subject = new Subject();
		Set<UserPrincipal> principals = new HashSet<UserPrincipal>();
		principals.add(new UserPrincipal("admin"));

		subject.getPrincipals().addAll(principals);

		List<MenuItem>[] items = null;
		int i = 0;
		items = menuModule.getMenuItems(subject, "/medischdossier.journaaldetail.m");

		// level 0
		int size = items[0].size();
		for (; i < size; i++)
		{
			MenuItem item = items[0].get(i);
			if ("/medischdossier.m".equals(item.getLink()))
			{
				assertTrue(item.isActive());
			}
		}

		// level 1
		size = items[1].size();
		for (; i < size; i++)
		{
			MenuItem item = items[1].get(i);
			if ("/medischdossier.journaal.m".equals(item.getLink()))
			{
				assertTrue(item.isActive());
			}
		}
	}

	public void testRequestScopeFilter()
	{
		ComponentRepository cRepo = RepositoryFactory.getRepository();
		MenuModule menuModule = (MenuModule) cRepo.getComponent("MenuModule");
		Subject subject = new Subject();
		Set<UserPrincipal> principals = new HashSet<UserPrincipal>();
		principals.add(new UserPrincipal("admin"));

		subject.getPrincipals().addAll(principals);

		// zet indicator variable
		menuModule.putFilterContextVariable(RequestScopeTestFilter.TEST_CONTEXT_KEY, new Object());

		List<MenuItem>[] items = menuModule.getMenuItems(subject);
		List<String> linkChecks = new ArrayList<String>();
		linkChecks.add("/medischdossier.m");
		int i = 0;
		assertEquals(1, items.length);
		int size = items[0].size();
		assertEquals(1, size);
		for (; i < size; i++)
		{
			MenuItem item = items[0].get(i);
			assertEquals(linkChecks.get(i), item.getLink());
		}

		// verwijder indicator variable
		menuModule.resetContextForCurrentThread();

		items = menuModule.getMenuItems(subject);
		linkChecks = new ArrayList<String>();
		linkChecks.add("/medischdossier.m");
		linkChecks.add("/zoeken.m");
		i = 0;
		assertEquals(1, items.length);
		size = items[0].size();
		assertEquals(2, size);
		for (; i < size; i++)
		{
			MenuItem item = items[0].get(i);
			assertEquals(linkChecks.get(i), item.getLink());
		}
	}

	public void testNodeScopeFilter()
	{
		ComponentRepository cRepo = RepositoryFactory.getRepository();
		MenuModule menuModule = (MenuModule) cRepo.getComponent("MenuModule");
		Subject subject = new Subject();
		Set<UserPrincipal> principals = new HashSet<UserPrincipal>();
		principals.add(new UserPrincipal("admin"));

		subject.getPrincipals().addAll(principals);

		Object test =
			menuModule.getFilterContextVariable(RequestScopeTestFilterForOneItem.TEST_CONTEXT_KEY);

		test =
			menuModule.getFilterContextVariable(RequestScopeTestFilterForOneItem.TEST_CONTEXT_KEY);

		assertNotNull(test);
	}

	public void testChildListFiltering()
	{
		ComponentRepository cRepo = RepositoryFactory.getRepository();
		MenuModule menuModule = (MenuModule) cRepo.getComponent("MenuModule");
		Subject subject = new Subject();
		Set<UserPrincipal> principals = new HashSet<UserPrincipal>();
		principals.add(new UserPrincipal("admin"));
		subject.getPrincipals().addAll(principals);
		List<MenuItem>[] items = menuModule.getMenuItems(subject, "/zoeken.m");
		MenuItem actief = null;
		Iterator<MenuItem> it = items[0].iterator();
		while (it.hasNext())
		{
			actief = it.next();
			// the menuitems in items[1] should be exactly the same as those in
			// actief.children
			if (actief.isActive())
				assertEquals("Mismatch in children.", items[1], actief.getChildren());
		}
	}

	public void testSecondModuleInstance()
	{
		ComponentRepository cRepo = RepositoryFactory.getRepository();
		MenuModule menuModule2 = (MenuModule) cRepo.getComponent("MenuModule2");
		assertNotNull(menuModule2);
		List<MenuItem>[] items = menuModule2.getMenuItems(new Subject());
		assertNotNull(items);
		assertEquals(1, items.length);
		List<MenuItem> level0 = items[0];
		assertNotNull(level0);
		MenuItem item = level0.get(0);
		assertEquals(item.getTag(), "Test");
		assertEquals(item.getLink(), "/test.m");
	}

	public void testUseRootForNullPath()
	{
		ComponentRepository cRepo = RepositoryFactory.getRepository();
		MenuModule menuModule2 = (MenuModule) cRepo.getComponent("MenuModule2");
		assertNotNull(menuModule2);
		assertTrue(menuModule2.isUseRootForNullPath());
		// as useRootForNullPath == true, we should get the first level of items
		List<MenuItem>[] items = menuModule2.getMenuItems(new Subject(), "/admin.onderhoud.m");
		assertNotNull(items);
		assertFalse(items.length == 0);
		menuModule2.setUseRootForNullPath(false);
		// as useRootForNullPath == false, we should not get any items
		items = menuModule2.getMenuItems(new Subject(), "/admin.onderhoud.m");
		assertNotNull(items);
		assertTrue(items.length == 0);
	}
}
