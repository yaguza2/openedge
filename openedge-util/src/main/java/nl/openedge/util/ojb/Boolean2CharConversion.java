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
 * Converts a Boolean in Java to a Char in SQL and vice versa.
 * OJB converts a SQL char to string so we use a String for the char.
 * The valid values for the string are either '0' or '1'.
 */
public class Boolean2CharConversion implements FieldConversion
{
	private static String  S_TRUE  = new String("1");
    private static String  S_FALSE = new String("0");

    private static Boolean B_TRUE  = new Boolean(true);
    private static Boolean B_FALSE = new Boolean(false);

    /*
     * Converts a Java boolean to a SQL char.
     * @see FieldConversion#javaToSql(Object)
     * @param source the object to be converted to SQL char.
     * @throw ConversionException if the source was not a Boolean.
     */
    public Object javaToSql(Object source)
    {
    	String result = S_FALSE;
        if (source instanceof Boolean)
        {
            if (source.equals(B_TRUE))
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
            throw new ConversionException("Unable to convert Boolean to char");
        }
        return result;
    }

    /*
     * Converts a SQL char to a Java boolean. The
     * char has to be 0 for false or 1 for true.
     * @see FieldConversion#sqlToJava(Object)
     * @param source the char to be converted to a Boolean
     * @throw ConversionException thrown when the source is not a valid string.
     */
    public Object sqlToJava(Object source)
    {
        Boolean result = B_FALSE;
        
        if (source instanceof String)
        {
        	String stringSource = ((String)source).trim();
            if (stringSource.equals(S_TRUE))
            {
                result = B_TRUE;
            }
            else
            {
                result = B_FALSE;
            }
        }
        else
        {
            throw new ConversionException("Unable to convert char to Boolean");
        }
        
        return result;
    }

}