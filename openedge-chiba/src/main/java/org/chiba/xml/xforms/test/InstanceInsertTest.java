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

import org.chiba.xml.xforms.ChibaBean;
import org.chiba.xml.xforms.Instance;
import org.chiba.xml.xforms.ModelItem;

/**
 * Test cases for instance insert.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: InstanceInsertTest.java,v 1.5 2004/08/15 14:14:14 joernt Exp $
 */
public class InstanceInsertTest extends XMLTestBase {

//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private ChibaBean chibaBean;
    private Instance instance;

    /**
     * Test case for simple nodeset insertion before an existing collection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertSimpleBefore() throws Exception {
        // modify last collection member in order to see a difference to the inserted one
        this.instance.getInstanceContext().setValue("/repeats[1]/simple[2]", "modified");

        // insert before collection
        this.instance.insertNode("/repeats[1]/simple", 1);
        assertTrue(getComparator().compare(getXmlResource("InstanceInsertTestResult001.xml"), this.instance.getInstanceDocument()));

        // assert ModelItems
        chibaBean.getContainer().getDefaultModel().rebuild();
        ModelItem item = instance.getModelItem("/repeats[1]/simple[1]");
        assertNotNull(item);
        assertEquals("listItem", item.getDatatype());
    }

    /**
     * Test case for simple nodeset insertion into an existing collection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertSimpleInto() throws Exception {
        // modify last collection member in order to see a difference to the inserted one
        this.instance.getInstanceContext().setValue("/repeats[1]/simple[2]", "modified");

        // insert into collection
        this.instance.insertNode("/repeats[1]/simple", 2);
        assertTrue(getComparator().compare(getXmlResource("InstanceInsertTestResult002.xml"), this.instance.getInstanceDocument()));

        // assert ModelItems
        chibaBean.getContainer().getDefaultModel().rebuild();
        ModelItem item = instance.getModelItem("/repeats[1]/simple[1]");
        assertNotNull(item);
        assertEquals("listItem", item.getDatatype());
    }

    /**
     * Test case for simple nodeset insertion after an existing collection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertSimpleAfter() throws Exception {
        // modify last collection member in order to see a difference to the inserted one
        this.instance.getInstanceContext().setValue("/repeats[1]/simple[2]", "modified");

        // insert after collection
        this.instance.insertNode("/repeats[1]/simple", 3);
        assertTrue(getComparator().compare(getXmlResource("InstanceInsertTestResult003.xml"), this.instance.getInstanceDocument()));

        // assert ModelItems
        chibaBean.getContainer().getDefaultModel().rebuild();
        ModelItem item = instance.getModelItem("/repeats[1]/simple[1]");
        assertNotNull(item);
        assertEquals("listItem", item.getDatatype());
    }

    /**
     * Test case for simple nodeset insertion into an empty collection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertSimpleEmpty() throws Exception {
        // remove all collection members
        this.instance.getInstanceContext().removeAll("/repeats[1]/simple");

        // insert empty collection
        this.instance.insertNode("/repeats[1]/simple", 1);
        assertTrue(getComparator().compare(getXmlResource("InstanceInsertTestResult004.xml"), this.instance.getInstanceDocument()));

        // assert ModelItems
        chibaBean.getContainer().getDefaultModel().rebuild();
        ModelItem item = instance.getModelItem("/repeats[1]/simple[1]");
        assertNotNull(item);
        assertEquals("listItem", item.getDatatype());
    }

    /**
     * Test case for complex nodeset insertion before an existing collection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertComplexBefore() throws Exception {
        // modify last collection member in order to see a difference to the inserted one
        this.instance.getInstanceContext().setValue("/repeats[1]/complex[2]/content", "modified");

        // insert before collection
        this.instance.insertNode("/repeats[1]/complex", 1);
        assertTrue(getComparator().compare(getXmlResource("InstanceInsertTestResult005.xml"), this.instance.getInstanceDocument()));
    }

    /**
     * Test case for complex nodeset insertion into an existing collection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertComplexInto() throws Exception {
        // modify last collection member in order to see a difference to the inserted one
        this.instance.getInstanceContext().setValue("/repeats[1]/complex[2]/content", "modified");

        // insert into collection
        this.instance.insertNode("/repeats[1]/complex", 2);
        assertTrue(getComparator().compare(getXmlResource("InstanceInsertTestResult006.xml"), this.instance.getInstanceDocument()));
    }

    /**
     * Test case for complex nodeset insertion after an existing collection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertComplexAfter() throws Exception {
        // modify last collection member in order to see a difference to the inserted one
        this.instance.getInstanceContext().setValue("/repeats[1]/complex[2]/content", "modified");

        // insert after collection
        this.instance.insertNode("/repeats[1]/complex", 3);
        assertTrue(getComparator().compare(getXmlResource("InstanceInsertTestResult007.xml"), this.instance.getInstanceDocument()));
    }

    /**
     * Test case for complex nodeset insertion into an empty collection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertComplexEmpty() throws Exception {
        // remove all collection members
        this.instance.getInstanceContext().removeAll("/repeats[1]/complex");

        // insert empty collection
        this.instance.insertNode("/repeats[1]/complex", 1);
        assertTrue(getComparator().compare(getXmlResource("InstanceInsertTestResult008.xml"), this.instance.getInstanceDocument()));
    }

    /**
     * Test case for enclosing nodeset insertion before an existing collection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertEnclosingBefore() throws Exception {
        // modify last collection member in order to see a difference to the inserted one
        this.instance.getInstanceContext().setValue("/repeats[1]/enclosing[2]/nested[1]", "modified");
        this.instance.getInstanceContext().setValue("/repeats[1]/enclosing[2]/nested[2]", "modified");

        // insert before collection
        this.instance.insertNode("/repeats[1]/enclosing", 1);
        assertTrue(getComparator().compare(getXmlResource("InstanceInsertTestResult009.xml"), this.instance.getInstanceDocument()));
    }

    /**
     * Test case for enclosing nodeset insertion into an existing collection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertEnclosingInto() throws Exception {
        // modify last collection member in order to see a difference to the inserted one
        this.instance.getInstanceContext().setValue("/repeats[1]/enclosing[2]/nested[1]", "modified");
        this.instance.getInstanceContext().setValue("/repeats[1]/enclosing[2]/nested[2]", "modified");

        // insert into collection
        this.instance.insertNode("/repeats[1]/enclosing", 2);
        assertTrue(getComparator().compare(getXmlResource("InstanceInsertTestResult010.xml"), this.instance.getInstanceDocument()));
    }

    /**
     * Test case for enclosing nodeset insertion after an existing collection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertEnclosingAfter() throws Exception {
        // modify last collection member in order to see a difference to the inserted one
        this.instance.getInstanceContext().setValue("/repeats[1]/enclosing[2]/nested[1]", "modified");
        this.instance.getInstanceContext().setValue("/repeats[1]/enclosing[2]/nested[2]", "modified");

        // insert after collection
        this.instance.insertNode("/repeats[1]/enclosing", 3);
        assertTrue(getComparator().compare(getXmlResource("InstanceInsertTestResult011.xml"), this.instance.getInstanceDocument()));
    }

    /**
     * Test case for enclosing nodeset insertion into an empty collection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertEnclosingEmpty() throws Exception {
        // remove all collection members
        this.instance.getInstanceContext().removeAll("/repeats[1]/enclosing");

        // insert empty collection
        this.instance.insertNode("/repeats[1]/enclosing", 1);
        assertTrue(getComparator().compare(getXmlResource("InstanceInsertTestResult012.xml"), this.instance.getInstanceDocument()));
    }

    /**
     * Test case for nested nodeset insertion before an existing collection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertFirstNestedBefore() throws Exception {
        // modify last collection member in order to see a difference to the inserted one
        this.instance.getInstanceContext().setValue("/repeats[1]/enclosing[1]/nested[2]", "modified");

        // insert before collection
        this.instance.insertNode("/repeats[1]/enclosing[1]/nested", 1);
        assertTrue(getComparator().compare(getXmlResource("InstanceInsertTestResult013.xml"), this.instance.getInstanceDocument()));
    }

    /**
     * Test case for nested nodeset insertion into an existing collection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertFirstNestedInto() throws Exception {
        // modify last collection member in order to see a difference to the inserted one
        this.instance.getInstanceContext().setValue("/repeats[1]/enclosing[1]/nested[2]", "modified");

        // insert into collection
        this.instance.insertNode("/repeats[1]/enclosing[1]/nested", 2);
        assertTrue(getComparator().compare(getXmlResource("InstanceInsertTestResult014.xml"), this.instance.getInstanceDocument()));
    }

    /**
     * Test case for nested nodeset insertion after an existing collection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertFirstNestedAfter() throws Exception {
        // modify last collection member in order to see a difference to the inserted one
        this.instance.getInstanceContext().setValue("/repeats[1]/enclosing[1]/nested[2]", "modified");

        // insert after collection
        this.instance.insertNode("/repeats[1]/enclosing[1]/nested", 3);
        assertTrue(getComparator().compare(getXmlResource("InstanceInsertTestResult015.xml"), this.instance.getInstanceDocument()));
    }

    /**
     * Test case for nested nodeset insertion into an empty collection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertFirstNestedEmpty() throws Exception {
        // remove all collection members
        this.instance.getInstanceContext().removeAll("/repeats[1]/enclosing[1]/nested");

        // insert empty collection
        this.instance.insertNode("/repeats[1]/enclosing[1]/nested", 1);
        assertTrue(getComparator().compare(getXmlResource("InstanceInsertTestResult016.xml"), this.instance.getInstanceDocument()));
    }

    /**
     * Test case for nested nodeset insertion before an existing collection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertLastNestedBefore() throws Exception {
        // modify last collection member in order to see a difference to the inserted one
        this.instance.getInstanceContext().setValue("/repeats[1]/enclosing[2]/nested[2]", "modified");

        // insert before collection
        this.instance.insertNode("/repeats[1]/enclosing[2]/nested", 1);
        assertTrue(getComparator().compare(getXmlResource("InstanceInsertTestResult017.xml"), this.instance.getInstanceDocument()));
    }

    /**
     * Test case for nested nodeset insertion into an existing collection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertLastNestedInto() throws Exception {
        // modify last collection member in order to see a difference to the inserted one
        this.instance.getInstanceContext().setValue("/repeats[1]/enclosing[2]/nested[2]", "modified");

        // insert into collection
        this.instance.insertNode("/repeats[1]/enclosing[2]/nested", 2);
        assertTrue(getComparator().compare(getXmlResource("InstanceInsertTestResult018.xml"), this.instance.getInstanceDocument()));
    }

    /**
     * Test case for nested nodeset insertion after an existing collection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertLastNestedAfter() throws Exception {
        // modify last collection member in order to see a difference to the inserted one
        this.instance.getInstanceContext().setValue("/repeats[1]/enclosing[2]/nested[2]", "modified");

        // insert after collection
        this.instance.insertNode("/repeats[1]/enclosing[2]/nested", 3);
        assertTrue(getComparator().compare(getXmlResource("InstanceInsertTestResult019.xml"), this.instance.getInstanceDocument()));
    }

    /**
     * Test case for nested nodeset insertion into an empty collection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertLastNestedEmpty() throws Exception {
        // remove all collection members
        this.instance.getInstanceContext().removeAll("/repeats[1]/enclosing[2]/nested");

        // insert empty collection
        this.instance.insertNode("/repeats[1]/enclosing[2]/nested", 1);
        assertTrue(getComparator().compare(getXmlResource("InstanceInsertTestResult020.xml"), this.instance.getInstanceDocument()));
    }

    /**
     * Test case for nested nodeset insertion before an existing collection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertNewNestedBefore() throws Exception {
        // insert new enclosing collection
        this.instance.insertNode("/repeats[1]/enclosing", 3);

        // modify last collection member in order to see a difference to the inserted one
        this.instance.getInstanceContext().setValue("/repeats[1]/enclosing[3]/nested[2]", "modified");

        // insert before collection
        this.instance.insertNode("/repeats[1]/enclosing[3]/nested", 1);
        assertTrue(getComparator().compare(getXmlResource("InstanceInsertTestResult021.xml"), this.instance.getInstanceDocument()));
    }

    /**
     * Test case for nested nodeset insertion into an existing collection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertNewNestedInto() throws Exception {
        // insert new enclosing collection
        this.instance.insertNode("/repeats[1]/enclosing", 3);

        // modify last collection member in order to see a difference to the inserted one
        this.instance.getInstanceContext().setValue("/repeats[1]/enclosing[3]/nested[2]", "modified");

        // insert into collection
        this.instance.insertNode("/repeats[1]/enclosing[3]/nested", 2);
        assertTrue(getComparator().compare(getXmlResource("InstanceInsertTestResult022.xml"), this.instance.getInstanceDocument()));
    }

    /**
     * Test case for nested nodeset insertion after an existing collection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertNewNestedAfter() throws Exception {
        // insert new enclosing collection
        this.instance.insertNode("/repeats[1]/enclosing", 3);

        // modify last collection member in order to see a difference to the inserted one
        this.instance.getInstanceContext().setValue("/repeats[1]/enclosing[3]/nested[2]", "modified");

        // insert after collection
        this.instance.insertNode("/repeats[1]/enclosing[3]/nested", 3);
        assertTrue(getComparator().compare(getXmlResource("InstanceInsertTestResult023.xml"), this.instance.getInstanceDocument()));
    }

    /**
     * Test case for nested nodeset insertion into an empty collection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertNewNestedEmpty() throws Exception {
        // insert new enclosing collection
        this.instance.insertNode("/repeats[1]/enclosing", 3);

        // remove all collection members
        this.instance.getInstanceContext().removeAll("/repeats[1]/enclosing[3]/nested");

        // insert empty collection
        this.instance.insertNode("/repeats[1]/enclosing[3]/nested", 1);
        assertTrue(getComparator().compare(getXmlResource("InstanceInsertTestResult024.xml"), this.instance.getInstanceDocument()));
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.chibaBean = new ChibaBean();
        this.chibaBean.setXMLContainer(getClass().getResourceAsStream("InstanceInsertTest.xml"));
        this.chibaBean.init();
        this.instance = this.chibaBean.getContainer().getDefaultModel().getDefaultInstance();
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.chibaBean.shutdown();
        this.chibaBean = null;
        this.instance = null;
    }

}

// end of class
