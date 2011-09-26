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
	 * @return the zipped and serialized objecten that were returned by the webservice
	 *         call
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
		SerializedAndZipped serializedAndZipped =
			(SerializedAndZipped) call.invoke(new Object[] {});
		return serializedAndZipped;
	}
}
