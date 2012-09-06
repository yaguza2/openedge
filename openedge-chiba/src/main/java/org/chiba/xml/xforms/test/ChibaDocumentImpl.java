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

import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.events.EventImpl;
import org.apache.xerces.dom.events.MutationEventImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.events.Event;

/**
 * Overwrites the standard Xerces Document implementation to re-define the DocumentEvent interface and provide
 * a new implementation of the createEvent method.
 *
 * @author joern turner <joernt@users.sourceforge.net>
 * @version $Id: ChibaDocumentImpl.java,v 1.6 2004/08/15 14:14:14 joernt Exp $
 */
public class ChibaDocumentImpl extends DocumentImpl {
    /**
     * __UNDOCUMENTED__
     *
     * @param type __UNDOCUMENTED__
     * @return __UNDOCUMENTED__
     * @throws DOMException __UNDOCUMENTED__
     */
    public Event createEvent(String type) throws DOMException {
        System.out.println("ChibaDocumentImpl createEvent");

        if (type.equalsIgnoreCase("Events") || "Event".equals(type)) {
            return new EventImpl();
        }

        if (type.equalsIgnoreCase("MutationEvents") || "MutationEvent".equals(type)) {
            return new MutationEventImpl();
        }

        if (type.equalsIgnoreCase("xforms-insert")) {
            return new XFormsInsertEvent();
        } else {
            String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN,
                    "NOT_SUPPORTED_ERR", null);
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
        }
    }
}


/*
   $Log: ChibaDocumentImpl.java,v $
   Revision 1.6  2004/08/15 14:14:14  joernt
   preparing release...
   -reformatted sources to fix mixture of tabs and spaces
   -optimized imports on all files

   Revision 1.5  2003/11/07 00:25:43  joernt
   optimized imports

   Revision 1.4  2003/10/10 01:40:41  joernt
   corrected package statement

   Revision 1.3  2003/10/08 20:59:56  joernt
   moved back into hierarchy

   Revision 1.2  2003/10/02 15:15:49  joernt
   applied chiba jalopy settings to whole src tree

   Revision 1.1  2003/09/11 21:25:01  joernt
   moved tests to their own hierarchy
   Revision 1.1.1.1  2003/05/23 14:54:08  unl
   no message
   Revision 1.1  2003/03/28 14:13:53  joernt
   testing DOM Events
 */
