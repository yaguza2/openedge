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

import java.security.Principal;


/**
 * 
 * Logon decorators can decorate principals.
 * Classes implementing this interface should be configured in
 * the JAAS configuration file.
 * 
 * The configured decorators are evaluated in the commit() method of the 
 * LoginModule for all &lt;code&gt;Principal&lt;/code&gt;s. The returned principals 
 * that can be an 'enriched' instance of the given principal are stored with the
 * subject. DO NOT change the name of the principals! This makes it other 
 * principals effectively. The only reason that default constructors and 
 * &lt;code&gt;setName(String)&lt;/code&gt; are supported in principals is to allow them 
 * to be handled by persistence engines like OJB.
 * 
 * The resulting Principals will be stored with the subject.
 * 
 * Note that, allthough the implementations of OpenEdge Access do support
 * &lt;code&gt;LoginDecorator&lt;/code&gt;s, the Implementors of the LogonModule 
 * may not support it.
 * 
 * @author Eelco Hillenius
 */
public interface LoginDecorator {

	/**
	 * get extra principals for this subject
	 * @param subject immutable set of principals
	 * @return the decorated principals to be saved with the subject.
	 */
	public Principal[] decorate(final Principal[] principal);

}
