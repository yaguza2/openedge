/*
 * $Header$
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
 * 
 * Helper class for easy replacement of substrings
 * 
 * @author	Eelco Hillenius
 */

public final class ScanReplaceClass
{

	/**
	 * Replace scanValue in scanString with replaceString
	 * @param scanString
	 * @param scanValue
	 * @param replaceString
	 * @return String
	 */
	public static String scanReplaceString(
		String scanString, String scanValue, String replaceString)
	{
		String returnString = scanString;
		int strIndex = returnString.indexOf(scanValue);
		StringBuffer helper = new StringBuffer();
		while (strIndex != -1)
		{
			helper.append(returnString.substring(0, strIndex))
					.append(replaceString).append(
				returnString.substring(strIndex + scanValue.length()));
			returnString = helper.toString();
			helper.delete(0, helper.length());
			if (strIndex == -1)
				break; // otherwise the String would be searched again from the start
			strIndex = returnString.indexOf(
				scanValue, strIndex + replaceString.length());
		}
		return returnString;
	}

}
