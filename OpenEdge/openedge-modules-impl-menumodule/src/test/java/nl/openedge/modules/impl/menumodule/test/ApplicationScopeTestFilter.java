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

import java.util.Map;

import nl.openedge.modules.impl.menumodule.AbstractMenuFilter;
import nl.openedge.modules.impl.menumodule.ApplicationScopeMenuFilter;
import nl.openedge.modules.impl.menumodule.MenuItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Eelco Hillenius
 */
public final class ApplicationScopeTestFilter extends AbstractMenuFilter implements
		ApplicationScopeMenuFilter
{

	private static Logger log = LoggerFactory.getLogger(ApplicationScopeTestFilter.class);

	/**
	 * test method
	 * 
	 * @param menuItem
	 *            menu item
	 * @param context
	 *            de filter context
	 * @see nl.promedico.asp.web.logic.menu.MenuFilter#accept(nl.promedico.asp.web.logic.menu.MenuItem,
	 *      java.util.Map)
	 */
	public boolean accept(MenuItem menuItem, Map context)
	{
		boolean accepted = true;

		if (menuItem.getLink().equals("/admin.onderhoud.filtertest.m"))
		{
			accepted = false;
		}

		return accepted;
	}

}

