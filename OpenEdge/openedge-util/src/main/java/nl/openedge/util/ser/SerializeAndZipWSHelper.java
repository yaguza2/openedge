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

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

/**
 * Helper for consuming SerializedAndZipped with a webservice.
 * 
 * @author Eelco Hillenius
 */
public final class SerializeAndZipWSHelper
{

	/**
	 * Hidden utility constructor.
	 */
	private SerializeAndZipWSHelper()
	{
		// nothing here
	}

	/**
	 * Get remote serialized and zipped objects.
	 * 
	 * @param endpoint
	 *            endpoint webservice
	 * @param method
	 *            webservice method
	 * @param qName
	 *            type qname
	 * @return the zipped and serialized objecten that were returned by the webservice call
	 * @throws ServiceException
	 *             when an unexpected WS exception occurs
	 * @throws MalformedURLException
	 *             with an invallid endpoint
	 * @throws RemoteException
	 *             when a server exception occurs
	 */
	public static SerializedAndZipped getRemoteObjects(String endpoint, String method, QName qName)
			throws ServiceException, MalformedURLException, RemoteException
	{
		Service service = new Service();
		Call call = (Call) service.createCall();
		call.setTargetEndpointAddress(new java.net.URL(endpoint));
		call.setOperationName(new QName("http://soapinterop.org/", method));
		call.registerTypeMapping(SerializedAndZipped.class, qName,
				SerializedAndZippedSerializerFactory.class,
				SerializedAndZippedDeserializerFactory.class);
		SerializedAndZipped serializedAndZipped = (SerializedAndZipped) call
				.invoke(new Object[] {});
		return serializedAndZipped;
	}
}
