/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003, Open Edge B.V.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions 
 * in binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution. Neither the name of OpenEdge B.V. 
 * nor the names of its contributors may be used to endorse or promote products 
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package nl.openedge.util.ser;

import ognl.Ognl;
import ognl.OgnlException;

import org.apache.axis.Constants;
import org.apache.axis.encoding.Base64;
import org.apache.axis.encoding.CallbackTarget;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.message.SOAPHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * XIS deserializer for SerializedAndZipped objects.
 *
 * @author Eelco Hillenius
 */
public class SerializedAndZippedDeserializer extends DeserializerImpl
{

    /**
     * Construct and create a new working copy of the value to deserialize.
     */
    public SerializedAndZippedDeserializer()
    {
        value = new SerializedAndZipped();
    }

    /**
     * @see org.apache.axis.message.SOAPHandler#onStartChild(java.lang.String,
     *      java.lang.String, java.lang.String, org.xml.sax.Attributes,
     *      org.apache.axis.encoding.DeserializationContext)
     */
    public SOAPHandler onStartChild(final String namespace, final String localName, final String prefix,
            final Attributes attributes, final DeserializationContext context) throws SAXException
    {

        // These can come in either order.
        Deserializer dSer = context.getDeserializerForType(Constants.XSD_STRING);
        // register this class as a callback target
        CallbackTarget target = new CallbackTarget(this, localName);
        dSer.registerValueTarget(target);

        return (SOAPHandler)dSer;
    }

    /**
     * @see org.apache.axis.encoding.Callback#setValue(java.lang.Object,
     *      java.lang.Object)
     */
    public void setValue(final Object value, final Object hint)
    {

        if(log.isDebugEnabled())
        {
            log.debug("setValue (hint = " + hint + ") " + value);
        }
        if(SerializedAndZipped.COMPRESSED_DATA.equals(hint))
        { // decode the string value
            String encoded = (String)value;
            byte[] data = Base64.decode(encoded);
            try
            {
                Ognl.setValue((String)hint, this.value, data);
            }
            catch(OgnlException e)
            {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        else
        { // just use Ognl to set the value
            try
            {
                Ognl.setValue((String)hint, this.value, value);
            }
            catch(OgnlException e)
            {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }
}