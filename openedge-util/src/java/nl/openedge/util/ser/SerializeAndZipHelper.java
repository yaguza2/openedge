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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import nl.openedge.util.rekenen.Rekenhulp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Helper for serialization and zipping of objects.
 * 
 * @author Eelco Hillenius
 */
public final class SerializeAndZipHelper
{

	/**
	 * Logger.
	 */
	private static Log log = LogFactory.getLog(SerializeAndZipHelper.class);

	/**
	 * Hidden constructor.
	 */
	private SerializeAndZipHelper()
	{
		// no nada
	}

	/**
	 * Unzip and deserialize the given struct.
	 * 
	 * @param serializedAndZipped
	 *            struct with serialized and zipped data
	 * @return root object that was serialized and zipped
	 * @throws IOException
	 * @throws DataFormatException
	 */
	public static Object unzipAndDeserialize(SerializedAndZipped serializedAndZipped)
			throws IOException, DataFormatException, ClassNotFoundException
	{

		long begin = System.currentTimeMillis();

		byte[] objectData = unzip(serializedAndZipped);
		Object root = deserialize(objectData);

		long end = System.currentTimeMillis();
		if (log.isDebugEnabled())
		{
			log.debug("unzipped and deserialized in " + (end - begin) + " milis");
		}

		return root;
	}

	/**
	 * Serialize and zip objects in ZLIB compression.
	 * 
	 * @param root
	 *            the root object to serialize and zip; if you need to process more than one object,
	 *            try packing them in a list or a composite object
	 * @return struct that holds the serialized and zipped data
	 * @throws IOException
	 */
	public static SerializedAndZipped serializeAndZip(Object root) throws IOException
	{

		if (root == null)
		{
			throw new IllegalArgumentException("object to serialize and zip is null");
		}
		long begin = System.currentTimeMillis();

		byte[] objectData = serialize(root);
		SerializedAndZipped serializedAndZipped = zip(objectData);

		long end = System.currentTimeMillis();
		if (log.isDebugEnabled())
		{
			log.debug("zipped and serialized in " + (end - begin) + " milis");
			final int PERC_FACTOR = 100;
			int compressedDataLength = serializedAndZipped.getCompressedData().length;
			int uncompressedDataLength = serializedAndZipped.getUncompressedDataLength();
			double zipRatio = ((double) compressedDataLength / (double) uncompressedDataLength) * 100;
			zipRatio = Rekenhulp.rondAf(zipRatio, 2);
			log.debug("zip ratio = "
					+ zipRatio + "%" + " (compr: " + compressedDataLength + " - uncompr: "
					+ uncompressedDataLength + ")");
		}

		return serializedAndZipped;
	}

	/**
	 * Serialize given object.
	 * 
	 * @param root
	 *            root object to serialize
	 * @return serialized data
	 * @throws IOException
	 */
	public static byte[] serialize(Object root) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(baos);

		objectOutputStream.writeObject(root);
		objectOutputStream.flush();
		objectOutputStream.close();
		baos.close();

		return baos.toByteArray();
	}

	/**
	 * De-serialize objects from given object data.
	 * 
	 * @param objectData
	 *            object data
	 * @return Object de-serialized object
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object deserialize(byte[] objectData) throws IOException, ClassNotFoundException
	{
		ByteArrayInputStream bais = new ByteArrayInputStream(objectData);
		ObjectInputStream objectInputStream = new ObjectInputStream(bais);
		return objectInputStream.readObject();
	}

	/**
	 * Zip object data with ZLIB compression.
	 * 
	 * @param objectData
	 *            object data
	 * @return struct for zipped data
	 */
	public static SerializedAndZipped zip(byte[] objectData)
	{
		Deflater compresser = new Deflater();
		compresser.setInput(objectData);
		compresser.finish();
		int uncompressedDataLength = objectData.length;
		byte[] temp = new byte[uncompressedDataLength];
		int compressedDataLength = compresser.deflate(temp);
		byte[] comprBinObjects = new byte[compressedDataLength];

		System.arraycopy(temp, 0, comprBinObjects, 0, compressedDataLength);

		SerializedAndZipped serializedAndZipped = new SerializedAndZipped(uncompressedDataLength,
				comprBinObjects);
		return serializedAndZipped;
	}

	/**
	 * Unzip object data with ZLIB compression.
	 * 
	 * @param serializedAndZipped
	 *            struct with zipped object data
	 * @return object data
	 * @throws DataFormatException
	 */
	public static byte[] unzip(SerializedAndZipped serializedAndZipped) throws DataFormatException
	{
		Inflater decompresser = new Inflater();
		decompresser.setInput(serializedAndZipped.getCompressedData());
		byte[] objectData = new byte[serializedAndZipped.getUncompressedDataLength()];
		int resultLength = decompresser.inflate(objectData);
		decompresser.end();
		return objectData;
	}
}