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
package nl.openedge.util.jetty;

import nl.openedge.util.net.HttpHelper;


/**
 * Test for JettyTestCase that spawns a new process/ new VM where Jetty will be started.
 *
 * @author Eelco Hillenius
 */
public class JettyExternalVMWithArgsTestCaseTest extends JettyExternalVMTestCase
{
    /**
     * Construct.
     */
    public JettyExternalVMWithArgsTestCaseTest()
    {
        super();
    }

    /**
     * Construct with name.
     * @param name test name
     */
    public JettyExternalVMWithArgsTestCaseTest(String name)
    {
        super(name);
    }

    /**
     * Test the ping page of the test webapp.
     * @throws Exception
     */
    public void testPing() throws Exception
    {
		String pingBody = HttpHelper.get("http://localhost:8098/test/ping.txt");
		assertEquals("hi!", pingBody);
    }

    /**
     * @see nl.openedge.util.jetty.JettyTestCase#beforeSetUp()
     */
    public void beforeSetUp()
    {
        setUseJettyPlus(true);
	    setPort(8098);
	    setWebappContextRoot("src/webapp");
	    setContextPath("/test");

	    // comment this for non-Windows systems (in that case {"java"} will be used.
	    //setStartCommand(new String[]{"cmd", "/C", "start", "java"});
    }

}
