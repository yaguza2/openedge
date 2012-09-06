/*
 *
 *    Artistic License
 *
 *    Preamble
 *
 *    The intent of this document is to state the conditions under which a
 *    Package may be copied, such that the Copyright Holder maintains some
 *    semblance of artistic control over the development of the package,
 *    while giving the users of the package the right to use and distribute
 *    the Package in a more-or-less customary fashion, plus the right to make
 *    reasonable modifications.
 *
 *    Definitions:
 *
 *    "Package" refers to the collection of files distributed by the
 *    Copyright Holder, and derivatives of that collection of files created
 *    through textual modification.
 *
 *    "Standard Version" refers to such a Package if it has not been
 *    modified, or has been modified in accordance with the wishes of the
 *    Copyright Holder.
 *
 *    "Copyright Holder" is whoever is named in the copyright or copyrights
 *    for the package.
 *
 *    "You" is you, if you're thinking about copying or distributing this Package.
 *
 *    "Reasonable copying fee" is whatever you can justify on the basis of
 *    media cost, duplication charges, time of people involved, and so
 *    on. (You will not be required to justify it to the Copyright Holder,
 *    but only to the computing community at large as a market that must bear
 *    the fee.)
 *
 *    "Freely Available" means that no fee is charged for the item itself,
 *    though there may be fees involved in handling the item. It also means
 *    that recipients of the item may redistribute it under the same
 *    conditions they received it.
 *
 *    1. You may make and give away verbatim copies of the source form of the
 *    Standard Version of this Package without restriction, provided that you
 *    duplicate all of the original copyright notices and associated
 *    disclaimers.
 *
 *    2. You may apply bug fixes, portability fixes and other modifications
 *    derived from the Public Domain or from the Copyright Holder. A Package
 *    modified in such a way shall still be considered the Standard Version.
 *
 *    3. You may otherwise modify your copy of this Package in any way,
 *    provided that you insert a prominent notice in each changed file
 *    stating how and when you changed that file, and provided that you do at
 *    least ONE of the following:
 *
 *        a) place your modifications in the Public Domain or otherwise make
 *        them Freely Available, such as by posting said modifications to
 *        Usenet or an equivalent medium, or placing the modifications on a
 *        major archive site such as ftp.uu.net, or by allowing the Copyright
 *        Holder to include your modifications in the Standard Version of the
 *        Package.
 *
 *        b) use the modified Package only within your corporation or
 *        organization.
 *
 *        c) rename any non-standard executables so the names do not conflict
 *        with standard executables, which must also be provided, and provide
 *        a separate manual page for each non-standard executable that
 *        clearly documents how it differs from the Standard Version.
 *
 *        d) make other distribution arrangements with the Copyright Holder.
 *
 *    4. You may distribute the programs of this Package in object code or
 *    executable form, provided that you do at least ONE of the following:
 *
 *        a) distribute a Standard Version of the executables and library
 *        files, together with instructions (in the manual page or
 *        equivalent) on where to get the Standard Version.
 *
 *        b) accompany the distribution with the machine-readable source of
 *        the Package with your modifications.
 *
 *        c) accompany any non-standard executables with their corresponding
 *        Standard Version executables, giving the non-standard executables
 *        non-standard names, and clearly documenting the differences in
 *        manual pages (or equivalent), together with instructions on where
 *        to get the Standard Version.
 *
 *        d) make other distribution arrangements with the Copyright Holder.
 *
 *    5. You may charge a reasonable copying fee for any distribution of this
 *    Package. You may charge any fee you choose for support of this
 *    Package. You may not charge a fee for this Package itself.  However,
 *    you may distribute this Package in aggregate with other (possibly
 *    commercial) programs as part of a larger (possibly commercial) software
 *    distribution provided that you do not advertise this Package as a
 *    product of your own.
 *
 *    6. The scripts and library files supplied as input to or produced as
 *    output from the programs of this Package do not automatically fall
 *    under the copyright of this Package, but belong to whomever generated
 *    them, and may be sold commercially, and may be aggregated with this
 *    Package.
 *
 *    7. C or perl subroutines supplied by you and linked into this Package
 *    shall not be considered part of this Package.
 *
 *    8. The name of the Copyright Holder may not be used to endorse or
 *    promote products derived from this software without specific prior
 *    written permission.
 *
 *    9. THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 *    WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 *    MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package org.chiba.xml.xforms.test;

import junit.framework.TestCase;
import org.chiba.xml.xforms.ModelItem;
import org.chiba.xml.xforms.ModelItemListener;

/**
 * Model item test cases.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ModelItemTest.java,v 1.3 2004/08/15 14:14:14 joernt Exp $
 */
public class ModelItemTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private TestModelItemListener modelItemListener;
    private ModelItem modelItem;
    private ModelItem parentItem;

    /**
     * Tests default properties.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDefaultProperties() throws Exception {
        assertEquals(true, this.modelItem.isValid());
        assertEquals(false, this.modelItem.isReadonly());
        assertEquals(false, this.modelItem.isRequired());
        assertEquals(true, this.modelItem.isEnabled());
    }

    /**
     * Tests default notification.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDefaultNotification() throws Exception {
        this.modelItem.notifyListeners();
        assertNull(this.modelItemListener.state);
        assertNull(this.modelItemListener.method);
    }

    /**
     * Tests valid notification.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testValidNotification() throws Exception {
        this.modelItem.setConstraintValid(false);
        this.modelItem.synchronizeChangeState();
        this.modelItem.setConstraintValid(true);
        this.modelItem.notifyListeners();
        assertEquals(Boolean.TRUE, this.modelItemListener.state);
        assertEquals("validChanged", this.modelItemListener.method);
    }

    /**
     * Tests invalid notification.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInvalidNotification() throws Exception {
        this.modelItem.setConstraintValid(true);
        this.modelItem.synchronizeChangeState();
        this.modelItem.setConstraintValid(false);
        this.modelItem.notifyListeners();
        assertEquals(Boolean.FALSE, this.modelItemListener.state);
        assertEquals("validChanged", this.modelItemListener.method);
    }

    /**
     * Tests readonly notification.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testReadonlyNotification() throws Exception {
        this.modelItem.setLocalReadonly(false);
        this.modelItem.synchronizeChangeState();
        this.modelItem.setLocalReadonly(true);
        this.modelItem.notifyListeners();
        assertEquals(Boolean.TRUE, this.modelItemListener.state);
        assertEquals("readonlyChanged", this.modelItemListener.method);
    }

    /**
     * Tests readwrite notification.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testReadwriteNotification() throws Exception {
        this.modelItem.setLocalReadonly(true);
        this.modelItem.synchronizeChangeState();
        this.modelItem.setLocalReadonly(false);
        this.modelItem.notifyListeners();
        assertEquals(Boolean.FALSE, this.modelItemListener.state);
        assertEquals("readonlyChanged", this.modelItemListener.method);
    }

    /**
     * Tests required notification.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testRequiredNotification() throws Exception {
        this.modelItem.setLocalRequired(false);
        this.modelItem.synchronizeChangeState();
        this.modelItem.setLocalRequired(true);
        this.modelItem.notifyListeners();
        assertEquals(Boolean.TRUE, this.modelItemListener.state);
        assertEquals("requiredChanged", this.modelItemListener.method);
    }

    /**
     * Tests optional notification.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testOptionalNotification() throws Exception {
        this.modelItem.setLocalRequired(true);
        this.modelItem.synchronizeChangeState();
        this.modelItem.setLocalRequired(false);
        this.modelItem.notifyListeners();
        assertEquals(Boolean.FALSE, this.modelItemListener.state);
        assertEquals("requiredChanged", this.modelItemListener.method);
    }

    /**
     * Tests enabled notification.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testEnabledNotification() throws Exception {
        this.modelItem.setLocalRelevant(false);
        this.modelItem.synchronizeChangeState();
        this.modelItem.setLocalRelevant(true);
        this.modelItem.notifyListeners();
        assertEquals(Boolean.TRUE, this.modelItemListener.state);
        assertEquals("enabledChanged", this.modelItemListener.method);
    }

    /**
     * Tests disabled notification.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDisabledNotification() throws Exception {
        this.modelItem.setLocalRelevant(true);
        this.modelItem.synchronizeChangeState();
        this.modelItem.setLocalRelevant(false);
        this.modelItem.notifyListeners();
        assertEquals(Boolean.FALSE, this.modelItemListener.state);
        assertEquals("enabledChanged", this.modelItemListener.method);
    }

    /**
     * Tests valid computation when optional.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testValidComputationWhenOptional() throws Exception {
        this.modelItem.setLocalRequired(false);
        assertEquals(false, this.modelItem.isRequired());

        this.modelItem.setConstraintValid(true);
        this.modelItem.setDatatypeValid(true);
        this.modelItem.setRequiredValid(true);
        assertEquals(true, this.modelItem.isValid());

        this.modelItem.setConstraintValid(false);
        this.modelItem.setDatatypeValid(true);
        this.modelItem.setRequiredValid(true);
        assertEquals(false, this.modelItem.isValid());

        this.modelItem.setConstraintValid(true);
        this.modelItem.setDatatypeValid(false);
        this.modelItem.setRequiredValid(true);
        assertEquals(false, this.modelItem.isValid());

        this.modelItem.setConstraintValid(false);
        this.modelItem.setDatatypeValid(false);
        this.modelItem.setRequiredValid(true);
        assertEquals(false, this.modelItem.isValid());

        this.modelItem.setConstraintValid(true);
        this.modelItem.setDatatypeValid(true);
        this.modelItem.setRequiredValid(false);
        assertEquals(true, this.modelItem.isValid());

        this.modelItem.setConstraintValid(false);
        this.modelItem.setDatatypeValid(true);
        this.modelItem.setRequiredValid(false);
        assertEquals(true, this.modelItem.isValid());

        this.modelItem.setConstraintValid(true);
        this.modelItem.setDatatypeValid(false);
        this.modelItem.setRequiredValid(false);
        assertEquals(true, this.modelItem.isValid());

        this.modelItem.setConstraintValid(false);
        this.modelItem.setDatatypeValid(false);
        this.modelItem.setRequiredValid(false);
        assertEquals(true, this.modelItem.isValid());
    }

    /**
     * Tests valid computation when required.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testValidComputationWhenRequired() throws Exception {
        this.modelItem.setLocalRequired(true);
        assertEquals(true, this.modelItem.isRequired());

        this.modelItem.setConstraintValid(true);
        this.modelItem.setDatatypeValid(true);
        this.modelItem.setRequiredValid(true);
        assertEquals(true, this.modelItem.isValid());

        this.modelItem.setConstraintValid(false);
        this.modelItem.setDatatypeValid(true);
        this.modelItem.setRequiredValid(true);
        assertEquals(false, this.modelItem.isValid());

        this.modelItem.setConstraintValid(true);
        this.modelItem.setDatatypeValid(false);
        this.modelItem.setRequiredValid(true);
        assertEquals(false, this.modelItem.isValid());

        this.modelItem.setConstraintValid(false);
        this.modelItem.setDatatypeValid(false);
        this.modelItem.setRequiredValid(true);
        assertEquals(false, this.modelItem.isValid());

        this.modelItem.setConstraintValid(true);
        this.modelItem.setDatatypeValid(true);
        this.modelItem.setRequiredValid(false);
        assertEquals(false, this.modelItem.isValid());

        this.modelItem.setConstraintValid(false);
        this.modelItem.setDatatypeValid(true);
        this.modelItem.setRequiredValid(false);
        assertEquals(false, this.modelItem.isValid());

        this.modelItem.setConstraintValid(true);
        this.modelItem.setDatatypeValid(false);
        this.modelItem.setRequiredValid(false);
        assertEquals(false, this.modelItem.isValid());

        this.modelItem.setConstraintValid(false);
        this.modelItem.setDatatypeValid(false);
        this.modelItem.setRequiredValid(false);
        assertEquals(false, this.modelItem.isValid());
    }

    /**
     * Tests readonly computation.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testReadonlyComputation() throws Exception {
        this.parentItem.setLocalReadonly(true);
        this.modelItem.setLocalReadonly(true);
        assertEquals(true, this.modelItem.isReadonly());

        this.parentItem.setLocalReadonly(false);
        this.modelItem.setLocalReadonly(true);
        assertEquals(true, this.modelItem.isReadonly());

        this.parentItem.setLocalReadonly(true);
        this.modelItem.setLocalReadonly(false);
        assertEquals(true, this.modelItem.isReadonly());

        this.parentItem.setLocalReadonly(false);
        this.modelItem.setLocalReadonly(false);
        assertEquals(false, this.modelItem.isReadonly());
    }

    /**
     * Tests required computation.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testRequiredComputation() throws Exception {
        this.modelItem.setLocalRequired(true);
        assertEquals(true, this.modelItem.isRequired());

        this.modelItem.setLocalRequired(false);
        assertEquals(false, this.modelItem.isRequired());
    }

    /**
     * Tests enabled computation.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testEnabledComputation() throws Exception {
        this.parentItem.setLocalRelevant(true);
        this.modelItem.setLocalRelevant(true);
        assertEquals(true, this.modelItem.isEnabled());

        this.parentItem.setLocalRelevant(false);
        this.modelItem.setLocalRelevant(true);
        assertEquals(false, this.modelItem.isEnabled());

        this.parentItem.setLocalRelevant(true);
        this.modelItem.setLocalRelevant(false);
        assertEquals(false, this.modelItem.isEnabled());

        this.parentItem.setLocalRelevant(false);
        this.modelItem.setLocalRelevant(false);
        assertEquals(false, this.modelItem.isEnabled());
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.parentItem = new ModelItem();
        this.modelItemListener = new TestModelItemListener();

        this.modelItem = new ModelItem();
        this.modelItem.setParent(this.parentItem);
        this.modelItem.register(this.modelItemListener);
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.modelItem.deregister(this.modelItemListener);
        this.modelItem.setParent(null);
        this.modelItem = null;

        this.modelItemListener = null;
        this.parentItem = null;
    }

    class TestModelItemListener implements ModelItemListener {
        Boolean state;
        String method;

        public void enabledChanged(boolean enabled) {
            this.state = Boolean.valueOf(enabled);
            this.method = "enabledChanged";
        }

        public void readonlyChanged(boolean readonly) {
            this.state = Boolean.valueOf(readonly);
            this.method = "readonlyChanged";
        }

        public void requiredChanged(boolean required) {
            this.state = Boolean.valueOf(required);
            this.method = "requiredChanged";
        }

        public void validChanged(boolean valid) {
            this.state = Boolean.valueOf(valid);
            this.method = "validChanged";
        }
    }

}

// end of class
