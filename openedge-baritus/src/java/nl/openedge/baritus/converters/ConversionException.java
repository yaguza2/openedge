/*
 * $Id: ConversionException.java,v 1.1.1.1 2004-02-24 20:34:07 eelco12 Exp $
 * $Revision: 1.1.1.1 $
 * $Date: 2004-02-24 20:34:07 $
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
 * Modified ConversionException that saves the desired pattern as an extra field
 * @author Eelco Hillenius
 */
public final class ConversionException
	extends org.apache.commons.beanutils.ConversionException
{

	private String desiredPattern;

	/**
	 * @param message
	 */
	public ConversionException(String message)
	{
		super(message);
	}
	
	/**
	 * @param message
	 * @param string the desired pattern
	 */
	public ConversionException(String message, String desiredPattern)
	{
		super(message);
		setDesiredPattern(desiredPattern);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ConversionException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	/**
	 * @param message
	 * @param cause
	 * @param string the desired pattern
	 */
	public ConversionException(String message, Throwable cause, String desiredPattern)
	{
		super(message, cause);
		setDesiredPattern(desiredPattern);
	}

	/**
	 * @param cause
	 */
	public ConversionException(Throwable cause)
	{
		super(cause);
	}
	
	/**
	 * @param cause
	 * @param string the desired pattern
	 */
	public ConversionException(Throwable cause, String desiredPattern)
	{
		super(cause);
		setDesiredPattern(desiredPattern);
	}

	/**
	 * get the desired pattern
	 * @return String optionally the desired pattern
	 */
	public String getDesiredPattern()
	{
		return desiredPattern;
	}

	/**
	 * set the desired pattern
	 * @param string the desired pattern
	 */
	public void setDesiredPattern(String string)
	{
		desiredPattern = string;
	}

	/**
	 * string rep
	 * @return String string rep
	 */
	public String toString() 
	{
		String s = getClass().getName();
		String message = getLocalizedMessage();
		message = (message != null) ? (s + ": " + message) : s;
		
		if(getDesiredPattern() != null)
		{
			message += "; pattern should be: " + getDesiredPattern();
		}
		
		return message;
	}

}
