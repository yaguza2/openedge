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
 
package com.foo;

import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infohazard.maverick.flow.ConfigException;
import org.infohazard.maverick.flow.ControllerContext;
import org.infohazard.maverick.util.XML;
import org.jdom.Element;

import nl.openedge.access.AccessCallbackHandler;
import nl.openedge.access.AccessFilter;
import nl.openedge.access.SessionLoginContext;
import nl.openedge.access.UserPrincipal;
import nl.openedge.maverick.framework.AbstractCtrl;
import nl.openedge.maverick.framework.AbstractForm;

/**
 * @author Hillenius
 * login user
 */
public class LoginCtrl extends AbstractCtrl
{

	/** alias of logon module to use */
	protected String logonModuleAlias;

	/** parameters for this control */
	protected Map parameters;

	/** logger */
	protected Log log = LogFactory.getLog(getClass());

	/** special logger for logging login attempts */
	protected Log loginLog = LogFactory.getLog("com.foo.loginlog");

	/**
	 * perform login
	 * @see org.infohazard.maverick.ctl.CommonCtrlBean#perform()
	 */
	protected String perform(Object form, ControllerContext cctx) 
		throws Exception
	{

		LoginForm formBean = (LoginForm)form;
		HttpSession session = cctx.getRequest().getSession();

		try
		{

			// create the callback handler
			AccessCallbackHandler cbh =
				new AccessCallbackHandler(
					formBean.getUsername(), formBean.getPassword());
					
			// loose form to avoid loops
			formBean = (LoginForm)makeFormBean(cctx);
			cctx.setModel(formBean);
			
			// create the login context. We use a wrapper to be able to
			// react on session events
			LoginContext lc = new SessionLoginContext(logonModuleAlias, cbh);

			lc.login();
			// if we get here, login succeded
			Subject subject = lc.getSubject();
			// log login
			loginLog.info("subject " + subject + " logged on");
			// save subject in session
			session.setAttribute(AccessFilter.AUTHENTICATED_SUBJECT_KEY, subject);
			// bind LoginContext (wrapper) to session. By doing this, logoff 
			// will be called when the session times out.
			session.setAttribute("_loginContext", lc);

			Set set = subject.getPrincipals(UserPrincipal.class);
			if (!set.isEmpty())
			{
				UserPrincipal user = (UserPrincipal)set.toArray()[0];
				if (log.isDebugEnabled())
					log.debug(user + " logged on");
			}
			else
			{
				throw new LoginException(
					"de gebruiker was gevonden maar er is een " +
					"laad- of configuratiefout opgetreden!");
			}

		}
		catch (LoginException e)
		{
			// login failed. Log this with reason
			loginLog.info(
				"aanmelden voor gebruiker " + formBean.getUsername() 
				+ " is niet geslaagd");
			loginLog.info("reden: " + e.getMessage());
			formBean.setError(e.getMessage());
			// return to login form
			return ERROR;
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
			formBean.setError(e.getMessage());
			// return to login form
			return ERROR;
		}
		// redirect to last request
		String redir = (String)session.getAttribute(AccessFilter.LAST_REQUEST_KEY);
		if (redir != null)
		{
			session.removeAttribute(AccessFilter.LAST_REQUEST_KEY);
			redir = cctx.getResponse().encodeRedirectURL(redir);
		}
		else
		{
			redir = cctx.getResponse().encodeRedirectURL("index.m");
		}
		cctx.setModel(redir);
		// return redirect view (which in this case is SUCCESS)
		return SUCCESS;
	}
	
	/**
	 * validate the form
	 * @param ctx current maverick context
	 * @param formBean form
	 * @return true if validation succeeded, false otherwise
	 */
	protected boolean validateForm(ControllerContext ctx, AbstractForm formBean)
	{
		LoginForm form = (LoginForm)formBean;
		if (form.getUsername() == null || 
			form.getUsername().trim().equals(""))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	/** initialise */
	public void init(Element controllerNode) throws ConfigException
	{

		this.parameters = XML.getParams(controllerNode);
		this.logonModuleAlias = (String)parameters.get("loginModule");
		if (logonModuleAlias == null)
			throw new ConfigException(
				"loginModule is a mandatory parameter of this control");
	}

	/** create formBean */
	protected AbstractForm makeFormBean(ControllerContext cctx)
	{
		return new LoginForm();
	}
}
