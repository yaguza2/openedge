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
package nl.openedge.access.util;

import java.security.*;

/**
 * Helper class for password handling
 * based on Tagisch JAAS example (http://free.tagish.net/jaas/doc.html)
 * @author Andy Armstrong
 * @author Eelco Hillenius
 */
public final class PasswordHelper {
	
	private final static String ALGORITHM   = "MD5";
	private static MessageDigest md = null;


	/**
	 * Turn a byte array into a char array containing a printable
	 * hex representation of the bytes. Each byte in the source array
	 * contributes a pair of hex digits to the output array.
	 *
	 * @param src the source array
	 * @return a char array containing a printable version of the source
	 * data
	 */
	private static char[] hexDump(byte src[]) {
		
		char buf[] = new char[src.length * 2];
		for (int b = 0; b < src.length; b++) {
			String byt = Integer.toHexString((int) src[b] & 0xFF);
			if (byt.length() < 2) {
				buf[b * 2 + 0] = '0';
				buf[b * 2 + 1] = byt.charAt(0);
			} else {
				buf[b * 2 + 0] = byt.charAt(0);
				buf[b * 2 + 1] = byt.charAt(1);
			}
		}
		return buf;
	}

	/**
	 * Zero the contents of the specified array. Typically used to
	 * erase temporary storage that has held plaintext passwords
	 * so that we don't leave them lying around in memory.
	 *
	 * @param pwd the array to zero
	 */
	public static void smudge(char pwd[]) {
		
		if (null != pwd) {
			for (int b = 0; b < pwd.length; b++) {
				pwd[b] = 0;
			}
		}
	}

	/**
	 * Zero the contents of the specified array.
	 *
	 * @param pwd the array to zero
	 */
	public static void smudge(byte pwd[]) {
		
		if (null != pwd) {
			for (int b = 0; b < pwd.length; b++) {
				pwd[b] = 0;
			}
		}
	}

	/**
	 * Perform MD5 hashing on the supplied password and return a char array
	 * containing the encrypted password as a printable string. The hash is
	 * computed on the low 8 bits of each character.
	 *
	 * @param pwd The password to encrypt
	 * @return a character array containing a 32 character long hex encoded
	 * MD5 hash of the password
	 */
	public static char[] cryptPassword(char pwd[]) throws Exception {
		
		if (null == md) { md = MessageDigest.getInstance(ALGORITHM); }
		md.reset();
		byte pwdb[] = new byte[pwd.length];
		for (int b = 0; b < pwd.length; b++) {
			pwdb[b] = (byte) pwd[b];
		}
		char crypt[] = hexDump(md.digest(pwdb));
		smudge(pwdb);
		return crypt;
	}
}
