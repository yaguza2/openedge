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

import nl.openedge.util.hibernate.HibernateHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.plus.Server;


/**
 * Opstartklasse zodat we Jetty in een aparte VM kunnen starten.
 *
 * @author Eelco Hillenius
 */
public class JettyStarterPrg {

    /** Logger. */
    private static Log log = LogFactory.getLog(JettyStarterPrg.class);
    
    /**
     * Start Jetty op.
     * @param args prg argumenten
     */
    public static void main(String[] args) {

        log.info("remote start van Jetty");

        URL jettyConfig = null;
        Server jettyServer = null;
        URL url = JettyStarterPrg.class.getResource("/hibernate.cfg.xml");
        HibernateHelper.setConfigURL(url);
        jettyConfig = JettyStarterPrg.class.getResource("/jetty-test-config.xml");
        try {
            log.info("Start Jetty op basis van configuratie " + jettyConfig);
            jettyServer = new Server(jettyConfig);

            JettyMonitor.monitor(jettyServer); // start monitor voor afsluiten via socket

            log.info("Jetty geconfigureerd");
            jettyServer.start();
            log.info("Jetty opgestart");

        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }
}
