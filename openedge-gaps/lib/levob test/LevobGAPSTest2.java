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

import java.util.List;

import junit.framework.TestCase;

import nl.levob.parameters.hibernate.TestHibernate;
import nl.openedge.gaps.core.groups.StructuralGroup;
import nl.openedge.gaps.core.groups.StructuralRootGroup;
import nl.openedge.gaps.support.ParameterBrowser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Test een paar basale GAPS zaken zoals of er uberhaupt een vulling is, en of
 * er een root group is met kinderen.
 */
public class LevobGAPSTest2 extends TestHibernate {

    /** log. */
    private static Log log = LogFactory.getLog(LevobGAPSTest2.class);

    /**
     * Construct.
     */
    public LevobGAPSTest2() {
        super();
    }

    /**
     * Test of de root groep gevonden kan worden en kinderen heeft.
     */
    public void testRootGroupHasChilds() {
        ParameterBrowser browser = new ParameterBrowser();
        Object pos = browser.navigate("/");
        assertNotNull(pos);
        assertTrue(pos instanceof StructuralRootGroup);
        StructuralGroup group = (StructuralGroup) pos;
        List structChildIds = group.getStructuralChildIds();
        assertNotNull(structChildIds);
        assertFalse(structChildIds.isEmpty());
        StructuralGroup[] childs = group.getStructuralChilds();
        assertNotNull(childs);
        assertEquals(3, childs.length);
    }
}