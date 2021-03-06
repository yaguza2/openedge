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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper for serialization and zipping of objects.
 * 
 * @author Eelco Hillenius
 */
public final class SerializeAndZipHelper
{
	/** a percentage factor. */
	static final int PERC_FACTOR = 100;

	/**
	 * Logger.
	 */
	private static Logger log = LoggerFactory.getLogger(SerializeAndZipHelper.class);

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
	 *             see exception
	 * @throws DataFormatException
	 *             see exception
	 * @throws ClassNotFoundException
	 *             see exception
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
	 *            the root object to serialize and zip; if you need to process more than
	 *            one object, try packing them in a list or a composite object
	 * @return struct that holds the serialized and zipped data
	 * @throws IOException
	 *             see exception
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
			int compressedDataLength = serializedAndZipped.getCompressedData().length;
			int uncompressedDataLength = serializedAndZipped.getUncompressedDataLength();
			double zipRatio =
				((double) compressedDataLength / (double) uncompressedDataLength) * PERC_FACTOR;
			zipRatio = Rekenhulp.rondAf(zipRatio, 2);
			log.debug("zip ratio = " + zipRatio + "%" + " (compr: " + compressedDataLength
				+ " - uncompr: " + uncompressedDataLength + ")");
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
	 *             see exception
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
	 *             see exception
	 * @throws ClassNotFoundException
	 *             see exception
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

		SerializedAndZipped serializedAndZipped =
			new SerializedAndZipped(uncompressedDataLength, comprBinObjects);
		return serializedAndZipped;
	}

	/**
	 * Unzip object data with ZLIB compression.
	 * 
	 * @param serializedAndZipped
	 *            struct with zipped object data
	 * @return object data
	 * @throws DataFormatException
	 *             see exception
	 */
	public static byte[] unzip(SerializedAndZipped serializedAndZipped) throws DataFormatException
	{
		Inflater decompresser = new Inflater();
		decompresser.setInput(serializedAndZipped.getCompressedData());
		byte[] objectData = new byte[serializedAndZipped.getUncompressedDataLength()];
		int resultLength = decompresser.inflate(objectData);
		if (log.isDebugEnabled())
		{
			log.debug("result length: " + resultLength);
		}
		decompresser.end();
		return objectData;
	}
}
