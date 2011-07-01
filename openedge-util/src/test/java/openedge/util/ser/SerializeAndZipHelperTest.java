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
public class SerializeAndZipHelperTest extends TestCase
{

	/**
	 * Construct.
	 */
	public SerializeAndZipHelperTest()
	{
		super();
	}

	/**
	 * Construct with name.
	 * 
	 * @param name
	 *            name unit test
	 */
	public SerializeAndZipHelperTest(String name)
	{
		super(name);
	}

	/**
	 * Test serializing, zipping and deserializing, unzipping.
	 * 
	 * @throws IOException
	 * @throws DataFormatException
	 * @throws ClassNotFoundException
	 */
	public void testSerZipAndDeSerUnzipZip() throws IOException, DataFormatException,
			ClassNotFoundException
	{

		List theObjects = new ArrayList();
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

		System.out.print("byte array: ");
		for (int i = 0; i < compressedData.length; i++)
		{
			System.out.print(compressedData[i]);
		}
		System.out.print("\n");

		List unpackedObjects = (List) SerializeAndZipHelper
				.unzipAndDeserialize(serializedAndZipped);
		assertNotNull(unpackedObjects);
		assertEquals(3, unpackedObjects.size());

		assertEquals(string1, unpackedObjects.get(0));
		assertEquals(string2, unpackedObjects.get(1));
		assertEquals(date, unpackedObjects.get(2));
	}
}