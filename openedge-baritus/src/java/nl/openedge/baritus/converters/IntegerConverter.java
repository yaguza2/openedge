/*
 * $Id: IntegerConverter.java,v 1.2 2004-04-04 18:27:45 eelco12 Exp $
 * $Revision: 1.2 $
 * $Date: 2004-04-04 18:27:45 $
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
package nl.openedge.baritus.converters;

/**
 * <p>{@link Converter} implementation that converts an incoming
 * String into a <code>java.lang.Integer</code> object, 
 * throwing a {@link ConversionException} if a conversion
 * error occurs.</p>
 *
 * @author Eelco Hillenius
 */

public final class IntegerConverter implements Converter 
{

    /**
     * Create a {@link Converter} that will throw a {@link ConversionException}
     * if a conversion error occurs.
     */
    public IntegerConverter() 
    {

    }

    /**
     * Convert the specified input object into an output object of the
     * specified type.
     *
     * @param type Data type to which this value should be converted
     * @param value The input value to be converted
     *
     * @exception ConversionException if conversion cannot be performed
     *  successfully
     */
    public Object convert(Class type, Object value) 
    {

        if (value == null) 
        {
            return null;
        }

        if (value instanceof Integer) 
        {
            return (value);
        } 
        else if(value instanceof Number) 
        {
            return new Integer(((Number)value).intValue());
        }

        try 
        {
            return (new Integer(value.toString()));
        } 
        catch (Exception e) 
        {
			throw new ConversionException(e);
        }

    }


}
