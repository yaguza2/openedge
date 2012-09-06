/*
 *
 *    Artistic License
 *
 *    Preamble
 *
 *    The intent of this document is to state the conditions under which a Package may be copied, such that
 *    the Copyright Holder maintains some semblance of artistic control over the development of the
 *    package, while giving the users of the package the right to use and distribute the Package in a
 *    more-or-less customary fashion, plus the right to make reasonable modifications.
 *
 *    Definitions:
 *
 *    "Package" refers to the collection of files distributed by the Copyright Holder, and derivatives
 *    of that collection of files created through textual modification.
 *
 *    "Standard Version" refers to such a Package if it has not been modified, or has been modified
 *    in accordance with the wishes of the Copyright Holder.
 *
 *    "Copyright Holder" is whoever is named in the copyright or copyrights for the package.
 *
 *    "You" is you, if you're thinking about copying or distributing this Package.
 *
 *    "Reasonable copying fee" is whatever you can justify on the basis of media cost, duplication
 *    charges, time of people involved, and so on. (You will not be required to justify it to the
 *    Copyright Holder, but only to the computing community at large as a market that must bear the
 *    fee.)
 *
 *    "Freely Available" means that no fee is charged for the item itself, though there may be fees
 *    involved in handling the item. It also means that recipients of the item may redistribute it under
 *    the same conditions they received it.
 *
 *    1. You may make and give away verbatim copies of the source form of the Standard Version of this
 *    Package without restriction, provided that you duplicate all of the original copyright notices and
 *    associated disclaimers.
 *
 *    2. You may apply bug fixes, portability fixes and other modifications derived from the Public Domain
 *    or from the Copyright Holder. A Package modified in such a way shall still be considered the
 *    Standard Version.
 *
 *    3. You may otherwise modify your copy of this Package in any way, provided that you insert a
 *    prominent notice in each changed file stating how and when you changed that file, and provided that
 *    you do at least ONE of the following:
 *
 *        a) place your modifications in the Public Domain or otherwise make them Freely
 *        Available, such as by posting said modifications to Usenet or an equivalent medium, or
 *        placing the modifications on a major archive site such as ftp.uu.net, or by allowing the
 *        Copyright Holder to include your modifications in the Standard Version of the Package.
 *
 *        b) use the modified Package only within your corporation or organization.
 *
 *        c) rename any non-standard executables so the names do not conflict with standard
 *        executables, which must also be provided, and provide a separate manual page for each
 *        non-standard executable that clearly documents how it differs from the Standard
 *        Version.
 *
 *        d) make other distribution arrangements with the Copyright Holder.
 *
 *    4. You may distribute the programs of this Package in object code or executable form, provided that
 *    you do at least ONE of the following:
 *
 *        a) distribute a Standard Version of the executables and library files, together with
 *        instructions (in the manual page or equivalent) on where to get the Standard Version.
 *
 *        b) accompany the distribution with the machine-readable source of the Package with
 *        your modifications.
 *
 *        c) accompany any non-standard executables with their corresponding Standard Version
 *        executables, giving the non-standard executables non-standard names, and clearly
 *        documenting the differences in manual pages (or equivalent), together with instructions
 *        on where to get the Standard Version.
 *
 *        d) make other distribution arrangements with the Copyright Holder.
 *
 *    5. You may charge a reasonable copying fee for any distribution of this Package. You may charge
 *    any fee you choose for support of this Package. You may not charge a fee for this Package itself.
 *    However, you may distribute this Package in aggregate with other (possibly commercial) programs as
 *    part of a larger (possibly commercial) software distribution provided that you do not advertise this
 *    Package as a product of your own.
 *
 *    6. The scripts and library files supplied as input to or produced as output from the programs of this
 *    Package do not automatically fall under the copyright of this Package, but belong to whomever
 *    generated them, and may be sold commercially, and may be aggregated with this Package.
 *
 *    7. C or perl subroutines supplied by you and linked into this Package shall not be considered part of
 *    this Package.
 *
 *    8. The name of the Copyright Holder may not be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 *    9. THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 *    WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 *    MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package org.chiba.xml.xforms.xpath.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.commons.jxpath.ClassFunctions;
import org.apache.commons.jxpath.JXPathContext;
import org.chiba.xml.xforms.ChibaBean;
import org.chiba.xml.xforms.Instance;
import org.chiba.xml.xforms.config.Config;
import org.chiba.xml.xforms.test.XMLTestBase;
import org.chiba.xml.xforms.xpath.ChibaExtensionFunctions;
import org.w3c.dom.Document;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Tests chiba extension functions.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ChibaExtensionFunctionsTest.java,v 1.15 2004/08/15 14:14:16 joernt Exp $
 */
public class ChibaExtensionFunctionsTest extends XMLTestBase {

//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private ChibaBean chibaBean;
    private JXPathContext context;

    /**
     * Creates a new chiba extension functions test.
     *
     * @param name the test name.
     */
    public ChibaExtensionFunctionsTest(String name) {
        super(name);
    }

    /**
     * Runs the chiba extension functions test.
     *
     * @param args arguments are ignored.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Returns a test suite.
     *
     * @return a test suite.
     */
    public static Test suite() {
        return new TestSuite(ChibaExtensionFunctionsTest.class);
    }

    /**
     * Test case for chiba calculate extension function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testCalculate() throws Exception {
        double random = Math.random();
        String expected = String.valueOf(random);

        assertEquals(expected, this.context.getValue("chiba:calculate('test://some-arbitrary-test-resource/" + random + "')", String.class));
    }

    /**
     * Test case for chiba validate extension function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testValidate() throws Exception {
        double random = Math.random();
        Boolean expected = random > 0.5d ? Boolean.TRUE : Boolean.FALSE;

        assertEquals(expected, this.context.getValue("chiba:validate('test://some-arbitrary-test-resource/" + random + "')", Boolean.class));
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        Config.getInstance(getClass().getResource("ChibaExtensionFunctionsTestConfig.xml").getPath());
        this.chibaBean = new ChibaBean();
        this.chibaBean.setXMLContainer(getClass().getResourceAsStream("ChibaExtensionFunctionsTest.xml"));
        this.chibaBean.init();
        this.context = this.chibaBean.getContainer().getDefaultModel().getDefaultInstance().getInstanceContext();
        this.context.setFunctions(new ClassFunctions(ChibaExtensionFunctions.class, "chiba"));
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.chibaBean.shutdown();
        this.chibaBean = null;
        this.context = null;
    }

    public void testMatch() throws Exception {
        assertTrue(ChibaExtensionFunctions.match("cat", "cat", "i"));
        assertTrue(ChibaExtensionFunctions.match("cat", ".a.", "i"));
        assertTrue(ChibaExtensionFunctions.match("cat", "cat", null));
        assertFalse(ChibaExtensionFunctions.match("cat", "CAT", null));
        assertFalse(ChibaExtensionFunctions.match("cat", "catalog", null));
    }

    public void testFileSize() throws Exception {
        //determine size of test input file
        URL url = getClass().getResource("FileFunctionsTest.xml");
        File inFile = new File(url.getFile());
        String size = "" + inFile.length();

        ChibaBean chibaBean = initProcessor(inFile);

        Instance instance = chibaBean.getContainer().getDefaultModel().getDefaultInstance();
        JXPathContext context = JXPathContext.newContext(instance);
        String resultofCalc = (String) context.getValue("//filesize");

        assertEquals(size, resultofCalc);
    }

    public void testFileDate() throws Exception {
        //determine lastmodified date of test input file
        URL url = getClass().getResource("FileFunctionsTest.xml");
        File inFile = new File(url.getFile());
        long modified = inFile.lastModified();

        //test default date formatting when argument 'format' is omitted
        Calendar calendar = new GregorianCalendar(Locale.getDefault());
        calendar.setTimeInMillis(modified);

        SimpleDateFormat simple1 = new SimpleDateFormat("dd.MM.yyyy H:m:s");
        String s = simple1.format(calendar.getTime());

        ChibaBean chibaBean = initProcessor(inFile);

        Instance instance = chibaBean.getContainer().getDefaultModel().getDefaultInstance();
        JXPathContext context = JXPathContext.newContext(instance);
        String resultofCalc = (String) context.getValue("//filedate1");

//        DOMUtil.prettyPrintDOM(chibaBean.getContainer().getDefaultModel().getDefaultInstance().getInstanceDocument());
        assertEquals(s, resultofCalc);

        //test date formatting with given pattern
        SimpleDateFormat simple2 = new SimpleDateFormat("yyyy.MM.dd");
        s = simple2.format(calendar.getTime());

        resultofCalc = (String) context.getValue("//filedate2");
        assertEquals(s, resultofCalc);

    }

    private ChibaBean initProcessor(File inFile) throws Exception {
        Document inDocument = getXmlResource("FileFunctionsTest.xml");
        ChibaBean chibaBean = new ChibaBean();
        chibaBean.setXMLContainer(inDocument);

        //set the base URI for processor
        String file = inFile.getParentFile().toURL().toExternalForm();
        chibaBean.setBaseURI(file);
        chibaBean.init();
        return chibaBean;
    }


}

// end of class
