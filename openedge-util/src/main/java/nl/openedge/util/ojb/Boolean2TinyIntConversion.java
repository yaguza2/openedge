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
package nl.openedge.util.ojb;

import org.apache.ojb.broker.accesslayer.conversions.ConversionException;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

/**
 * @author Sander Hofstee
 * 
 * Converteerd een Java Boolean naar een SQL TinyInt en andersom.
 * als het die input van het verkeerde type is wordt een ConversionException
 * gegooid.
 */
public class Boolean2TinyIntConversion implements FieldConversion 
{
	/* De SQL uitkomsten. Dit zijn Java Bytes, maar worden TinyInt's. */
	private static Byte  S_TRUE  = new Byte("1");
	private static Byte  S_FALSE = new Byte("0");

	/* De Java uitkomsten */
	private static Boolean J_TRUE  = new Boolean(true);
	private static Boolean J_FALSE = new Boolean(false);

	/**
	 * Verwacht een Boolean input en geeft een Byte (TinyInt in OJB) terug.
	 */
	public Object javaToSql(Object source)
	{
		Object result = null;
		if (source instanceof Boolean)
		{
			if (source.equals(J_TRUE))
			{
				result = S_TRUE;
			}
			else
			{
				result = S_FALSE;
			}
		}
		else 
		{
			throw new ConversionException("Source is not a Boolean");
		}
		return result;
	}
	
	/**
	 * Verwacht een Byte (TinyInt in OJB) en geeft een Boolean terug.
	 */
	public Object sqlToJava(Object source)
	{
		Object result = null;
		if (source instanceof Byte)
		{
			if (source.equals(S_TRUE))
			{
				result = J_TRUE;
			}
			else
			{
				result = J_FALSE;
			}
		}
		else 
		{
			throw new ConversionException("Source is not a Byte");
		}
		
		return result;
	}

}
