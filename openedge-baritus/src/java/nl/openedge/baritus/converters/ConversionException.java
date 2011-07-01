/*
 * $Id: ConversionException.java,v 1.3 2004-04-04 18:27:45 eelco12 Exp $
 * $Revision: 1.3 $
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
 * Modified ConversionException that saves the desired pattern as an extra field
 * 
 * @author Eelco Hillenius
 */
public final class ConversionException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	private String desiredPattern;

	/**
	 * Construct exception with message.
	 * 
	 * @param message
	 *            message
	 */
	public ConversionException(String message)
	{
		super(message);
	}

	/**
	 * Construct exception with message and desired pattern.
	 * 
	 * @param message
	 *            message
	 * @param desiredPattern
	 *            the desired pattern
	 */
	public ConversionException(String message, String desiredPattern)
	{
		super(message);
		setDesiredPattern(desiredPattern);
	}

	/**
	 * Construct exception with message and cause.
	 * 
	 * @param message
	 *            message
	 * @param cause
	 *            cause
	 */
	public ConversionException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Construct exception with message, cause and desired pattern.
	 * 
	 * @param message
	 *            message
	 * @param cause
	 *            cause
	 * @param desiredPattern
	 *            the desired pattern
	 */
	public ConversionException(String message, Throwable cause, String desiredPattern)
	{
		super(message, cause);
		setDesiredPattern(desiredPattern);
	}

	/**
	 * Construct exception with cause.
	 * 
	 * @param cause
	 *            cause
	 */
	public ConversionException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Construct exception with cause and desired pattern.
	 * 
	 * @param cause
	 *            cause
	 * @param desiredPattern
	 *            the desired pattern
	 */
	public ConversionException(Throwable cause, String desiredPattern)
	{
		super(cause);
		setDesiredPattern(desiredPattern);
	}

	/**
	 * Get the desired pattern.
	 * 
	 * @return String optionally the desired pattern
	 */
	public String getDesiredPattern()
	{
		return desiredPattern;
	}

	/**
	 * Set the desired pattern.
	 * 
	 * @param string
	 *            the desired pattern
	 */
	public void setDesiredPattern(String string)
	{
		desiredPattern = string;
	}

	/**
	 * String representation of the exception.
	 * 
	 * @return String string representation
	 */
	@Override
	public String toString()
	{
		String s = getClass().getName();
		String message = getLocalizedMessage();
		message = (message != null) ? (s + ": " + message) : s;

		if (getDesiredPattern() != null)
		{
			message += "; pattern should be: " + getDesiredPattern();
		}

		return message;
	}
}
