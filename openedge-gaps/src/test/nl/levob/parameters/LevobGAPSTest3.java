/*
 * Project: Allureplan-rekenmodule
 * Door: Levob Java Ontwikkelteam
 *
 * $Id$
 * $Revision$
 * $Date$
 * 
 * ================================================================================
 * Copyright (c) 2004, Levob Bank en Verzekeringen
 * Alle rechten voorbehouden.
 */
package nl.levob.parameters;

import junit.framework.TestCase;
import nl.levob.parameters.hibernate.TestHibernate;
import nl.openedge.gaps.core.groups.ParameterGroup;
import nl.openedge.gaps.core.groups.StructuralGroup;
import nl.openedge.gaps.core.parameters.Parameter;
import nl.openedge.gaps.core.parameters.impl.NestedParameter;
import nl.openedge.gaps.support.ParameterBrowser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Test een paar basale GAPS zaken zoals of er uberhaupt een vulling is, en of
 * er een root group is met kinderen.
 * Test is diabled om maven jar:install te kunnen draaien
 */
public class LevobGAPSTest3 /* extends TestHibernate */ extends TestCase {

    /** log. */
    private static Log log = LogFactory.getLog(LevobGAPSTest3.class);

    /**
     * Construct.
     */
    public LevobGAPSTest3() {
        super();
    }

    /** test performance geneste parameters. */
    public void testPerformance() {
        /*
        String[] tabellen = { "GBM6165", "GBM7175", "GBM7680", "GBM8085",
                "GBM8590", "GBM9095", "GBV7175", "GBV7680", "GBV8085",
                "GBV8590", "GBV9095" };
        ParameterBrowser browser = new ParameterBrowser();
        log.info("start testen performance geneste parameters");
        long outerBegin = System.currentTimeMillis();
        StructuralGroup tafels = (StructuralGroup) browser
                .navigate("/algemeen/tabellen");
        assertNotNull(tafels);
        ParameterGroup[] gbmgbv = tafels.getParameterChilds();
        assertTrue(gbmgbv.length > 0);
        ParameterGroup group = (ParameterGroup) browser
                .navigate("/algemeen/tabellen:sterftetafels");
        assertNotNull(group);
        Parameter[] outerParams = group.getParameters();
        assertEquals(11, outerParams.length);
        int nestedCount = 0;
        for (int i = 0; i < outerParams.length; i++) {
            if (outerParams[i] instanceof NestedParameter) {
                nestedCount++;
                long begin = System.currentTimeMillis();
                assertEquals(outerParams[i].getLocalId(), tabellen[i]);
                Parameter[] params = ((NestedParameter) outerParams[i])
                        .getNested();
                for (int j = 0; j < params.length; j++) {
                    params[j].getValue().getValue();
                }
                long end = System.currentTimeMillis();
                log.info("\tophalen van " + params.length
                        + " geneste parameters duurde " + (end - begin)
                        + " miliseconden");
            }
        }
        long outerEnd = System.currentTimeMillis();
        log.info("ophalen " + nestedCount + " rijen duurde "
                + Math.round((outerEnd - outerBegin) / 1000) + " seconden");
        */
        assertTrue(true);
    }
}