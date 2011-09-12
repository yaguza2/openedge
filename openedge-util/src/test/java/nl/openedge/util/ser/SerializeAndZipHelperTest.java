package nl.openedge.util.ser;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.DataFormatException;

import org.junit.Test;

/**
 * Test for SerializeAndZipHelper.
 * 
 * @author Eelco Hillenius
 */
public class SerializeAndZipHelperTest
{
	@Test
	public void testSerZipAndDeSerUnzipZip() throws IOException, DataFormatException,
			ClassNotFoundException
	{
		List<Object> theObjects = new ArrayList<Object>();
		String string1 = "string1";
		theObjects.add(string1);
		String string2 = "string2";
		theObjects.add(string2);
		Calendar cal = Calendar.getInstance();
		cal.set(2002, 2, 2);
		Date date = cal.getTime();
		theObjects.add(date);

		SerializedAndZipped serializedAndZipped = SerializeAndZipHelper.serializeAndZip(theObjects);
		byte[] compressedData = serializedAndZipped.getCompressedData();

		assertTrue(serializedAndZipped.getUncompressedDataLength() > 0);
		assertNotNull(compressedData);

		System.out.print("\n");

		@SuppressWarnings("unchecked")
		List<Object> unpackedObjects =
			(List<Object>) SerializeAndZipHelper.unzipAndDeserialize(serializedAndZipped);
		assertNotNull(unpackedObjects);
		assertEquals(3, unpackedObjects.size());

		assertEquals(string1, unpackedObjects.get(0));
		assertEquals(string2, unpackedObjects.get(1));
		assertEquals(date, unpackedObjects.get(2));
	}
}
