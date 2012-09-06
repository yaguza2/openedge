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
package org.chiba.xml.xforms.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.xerces.dom.NodeImpl;
import org.chiba.xml.util.DOMUtil;
import org.chiba.xml.xforms.ChibaBean;
import org.chiba.xml.xforms.Instance;
import org.chiba.xml.xforms.ModelItem;

/**
 * Test cases for instance insert.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: RepeatedModelItemsTest.java,v 1.2 2004/08/15 12:58:59 joernt Exp $
 */
public class RepeatedModelItemsTest extends XMLTestBase {

//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private ChibaBean chibaBean = null;
//    private Instance instance = null;

    /**
     * Creates a new instance insert test.
     *
     * @param name the test name.
     */
    public RepeatedModelItemsTest(String name) {
        super(name);
    }

    /**
     * Runs the instance insert test.
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
        return new TestSuite(RepeatedModelItemsTest.class);
    }

    public void testInitialState() throws Exception {
        this.chibaBean.setXMLContainer(getClass().getResourceAsStream("repeatedModelItems.xhtml"));
        this.chibaBean.init();
        Instance instance = this.chibaBean.getContainer().getDefaultModel().getDefaultInstance();
//        ModelItem item = instance.getModelItem("/data/items/item[2]/a[1]");
//        assertNotNull(item);
//
//        System.out.println("item type: " + item.getDatatype());
//        System.out.println("item type: " + item.getRelevant());
//        System.out.println("item type: " + item.getReadonly());

        JXPathContext context = JXPathContext.newContext(instance.getInstanceDocument());
        NodeImpl node = (NodeImpl) context.getPointer("//x").getNode();
        Object object = node.getUserData();
        assertNotNull(object);

        ModelItem item = instance.getModelItem("//x");
        assertNotNull(item);

        item = instance.getModelItem("//items/item[2]/a");
        assertNotNull(item);
        assertEquals("string", item.getDatatype());
        assertEquals("false()", item.getReadonly());
        assertEquals("string-length(.) = 3", item.getRelevant());

        item = instance.getModelItem("//items/item[2]/b");
        assertEquals("integer", item.getDatatype());
        assertEquals("false()", item.getRequired());
        assertEquals("string-length(.) = 3", item.getRelevant());


        item = instance.getModelItem("//items/item[2]/c");
        assertEquals("count(//items/item)", item.getCalculate());

        chibaBean.getContainer().dispatch("insert", "DOMActivate");
        DOMUtil.prettyPrintDOM(chibaBean.getContainer().getDefaultModel().getDefaultInstance().getInstanceDocument());

        item = instance.getModelItem("/data[1]/items[1]/item[3]");
        assertNotNull(item);
        assertEquals("false()", item.getReadonly());

        item = instance.getModelItem("/data[1]/items[1]/item[3]/a[1]");
        assertNotNull(item);
        assertEquals("string", item.getDatatype());
        assertEquals("false()", item.getReadonly());
        assertEquals("string-length(.) = 3", item.getRelevant());


    }

    //reproduce bug reported by William Boyd
    public void testDatatype() throws Exception {
        this.chibaBean.setXMLContainer(getClass().getResourceAsStream("jobAppMini-broken.xml"));
        this.chibaBean.init();

        chibaBean.getContainer().dispatch("insert-trigger", "DOMActivate");

        JXPathContext context = JXPathContext.newContext(chibaBean.getContainer().getDocument());
        String type = (String) context.getValue("//*[@chiba:xpath='/jobApplication[1]/resume[1]/education-info[2]/school-name[1]/value[1]']/@chiba:type");
        assertEquals("token", type);

        type = (String) context.getValue("//*[@chiba:xpath='/jobApplication[1]/resume[1]/education-info[2]/completion-date[1]/value[1]']/@chiba:type");
        assertEquals("date", type);
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.chibaBean = new ChibaBean();
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.chibaBean.shutdown();
        this.chibaBean = null;
    }


}

// end of class
