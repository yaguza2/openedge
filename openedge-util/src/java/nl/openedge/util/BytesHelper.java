package nl.openedge.util;

/**
 * Helper class for bytes
 * @author unknown... copied from Hibernate
 */
public final class BytesHelper {
	
	/**
	 * convert bytes to int
	 * @param bytes
	 * @return int
	 */
	public static int toInt( byte[] bytes ) {
		int result = 0;
		for (int i=0; i<4; i++) {
			result = ( result << 8 ) - Byte.MIN_VALUE + (int) bytes[i];
		}
		return result;
	}
	
	/**
	 * convert bytes to short
	 * @param bytes
	 * @return short
	 */
	public static short toShort( byte[] bytes ) {
		return (short) ( ( ( - (short) Byte.MIN_VALUE + (short) bytes[0] ) << 8  ) - (short) Byte.MIN_VALUE + (short) bytes[1] );
	}
	
	/**
	 * convert int to bytes
	 * @param value
	 * @return byte[]
	 */
	public static byte[] toBytes(int value) {
		byte[] result = new byte[4];
		for (int i=3; i>=0; i--) {
			result[i] = (byte) ( ( 0xFFl & value ) + Byte.MIN_VALUE );
			value >>>= 8;
		}
		return result;
	}
	
	/**
	 * convert short to bytes
	 * @param value
	 * @return byte[]
	 */
	public static byte[] toBytes(short value) {
		byte[] result = new byte[2];
		for (int i=1; i>=0; i--) {
			result[i] = (byte) ( ( 0xFFl & value )  + Byte.MIN_VALUE );
			value >>>= 8;
		}
		return result;
	}
	
}
