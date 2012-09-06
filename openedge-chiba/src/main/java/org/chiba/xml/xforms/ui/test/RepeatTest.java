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
package org.chiba.xml.xforms.ui.test;

import junit.framework.TestCase;
import org.apache.commons.jxpath.JXPathContext;
import org.chiba.xml.xforms.ChibaBean;
import org.chiba.xml.xforms.NamespaceCtx;
import org.chiba.xml.xforms.events.EventFactory;
import org.chiba.xml.xforms.test.TestEventListener;
import org.chiba.xml.xforms.ui.Repeat;
import org.chiba.xml.xforms.ui.Text;
import org.w3c.dom.events.EventTarget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Tests repeat structures.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: RepeatTest.java,v 1.13 2004/12/17 21:00:45 unl Exp $
 */
public class RepeatTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private ChibaBean chibaBean;
    private Repeat repeat;
    private EventTarget eventTarget;
    private TestEventListener itemInsertedListener;
    private TestEventListener itemDeletedListener;
    private TestEventListener indexChangedListener;

    /**
     * Tests inserting before the current index.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertBefore() throws Exception {
        this.chibaBean.dispatch("trigger-insert-before", EventFactory.DOM_ACTIVATE);

        assertEquals(2, this.repeat.getIndex());
        assertEquals(4, this.repeat.getContextSize());

        assertEquals("repeat", this.itemInsertedListener.getId());
        assertEquals("2", this.itemInsertedListener.getContext());
        assertNull(this.itemDeletedListener.getId());
        assertNull(this.itemDeletedListener.getContext());
        assertEquals("repeat", this.indexChangedListener.getId());
        assertEquals("2", this.indexChangedListener.getContext());

        assertTrue(this.itemInsertedListener.getTime() < this.indexChangedListener.getTime());
    }

    /**
     * Tests inserting after the current index.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertAfter() throws Exception {
        this.chibaBean.dispatch("trigger-insert-after", EventFactory.DOM_ACTIVATE);

        assertEquals(3, this.repeat.getIndex());
        assertEquals(4, this.repeat.getContextSize());

        assertEquals("repeat", this.itemInsertedListener.getId());
        assertEquals("3", this.itemInsertedListener.getContext());
        assertNull(this.itemDeletedListener.getId());
        assertNull(this.itemDeletedListener.getContext());
        assertEquals("repeat", this.indexChangedListener.getId());
        assertEquals("3", this.indexChangedListener.getContext());

        assertTrue(this.itemInsertedListener.getTime() < this.indexChangedListener.getTime());
    }

    /**
     * Tests deleting at the current index.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDeleteAt() throws Exception {
        this.chibaBean.dispatch("trigger-delete-at", EventFactory.DOM_ACTIVATE);

        assertEquals(1, this.repeat.getIndex());
        assertEquals(2, this.repeat.getContextSize());

        assertNull(this.itemInsertedListener.getId());
        assertNull(this.itemInsertedListener.getContext());
        assertEquals("repeat", this.itemDeletedListener.getId());
        assertEquals("2", this.itemDeletedListener.getContext());
        assertNull(this.indexChangedListener.getId());
        assertNull(this.indexChangedListener.getContext());
    }

    /**
     * Tests deleting all nodes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDeleteAll() throws Exception {
        this.chibaBean.dispatch("trigger-delete-all", EventFactory.DOM_ACTIVATE);

        assertEquals(0, this.repeat.getIndex());
        assertEquals(0, this.repeat.getContextSize());

        assertNull(this.itemInsertedListener.getId());
        assertNull(this.itemInsertedListener.getContext());
        assertEquals("repeat", this.itemDeletedListener.getId());
        assertEquals("1", this.itemDeletedListener.getContext());
        assertEquals("repeat", this.indexChangedListener.getId());
        assertEquals("0", this.indexChangedListener.getContext());

        assertTrue(this.itemDeletedListener.getTime() < this.indexChangedListener.getTime());
    }

    /**
     * Tests multiple insert and delete operations en bloc.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testBatchUpdate() throws Exception {
        this.chibaBean.dispatch("trigger-batch-update", EventFactory.DOM_ACTIVATE);

        assertEquals(1, this.repeat.getIndex());
        assertEquals(3, this.repeat.getContextSize());

        assertEquals("repeat", this.itemInsertedListener.getId());
        assertEquals("1", this.itemInsertedListener.getContext());
        assertEquals("repeat", this.itemDeletedListener.getId());
        assertEquals("3", this.itemDeletedListener.getContext());
        assertNull(this.indexChangedListener.getId());
        assertNull(this.indexChangedListener.getContext());

        assertTrue(this.itemInsertedListener.getTime() < this.itemDeletedListener.getTime());
    }

    /**
     * Tests index updating.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetIndex() throws Exception {
        this.chibaBean.dispatch("trigger-setindex", EventFactory.DOM_ACTIVATE);

        assertEquals(3, this.repeat.getIndex());
        assertEquals(3, this.repeat.getContextSize());

        assertNull(this.itemInsertedListener.getId());
        assertNull(this.itemInsertedListener.getContext());
        assertNull(this.itemDeletedListener.getId());
        assertNull(this.itemDeletedListener.getContext());
        assertEquals("repeat", this.indexChangedListener.getId());
        assertEquals("3", this.indexChangedListener.getContext());
    }

    /**
     * Tests for correct prototype handling.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testRepeatPrototype() throws Exception {
        JXPathContext context = JXPathContext.newContext(this.repeat.getElement());

        /*
        <chiba:data ... >
            <xforms:group chiba:transient="true" chiba:position="0">
                <xforms:input id="input" xforms:ref=".">
                    <xforms:alert id="..."/>
                </xforms:input>
                <xforms:output id="output" xforms:ref="."/>
            </xforms:group>
        </chiba:data>
        */
        assertEquals("1", context.getValue("count(chiba:data/*)", Integer.class).toString());

        assertEquals("group", context.getValue("local-name(chiba:data/*[1])").toString());
        assertEquals(NamespaceCtx.XFORMS_NS, context.getValue("namespace-uri(chiba:data/*[1])").toString());
        assertEquals("true", context.getValue("chiba:data/xforms:group/@chiba:transient").toString());
        assertEquals("0", context.getValue("chiba:data/xforms:group/@chiba:position").toString());
        assertEquals("2", context.getValue("count(chiba:data/xforms:group/*)", Integer.class).toString());

        assertEquals("input", context.getValue("local-name(chiba:data/xforms:group/*[1])").toString());
        assertEquals(NamespaceCtx.XFORMS_NS, context.getValue("namespace-uri(chiba:data/xforms:group/*[1])").toString());
        assertEquals("input", context.getValue("chiba:data/xforms:group/xforms:input/@id").toString());
        assertEquals(".", context.getValue("chiba:data/xforms:group/xforms:input/@xforms:ref").toString());
        assertEquals("1", context.getValue("count(chiba:data/xforms:group/xforms:input/*)", Integer.class).toString());

        assertEquals("alert", context.getValue("local-name(chiba:data/xforms:group/xforms:input/*[1])").toString());
        assertEquals(NamespaceCtx.XFORMS_NS, context.getValue("namespace-uri(chiba:data/xforms:group/xforms:input/*[1])").toString());
        assertEquals(true, context.getValue("chiba:data/xforms:group/xforms:output/@id").toString().length() > 0);
        assertEquals("0", context.getValue("count(chiba:data/xforms:group/xforms:output/*)", Integer.class).toString());

        assertEquals("output", context.getValue("local-name(chiba:data/xforms:group/*[2])").toString());
        assertEquals(NamespaceCtx.XFORMS_NS, context.getValue("namespace-uri(chiba:data/xforms:group/*[2])").toString());
        assertEquals("output", context.getValue("chiba:data/xforms:group/xforms:output/@id").toString());
        assertEquals(".", context.getValue("chiba:data/xforms:group/xforms:output/@xforms:ref").toString());
        assertEquals("0", context.getValue("count(chiba:data/xforms:group/xforms:output/*)", Integer.class).toString());
    }

    /**
     * Tests canonical path resolution.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testCanonicalPath() throws Exception {
        assertEquals("/data[1]/item", this.repeat.getCanonicalPath());
    }

    /**
     * Tests canonical path resolution.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testCanonicalPathEmptyNodeset() throws Exception {
        Repeat emptyNodeset = (Repeat) this.chibaBean.getContainer().lookup("repeat-empty");
        assertEquals("/data[1]/empty", emptyNodeset.getCanonicalPath());
    }

    /**
     * Tests location path resolution.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testLocationPath() throws Exception {
        List ids = new ArrayList();
        JXPathContext context = JXPathContext.newContext(this.repeat.getElement());
        Iterator iterator = context.iterate("//xforms:input[chiba:data]/@id");
        while (iterator.hasNext()) {
            String id = (String) iterator.next();
            ids.add(id);
        }

        assertEquals(3, ids.size());
        for (int index = 0; index < ids.size(); index++) {
            String id = (String) ids.get(index);
            Text input = (Text) this.chibaBean.getContainer().lookup(id);
            assertEquals("/*[1]/item[" + String.valueOf(index + 1) + "]", input.getLocationPath());
        }
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.chibaBean = new ChibaBean();
        this.chibaBean.setXMLContainer(getClass().getResourceAsStream("RepeatTest.xhtml"));
        this.chibaBean.init();

        this.itemInsertedListener = new TestEventListener();
        this.itemDeletedListener = new TestEventListener();
        this.indexChangedListener = new TestEventListener();

        this.eventTarget = (EventTarget) this.chibaBean.getXMLContainer().getDocumentElement();
        this.eventTarget.addEventListener(EventFactory.ITEM_INSERTED, this.itemInsertedListener, true);
        this.eventTarget.addEventListener(EventFactory.ITEM_DELETED, this.itemDeletedListener, true);
        this.eventTarget.addEventListener(EventFactory.INDEX_CHANGED, this.indexChangedListener, true);

        this.repeat = (Repeat) this.chibaBean.getContainer().lookup("repeat");
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.eventTarget.removeEventListener(EventFactory.ITEM_INSERTED, this.itemInsertedListener, true);
        this.eventTarget.removeEventListener(EventFactory.ITEM_DELETED, this.itemDeletedListener, true);
        this.eventTarget.removeEventListener(EventFactory.INDEX_CHANGED, this.indexChangedListener, true);
        this.eventTarget = null;

        this.itemInsertedListener = null;
        this.itemDeletedListener = null;

        this.repeat = null;

        this.chibaBean.shutdown();
        this.chibaBean = null;
    }

}

// end of class
