/*
 *
 *	Artistic License
 *
 *	Preamble
 *
 *	The intent of this document is to state the conditions under which a
 *	Package may be copied, such that the Copyright Holder maintains some
 *	semblance of artistic control over the development of the package,
 *	while giving the users of the package the right to use and distribute
 *	the Package in a more-or-less customary fashion, plus the right to make
 *	reasonable modifications.
 *
 *	Definitions:
 *
 *	"Package" refers to the collection of files distributed by the
 *	Copyright Holder, and derivatives of that collection of files created
 *	through textual modification.
 *
 *	"Standard Version" refers to such a Package if it has not been
 *	modified, or has been modified in accordance with the wishes of the
 *	Copyright Holder.
 *
 *	"Copyright Holder" is whoever is named in the copyright or copyrights
 *	for the package.
 *
 *	"You" is you, if you're thinking about copying or distributing this Package.
 *
 *	"Reasonable copying fee" is whatever you can justify on the basis of
 *	media cost, duplication charges, time of people involved, and so
 *	on. (You will not be required to justify it to the Copyright Holder,
 *	but only to the computing community at large as a market that must bear
 *	the fee.)
 *
 *	"Freely Available" means that no fee is charged for the item itself,
 *	though there may be fees involved in handling the item. It also means
 *	that recipients of the item may redistribute it under the same
 *	conditions they received it.
 *
 *	1. You may make and give away verbatim copies of the source form of the
 *	Standard Version of this Package without restriction, provided that you
 *	duplicate all of the original copyright notices and associated
 *	disclaimers.
 *
 *	2. You may apply bug fixes, portability fixes and other modifications
 *	derived from the Public Domain or from the Copyright Holder. A Package
 *	modified in such a way shall still be considered the Standard Version.
 *
 *	3. You may otherwise modify your copy of this Package in any way,
 *	provided that you insert a prominent notice in each changed file
 *	stating how and when you changed that file, and provided that you do at
 *	least ONE of the following:
 *
 *		a) place your modifications in the Public Domain or otherwise make
 *		them Freely Available, such as by posting said modifications to
 *		Usenet or an equivalent medium, or placing the modifications on a
 *		major archive site such as ftp.uu.net, or by allowing the Copyright
 *		Holder to include your modifications in the Standard Version of the
 *		Package.
 *
 *		b) use the modified Package only within your corporation or
 *		organization.
 *
 *		c) rename any non-standard executables so the names do not conflict
 *		with standard executables, which must also be provided, and provide
 *		a separate manual page for each non-standard executable that
 *		clearly documents how it differs from the Standard Version.
 *
 *		d) make other distribution arrangements with the Copyright Holder.
 *
 *	4. You may distribute the programs of this Package in object code or
 *	executable form, provided that you do at least ONE of the following:
 *
 *		a) distribute a Standard Version of the executables and library
 *		files, together with instructions (in the manual page or
 *		equivalent) on where to get the Standard Version.
 *
 *		b) accompany the distribution with the machine-readable source of
 *		the Package with your modifications.
 *
 *		c) accompany any non-standard executables with their corresponding
 *		Standard Version executables, giving the non-standard executables
 *		non-standard names, and clearly documenting the differences in
 *		manual pages (or equivalent), together with instructions on where
 *		to get the Standard Version.
 *
 *		d) make other distribution arrangements with the Copyright Holder.
 *
 *	5. You may charge a reasonable copying fee for any distribution of this
 *	Package. You may charge any fee you choose for support of this
 *	Package. You may not charge a fee for this Package itself.  However,
 *	you may distribute this Package in aggregate with other (possibly
 *	commercial) programs as part of a larger (possibly commercial) software
 *	distribution provided that you do not advertise this Package as a
 *	product of your own.
 *
 *	6. The scripts and library files supplied as input to or produced as
 *	output from the programs of this Package do not automatically fall
 *	under the copyright of this Package, but belong to whomever generated
 *	them, and may be sold commercially, and may be aggregated with this
 *	Package.
 *
 *	7. C or perl subroutines supplied by you and linked into this Package
 *	shall not be considered part of this Package.
 *
 *	8. The name of the Copyright Holder may not be used to endorse or
 *	promote products derived from this software without specific prior
 *	written permission.
 *
 *	9. THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 *	WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 *	MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package org.chiba.xml.util.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.chiba.xml.util.DOMWhitespace;

import java.util.List;

/**
 * Test cases for the DOM whitespace utility.
 *
 * @author <a href="mailto:unl@users.sourceforge.net">Ulrich Nicolas Liss&eacute;</a>
 * @version $Id: DOMWhitespaceTest.java,v 1.4 2004/08/15 14:14:09 joernt Exp $
 */
public class DOMWhitespaceTest extends TestCase {

    /**
     * Creates a new DOM whitespace test.
     *
     * @param name the test name.
     */
    public DOMWhitespaceTest(String name) {
        super(name);
    }

    /**
     * Runs the DOM whitespace  test.
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
        return new TestSuite(DOMWhitespaceTest.class);
    }

    /**
     * Test case 1 for text denormalization.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDenormalizeText1() throws Exception {
        List list = DOMWhitespace.denormalizeText(null);
        assertNull("list should be null", list);
    }

    /**
     * Test case 2 for text denormalization.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDenormalizeText2() throws Exception {
        List list = DOMWhitespace.denormalizeText("");
        assertNotNull("no list", list);
        assertTrue("wrong list size", list.size() == 1);
        assertTrue("wrong list item at index 0", list.get(0).equals(""));
    }

    /**
     * Test case 3 for text denormalization.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDenormalizeText3() throws Exception {
        List list = DOMWhitespace.denormalizeText(" \n\t");
        assertNotNull("no list", list);
        assertTrue("wrong list size", list.size() == 1);
        assertTrue("wrong list item at index 0", list.get(0).equals(" \n\t"));
    }

    /**
     * Test case 4 for text denormalization.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDenormalizeText4() throws Exception {
        List list = DOMWhitespace.denormalizeText("this is text");
        assertNotNull("no list", list);
        assertTrue("wrong list size", list.size() == 1);
        assertTrue("wrong list item at index 0", list.get(0).equals("this is text"));
    }

    /**
     * Test case 5 for text denormalization.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDenormalizeText5() throws Exception {
        List list = DOMWhitespace.denormalizeText("this is text ");
        assertNotNull("no list", list);
        assertTrue("wrong list size", list.size() == 2);
        assertTrue("wrong list item at index 0", list.get(0).equals("this is text"));
        assertTrue("wrong list item at index 1", list.get(1).equals(" "));
    }

    /**
     * Test case 6 for text denormalization.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDenormalizeText6() throws Exception {
        List list = DOMWhitespace.denormalizeText(" this is text");
        assertNotNull("no list", list);
        assertTrue("wrong list size", list.size() == 2);
        assertTrue("wrong list item at index 0", list.get(0).equals(" "));
        assertTrue("wrong list item at index 1", list.get(1).equals("this is text"));

    }

    /**
     * Test case 7 for text denormalization.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDenormalizeText7() throws Exception {
        List list = DOMWhitespace.denormalizeText(" this is text ");
        assertNotNull("no list", list);
        assertTrue("wrong list size", list.size() == 3);
        assertTrue("wrong list item at index 0", list.get(0).equals(" "));
        assertTrue("wrong list item at index 1", list.get(1).equals("this is text"));
        assertTrue("wrong list item at index 2", list.get(2).equals(" "));
    }

    /**
     * Test case 8 for text denormalization.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDenormalizeText8() throws Exception {
        List list = DOMWhitespace.denormalizeText("this is text  this is text");
        assertNotNull("no list", list);
        assertTrue("wrong list size", list.size() == 3);
        assertTrue("wrong list item at index 0", list.get(0).equals("this is text"));
        assertTrue("wrong list item at index 1", list.get(1).equals("  "));
        assertTrue("wrong list item at index 2", list.get(2).equals("this is text"));
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
    }

}
