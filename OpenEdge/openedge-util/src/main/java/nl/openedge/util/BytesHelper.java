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
