/*
 * $Id$
 */
package nl.openedge.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The base class for ID generators that use a UUID algorithm. This class implements the algorithm,
 * subclasses define the ID format.
 * 
 * @see UUIDHexGenerator
 * @see UUIDStringGenerator
 * @author Gavin King
 */

public abstract class UUIDGenerator
{
	/** ip adress. */
	private static final int IP_ADDRESS;

	/** counter. */
	private static short counter = (short) 0;

	/** really usefull, it's for keeping checkstyle from wheeping. */
	private static final int JVM_SHIFT = 8;

	/** really usefull, it's for keeping checkstyle from wheeping. */
	private static final int HIGH_TIME_SHIFT = 32;

	/** JVM_TIME time. */
	private static final int JVM_TIME = (int) (System.currentTimeMillis() >>> JVM_SHIFT);

	/** log. */
	private static Logger log = LoggerFactory.getLogger(UUIDGenerator.class);

	static
	{
		int ipadd;

		try
		{
			ipadd = BytesHelper.toInt(InetAddress.getLocalHost().getAddress());
		}
		catch (UnknownHostException e)
		{
			log.error(e.getMessage(), e);
			ipadd = 0;
		}

		IP_ADDRESS = ipadd;
	}

	/**
	 * construct.
	 */
	public UUIDGenerator()
	{
		// nothing here
	}

	/**
	 * Unique across JVMs on this machine (unless they load this class in the same
	 * quater second - very unlikely).
	 * 
	 * @return int jvm time
	 */
	protected int getJVM()
	{
		return JVM_TIME;
	}

	/**
	 * Unique in a millisecond for this JVM instance
	 * (unless there are > Short.MAX_VALUE instances
	 * created in a millisecond).
	 * 
	 * @return short count
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
	 * Unique in a local network.
	 * 
	 * @return int ip address
	 */
	protected int getIP()
	{
		return IP_ADDRESS;
	}

	/**
	 * Get hi time (unique down to millisecond).
	 * 
	 * @return short hi time
	 */
	protected short getHiTime()
	{
		return (short) (System.currentTimeMillis() >>> HIGH_TIME_SHIFT);
	}

	/**
	 * Get lo time.
	 * @return int lo time
	 */
	protected int getLoTime()
	{
		return (int) System.currentTimeMillis();
	}
}
