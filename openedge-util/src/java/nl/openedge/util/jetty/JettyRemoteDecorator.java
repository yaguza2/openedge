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

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;

import junit.extensions.TestSetup;
import junit.framework.Test;
import net.sf.hibernate.Session;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.dialect.HSQLDialect;
import nl.openedge.util.hibernate.HibernateHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.plus.Server;

/**
 * Decorator voor opstarten lokale en remote Jetty instantie.
 * @author Eelco Hillenius
 */
public class JettyRemoteDecorator extends TestSetup {

    /** aantal tests dat nu draait. */
    private static int aantalTests = 0;

    /** instance of jetty server. */
    private static Server jettyServer = null;

    /** logger. */
    private static Log log = LogFactory.getLog(JettyRemoteDecorator.class);

    /** Remote proces. */
    private Process process = null;
    
    /** commando poort. */
    private int _port = Integer.getInteger("STOP.PORT", 8079).intValue();

    /** auth key. */
    private String _key = System.getProperty("STOP.KEY", "mortbay");

    /**
     * construct.
     * @param test test case
     */
    public JettyRemoteDecorator(final Test test) {
        super(test);
    }

    /**
     * Start Jetty.
     * @throws Exception
     * @see junit.extensions.TestSetup#setUp()
     */
    public void setUp() throws Exception {
        aantalTests++;
        try {
            if (process == null) {

                JettyRemoteStartWorker worker = new JettyRemoteStartWorker();
                worker.start(); // start workertrhead
                worker.join(); // laat deze thread wachten op de worker

                // gooi een exception indien Jetty niet is gestart
                if(!worker.isJettyStarted()) {
                    String msg = "Jetty kon niet (op tijd) worden opgestart";
                    throw new Exception(msg);
                }

                process = worker.getProcess(); // worker houdt instantie bij van remote proces
            }
            if (jettyServer == null) {
                // start Jetty
                URL jettyConfig = null;
                URL url = JettyRemoteDecorator.class.getResource("/hibernate-hsql.cfg.xml");
                HibernateHelper.setConfigURL(url);
                jettyConfig = JettyRemoteDecorator.class.getResource("/jetty-test-local-config.xml");
                try {
                    setupDB();
                    log.info("Start Jetty op basis van configuratie " + jettyConfig);
                    jettyServer = new Server(jettyConfig);
                    log.info("Jetty geconfigureerd");
                    jettyServer.start();
                    log.info("Jetty opgestart");
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        if (aantalTests != 0) { return; }
        
        stopRemoteJetty();
        int exitval;
        try {
            // even wachten om nog wat evt uitvoer te kunnen opvangen
            Thread.sleep(500);
            exitval = process.exitValue();
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            log.error("proces is nog steeds bezig; wacht op einde proces...");
            exitval = process.waitFor();
        }
        log.info("proces klaar met exitcode " + exitval);
        
        if (jettyServer != null) {
            try {
                log.info("Jetty afsluiten");
                jettyServer.stop();
                log.info("Jetty afgesloten");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    /**
     * Stop Jetty remote dmv socket aanroep.
     */
    private void stopRemoteJetty() {
        try {
            Socket s = new Socket(InetAddress.getByName("127.0.0.1"), _port);
            OutputStream out = s.getOutputStream();
            out.write((_key + "\r\nstop\r\n").getBytes());
            out.flush();
            s.shutdownOutput();
            s.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Creer DB
     */
    private void setupDB() throws Exception {
        log.info("creeer DB");
        HibernateHelper.init();
        Configuration hibConfig = HibernateHelper.getConfiguration();
        String[] drops = hibConfig.generateDropSchemaScript(new HSQLDialect());
        String[] creates = hibConfig.generateSchemaCreationScript(new HSQLDialect());
        executeScripts(drops);
        executeScripts(creates);
    }

    /**
     * Voer scripts uit.
     * @param scripts de scripts
     */
    private void executeScripts(String[] scripts) throws Exception {

        Session session = null;
        try {
            session = HibernateHelper.getSession();
            Connection conn = session.connection();
            for (int i = 0; i < scripts.length; i++) {
                String script = scripts[i];
                log.info("executeer " + script);
                try {
                    Statement stmt = conn.createStatement();
                    stmt.execute(script);
                    conn.commit();
                } catch (Exception e) {
                    log.error("\n\t\texecution failed: " + e.getMessage() + "\n");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            HibernateHelper.closeSession();
        }
    }

}