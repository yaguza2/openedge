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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.rpc.encoding.Deserializer;

import org.apache.axis.Constants;
import org.apache.axis.encoding.DeserializerFactory;

/**
 * AXIS deserializer factory for SerializedAndZipped objects.
 * An example of a deployment (.wsdd) script:
 * <pre>
 * &lt;deployment name="polisservice" xmlns="http://xml.apache.org/axis/wsdd/"
 * 	xmlns:java="http://xml.apache.org/axis/wsdd/providers/java"
 *	xmlns:xsi="http://www.w3.org/2000/10/XMLSchema-instance"&gt;
 *	&lt;service name="polisservice" provider="java:RPC"&gt;
 *		&lt;parameter name="alias" value="polisservice"/&gt;
 *		&lt;parameter name="className" value="nl.levob.flexipluspensioen.webservices.PolisService"/&gt;
 *		&lt;parameter name="allowedMethods" value="*"/&gt;
 *		&lt;parameter name="scope" value="Request"/&gt;
 *		&lt;typeMapping xmlns:ns="http://levob/flexipluspensioen/"
 * 			qname="ns:SerializedAndZipped"
 * 			languageSpecificType="java:nl.openedge.util.ser.SerializedAndZipped"
 * 			serializer="nl.openedge.util.ser.SerializedAndZippedSerializerFactory"
 * 			deserializer="nl.openedge.util.ser.SerializedAndZippedDeserializerFactory"
 * 			encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" /&gt;
 *	&lt;/service&gt;
 * &lt;/deployment&gt;
 * </pre>
 * </p>
 * <p>
 * 	This can then be consumed by a AXIS client like:
 * <pre>
 * QName typeQName = new QName("http://levob/flexipluspensioen/", "SerializedAndZipped");
 * SerializedAndZipped serializedAndZipped = SerializeAndZipWSHelper.getRemoteObjects(endpoint, "getPolissen", typeQName);
 * </pre>
 * </p>
 *
 * @author Eelco Hillenius
 */
public class SerializedAndZippedDeserializerFactory implements DeserializerFactory
{

    /**
     * Construct.
     */
    public SerializedAndZippedDeserializerFactory()
    {
        // noop
    }

    /** SAX mechanisms. */
    private List mechanisms = null;

    /**
     * @see javax.xml.rpc.encoding.SerializerFactory#getSerializerAs(java.lang.String)
     */
    public Deserializer getDeserializerAs(final String string)
    {
        return new SerializedAndZippedDeserializer();
    }

    /**
     * @see javax.xml.rpc.encoding.SerializerFactory#getSupportedMechanismTypes()
     */
    public Iterator getSupportedMechanismTypes()
    {
        if(mechanisms == null)
        {
            mechanisms = new ArrayList();
            mechanisms.add(Constants.AXIS_SAX);
        }
        return mechanisms.iterator();
    }
}