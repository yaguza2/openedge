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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Worker voor het opstarten van een remote uitvoering van Jetty.
 */
class JettyRemoteStartWorker extends Thread
{

    /** Log. */
    private static Log log = LogFactory.getLog(JettyRemoteStartWorker.class);

    /**
     * Is Jetty opgestart (en weten we dat).
     */
    private boolean jettyStarted = false;

    /**
     * Pointer naar opgestarte remote proces.
     */
    private Process process = null;

    /**
     * Max aantal keer dat we pingen.
     */
    private static final int MAX_TRIES = 30;

    /**
     * Aantal miliseconden dat we wachten tussen de pogingen in.
     */
    private static final int SLEEP_BETWEEN_TRIES = 1000;

    /** commando poort. */
    private int _port = Integer.getInteger("STOP.PORT", 8079).intValue();

    /** auth key. */
    private String _key = System.getProperty("STOP.KEY", "mortbay");

    /**
     * Ping Jetty tot succes of max tries.
     * 
     * @see java.lang.Runnable#run()
     */
    public void run()
    {

        try
        {
            startRemote();
        }
        catch(IOException e)
        {
            log.error(e.getMessage(), e);
        }
        try
        {
            // geef Jetty remote even de tijd om op te starten
            Thread.sleep(2000);
        }
        catch(InterruptedException e)
        {
            log.warn(e);
        }
        pingRemoteJetty();
    }

    /**
     * Start mbv systeem call een instantie van Jetty op.
     */
    private void startRemote() throws IOException
    {

        String classPath = System.getProperty("java.class.path");
        // voorlopig allen true mogelijk onder Windows
        boolean sepProces = false;

        String[] cmd = null;
        if(sepProces)
        {
            // met onderstaande cmd wordt een nieuw scherm geopend (dosbox)
            cmd = new String[] { "cmd", "/C", "start", "java", "-classpath", classPath, JettyStarterPrg.class.getName() };
        }
        else
        {
            // met onderstaand cmd wordt geen nieuw scherm geopend (met voordeel
            // dat dit ook op bijv. Linux kan werken), en wordt de logging
            // afgevangen
            // en in de huidige logging samengevoegd
            cmd = new String[] { "java", "-classpath", classPath, JettyStarterPrg.class.getName() };
        }

        log.info("executeer " + cmd);
        process = Runtime.getRuntime().exec(cmd);

        // LET OP: we krijgen alleen output als het process niet als
        // afzonderlijk
        // (dos cmd 'start') is opgestart.
        connectOutput(process);
    }

    /**
     * Connect output van het proces.
     * 
     * @param process het process
     */
    private void connectOutput(Process process)
    {
        InputStream errInput = process.getErrorStream();
        LogConnector errConn = new LogConnector();
        errConn.setIs(errInput);
        errConn.start();
        InputStream outInput = process.getInputStream();
        LogConnector outConn = new LogConnector();
        outConn.setIs(outInput);
        outConn.start();
    }

    /**
     * Ping remote Jetty server tot succes of 30 keer is geprobeerd met
     * tussenposen van 1 sec.
     * 
     * @return true indien in de lucht, anders false
     */
    private boolean pingRemoteJetty()
    {
        int tries = 0;
        while((!jettyStarted) && ((tries++) < MAX_TRIES))
        {
            try
            {
                Socket s = new Socket(InetAddress.getLocalHost(), _port);
                OutputStream out = s.getOutputStream();
                out.write((_key + "\r\nstatus\r\n").getBytes());
                out.flush();
                InputStream is = s.getInputStream();
                String result = readFromInputStream(is).trim();
                if("OK".equalsIgnoreCase(result))
                {
                    jettyStarted = true;
                    log.info("Jetty is opgestart");
                }
                else if("STARTING".equalsIgnoreCase(result))
                {
                    log.info("wacht tot Jetty is opgestart");
                }
                else
                {
                    String msg = "Onbekend antwoord van socket ontvangen: " + result;
                    log.error(msg);
                    return false;
                }
                s.shutdownOutput();
                s.close();
                try
                {
                    Thread.sleep(SLEEP_BETWEEN_TRIES);
                }
                catch(InterruptedException e1)
                {
                    log.warn(e1);
                }
            }
            catch(Exception e)
            {
                log.error(e.getMessage(), e);
            }
        }
        return jettyStarted;
    }

    /**
     * Lees string uit inputstream.
     * 
     * @param is inputstream
     * @return String
     * @throws Exception
     */
    private String readFromInputStream(InputStream is) throws Exception
    {
        StringBuffer b = new StringBuffer();
        int i;
        while((i = is.read()) != -1)
        {
            b.append((char)i);
        }
        return b.toString();
    }

    /**
     * Get jettyStarted.
     * 
     * @return boolean Returns the jettyStarted.
     */
    public boolean isJettyStarted()
    {
        return jettyStarted;
    }

    /**
     * Set jettyStarted.
     * 
     * @param jettyStarted jettyStarted to set.
     */
    public void setJettyStarted(boolean jettyStarted)
    {
        this.jettyStarted = jettyStarted;
    }

    /**
     * Get process.
     * 
     * @return Process Returns the process.
     */
    public Process getProcess()
    {
        return process;
    }

    /**
     * Set process.
     * 
     * @param process process to set.
     */
    public void setProcess(Process process)
    {
        this.process = process;
    }
}