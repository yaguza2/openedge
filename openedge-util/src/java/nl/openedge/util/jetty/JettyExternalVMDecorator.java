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

import junit.framework.Test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Decorator for starting a Jetty instance in a seperate VM.
 *
 * @author Eelco Hillenius
 */
public class JettyExternalVMDecorator extends AbstractJettyDecorator
{
    /** logger. */
    private static Log log = LogFactory.getLog(JettyExternalVMDecorator.class);

    /**
     * command to execute; see Runtime.exec(String[]).
     * Eg {"cmd", "/C", "start", "java"} opens a new window on DOS systems using
     * that window for output, and {"java"} starts an invisible process where the
     * output will be intercepted and interleaved with the current output (commons logger).
     * Default == { "java" }.
     */
    private String[] startCommand = 
        new String[] { "java" };

    /** Remote proces. */
    private Process process = null;

    /** command port. */
    private int monitorPort = Integer.getInteger("STOP.PORT", 8079).intValue();

    /** auth key. */
    private String commKey = System.getProperty("STOP.KEY", "mortbay");

    /** adress of Jetty instance */
    private String host = "127.0.0.1";

    /**
     * Maximum number of ping tries.
     */
    private int maxTries = 60;

    /**
     * Miliseconds to wait between ping tries.
     */
    private long sleepBetweenTries = 1000;

    /**
     * Construct.
     *
     * @param test test case
     */
    public JettyExternalVMDecorator(final Test test)
    {
        super(test);
    }

    /**
     * Start Jetty.
     *
     * @throws Exception
     * @see junit.extensions.TestSetup#setUp()
     */
    public void setUp() throws Exception
    {
        try
        {
            if(process == null)
            {
                String[] startCommandWithArgs = Util.addCommandArguments(
                    startCommand, getJettyConfig(), getPort(),
                    getWebappContextRoot(), getContextPath(), isUseJettyPlus());

                JettyExternalVMStartupWorker worker = new JettyExternalVMStartupWorker(
                    startCommandWithArgs, monitorPort, commKey, maxTries, sleepBetweenTries);
                worker.start(); // start worker trhead
                worker.join(); // wait for worker to finish

                // throw exception if the worker was not able to start Jetty in time
                if(!worker.isJettyStarted())
                {
                    String msg = "Starting Jetty in a seperate VM failed";
                    throw new Exception(msg);
                }
                process = worker.getProcess(); // keep reference to external process
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Stop Jetty.
     * 
     * @throws Exception
     * @see junit.extensions.TestSetup#tearDown()
     */
    public void tearDown() throws Exception
    {
        JettyHelper.issueStopCommandToMonitor(commKey, host, monitorPort);
        int exitval;
        try
        {
            exitval = process.exitValue();
        }
        catch(IllegalThreadStateException e)
        {
            log.error(e.getMessage());
            log.error("process is still busy; wait for process to end...");
            exitval = process.waitFor();
        }
        log.info("process finished with exitcode " + exitval);
    }

    /**
     * Get commKey.
     * @return String Returns the commKey.
     */
    public String getCommKey()
    {
        return commKey;
    }
    /**
     * Set commKey.
     * @param commKey commKey to set.
     */
    public void setCommKey(String commKey)
    {
        this.commKey = commKey;
    }
    /**
     * Get host.
     * @return String Returns the host.
     */
    public String getHost()
    {
        return host;
    }
    /**
     * Set host.
     * @param host host to set.
     */
    public void setHost(String host)
    {
        this.host = host;
    }
    /**
     * Get monitorPort.
     * @return int Returns the monitorPort.
     */
    public int getMonitorPort()
    {
        return monitorPort;
    }
    /**
     * Set monitorPort.
     * @param monitorPort monitorPort to set.
     */
    public void setMonitorPort(int monitorPort)
    {
        this.monitorPort = monitorPort;
    }
    /**
     * Get maximum number of ping tries.
     * @return int Returns the maxTries.
     */
    public int getMaxTries()
    {
        return maxTries;
    }
    /**
     * Set maximum number of ping tries.
     * @param maxTries maximum number of ping tries to set.
     */
    public void setMaxTries(int maxTries)
    {
        this.maxTries = maxTries;
    }
    /**
     * Get sleepBetweenTries.
     * @return long Returns the sleepBetweenTries.
     */
    public long getSleepBetweenTries()
    {
        return sleepBetweenTries;
    }
    /**
     * Set sleepBetweenTries.
     * @param sleepBetweenTries sleepBetweenTries to set.
     */
    public void setSleepBetweenTries(long sleepBetweenTries)
    {
        this.sleepBetweenTries = sleepBetweenTries;
    }
    /**
     * Get command to execute; see Runtime.exec(String[]).
     * Eg {"cmd", "/C", "start", "java"} opens a new window on DOS systems using
     * that window for output, and {"java"} starts an invisible process where the
     * output will be intercepted and interleaved with the current output (commons logger).
     * Default == { "java" }.
     * @return String[] command to execute
     */
    public String[] getStartCommand()
    {
        return startCommand;
    }
    /**
     * Set command to execute; see Runtime.exec(String[]).
     * Eg {"cmd", "/C", "start", "java"} opens a new window on DOS systems using
     * that window for output, and {"java"} starts an invisible process where the
     * output will be intercepted and interleaved with the current output (commons logger).
     * Default == { "java" }.
     * @param startCommand command to execute
     */
    public void setStartCommand(String[] startCommand)
    {
        this.startCommand = startCommand;
    }
}