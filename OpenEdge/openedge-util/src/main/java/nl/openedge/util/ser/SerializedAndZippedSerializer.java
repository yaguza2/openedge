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
	private static final long serialVersionUID = 1L;

	/**
	 * Creeer.
	 */
	public SerializedAndZippedSerializer()
	{
		super();
	}

	/**
	 * @see org.apache.axis.encoding.Serializer#serialize(javax.xml.namespace.QName,
	 *      org.xml.sax.Attributes, java.lang.Object,
	 *      org.apache.axis.encoding.SerializationContext)
	 */
	@Override
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
	@Override
	public String getMechanismType()
	{
		return Constants.AXIS_SAX;
	}

	/**
	 * @see org.apache.axis.encoding.Serializer#writeSchema(java.lang.Class,
	 *      org.apache.axis.wsdl.fromJava.Types)
	 */
	@Override
	public Element writeSchema(final Class javaType, final Types types) throws Exception
	{
		return null;
	}

}
