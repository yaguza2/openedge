/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c)
 * All rechten voorbehouden.
 */
package nl.openedge.util.ser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.DataFormatException;

import junit.framework.TestCase;


/**
 * Test for SerializeAndZipHelper.
 *
 * @author Eelco Hillenius
 */
public class SerializeAndZipHelperTest extends TestCase {

    /**
     * Construct.
     */
    public SerializeAndZipHelperTest() {
        super();
    }

    /**
     * Construct with naam.
     * @param name naam unit test
     */
    public SerializeAndZipHelperTest(String name) {
        super(name);
    }

    /**
     * Test serializing and deserializing.
     *
     * @throws IOException
     * @throws DataFormatException
     * @throws ClassNotFoundException
     */
    public void testSerialisatieEnDeserialisatie() throws 
    		IOException,
    		DataFormatException,
    		ClassNotFoundException {

        List theObjects = new ArrayList();
        String string1 = "string1";
        theObjects.add(string1);
        String string2 = "string2";
        theObjects.add(string2);
        Calendar cal = Calendar.getInstance();
        cal.set(2002, 2, 2);
        Date date = cal.getTime();
        theObjects.add(date);
        
        SerializedAndZipped serializedAndZipped = 
            SerializeAndZipHelper.serializeAndZip(theObjects);
        byte[] compressedData = serializedAndZipped.getCompressedData();

        assertTrue(serializedAndZipped.getUncompressedDataLength() > 0);
        assertNotNull(compressedData);

        System.out.print("byte array: ");
        for(int i = 0; i < compressedData.length; i++) {
            System.out.print(compressedData[i]);
        }
        System.out.print("\n");

        List unpackedObjects = (List)
        	SerializeAndZipHelper.unzipAndDeserialize(serializedAndZipped);
        assertNotNull(unpackedObjects);
        assertEquals(3, unpackedObjects.size());

        assertEquals(string1, unpackedObjects.get(0));
        assertEquals(string2, unpackedObjects.get(1));
        assertEquals(date, unpackedObjects.get(2));
    }
}
