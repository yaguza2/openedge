package nl.openedge.util.ser;

import java.io.Serializable;

/**
 * Struct for serialized and zipped objects.
 * 
 * @author Eelco Hillenius
 */
public final class SerializedAndZipped implements Serializable, Cloneable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Member compressed data data, value = 'compressedData'.
	 */
	public static final String COMPRESSED_DATA = "compressedData";

	/**
	 * Member uncompressed data length, value = 'uncompressedDataLength'.
	 */
	public static final String UNCOMPRESSED_DATA_LENGTH = "uncompressedDataLength";

	/**
	 * zipped object data array.
	 */
	private byte[] compressedData;

	/**
	 * de original length of object data array.
	 */
	private int uncompressedDataLength;

	/**
	 * Construct.
	 */
	public SerializedAndZipped()
	{
		// nothing here
	}

	/**
	 * Construct with original length and compressed data.
	 * 
	 * @param uncompressedDataLength
	 *            original length of data
	 * @param compressedData
	 *            the compressed data
	 */
	public SerializedAndZipped(int uncompressedDataLength, byte[] compressedData)
	{
		this.uncompressedDataLength = uncompressedDataLength;
		this.compressedData = compressedData;
	}

	/**
	 * Get objectData.
	 * 
	 * @return byte[] Returns the objectData.
	 */
	public byte[] getCompressedData()
	{
		return compressedData;
	}

	/**
	 * Set objectData.
	 * 
	 * @param objectData
	 *            objectData to set.
	 */
	public void setCompressedData(byte[] objectData)
	{
		this.compressedData = objectData;
	}

	/**
	 * Get originalLength.
	 * 
	 * @return int Returns the originalLength.
	 */
	public int getUncompressedDataLength()
	{
		return uncompressedDataLength;
	}

	/**
	 * Set originalLength.
	 * 
	 * @param originalLength
	 *            originalLength to set.
	 */
	public void setUncompressedDataLength(int originalLength)
	{
		this.uncompressedDataLength = originalLength;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "SerializedAndZipped {uncompressedDataLength = " + uncompressedDataLength
			+ ", compressedData = " + compressedData + "}";
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
