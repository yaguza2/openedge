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

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;
import nl.openedge.gaps.core.versions.Version;
import nl.openedge.gaps.core.versions.VersionRegistry;
import nl.openedge.gaps.util.CustomOracle9Dialect;
import nl.openedge.util.hibernate.HibernateHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Tests voor {@link nl.openedge.gaps.core.parameters.ParameterRegistry}.
 */
public class OracleParameterTest extends AbstractParameterTest {

    /** Log. */
    private static Log log = LogFactory.getLog(OracleParameterTest.class);

    /**
     * Construct.
     */
    public OracleParameterTest() {
        // noop
    }

    /**
     * Construct.
     * @param name
     */
    public OracleParameterTest(String name) {
        super(name);
    }

    /**
     * Suite methode.
     * @return Test suite
     */
    public static Test suite() throws Exception {
	    TestSuite suite = new TestSuite();
	    suite.addTest(new OracleParameterTest("testGroepen"));
	    suite.addTest(new OracleParameterTest("testSaveGetDelParameter"));
	    suite.addTest(new OracleParameterTest("testBuildString"));
	    suite.addTest(new OracleParameterTest("testBuildPercentage"));
	    suite.addTest(new OracleParameterTest("testBuildBoolean"));
	    suite.addTest(new OracleParameterTest("testBuildDate"));
	    suite.addTest(new OracleParameterTest("testBuildFixedSet"));
	    suite.addTest(new OracleParameterTest("testParameterInExpressie"));
//	    suite.addTest(new HSQLParameterTest("testNavigatie"));
//	    suite.addTest(new HSQLParameterTest("testParameterGroepOvererving"));
	    TestSetupDeco testDeco = new TestSetupDeco(suite);
	    return testDeco;
    }

    /**
     * Setup decorator voor het opzetten van de registries met dummy implementaties.
     */
    private static class TestSetupDeco extends TestSetup {

        /** log. */
        private static Log log = LogFactory.getLog(TestSetupDeco.class);

        /**
         * @param test
         */
        public TestSetupDeco(Test test) {
            super(test);
        }

        /**
         * @see junit.extensions.TestSetup#setUp()
         */
        protected void setUp() throws Exception {

            try {
                String configFile = "/hibernate-oracle.cfg.xml";
                Util.initAndCreateDB(configFile, new CustomOracle9Dialect());

                SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
                Date vdate = Util.getTestDatum();
                Version version = VersionRegistry.createVersion(vdate, sdf.format(vdate), null);
                version.setGoedgekeurd(true);
                VersionRegistry.updateVersion(version);

            } catch (Throwable e) {
                log.error(e.getMessage(), e);
                throw new Exception(e);
            }
        }

        /**
         * @see junit.extensions.TestSetup#tearDown()
         */
        protected void tearDown() throws Exception {
            HibernateHelper.closeSession();
        }
    }
}
