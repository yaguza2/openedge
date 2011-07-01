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

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Server;

/**
 * Testcase for JettyHelper.
 * 
 * @author Eelco Hillenius
 */
public class JettyHelperTest extends TestCase
{
	/** Log. */
	private Log log = LogFactory.getLog(JettyHelperTest.class);

	/**
	 * Localhost constant.
	 */
	private final static String LOCALHOST = "127.0.0.1";

	/**
	 * Construct.
	 */
	public JettyHelperTest()
	{
		super();
	}

	/**
	 * Construct with test name.
	 * 
	 * @param name
	 *            name of test
	 */
	public JettyHelperTest(String name)
	{
		super(name);
	}

	/**
	 * Test local (in this VM) startup of Jetty and monitor and shutdown.
	 * 
	 * @throws Exception
	 */
	public void testLocalStartupAndShutdownWithMonitor() throws Exception
	{
		String testMonitorCommKey = "mortbay";
		int testMonitorPort = 8078;
		int jettyServerPort = 8099;
		String webContextRoot = "src/webapp";
		String contextPath = "/test";

		// start server
		Server server = JettyHelper.startJetty(jettyServerPort, webContextRoot, contextPath, false);

		// start monitor
		JettyMonitor.startMonitor(server, testMonitorCommKey, testMonitorPort);

		Thread.sleep(1000); // wait a bit longer to be sure that everything is really up

		// ping for startup
		boolean started = JettyHelper.pingMonitorForServerStarted(testMonitorCommKey, LOCALHOST,
				testMonitorPort, 30, 1000);
		assertTrue(started);
		log.info("Stopping Jetty");

		// issue stop command; throws exception when issueing failed
		JettyHelper.issueStopCommandToMonitor(testMonitorCommKey, LOCALHOST, testMonitorPort);
		log.info("Jetty stopped");
	}

}