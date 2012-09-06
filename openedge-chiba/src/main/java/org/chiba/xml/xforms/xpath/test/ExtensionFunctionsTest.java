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
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.chiba.xml.xforms.ChibaBean;
import org.chiba.xml.xforms.xpath.XPathExtensionFunctions;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Test cases for extension functions.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ExtensionFunctionsTest.java,v 1.15 2004/11/14 02:09:32 joernt Exp $
 */
public class ExtensionFunctionsTest extends TestCase {

    JXPathContext context;

    /**
     * Creates a new ExtensionFunctionsTest object.
     *
     * @param name __UNDOCUMENTED__
     */
    public ExtensionFunctionsTest(String name) {
        super(name);
    }

    /**
     * __UNDOCUMENTED__
     *
     * @param args __UNDOCUMENTED__
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * __UNDOCUMENTED__
     *
     * @return __UNDOCUMENTED__
     */
    public static Test suite() {
        return new TestSuite(ExtensionFunctionsTest.class);
    }


    public void testInstance() throws Exception {
        Document inDocument = getXmlResource("instance-test.xml");

        ChibaBean chibaBean = new ChibaBean();
        chibaBean.setXMLContainer(inDocument);
        chibaBean.init();

//        XPathExtensionFunctions functions = new XPathExtensionFunctions(chibaBean.getContainer().getDefaultModel());
        XPathExtensionFunctions functions = new XPathExtensionFunctions();
        functions.setNamespaceContext(chibaBean.getContainer().getDefaultModel().getDefaultInstance().getElement());

        JXPathContext context = chibaBean.getContainer().getDefaultModel().getDefaultInstance().getInstanceContext();
        context.setFunctions(functions);

        Object o = context.getValue("instance('first')/some-dummy");
        assertTrue(o.equals("some dummy value"));

        o = context.getValue("xforms:instance('first')/some-dummy");
        assertTrue(o.equals("some dummy value"));

        o = context.getValue("instance('second')/.");
        assertTrue(o.equals("another dummy value"));

        Pointer pointer = context.getPointer("instance('second')/.");
        assertEquals("another dummy value", pointer.getValue());
        assertEquals("/another-dummy[1]", pointer.asPath());

        o = context.getValue("xforms:instance('second')/.");
        assertTrue(o.equals("another dummy value"));
    }

    /**
     * __UNDOCUMENTED__
     *
     * @throws Exception __UNDOCUMENTED__
     */
    public void testAvg() throws Exception {
        Double avg = (Double) context.getValue("avg(/data/repeatdata/input)", Double.class);

        assertNotNull(avg);
        assertTrue(avg.equals(new Double(8.5d)));
    }

    /**
     * __UNDOCUMENTED__
     *
     * @throws Exception __UNDOCUMENTED__
     */
    public void testBooleanFromString() throws Exception {
    }

    /**
     * __UNDOCUMENTED__
     *
     * @throws Exception __UNDOCUMENTED__
     */
    public void testCountNonEmpty() throws Exception {
        Integer count_non_empty = (Integer) this.context.getValue("count-non-empty(/data/*)", Integer.class);

        assertNotNull(count_non_empty);
        assertTrue(count_non_empty.equals(new Integer(3)));
    }

    /**
     * __UNDOCUMENTED__
     *
     * @throws Exception __UNDOCUMENTED__
     */
    public void testIf() throws Exception {
    }

    /**
     * __UNDOCUMENTED__
     *
     * @throws Exception __UNDOCUMENTED__
     */
    public void testMax() throws Exception {
        Double max = (Double) this.context.getValue("max(/data/repeatdata/input)", Double.class);

        assertNotNull(max);
        assertTrue(max.equals(new Double(20.0d)));
    }

    /**
     * __UNDOCUMENTED__
     *
     * @throws Exception __UNDOCUMENTED__
     */
    public void testMin() throws Exception {
        Double min = (Double) this.context.getValue("min(/data/repeatdata/input)", Double.class);

        assertNotNull(min);
        assertTrue(min.equals(new Double(1.0d)));
    }

    /**
     * __UNDOCUMENTED__
     *
     * @throws Exception __UNDOCUMENTED__
     */
    public void testNow() throws Exception {
        assertNotNull(this.context.getValue("now()"));
    }

    /**
     * __UNDOCUMENTED__
     *
     * @throws Exception __UNDOCUMENTED__
     */
    public void testProperty() throws Exception {
    }

    /**
     * __UNDOCUMENTED__
     *
     * @throws Exception __UNDOCUMENTED__
     */
    protected void setUp() throws Exception {
        Document document = getXmlResource("functions.xml");

        //dirty fix
        ChibaBean chibaBean = new ChibaBean();
        chibaBean.setXMLContainer(getClass().getResourceAsStream("ChibaExtensionFunctionsTest.xml"));
        chibaBean.init();

        XPathExtensionFunctions functions = new XPathExtensionFunctions();
        functions.setNamespaceContext(document.getDocumentElement());

        this.context = JXPathContext.newContext(document);
        this.context.setFunctions(functions);
    }

    /**
     * __UNDOCUMENTED__
     */
    protected void tearDown() {
        this.context = null;
    }

    //helper - should be moved elsewhere...
    private Document getXmlResource(String fileName) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);

        // Create builder.
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Parse files.
        return builder.parse(getClass().getResourceAsStream(fileName));
    }
}

// end of class
