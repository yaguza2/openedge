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

import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.io.LineNumberReader;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.plus.Server;

/**
 * Monitor thread. This thread listens on the port specified by the STOP.PORT
 * system parameter (defaults to 8079) for request authenticated with the key
 * given by the STOP.KEY system parameter (defaults to "mortbay") for admin
 * requests. Commands "stop" and "status" are currently supported.
 *
 * Originele code meegeleverd met JettyServer. Aangepaste versie.
 */
public class JettyMonitor extends Thread {

    /** Listen port. */
    private int _port = Integer.getInteger("STOP.PORT", 8079).intValue();

    /** Key. */
    private String _key = System.getProperty("STOP.KEY", "mortbay");

    /** Log. */
    private Log log = LogFactory.getLog(JettyMonitor.class);

    /** socket voor commands. */
    private ServerSocket _socket;

    /** JettyServer instantie, zodat we (nog) schoner kunnen afsluiten */
    private Server server = null;
    
    /**
     * Hidden constructor.
     */
    private JettyMonitor() {
        try {
            if (_port < 0) return;
            setDaemon(true);
            _socket = new ServerSocket(_port, 1, InetAddress.getByName("127.0.0.1"));
            if (_port == 0) {
                _port = _socket.getLocalPort();
                System.out.println(_port);
            }
            if (!"mortbay".equals(_key)) {
                _key = Long.toString((long) (Long.MAX_VALUE * Math.random()), 36);
                log.debug("using key " + _key);
            }
        } catch (Exception e) {
            log.fatal(e.getMessage(), e);
            log.fatal("************ SHUTTING DOWN!");
            System.exit(1);
        }
        if (_socket != null) {
            this.start();
        } else {
            log.error("WARN: Not listening on monitor port: " + _port);
        }
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
        log.info("Starting Jetty Monitor");
        while (true) {
            Socket socket = null;
            try {
                socket = _socket.accept();

                LineNumberReader lin = new LineNumberReader(new InputStreamReader(socket.getInputStream()));
                String key = lin.readLine();
                if (!_key.equals(key)) {
                    log.warn("keys '" + _key + "' and '" + key + "' do not match!");
                    continue;
                }

                String cmd = lin.readLine();
                log.info("command = " + cmd);
                if ("stop".equals(cmd)) {
                    log.info("Shutting down");
                    try {
                        server.stop();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    try {
                        socket.close();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    try {
                        _socket.close();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    System.out.flush();
                    System.err.flush();
                    System.exit(0);
                } else if ("status".equals(cmd)) {
                    if((server != null) && server.isStarted()) {
                        socket.getOutputStream().write("OK\r\n".getBytes());   
                    } else {
                        if(server == null) {
                            log.debug("server is nog null");
                        } else {
                            log.debug("server is nog niet gestart");
                        }
                        socket.getOutputStream().write("STARTING\r\n".getBytes());
                    }
                    socket.getOutputStream().flush();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
                socket = null;
            }
        }
    }

    /**
     * Start a Monitor. This static method starts a monitor that listens for
     * admin requests.
     */
    public static void monitor(Server deServer) {
        
        JettyMonitor monitor = new JettyMonitor();
        monitor.setServer(deServer);
    }

    /**
     * Get server.
     * @return Server Returns the server.
     */
    public Server getServer() {
        return server;
    }
    /**
     * Set server.
     * @param server server to set.
     */
    public void setServer(Server server) {
        this.server = server;
    }
}