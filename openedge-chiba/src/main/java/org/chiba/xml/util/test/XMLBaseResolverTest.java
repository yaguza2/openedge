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
package org.chiba.xml.util.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.log4j.BasicConfigurator;
import org.chiba.xml.util.XMLBaseResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @author joern turner
 * @version $Id: XMLBaseResolverTest.java,v 1.4 2004/08/15 14:14:09 joernt Exp $
 */
public class XMLBaseResolverTest extends TestCase {
    static {
        BasicConfigurator.configure();
    }

    /**
     * Creates a new XMLBaseResolverTest object.
     *
     * @param name __UNDOCUMENTED__
     */
    public XMLBaseResolverTest(String name) {
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
        return new TestSuite(XMLBaseResolverTest.class);
    }

    /**
     * __UNDOCUMENTED__
     *
     * @throws Exception __UNDOCUMENTED__
     */
    public void testResolveXMLBase() throws Exception {
        Document doc = getXmlResource("xmlbase1.xml");

        JXPathContext context = JXPathContext.newContext(doc);
        Pointer p = context.getPointer("descendant::*[@xlink:href][1]");
        Node n = (Node) p.getNode();
        String s = XMLBaseResolver.resolveXMLBase(n, "new.xml");
        assertTrue(s.equals("http://example.org/today/new.xml"));

        p = context.getPointer("descendant::*[@xlink:href][2]");
        n = (Node) p.getNode();
        s = XMLBaseResolver.resolveXMLBase(n, "pick1.xml");
        assertTrue(s.equals("http://example.org/hotpicks/pick1.xml"));

        p = context.getPointer("descendant::*[@xlink:href][3]");
        n = (Node) p.getNode();
        s = XMLBaseResolver.resolveXMLBase(n, "pick2.xml");
        assertTrue(s.equals("http://example.org/hotpicks/pick2.xml"));

        p = context.getPointer("descendant::*[@xlink:href][4]");
        n = (Node) p.getNode();
        s = XMLBaseResolver.resolveXMLBase(n, "pick3.xml");
        assertTrue(s.equals("http://example.org/hotpicks/pick3.xml"));
    }

    /**
     * __UNDOCUMENTED__
     *
     * @throws Exception __UNDOCUMENTED__
     */
    public void testResolveXMLBaseLess() throws Exception {
        Document doc = getXmlResource("xmlbase2.xml");

        JXPathContext context = JXPathContext.newContext(doc);
        Pointer p = context.getPointer("descendant::*[@xlink:href][1]");
        Node n = (Node) p.getNode();
        String s = XMLBaseResolver.resolveXMLBase(n, "new.xml");
        assertTrue(s.equals("new.xml"));

        p = context.getPointer("descendant::*[@xlink:href][2]");
        n = (Node) p.getNode();
        s = XMLBaseResolver.resolveXMLBase(n, "pick1.xml");
        assertTrue(s.equals("pick1.xml"));

        p = context.getPointer("descendant::*[@xlink:href][3]");
        n = (Node) p.getNode();
        s = XMLBaseResolver.resolveXMLBase(n, "pick2.xml");
        assertTrue(s.equals("pick2.xml"));

        p = context.getPointer("descendant::*[@xlink:href][4]");
        n = (Node) p.getNode();
        s = XMLBaseResolver.resolveXMLBase(n, "pick3.xml");
        assertTrue(s.equals("pick3.xml"));
    }

    /**
     *
     */
    protected void setUp() throws Exception {
    }

    /**
     * __UNDOCUMENTED__
     */
    protected void tearDown() {
    }

    // ++++++++++++ oops, don't look at my private parts!  +++++++++++++++
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