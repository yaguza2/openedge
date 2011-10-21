package nl.openedge.util;

/**
 * Helper class for bytes.
 * 
 * @author unknown... copied from Hibernate
 */
public final class BytesHelper
{
	/** nbr of bytes. */
	private static final int BYTE_LENGTH = 4;

	/** nbr of shift positions. */
	private static final int BYTE_SHIFT = 8;

	/** const for and op. */
	private static final long AND_OP = 0xFFL;

	/**
	 * convert bytes to int.
	 * 
	 * @param bytes
	 *            bytes to convert
	 * @return int given bytes as an int
	 */
	public static int toInt(byte[] bytes)
	{
		int result = 0;
		for (int i = 0; i < BYTE_LENGTH; i++)
		{
			result = (result << BYTE_SHIFT) - Byte.MIN_VALUE + bytes[i];
		}
		return result;
	}

	/**
	 * convert bytes to short.
	 * 
	 * @param bytes
	 *            bytes to convert
	 * @return short given bytes as an int
	 */
	public static short toShort(byte[] bytes)
	{
		return (short) (((-(short) Byte.MIN_VALUE + bytes[0]) << BYTE_SHIFT) - Byte.MIN_VALUE + bytes[1]);
	}

	/**
	 * convert int to bytes.
	 * 
	 * @param value
	 *            int to convert to bytes
	 * @return byte[] int as bytes
	 */
	public static byte[] toBytes(int value)
	{
		int workValue = value;
		byte[] result = new byte[BYTE_LENGTH];
		for (int i = (BYTE_LENGTH - 1); i >= 0; i--)
		{
			result[i] = (byte) ((AND_OP & workValue) + Byte.MIN_VALUE);
			workValue >>>= BYTE_SHIFT;
		}
		return result;
	}

	/**
	 * convert short to bytes.
	 * 
	 * @param value
	 *            short to convert
	 * @return byte[] short as bytes
	 */
	public static byte[] toBytes(short value)
	{
		short workValue = value;
		byte[] result = new byte[2];
		for (int i = 1; i >= 0; i--)
		{
			result[i] = (byte) ((AND_OP & workValue) + Byte.MIN_VALUE);
			workValue >>>= BYTE_SHIFT;
		}
		return result;
	}

}
