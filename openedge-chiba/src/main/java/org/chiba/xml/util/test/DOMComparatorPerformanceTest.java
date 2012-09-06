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
import org.chiba.xml.util.DOMComparator;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.text.NumberFormat;

/**
 * The performance test for the DOM Comparator implementation.
 *
 * @author <a href="mailto:unl@users.sourceforge.net">uli</a>
 * @version $Id: DOMComparatorPerformanceTest.java,v 1.6 2004/08/15 14:14:08 joernt Exp $
 */
public class DOMComparatorPerformanceTest extends TestCase {
    private Document testDocument1 = null;
    private Document testDocument2 = null;
    private int performanceLaps = 100;

    /**
     * Creates a new DOMComparatorPerformanceTest object.
     *
     * @param name __UNDOCUMENTED__
     */
    public DOMComparatorPerformanceTest(String name) {
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
        return new TestSuite(DOMComparatorPerformanceTest.class);
    }

    /**
     * __UNDOCUMENTED__
     *
     * @throws Exception __UNDOCUMENTED__
     */
    public void testComparisonByEqualsPerformance() throws Exception {
        // Create appropriate comparator.
        DOMComparator comparator = new DOMComparator();
        comparator.setComparisonMethod(DOMComparator.COMPARISON_BY_EQUALS);

        // Start clock.
        long start = System.currentTimeMillis();

        for (int index = 0; index < this.performanceLaps; index++) {
            // Run comparison.
            comparator.compare(testDocument1, testDocument2);
        }

        // Stop clock.
        long end = System.currentTimeMillis();
        float duration = (new Long(end - start)).floatValue() / (new Integer(this.performanceLaps)).floatValue();

        System.out.println("DOMComparator.COMPARISON_BY_EQUALS took " +
                NumberFormat.getNumberInstance().format(duration) + " ms (avg)");
    }

    /**
     * __UNDOCUMENTED__
     *
     * @throws Exception __UNDOCUMENTED__
     */
    public void testComparisonByHashCodePerformance() throws Exception {
        // Create appropriate comparator.
        DOMComparator comparator = new DOMComparator();
        comparator.setComparisonMethod(DOMComparator.COMPARISON_BY_HASH_CODE);

        // Start clock.
        long start = System.currentTimeMillis();

        for (int index = 0; index < this.performanceLaps; index++) {
            // Run comparison.
            comparator.compare(testDocument1, testDocument2);
        }

        // Stop clock.
        long end = System.currentTimeMillis();
        float duration = (new Long(end - start)).floatValue() / (new Integer(this.performanceLaps)).floatValue();

        System.out.println("DOMComparator.COMPARISON_BY_HASH_CODE took " +
                NumberFormat.getNumberInstance().format(duration) + " ms (avg)");
    }

    /**
     * __UNDOCUMENTED__
     *
     * @throws Exception __UNDOCUMENTED__
     */
    public void testComparisonByInternPerformance() throws Exception {
        // Create appropriate comparator.
        DOMComparator comparator = new DOMComparator();
        comparator.setComparisonMethod(DOMComparator.COMPARISON_BY_INTERN);

        // Start clock.
        long start = System.currentTimeMillis();

        for (int index = 0; index < this.performanceLaps; index++) {
            // Run comparison.
            comparator.compare(testDocument1, testDocument2);
        }

        // Stop clock.
        long end = System.currentTimeMillis();
        float duration = (new Long(end - start)).floatValue() / (new Integer(this.performanceLaps)).floatValue();

        System.out.println("DOMComparator.COMPARISON_BY_INTERN took " +
                NumberFormat.getNumberInstance().format(duration) + " ms (avg)");
    }

    /**
     * __UNDOCUMENTED__
     *
     * @throws Exception __UNDOCUMENTED__
     */
    protected void setUp() throws Exception {
        // Setup factory.
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setCoalescing(false);
        factory.setExpandEntityReferences(false);
        factory.setIgnoringComments(false);
        factory.setIgnoringElementContentWhitespace(false);
        factory.setNamespaceAware(true);
        factory.setValidating(false);

        // Create builder.
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Parse files.
        this.testDocument1 = builder.parse(getClass().getResourceAsStream("DOMComparatorPerformanceTest.xml"));
        this.testDocument2 = builder.parse(getClass().getResourceAsStream("DOMComparatorPerformanceTest.xml"));

        try {
            // Attempt to read vm environment
            this.performanceLaps = Integer.parseInt(System.getProperty("org.chiba.test.xml.util.DOMComparatorPerformanceTest.performanceLaps"));
        } catch (Exception e) {
            // Fallback
        }
    }
}


/*
   $Log: DOMComparatorPerformanceTest.java,v $
   Revision 1.6  2004/08/15 14:14:08  joernt
   preparing release...
   -reformatted sources to fix mixture of tabs and spaces
   -optimized imports on all files

   Revision 1.5  2003/11/07 00:19:23  joernt
   optimized imports

   Revision 1.4  2003/10/10 01:23:31  joernt
   corrrected package statement after move

   Revision 1.3  2003/10/08 20:28:12  joernt
   moving tests back in again

   Revision 1.2  2003/10/02 15:15:51  joernt
   applied chiba jalopy settings to whole src tree

   Revision 1.1  2003/09/11 21:02:26  joernt
   moved tests to their own hierarchy
   Revision 1.1.1.1  2003/05/23 14:54:06  unl
   no message
   Revision 1.2  2002/12/09 01:27:16  joernt
   -cleanup;
   -reorganised imports
 */
