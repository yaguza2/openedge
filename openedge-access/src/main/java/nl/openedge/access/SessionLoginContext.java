/*
 * $Header$
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
package nl.openedge.access;

import javax.security.auth.Subject;
import javax.security.auth.login.*;
import javax.security.auth.callback.CallbackHandler;
import javax.servlet.http.*;

/**
 * The SessionLoginContext extends the JAAS LoginContext, so that,
 * when bound to an HttpSession, it will execute logout() when the
 * session times out. One can bind the SessionLoginContext to the 
 * session, by calling<br><pre>
 *      session.setAttribute("loginContext", myLoginContext);
 * </pre>
 *
 * @author E.F. Hillenius
 */

public class SessionLoginContext extends LoginContext 
		implements HttpSessionBindingListener {

    /**
     * Default constructor. See javax.security.auth.login.LoginContext
     * for details.
     */
    public SessionLoginContext(String name) throws LoginException {
        super(name);
    }

    /**
     * Default constructor. See javax.security.auth.login.LoginContext
     * for details.
     */
    public SessionLoginContext(String name, CallbackHandler callbackHandler) throws LoginException {
        super(name, callbackHandler);
    }

    /**
     * Default constructor. See javax.security.auth.login.LoginContext
     * for details.
     */
    public SessionLoginContext(String name, Subject subject) throws LoginException {
        super(name, subject);
    }

    /**
     * Default constructor. See javax.security.auth.login.LoginContext
     * for details.
     */
    public SessionLoginContext(String name, Subject subject, CallbackHandler callbackHandler) throws LoginException {
        super(name, subject, callbackHandler);
    }

    /**
     * Notifies the object that it is being bound to a
     * session and identifies the session.
     *
     * @param event the event that identifies the session
     */
    public void valueBound(HttpSessionBindingEvent event) {
        /*try {
            login();
        } catch (LoginException ex) {
            throw new java.lang.RuntimeException(ex.getMessage());
        }*/
    }

    /**
     * Notifies the object that it is being unbound from a
     * session and identifies the session.
     *
     * @param event the event that identifies the session
     */
    public void valueUnbound(HttpSessionBindingEvent event) {
        try {
            logout();
        } catch (LoginException ex) {
            throw new java.lang.RuntimeException(ex.getMessage());
        }
    }
}

