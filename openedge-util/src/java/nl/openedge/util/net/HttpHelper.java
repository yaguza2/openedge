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
package nl.openedge.util.net;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;


/**
 * Helper class for Http related things.
 *
 * @author Eelco Hillenius
 */
public final class HttpHelper
{

    /**
     * Hidden constructor
     */
    private HttpHelper()
    {
        super();
    }

    /**
     * Execute http get with given url and return the result body as a string.
     * @param url url to get
     * @return result body as a string (or null)
     * @throws HttpHelperException when an unexpected exception occurs
     */
    public static String get(String url) throws HttpHelperException
    {
		GetMethod get = null;
		HttpClient client = new HttpClient(new SimpleHttpConnectionManager());
		String body = null;
		try 
		{
			get = new GetMethod(url);
			int resultcode = client.executeMethod(get);
			body = get.getResponseBodyAsString();
			if(resultcode != HttpStatus.SC_OK)
			{
				throw new HttpHelperException("resultcode was " + resultcode +
					", error:\n" + body);		
			}
		} 
		catch (Exception e) 
		{
			throw new HttpHelperException(e);
		} 
		finally 
		{
			get.releaseConnection();
		}
		return body;
    }
}
