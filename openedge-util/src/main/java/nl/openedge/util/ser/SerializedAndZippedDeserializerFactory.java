package nl.openedge.util.ser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.rpc.encoding.Deserializer;

import org.apache.axis.Constants;
import org.apache.axis.encoding.DeserializerFactory;

/**
 * AXIS deserializer factory for SerializedAndZipped objects. An example of a deployment
 * (.wsdd) script:
 * 
 * <pre>
 * 
 *  &lt;deployment name=&quot;polisservice&quot; xmlns=&quot;http://xml.apache.org/axis/wsdd/&quot;
 *  	xmlns:java=&quot;http://xml.apache.org/axis/wsdd/providers/java&quot;
 * 	xmlns:xsi=&quot;http://www.w3.org/2000/10/XMLSchema-instance&quot;&gt;
 * 	&lt;service name=&quot;polisservice&quot; provider=&quot;java:RPC&quot;&gt;
 * 		&lt;parameter name=&quot;alias&quot; value=&quot;polisservice&quot;/&gt;
 * 		&lt;parameter name=&quot;className&quot;
 * 			value=&quot;nl.levob.flexipluspensioen.webservices.PolisService&quot;/&gt;
 * 		&lt;parameter name=&quot;allowedMethods&quot; value=&quot;*&quot;/&gt;
 * 		&lt;parameter name=&quot;scope&quot; value=&quot;Request&quot;/&gt;
 * 		&lt;typeMapping xmlns:ns=&quot;http://levob/flexipluspensioen/&quot;
 *  			qname=&quot;ns:SerializedAndZipped&quot;
 *  			languageSpecificType=&quot;java:nl.openedge.util.ser.SerializedAndZipped&quot;
 *  			serializer=&quot;nl.openedge.util.ser.SerializedAndZippedSerializerFactory&quot;
 *  			deserializer=&quot;nl.openedge.util.ser.SerializedAndZippedDeserializerFactory&quot;
 *  			encodingStyle=&quot;http://schemas.xmlsoap.org/soap/encoding/&quot; /&gt;
 * 	&lt;/service&gt;
 *  &lt;/deployment&gt;
 * 
 * </pre>
 * 
 * <p>
 * This can then be consumed by a AXIS client like:
 * 
 * <pre>
 * QName typeQName = new QName(&quot;http://levob/flexipluspensioen/&quot;, &quot; SerializedAndZipped&quot;);
 * 
 * SerializedAndZipped serializedAndZipped = SerializeAndZipWSHelper.getRemoteObjects(endpoint,
 * 	&quot;getPolissen&quot;, typeQName);
 * 
 * </pre>
 * 
 * </p>
 * 
 * @author Eelco Hillenius
 */
public class SerializedAndZippedDeserializerFactory implements DeserializerFactory
{
	private static final long serialVersionUID = 1L;

	/** SAX mechanisms. */
	private List mechanisms = null;

	/**
	 * Construct.
	 */
	public SerializedAndZippedDeserializerFactory()
	{
		// noop
	}

	/**
	 * @see javax.xml.rpc.encoding.SerializerFactory#getSerializerAs(java.lang.String)
	 */
	@Override
	public Deserializer getDeserializerAs(final String string)
	{
		return new SerializedAndZippedDeserializer();
	}

	/**
	 * @see javax.xml.rpc.encoding.SerializerFactory#getSupportedMechanismTypes()
	 */
	@Override
	public Iterator getSupportedMechanismTypes()
	{
		if (mechanisms == null)
		{
			mechanisms = new ArrayList();
			mechanisms.add(Constants.AXIS_SAX);
		}
		return mechanisms.iterator();
	}
}
