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

import javax.security.auth.callback.*;

/**
 * <p>
 * AccessCallbackHandler has constructor that takes
 * a username and password so its handle() method does
 * not have to prompt the user for input.
 * Useful for server-side applications.
 * 
 * Besides this the default username/ password, additional fields can be set
 * using the delegate construction
 *
 * @author Eelco Hillenius
 */

public class AccessCallbackHandler implements CallbackHandler {

    private String username;
    char[] password;

	/**
	 * Creates a callback handler with the give username
     * and password.
	 * @param user username
	 * @param pass password
	 */
    public AccessCallbackHandler(String user, String pass) {
    	
        this.username = user;
        this.password = (pass != null) ? pass.toCharArray() : null;
    }

    /**
     * Handles the specified set of Callbacks. Uses the
     * username and password that were supplied to our
     * constructor to popluate the Callbacks.
     *
     * This class supports NameCallback and PasswordCallback.
     *
     * @param   callbacks the callbacks to handle
     * @throws  IOException if an input or output error occurs.
     * @throws  UnsupportedCallbackException if the callback is not an
     * instance of NameCallback or PasswordCallback
     */
    public void handle(Callback[] callbacks)
        throws java.io.IOException, UnsupportedCallbackException {
        	
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof NameCallback) {
                ((NameCallback)callbacks[i]).setName(username);
            } else if (callbacks[i] instanceof PasswordCallback) {
                ((PasswordCallback)callbacks[i]).setPassword(password);
            } else {
                throw new UnsupportedCallbackException(
                            callbacks[i], "Callback class not supported");
            }
        }
    }

    /**
     * Clears out password state.
     */
    public void clearPassword() {
        if (password != null) {
            for (int i = 0; i < password.length; i++)
                password[i] = ' ';
            password = null;
        }
    }

}
