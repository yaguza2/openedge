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

import nl.openedge.maverick.framework.AbstractForm;

/**
 * bean to store login related info
 * 
 * @author Hillenius
 */
public class LoginForm extends AbstractForm
{

	/* username for login */
	private String username;

	/* password for login */
	private String password;

	/* error message if set */
	private String error;

	/**
	 * @return String
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * @return String
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * Sets the password.
	 * @param password The password to set
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * Sets the username.
	 * @param username The username to set
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}
	/**
	 * @return String
	 */
	public String getError()
	{
		return error;
	}

	/**
	 * Sets the error.
	 * @param error The error to set
	 */
	public void setError(String error)
	{
		this.error = error;
	}

}
