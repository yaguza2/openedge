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

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.axis.Constants;
import org.apache.axis.encoding.Base64;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * AXIS serializer for SerializedAndZipped objects.
 * 
 * @author Eelco Hillenius
 */
public class SerializedAndZippedSerializer implements Serializer
{

	/**
	 * Creeer.
	 */
	public SerializedAndZippedSerializer()
	{
		super();
	}

	/**
	 * @see org.apache.axis.encoding.Serializer#serialize(javax.xml.namespace.QName,
	 *      org.xml.sax.Attributes, java.lang.Object, org.apache.axis.encoding.SerializationContext)
	 */
	public void serialize(final QName name, final Attributes attributes, final Object value,
			final SerializationContext context) throws IOException
	{

		if (!(value instanceof SerializedAndZipped))
		{
			throw new IOException("Ongeldig type voor een SerializedAndZipped ("
					+ value.getClass().getName() + ")");
		}
		SerializedAndZipped struct = (SerializedAndZipped) value;
		context.startElement(name, attributes);
		if (struct != null)
		{
			Integer dataLength = new Integer(struct.getUncompressedDataLength());
			context.serialize(new QName("", SerializedAndZipped.UNCOMPRESSED_DATA_LENGTH), null,
					dataLength);
			String encoded = Base64.encode(struct.getCompressedData());
			context.serialize(new QName("", SerializedAndZipped.COMPRESSED_DATA), null, encoded);
		}
		context.endElement();
	}

	/**
	 * @see javax.xml.rpc.encoding.Serializer#getMechanismType()
	 */
	public String getMechanismType()
	{
		return Constants.AXIS_SAX;
	}

	/**
	 * @see org.apache.axis.encoding.Serializer#writeSchema(java.lang.Class,
	 *      org.apache.axis.wsdl.fromJava.Types)
	 */
	public Element writeSchema(final Class javaType, final Types types) throws Exception
	{
		return null;
	}

}