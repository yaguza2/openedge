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

import java.net.URL;

import junit.extensions.TestSetup;
import junit.framework.Test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.plus.Server;

/**
 * JUnit decorator die Jetty Opstart
 *
 * @author Eelco Hillenius
 */
public class JettyDecorator extends TestSetup {

    /** instance of jetty server. */
    private static Server jettyServer = null;
    /** aantal tests dat nu draait. */
    private static int aantalTests = 0;

    /** jetty configuration document */
    private String jettyConfig = "/jetty-test-config.xml";

    /** logger. */
    private static Log log = LogFactory.getLog(JettyDecorator.class);

    /**
     * construct.
     * @param test test case
     */
    public JettyDecorator(final Test test) {
        super(test);
    }

    /**
     * Start Jetty.
     * @throws Exception
     * @see junit.extensions.TestSetup#setUp()
     */
    public void setUp() throws Exception {
        aantalTests++;
        if (jettyServer != null) {
            return;
        }
        // start Jetty
        boolean useLocal = Boolean.getBoolean("local");
        URL jettyConfigURL = null;
        jettyConfigURL = JettyDecorator.class.getResource(jettyConfig);
        try {
            log.info("Start Jetty op basis van configuratie " + jettyConfigURL);
            jettyServer = new Server(jettyConfigURL);
            log.info("Jetty geconfigureerd");
            jettyServer.start();
            log.info("Jetty opgestart");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Stop Jetty.
     * @throws Exception
     * @see junit.extensions.TestSetup#tearDown()
     */
    public void tearDown() throws Exception {
        aantalTests--;
        if (aantalTests != 0) {
            return;
        }
        try {
            log.info("Jetty afsluiten");
            jettyServer.stop();
            log.info("Jetty afgesloten");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get jettyConfig.
     * @return String Returns the jettyConfig.
     */
    public String getJettyConfig()
    {
        return jettyConfig;
    }
    /**
     * Set jettyConfig.
     * @param jettyConfig jettyConfig to set.
     */
    public void setJettyConfig(String jettyConfig)
    {
        this.jettyConfig = jettyConfig;
    }
}
