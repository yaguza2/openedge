package nl.openedge.util;

import java.io.Serializable;

/**
 * <b>uuid.hex </b> <br>
 * <br>
 * A <tt>UUIDGenerator</tt> that returns a string of length 32, This string will consist
 * of only hex digits. Optionally, the string may be generated with seperators between
 * each component of the UUID.
 * 
 * @author Gavin King
 */
public class UUIDHexGenerator extends UUIDGenerator
{

	/** seperator. */
	private String sep;

	/**
	 * construct using the given seperator.
	 * 
	 * @param seperator
	 *            seperator
	 */
	public UUIDHexGenerator(String seperator)
	{
		this.sep = seperator;
	}

	/**
	 * construct using an empty string for the seperator.
	 */
	public UUIDHexGenerator()
	{
		this.sep = "";
	}

	/**
	 * format value.
	 * 
	 * @param intval
	 *            intvalue to format
	 * @return String formatted intval
	 */
	protected String format(int intval)
	{
		String formatted = Integer.toHexString(intval);
		StringBuffer buf = new StringBuffer("00000000");
		buf.replace(8 - formatted.length(), 8, formatted);
		return buf.toString();
	}

	/**
	 * format value
	 * 
	 * @param shortval
	 * @return String
	 */
	protected String format(short shortval)
	{
		String formatted = Integer.toHexString(shortval);
		StringBuffer buf = new StringBuffer("0000");
		buf.replace(4 - formatted.length(), 4, formatted);
		return buf.toString();
	}

	/**
	 * generate uuid
	 * 
	 * @return Serializable
	 */
	public Serializable generate()
	{
		return new StringBuffer(36).append(format(getIP())).append(sep).append(format(getJVM()))
			.append(sep).append(format(getHiTime())).append(sep).append(format(getLoTime()))
			.append(sep).append(format(getCount())).toString();
	}
}
