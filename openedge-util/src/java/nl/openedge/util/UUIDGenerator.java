/*
 * $Id$
 */
package nl.openedge.util;

import java.net.InetAddress;

/**
 * The base class for ID generators that use a UUID algorithm. This
 * class implements the algorithm, subclasses define the ID format.
 * 
 * @see UUIDHexGenerator
 * @see UUIDStringGenerator
 * @author Gavin King
 */

public abstract class UUIDGenerator
{

	private static final int ip;
	private static short counter = (short)0;
	private static final int jvm = (int) (System.currentTimeMillis() >>> 8);

	static {
		int ipadd;
		try
		{
			ipadd = BytesHelper.toInt(InetAddress.getLocalHost().getAddress());
		}
		catch (Exception e)
		{
			ipadd = 0;
		}
		ip = ipadd;
	}

	/**
	 * construct
	 */
	public UUIDGenerator()
	{
		// nothing here
	}

	/**
	 * Unique across JVMs on this machine (unless they load this class
	 * in the same quater second - very unlikely)
	 * @return int
	 */
	protected int getJVM()
	{
		return jvm;
	}

	/**
	 * Unique in a millisecond for this JVM instance (unless there
	 * are > Short.MAX_VALUE instances created in a millisecond)
	 * @return short
	 */
	protected short getCount()
	{
		synchronized (UUIDGenerator.class)
		{
			if (counter < 0)
				counter = 0;
			return counter++;
		}
	}

	/**
	 * Unique in a local network
	 * @return int
	 */
	protected int getIP()
	{
		return ip;
	}

	/**
	 * Unique down to millisecond
	 * @return short
	 */
	protected short getHiTime()
	{
		return (short) (System.currentTimeMillis() >>> 32);
	}
	/**
	 * @return int
	 */
	protected int getLoTime()
	{
		return (int)System.currentTimeMillis();
	}
}
