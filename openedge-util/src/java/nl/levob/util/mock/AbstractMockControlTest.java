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
package nl.levob.util.mock;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;

import junit.framework.TestCase;
import nl.openedge.baritus.ConverterRegistry;
import nl.openedge.baritus.converters.DateLocaleConverter;
import nl.openedge.util.baritus.converters.FallbackDateConverter;

import com.mockobjects.servlet.MockHttpSession;
import com.mockobjects.servlet.MockRequestDispatcher;
import com.mockobjects.servlet.MockServletConfig;
import com.mockobjects.servlet.MockServletContext;

/**
 * Basis test klasse voor het schrijven van Unit testen met mock objects.
 * Gebruik dit voor het testen van controllers, validators, populators, etc.
 * waarbij de view niet relevant is.
 * @author Eelco Hillenius
 */
public class AbstractMockControlTest extends TestCase {

    /**
     * Session key voor de huidige locale; dit is een constante van Baritus.
     */
    public static final String SESSION_KEY_CURRENT_LOCALE = "_currentLocale";

    /**
     * De nederlandse locale.
     */
    protected Locale nederlandseLocale = new Locale("nl", "NL");

    /**
     * Refentie naar mock request dispatcher.
     */
    protected MockRequestDispatcher requestDispatcher = null;

    /**
     * Refentie naar mock servlet context.
     */
    protected MockServletContext servletContext = null;

    /**
     * Referentie naar mock servlet config.
     */
    protected MockServletConfig servletConfig = null;

    /**
     * Referentie naar mock http sessie.
     */
    protected MockHttpSession session = null;

    /**
     * Refentie naar mock servlet response.
     */
    protected MockHttpServletResponse response = null;

    /**
     * Refentie naar mock servlet request.
     */
    protected MockHttpServletRequest request = null;

    /**
     * Construct leeg.
     */
    public AbstractMockControlTest() {
        super();
    }

    /**
     * Construct test met naam.
     * @param name naam van de test
     */
    public AbstractMockControlTest(final String name) {
        super(name);
    }

    /**
     * Bereid de unit test voor door het creeeren/ registreren van de relevante
     * mock objecten.
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        this.requestDispatcher = new MockRequestDispatcher();
        this.servletContext = new MockServletContext();
        this.servletContext.setupGetRequestDispatcher(requestDispatcher);
        this.servletConfig = new MockServletConfig();
        this.servletConfig.setServletContext(servletContext);
        this.session = new MockHttpSession();
        this.session.setupGetAttribute(SESSION_KEY_CURRENT_LOCALE,
                nederlandseLocale);

        this.session.setupServletContext(servletContext);
        this.response = new MockHttpServletResponse();
        this.request = new MockHttpServletRequest();
        this.request.setupGetAttribute("__formBeanContext");
        this.request.setSession(session);
        this.request.setupGetRequestDispatcher(requestDispatcher);
    }

    /**
     * Registreer de converters voor dit project.
     */
    protected void initConverters() {
        // get the converter registry
        ConverterRegistry reg = ConverterRegistry.getInstance();
        reg.deregisterByConverterClass(DateLocaleConverter.class);
        reg.register(new FallbackDateConverter(), Date.class);
        reg.register(new FallbackDateConverter(), java.sql.Date.class);
        reg.register(new FallbackDateConverter(), Timestamp.class);
    }

}