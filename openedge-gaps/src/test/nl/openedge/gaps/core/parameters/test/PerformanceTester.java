/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c)
 * All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters.test;

import nl.openedge.gaps.core.parameters.Parameter;
import nl.openedge.gaps.support.ParameterBrowser;
import nl.openedge.gaps.support.ParameterBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Deze klasse voert performance testen uit.
 */
public abstract class PerformanceTester {

    /** Log. */
    private static Log log = LogFactory.getLog(PerformanceTester.class);

    /**
     * Construct.
     */
    public PerformanceTester() {
        //
    }

    /**
     * Test performance.
     * @param aantal aantal parameters
     * @throws Exception
     */
    public void testParameterToevoegingen(int aantal)
    	throws Exception {

        ParameterBuilder builder = new ParameterBuilder();
        builder.navigate("/:DEFAULT");
        ParameterBrowser browser = new ParameterBrowser();
        try {
            long begin = System.currentTimeMillis();
            Parameter param = null;
            int stepCounter = 0;
            for(int i = 0; i < aantal; i++) {
                String nbr = String.valueOf(i);
                param = builder.createString(nbr, nbr);
                if(stepCounter > 100) {
                    System.out.print(".");
                    stepCounter = 0;
                    builder.createParameterGroup("DEFAULT_" + i, "TEST_" + i, true);
                } else {
                    stepCounter++;
                }
            }
            long end = System.currentTimeMillis();
            log.info(aantal + " toevoegingen: " + ((end - begin) / 1000d) + " sec.");
            
            begin = System.currentTimeMillis();
            String pg = "/:DEFAULT/";
            stepCounter = 0;
            for(int i = 0; i < aantal; i++) {
                browser.navigate(pg + i + "@value");
                if(stepCounter > 100) {
                    System.out.print(".");
                    stepCounter = 0;
                    pg = "/:DEFAULT_" + i + "/";
                } else {
                    stepCounter++;
                }
            }
            end = System.currentTimeMillis();
            log.info(aantal + " opvragingen: " + ((end - begin) / 1000d) + " sec.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
