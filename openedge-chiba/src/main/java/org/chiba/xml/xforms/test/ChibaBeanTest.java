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

import junit.framework.TestCase;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.jxpath.JXPathContext;
import org.chiba.xml.util.DOMComparator;
import org.chiba.xml.xforms.ChibaBean;
import org.chiba.xml.xforms.NamespaceCtx;
import org.chiba.xml.xforms.XFormsDocument;
import org.chiba.xml.xforms.events.EventFactory;
import org.chiba.xml.xforms.events.XFormsEvent;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedInputStream;
import java.net.URI;

/**
 * Tests various functions of the Chiba XForms Processor. Most functions are tested through XML in- and output-files.
 * <p/>
 * The tests in this class are 'stacked' that means they build on each other. If init fails all others may fail too.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ChibaBeanTest.java,v 1.21 2004/12/27 22:42:05 joernt Exp $
 */
public class ChibaBeanTest extends TestCase {

//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private ChibaBean processor;
    private Document form;
    private Document instance;
    private String baseURI;

    /**
     * Creates a new ChibaBean test.
     *
     * @param name the test name.
     */
    public ChibaBeanTest(String name) {
        super(name);
    }

    /**
     * tests the dispatching of an event to an element.
     *
     * @throws Exception __UNDOCUMENTED__
     */
    public void testDispatchEvent() throws Exception {
        processor.setXMLContainer(getClass().getResourceAsStream("actions.xhtml"));
        processor.init();
        assertTrue(!processor.dispatch("setvalue-trigger", EventFactory.DOM_ACTIVATE));
        
        //test the side effect of dispatching the DOMActivate to a setvalue trigger -> the instance has a new value
        Document instance = processor.getContainer().getDefaultModel().getDefaultInstance().getInstanceDocument();
        JXPathContext context = JXPathContext.newContext(instance);
        assertTrue(context.getValue("//helloworld").equals("Hello World"));
    }

    /**
     * test, if updateControlValue correctly changes the value and a value-changed event occurs on the relevant
     * control.
     *
     * @throws Exception
     */
    public void testUpdateControlValue1() throws Exception {
        processor.setXMLContainer(getClass().getResourceAsStream("actions.xhtml"));
        processor.init();

        Listener listener = new ChibaBeanTest.Listener();
        JXPathContext context = JXPathContext.newContext(this.processor.getXMLContainer());
        Node node = (Node) context.getPointer("//*[@id='hello-input']").getNode();
        EventTarget eventTarget = (EventTarget) node;
        eventTarget.addEventListener(EventFactory.VALUE_CHANGED, listener, false);

        processor.updateControlValue("hello-input", "Hello new World");
        assertTrue(listener.type.equals(EventFactory.VALUE_CHANGED));
        assertTrue(listener.target.equals("hello-input"));
        
        //test the side effect of dispatching the DOMActivate to a setvalue trigger -> the instance has a new value
        Document instance = processor.getContainer().getDefaultModel().getDefaultInstance().getInstanceDocument();
        JXPathContext context1 = JXPathContext.newContext(instance);
        assertTrue(context1.getValue("//helloworld").equals("Hello new World"));
    }

    /**
     * test if updateControlValue suppresses update for unchanged value.
     *
     * @throws Exception
     */
    public void testUpdateControlValue2() throws Exception {
        processor.setXMLContainer(getClass().getResourceAsStream("actions.xhtml"));
        processor.init();

        Listener listener = new ChibaBeanTest.Listener();
        JXPathContext context = JXPathContext.newContext(this.processor.getXMLContainer());
        Node node = (Node) context.getPointer("//*[@id='hello-input']").getNode();
        EventTarget eventTarget = (EventTarget) node;
        eventTarget.addEventListener(EventFactory.VALUE_CHANGED, listener, false);

        processor.updateControlValue("hello-input", "Hello World");
        //value mustn't change cause value of node is 'Hello World' initially - test the case that setValue isn't called
        assertNull(listener.type);
        assertNull(listener.target);
    }

    /**
     * test, if updateControlValue correctly changes the value and a value-changed event occurs on the relevant control.
     * Check the new updateControlValue method suitable for Upload controls with base64 binary data.
     *
     * @throws Exception
     */
    public void testUploadBase64() throws Exception {
        String testFilename = "hello-upload.xhtml";
        String id = "upload-input";
        String nodePath = "//file";
        processor.setXMLContainer(getClass().getResourceAsStream(testFilename));
        processor.init();

        Listener listener = new ChibaBeanTest.Listener();
        JXPathContext context = JXPathContext.newContext(this.processor.getXMLContainer());
        Node node = (Node) context.getPointer("//*[@id='" + id + "']").getNode();
        EventTarget eventTarget = (EventTarget) node;
        eventTarget.addEventListener(EventFactory.VALUE_CHANGED, listener, false);
        BufferedInputStream bf = new BufferedInputStream(getClass().getResourceAsStream(testFilename));
        byte[] bytes = new byte[bf.available()];
        bf.read(bytes);
        String testString = new String(bytes);
        String uploadFilename = "upload-test.txt";
        String uploadMediatype = "text/xml";
        processor.updateControlValue(id, uploadMediatype, uploadFilename, bytes);

        assertTrue(listener.type.equals(EventFactory.VALUE_CHANGED));
        assertTrue(listener.target.equals(id));
        
        //test the side effect of dispatching the DOMActivate to a setvalue 
        //trigger -> the instance has a new value
        Document instance = processor.getContainer().getDefaultModel()
                .getDefaultInstance().getInstanceDocument();
        JXPathContext context1 = JXPathContext.newContext(instance);
        byte[] value = context1.getValue(nodePath).toString().getBytes();
        assertEquals(testString, new String(Base64.decodeBase64(value)));
        
        //Verify upload properties
        assertEquals(uploadFilename, context1.getValue(nodePath + "/@path").toString());
        assertEquals(uploadMediatype, context1.getValue(nodePath + "/@contentType").toString());
    }

    /**
     * test, if updateControlValue correctly changes the value and a value-changed event occurs on the relevant control.
     * Check the new updateControlValue method suitable for Upload controls with hex binary data.
     *
     * @throws Exception
     */
    public void testUploadHex() throws Exception {
        String testFilename = "hello-upload.xhtml";
        String id = "upload-input-hex";
        String nodePath = "//file2";
        processor.setXMLContainer(getClass().getResourceAsStream(testFilename));
        processor.init();

        Listener listener = new ChibaBeanTest.Listener();
        JXPathContext context = JXPathContext.newContext(this.processor.getXMLContainer());
        Node node = (Node) context.getPointer("//*[@id='" + id + "']").getNode();
        EventTarget eventTarget = (EventTarget) node;
        eventTarget.addEventListener(EventFactory.VALUE_CHANGED, listener, false);
        BufferedInputStream bf = new BufferedInputStream(getClass().getResourceAsStream(testFilename));
        byte[] bytes = new byte[bf.available()];
        bf.read(bytes);
        String testString = new String(bytes);
        String uploadFilename = "upload-test.txt";
        String uploadMediatype = "text/xml";
        processor.updateControlValue(id, uploadMediatype, uploadFilename, bytes);

        assertTrue(listener.type.equals(EventFactory.VALUE_CHANGED));
        assertTrue(listener.target.equals(id));
        
        //test the side effect of dispatching the DOMActivate to a setvalue
        //trigger -> the instance has a new value
        Document instance = processor.getContainer().getDefaultModel()
                .getDefaultInstance().getInstanceDocument();
        JXPathContext context1 = JXPathContext.newContext(instance);
        char[] value = context1.getValue(nodePath).toString().toCharArray();
        assertEquals(testString, new String(Hex.decodeHex(value)));
        
        //Verify upload properties
        assertEquals(uploadFilename, context1.getValue(nodePath + "/@path").toString());
        assertEquals(uploadMediatype, context1.getValue(nodePath + "/@contentType").toString());

    }

    /**
     * test, if updateControlValue correctly changes the value and a value-changed event occurs on the relevant control.
     * Check the new updateControlValue method suitable for Upload controls with anyURI type data.
     *
     * @throws Exception
     */
    public void testUploadAnyURI() throws Exception {
        String testFilename = "hello-upload.xhtml";
        String id = "upload-uri";
        String nodePath = "//file4";
        processor.setXMLContainer(getClass().getResourceAsStream(testFilename));
        processor.init();

        Listener listener = new ChibaBeanTest.Listener();
        JXPathContext context = JXPathContext.newContext(this.processor.getXMLContainer());
        Node node = (Node) context.getPointer("//*[@id='" + id + "']").getNode();
        EventTarget eventTarget = (EventTarget) node;
        eventTarget.addEventListener(EventFactory.VALUE_CHANGED, listener, false);
        /* the content must be the URI !!
        BufferedInputStream bf = new BufferedInputStream(getClass().getResourceAsStream(testFilename));
        byte[] bytes = new byte[bf.available()];
        bf.read(bytes);
        String testString = new String(bytes);
        */
        String uploadFilename = "upload-test.txt";
        String uploadMediatype = "text/xml";
        processor.updateControlValue(id, uploadMediatype, uploadFilename, 
            getClass().getResource(testFilename).toString().getBytes());

        assertTrue(listener.type.equals(EventFactory.VALUE_CHANGED));
        assertTrue(listener.target.equals(id));
        
        //test the side effect of dispatching the DOMActivate to a setvalue
        //trigger -> the instance has a new value
        Document instance = processor.getContainer().getDefaultModel()
                .getDefaultInstance().getInstanceDocument();
        JXPathContext context1 = JXPathContext.newContext(instance);
        String value = context1.getValue(nodePath).toString();
        assertEquals(getClass().getResource(testFilename).toString(), value);
        
        //Verify upload properties
        assertEquals(uploadFilename, context1.getValue(nodePath + "/@path").toString());
        assertEquals(uploadMediatype, context1.getValue(nodePath + "/@contentType").toString());

    }


    /**
     * test if updateControlValue accepts string types for uploading. The processor must throw an XFormsException
     *
     * @throws Exception
     */
    public void testUploadWrong() throws Exception {
        String id = "upload-wrong";
        processor.setXMLContainer(getClass().getResourceAsStream("hello-upload.xhtml"));
        processor.init();

        StringBuffer sb = new StringBuffer();
        sb.append("Hello new world");
        String uploadFilename = "";
        String msg = null;
        try {
            processor.updateControlValue(id, "text/plain", uploadFilename,
                    sb.toString().getBytes());
        } catch (XFormsException xfe) {
            msg = xfe.getMessage();
        }
        assertNotNull(msg);
        assertEquals(msg, "Unsupported datatype for Upload: string");
    }

    /**
     * variation of above test. if no newValue is passed to uplaodControlValue this Nullpointer
     * has to be catched and should result in an exception.
     *
     * @throws Exception
     */
    public void testUploadWrongNull() throws Exception {
        String id = "upload-wrong";
        processor.setXMLContainer(getClass().getResourceAsStream("hello-upload.xhtml"));
        processor.init();

        StringBuffer sb = new StringBuffer("Hello new world");
        String msg = null;
        try {
            processor.updateControlValue(id, "text/plain", null,
                    sb.toString().getBytes());
        } catch (XFormsException xfe) {
            msg = xfe.getMessage();
        }
        assertNotNull(msg);
        assertEquals(msg, "No name specified for Upload data - Param 'newValue' is null");
    }

    /**
     * __UNDOCUMENTED__
     *
     * @throws Exception __UNDOCUMENTED__
     */
    public void testGetInstanceDocument() throws Exception {
        //configure Bean with it
        processor.setXMLContainer(getClass().getResourceAsStream("buglet.xml"));
        processor.init();
        
        //get the instance-document
        Document out = processor.getContainer().getDefaultModel().getInstanceDocument("");
        assertNotNull(out);
        
        //get expected document
        Document expected = getXmlResource("buglet-instance.xml");
        assertTrue(getComparator().compare(processor.getContainer().getModel(null).getDefaultInstance()
                .getInstanceDocument().getDocumentElement(),
                expected.getDocumentElement()));
    }

    /**
     * tests processor initialization
     * <p/>
     * this may be taken as a blueprint for instanciating a ChibaBean in an arbitrary environment
     *
     * @throws Exception
     */
    public void testInit() throws Exception {
        //determine path of the the xml testfile and use it as baseURI for processor
        String path = getClass().getResource("buglet.xml").getPath();
        //        System.out.println("path: " + path);
        //set the XForms document to process
        this.processor.setXMLContainer(getClass().getResourceAsStream("buglet.xml"));
        
        //set the base URI for this process
        this.processor.setBaseURI("file://" + path);
        
        //set URI of instance document for default instance
        processor.setInstanceURI(null, "buglet-instance.xml");
        
        //set another URI for a instance named 'test'
        processor.setInstanceURI("another", "buglet-instance.xml");
        
        //initialize/bootstrap processor
        this.processor.init();

        //check, if default instance has input document
        assertTrue(getComparator().compare(processor.getContainer().getDefaultModel().getDefaultInstance()
                .getInstanceDocument(),
                getXmlResource("buglet-instance.xml")));
        
        //check if instance 'another' has input document
        assertTrue(getComparator().compare(processor.getContainer().getDefaultModel().getInstance("another")
                .getInstanceDocument(),
                getXmlResource("buglet-instance.xml")));
    }

    /**
     * Tests instance element assignment.
     *
     * @throws Exception in any error occurred during the test.
     */
    public void testSetInstanceElement() throws Exception {
        this.processor.setXMLContainer(this.form);
        this.processor.setInstanceElement("instance-1", this.instance.getDocumentElement());

        Element instance = (Element) this.processor.getXMLContainer()
                .getElementsByTagNameNS(NamespaceCtx.XFORMS_NS, "instance")
                .item(1);
        Element data = (Element) instance.getElementsByTagNameNS(null, "data").item(0);

        assertNotNull(data);
        assertTrue(getComparator().compare(this.instance.getDocumentElement(), data));
    }

    /**
     * Tests default instance element assignment.
     *
     * @throws Exception in any error occurred during the test.
     */
    public void testSetInstanceElementDefault() throws Exception {
        this.processor.setXMLContainer(this.form);
        this.processor.setInstanceElement("", this.instance.getDocumentElement());

        Element instance = (Element) this.processor.getXMLContainer()
                .getElementsByTagNameNS(NamespaceCtx.XFORMS_NS, "instance")
                .item(0);
        Element data = (Element) instance.getElementsByTagNameNS(null, "data").item(0);

        assertNotNull(data);
        assertTrue(getComparator().compare(this.instance.getDocumentElement(), data));
    }

    /**
     * Tests instance URI assignment.
     *
     * @throws Exception in any error occurred during the test.
     */
    public void testSetInstanceURI() throws Exception {
        this.processor.setXMLContainer(this.form);
        this.processor.setInstanceURI("instance-1", "test-uri");

        Element instance = (Element) this.processor.getXMLContainer()
                .getElementsByTagNameNS(NamespaceCtx.XFORMS_NS, "instance")
                .item(1);

        assertTrue(instance.hasAttributeNS(NamespaceCtx.XFORMS_NS, "src"));
        assertTrue(instance.getAttributeNS(NamespaceCtx.XFORMS_NS, "src").equals("test-uri"));
    }

    /**
     * Tests default instance URI assignment.
     *
     * @throws Exception in any error occurred during the test.
     */
    public void testSetInstanceURIDefault() throws Exception {
        this.processor.setXMLContainer(this.form);
        this.processor.setInstanceURI("", "test-uri");

        Element instance = (Element) this.processor.getXMLContainer()
                .getElementsByTagNameNS(NamespaceCtx.XFORMS_NS, "instance")
                .item(0);

        assertTrue(instance.hasAttributeNS(NamespaceCtx.XFORMS_NS, "src"));
        assertTrue(instance.getAttributeNS(NamespaceCtx.XFORMS_NS, "src").equals("test-uri"));
    }

    /**
     * Tests submission URI assignment.
     *
     * @throws Exception in any error occurred during the test.
     */
    public void testSetSubmissionURI() throws Exception {
        this.processor.setXMLContainer(this.form);
        this.processor.setSubmissionURI("submission-1", "test-uri");

        Element submission = (Element) this.processor.getXMLContainer()
                .getElementsByTagNameNS(NamespaceCtx.XFORMS_NS, "submission")
                .item(1);

        assertTrue(submission.hasAttributeNS(NamespaceCtx.XFORMS_NS, "action"));
        assertTrue(submission.getAttributeNS(NamespaceCtx.XFORMS_NS, "action").equals("test-uri"));
    }

    /**
     * Tests submission URI assignment.
     *
     * @throws Exception in any error occurred during the test.
     */
    public void testSetSubmissionURIDefault() throws Exception {
        this.processor.setXMLContainer(this.form);
        this.processor.setSubmissionURI("", "test-uri");

        Element submission = (Element) this.processor.getXMLContainer()
                .getElementsByTagNameNS(NamespaceCtx.XFORMS_NS, "submission")
                .item(0);

        assertTrue(submission.hasAttributeNS(NamespaceCtx.XFORMS_NS, "action"));
        assertTrue(submission.getAttributeNS(NamespaceCtx.XFORMS_NS, "action").equals("test-uri"));
    }

    /**
     * Tests setting a XML container file name.
     *
     * @throws Exception in any error occurred during the test.
     */
    public void testSetXMLContainerFile() throws Exception {
        this.processor.setXMLContainer(getClass().getResource("ChibaBeanTest.xhtml").getPath());

        assertNotNull(this.processor.getContainer());
        assertTrue(this.processor.getContainer().getDocument() instanceof XFormsDocument);
    }

    /**
     * Tests setting a XML container DOM.
     *
     * @throws Exception in any error occurred during the test.
     */
    public void testSetXMLContainerDOM() throws Exception {
        this.processor.setXMLContainer(this.form);

        assertNotNull(this.processor.getContainer());
        assertTrue(this.processor.getContainer().getDocument() instanceof XFormsDocument);
    }

    /**
     * Tests setting an absolute XML container URI.
     *
     * @throws Exception in any error occurred during the test.
     */
    public void testSetXMLContainerURIAbsolute() throws Exception {
        this.processor.setXMLContainer(new URI(getClass().getResource("ChibaBeanTest.xhtml").toExternalForm()));

        assertNotNull(this.processor.getContainer());
        assertTrue(this.processor.getContainer().getDocument() instanceof XFormsDocument);
    }

    /**
     * Tests setting a relative XML container URI.
     *
     * @throws Exception in any error occurred during the test.
     */
    public void testSetXMLContainerURIRelative() throws Exception {
        this.processor.setBaseURI(this.baseURI);
        this.processor.setXMLContainer(new URI("ChibaBeanTest.xhtml"));

        assertNotNull(this.processor.getContainer());
        assertTrue(this.processor.getContainer().getDocument() instanceof XFormsDocument);
    }

    /**
     * Tests setting a XML container input stream.
     *
     * @throws Exception in any error occurred during the test.
     */
    public void testSetXMLContainerInputStream() throws Exception {
        this.processor.setXMLContainer(getClass().getResourceAsStream("ChibaBeanTest.xhtml"));

        assertNotNull(this.processor.getContainer());
        assertTrue(this.processor.getContainer().getDocument() instanceof XFormsDocument);
    }

    /**
     * Tests setting a XML container input source.
     *
     * @throws Exception in any error occurred during the test.
     */
    public void testSetXMLContainerInputSource() throws Exception {
        this.processor.setXMLContainer(new InputSource(this.baseURI + "ChibaBeanTest.xhtml"));

        assertNotNull(this.processor.getContainer());
        assertTrue(this.processor.getContainer().getDocument() instanceof XFormsDocument);
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        this.form = builder.parse(getClass().getResourceAsStream("ChibaBeanTest.xhtml"));
        this.instance = builder.parse(getClass().getResourceAsStream("ChibaBeanTestInstance.xml"));

        String path = getClass().getResource("ChibaBeanTest.xhtml").getPath();
        this.baseURI = "file://" + path.substring(0, path.lastIndexOf("ChibaBeanTest.xhtml"));

        this.processor = new ChibaBean();
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.form = null;
        this.instance = null;
        this.baseURI = null;
        this.processor = null;
    }

    private DOMComparator getComparator() {
        DOMComparator comparator = new DOMComparator();
        comparator.setPrintErrors(true);
        comparator.setIgnoreNamespaceDeclarations(true);
        comparator.setIgnoreWhitespace(true);
        comparator.setIgnoreComments(true);

        return comparator;
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


    private class Listener implements EventListener {
        private String type = null;
        private String target = null;

        public void handleEvent(Event evt) {
            if (evt instanceof XFormsEvent) {
                XFormsEvent event = (XFormsEvent) evt;
                this.type = event.getType();
                this.target = ((Element) event.getTarget()).getAttribute("id");
            }
        }

    }
}

//end of class

