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
import java.util.List;
import java.util.Set;

import javax.security.auth.Subject;

import junit.framework.TestCase;

import nl.openedge.access.AccessHelper;
import nl.openedge.access.UserPrincipal;
import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.JDOMConfigurator;
import nl.openedge.modules.RepositoryFactory;
import nl.openedge.modules.impl.menumodule.MenuItem;
import nl.openedge.modules.impl.menumodule.MenuModule;


/**
 * Unit tests voor menu component
 * @author Eelco Hillenius
 */
public class MenuTest extends TestCase
{

	private static boolean init = true;
	protected static ComponentRepository cRepo = null;
	
	private static MenuModule menuModule = null;

	/**
	 * contstruct
	 * @param name naam test
	 */
	public MenuTest(String name)
	{
		super(name);
		if(init)
		{
			init = false;
			try
			{
				System.err.println("Initialising modules"); 
				setUpModules();
				System.err.println("Initializing access");
				setUpAccessFactory();
				System.err.println("Initialising done.");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				fail("Setup failed: " + e.getMessage());
			}
		}
	}
	
	/**
	 * test of menu module is geladen
	 *
	 */
	public void testMenuComponentLoaded()
	{
		try
		{
			assertNotNull(menuModule);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}
	
	/**
	 * test menu voor user chef
	 *
	 */
	public void testMenuForChef()
	{
		System.out.println("");
		Subject subject = new Subject();
		Set principals = new HashSet();
		principals.add(new UserPrincipal("chef"));
		
		subject.getPrincipals().addAll(principals);
		try
		{
			List[] items = menuModule.getMenuItems(subject);
			List linkChecks = new ArrayList();
			linkChecks.add("/admin.onderhoud.m");
			linkChecks.add("/medischdossier.m");
			linkChecks.add("/zoeken.m");
			System.out.println("level 1 ***********************************");
			int i = 0;
			assertEquals(1, items.length); // 1 niveau diep
			int size = items[0].size();
			assertEquals(3, size); // drie items
			for( ; i < size; i++ )
			{
				MenuItem item = (MenuItem)items[0].get(i);
				System.out.println("menuitem: " + item);
				assertEquals(linkChecks.get(i), item.getLink());
			}
			
			linkChecks.clear();
			linkChecks.add("/admin.onderhoud.functie.m");
			linkChecks.add("/admin.onderhoud.praktijk.m");
			items = menuModule.getMenuItems(subject, "/admin.onderhoud.m");
			System.out.println("level 2 ***********************************");
			i = 0;
			assertEquals(2, items.length); // 2 niveaus diep
			size = items[1].size();
			assertEquals(2, size); // 2 items
			for( ; i < size; i++ )
			{
				MenuItem item = (MenuItem)items[1].get(i);
				System.out.println("menuitem: " + item);
				assertEquals(linkChecks.get(i), item.getLink());
			}
			
			linkChecks.clear();
			linkChecks.add("/admin.onderhoud.functie.recursetest.m");
			items = menuModule.getMenuItems(subject, "/admin.onderhoud.functie.m");
			System.out.println("level 3 ***********************************");
			i = 0;
			assertEquals(3, items.length); // 3 niveaus diep
			size = items[2].size();
			assertEquals(1, size); // 1 item op dit niveau
			for( ; i < size; i++ )
			{
				MenuItem item = (MenuItem)items[2].get(i);
				System.out.println("menuitem: " + item);
				assertEquals(linkChecks.get(i), item.getLink());
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}
	
	/**
	 * test menu voor user admin
	 *
	 */
	public void testMenuForAdmin()
	{
		System.out.println("");
		Subject subject = new Subject();
		Set principals = new HashSet();
		principals.add(new UserPrincipal("admin"));
		
		subject.getPrincipals().addAll(principals);
		try
		{
			List[] items = menuModule.getMenuItems(subject);
			List linkChecks = new ArrayList();
			linkChecks.add("/medischdossier.m");
			linkChecks.add("/zoeken.m");
			System.out.println("level 1 ***********************************");
			int i = 0;
			assertEquals(1, items.length);
			int size = items[0].size();
			assertEquals(2, size);
			for( ; i < size; i++ )
			{
				MenuItem item = (MenuItem)items[0].get(i);
				System.out.println("menuitem: " + item);
				assertEquals(linkChecks.get(i), item.getLink());
			}
			
			linkChecks.clear();
			linkChecks.add("/medischdossier.journaal.m");
			linkChecks.add("/medischdossier.actuelemedicatie.m");
			linkChecks.add("/medischdossier.contraindicaties.m");
			linkChecks.add("/medischdossier.allergieen.m");
			linkChecks.add("/medischdossier.probleemlijst.m");
			items = menuModule.getMenuItems(subject, "/medischdossier.m");
			System.out.println("level 2 ***********************************");
			i = 0;
			assertEquals(2, items.length);
			size = items[1].size();
			assertEquals(5, size);
			for( ; i < size; i++ )
			{
				MenuItem item = (MenuItem)items[1].get(i);
				System.out.println("menuitem: " + item);
				assertEquals(linkChecks.get(i), item.getLink());
			}
			
			items = menuModule.getMenuItems(subject, "/medischdossier.allergieen.m");
			assertEquals(2, items.length); 
				// we horen nog steeds twee niveaus te krijgen
				// omdat deze selectie geen childs heeft
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}
	
	/**
	 * test attributes
	 *
	 */
	public void testAttribute()
	{
		System.out.println("");
		Subject subject = new Subject();
		Set principals = new HashSet();
		principals.add(new UserPrincipal("admin"));
		
		subject.getPrincipals().addAll(principals);
		try
		{
			List[] items = menuModule.getMenuItems(subject);
			int size = items[0].size();

			MenuItem item = (MenuItem)items[0].get(1);
			System.out.println("menuitem: " + item);
			
			// check:
			// <attribute name="test1">value1</attribute>
			// <attribute name="test2"><![CDATA[ test with CDATA: &'"<> ]]></attribute>
			Object test1Val = item.getAttribute("test1");
			Object test2Val = item.getAttribute("test2");
			System.out.println("test1: " + test1Val + ", test2: " + test2Val);
			
			assertEquals("value1", test1Val);
			assertEquals("test with CDATA: &'\"<>", test2Val);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}
	
	/**
	 * test 'actief' marker
	 *
	 */
	public void testActief()
	{
		System.out.println("");
		Subject subject = new Subject();
		Set principals = new HashSet();
		principals.add(new UserPrincipal("admin"));
		
		subject.getPrincipals().addAll(principals);
		try
		{
			List[] items = null;
			int i = 0;
			items = menuModule.getMenuItems(subject, "/medischdossier.allergieen.m");
			
			// level 0
			int size = items[0].size();
			for( ; i < size; i++ )
			{
				MenuItem item = (MenuItem)items[0].get(i);
				System.out.println(item);
				if("/medischdossier.m".equals(item.getLink()))
				{
					assertTrue(item.isActive());
					
				}
			}
			
			// level 1
			size = items[1].size();
			for( ; i < size; i++ )
			{
				MenuItem item = (MenuItem)items[1].get(i);
				System.out.println(item);
				if("/medischdossier.allergieen.m".equals(item.getLink()))
				{
					assertTrue(item.isActive());
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}
	
	/**
	 * test 'actief' marker
	 *
	 */
	public void testActiefAlias()
	{
		System.out.println("");
		Subject subject = new Subject();
		Set principals = new HashSet();
		principals.add(new UserPrincipal("admin"));
		
		subject.getPrincipals().addAll(principals);
		try
		{
			List[] items = null;
			int i = 0;
			items = menuModule.getMenuItems(subject, "/medischdossier.journaaldetail.m");
			
			// level 0
			int size = items[0].size();
			for( ; i < size; i++ )
			{
				MenuItem item = (MenuItem)items[0].get(i);
				System.out.println(item);
				if("/medischdossier.m".equals(item.getLink()))
				{
					assertTrue(item.isActive());
					
				}
			}
			
			// level 1
			size = items[1].size();
			for( ; i < size; i++ )
			{
				MenuItem item = (MenuItem)items[1].get(i);
				System.out.println(item);
				if("/medischdossier.journaal.m".equals(item.getLink()))
				{
					assertTrue(item.isActive());
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}
	
	public void testRequestScopeFilter()
	{
		Subject subject = new Subject();
		Set principals = new HashSet();
		principals.add(new UserPrincipal("admin"));
		
		subject.getPrincipals().addAll(principals);
		try
		{
			// zet indicator variable
			menuModule.putFilterContextVariable(
				RequestScopeTestFilter.TEST_CONTEXT_KEY, new Object());
			
			List[] items = menuModule.getMenuItems(subject);
			List linkChecks = new ArrayList();
			linkChecks.add("/medischdossier.m");
			int i = 0;
			assertEquals(1, items.length);
			int size = items[0].size();
			assertEquals(1, size);
			for( ; i < size; i++ )
			{
				MenuItem item = (MenuItem)items[0].get(i);
				assertEquals(linkChecks.get(i), item.getLink());
			}
	
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}

		try
		{
			// verwijder indicator variable
			menuModule.resetContextForCurrentThread();
			
			List[] items = menuModule.getMenuItems(subject);
			List linkChecks = new ArrayList();
			linkChecks.add("/medischdossier.m");
			linkChecks.add("/zoeken.m");
			int i = 0;
			assertEquals(1, items.length);
			int size = items[0].size();
			assertEquals(2, size);
			for( ; i < size; i++ )
			{
				MenuItem item = (MenuItem)items[0].get(i);
				assertEquals(linkChecks.get(i), item.getLink());
			}
	
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
			
	}
	
	public void testNodeScopeFilter()
	{
		System.out.println("");
		Subject subject = new Subject();
		Set principals = new HashSet();
		principals.add(new UserPrincipal("admin"));
		
		subject.getPrincipals().addAll(principals);
		try
		{
			Object test = menuModule.getFilterContextVariable(
				RequestScopeTestFilterForOneItem.TEST_CONTEXT_KEY);
			
			List[] items = menuModule.getMenuItems(subject);

			test = menuModule.getFilterContextVariable(
				RequestScopeTestFilterForOneItem.TEST_CONTEXT_KEY);
				
			assertNotNull(test);	
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}
	
	//--------------------------- SETUP METHODS --------------------------------
	
	/**
	 * loadModules is een helper method die de componenten laad.
	 * @throws Exception
	 */
	private void setUpModules() throws Exception
	{
		JDOMConfigurator c = new JDOMConfigurator("test.oemodules.xml");
		cRepo = RepositoryFactory.getRepository();
		menuModule = (MenuModule) cRepo.getComponent("MenuModule");
	}
		
	/**
	 * laad de access factory
	 * @throws Exception
	 */
	protected void setUpAccessFactory() throws Exception
	{
		try
		{
			AccessHelper.reload(
				System.getProperty("configfile", "/test.oeaccess.properties"), "test");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}

}
