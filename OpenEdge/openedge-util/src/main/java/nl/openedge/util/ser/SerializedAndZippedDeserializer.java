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

	private static final long serialVersionUID = 1L;

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
	@Override
	public SOAPHandler onStartChild(final String namespace, final String localName,
			final String prefix, final Attributes attributes, final DeserializationContext context)
			throws SAXException
	{

		// These can come in either order.
		Deserializer dSer = context.getDeserializerForType(Constants.XSD_STRING);
		// register this class as a callback target
		CallbackTarget target = new CallbackTarget(this, localName);
		dSer.registerValueTarget(target);

		return (SOAPHandler) dSer;
	}

	/**
	 * @see org.apache.axis.encoding.Callback#setValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setValue(final Object value, final Object hint)
	{

		if (log.isDebugEnabled())
		{
			log.debug("setValue (hint = " + hint + ") " + value);
		}
		if (SerializedAndZipped.COMPRESSED_DATA.equals(hint))
		{ // decode the string value
			String encoded = (String) value;
			byte[] data = Base64.decode(encoded);
			try
			{
				Ognl.setValue((String) hint, this.value, data);
			}
			catch (OgnlException e)
			{
				log.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}
		else
		{ // just use Ognl to set the value
			try
			{
				Ognl.setValue((String) hint, this.value, value);
			}
			catch (OgnlException e)
			{
				log.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}
	}
}
