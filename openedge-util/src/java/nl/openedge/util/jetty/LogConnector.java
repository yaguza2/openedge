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

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author Eelco Hillenius
 */
public class LogConnector extends Thread {

    private InputStream is;
    private StringBuffer buff = new StringBuffer();
    private Log log = LogFactory.getLog(LogConnector.class);

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
        while(true) {
            int c;
            char chr;
            try {
                while((c = is.read()) != -1) {
                    chr = (char)c;
                    if(chr == '\n') {
                        buff.insert(0, "** REMOTE ** >");
                        log.info(buff.toString());
                        buff.delete(0, buff.length());
                    } else {
                        buff.append(chr);
                    }
                }
                Thread.sleep(100);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                if(buff.length() > 0) {
                    log.info(buff.toString());
                }
                break;
            }
        }
    }

    /**
     * Get is.
     * @return InputStream Returns the is.
     */
    public InputStream getIs() {
        return is;
    }
    /**
     * Set is.
     * @param is is to set.
     */
    public void setIs(InputStream is) {
        this.is = is;
    }
}
