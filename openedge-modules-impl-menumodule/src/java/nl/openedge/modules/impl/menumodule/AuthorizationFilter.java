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
package nl.openedge.modules.impl.menumodule;

import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.Map;

import javax.security.auth.AuthPermission;
import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Eelco Hillenius
 */
public final class AuthorizationFilter implements SessionScopeMenuFilter
{

	private static Log log = LogFactory.getLog(AuthorizationFilter.class);

	/**
	 * kijkt naar permissie subject om te bepalen of een menu item mag worden getoon
	 * @param menuItem menu item
	 * @param context de filter context
	 * @see nl.promedico.asp.web.logic.menu.MenuFilter#accept(nl.promedico.asp.web.logic.menu.MenuItem, java.util.Map)
	 */
	public boolean accept(MenuItem menuItem, Map context)
	{
		boolean accepted = false; // default is afkeuren
		
		String link = menuItem.getLink();
		int ix = link.indexOf('?'); // strip parameter en ?
		if(ix != -1)
		{
			link = link.substring(0, ix);
		}
		UriAction action = new UriAction(link);
		Subject subject = (Subject)context.get(MenuFilter.CONTEXT_KEY_SUBJECT);
		try
		{
			
			// doe autorisatie
			Subject.doAsPrivileged(subject, action, null);
			// geen exception: subject is geautoriseerd
			// maak nieuwe werk node

			accepted = true; // ok
		}
		catch (SecurityException se)
		{
			// geen permissie
			if(log.isDebugEnabled())
			{
				log.debug(subject + " heeft geen permissie voor " + menuItem);
			}
		}
		
		return accepted;
	}

}

/** action for checking permissions for a specific subject */
class UriAction implements PrivilegedAction
{

	// uri to check on
	private String uri;

	/** construct with uri */
	public UriAction(String uri)
	{
		this.uri = uri;
	}

	/** run check */
	public Object run()
	{
		Permission p = new AuthPermission(uri);
		AccessController.checkPermission(p);
		return null;
	}
}
