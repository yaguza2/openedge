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
package nl.openedge.maverick.framework.util;

import java.util.Collection;
import java.util.Map;

/**
 * Misc utility methods for handling values
 * 
 * @author Eelco Hillenius
 */
public final class ValueUtils
{

	/**
	 * check if the value is null or empty
	 * @param value object to check on
	 * @return true if value is not null AND not empty (e.g. 
	 * in case of a String or Collection)
	 */
	public static boolean isNullOrEmpty(Object value)
	{
		if(value instanceof String)
		{
			return (value == null || (((String)value).trim().equals("")));
		}
		if(value instanceof Object[])
		{
			if(value == null)
			{
				return true;
			}
			else if(((Object[])value).length == 0)
			{
				return true;
			}
			else if(((Object[])value).length == 1)
			{
				return isNullOrEmpty(((Object[])value)[0]);
			}
			else
			{
				return false;
			}
		}
		else if(value instanceof Collection)
		{
			return (value == null || (((Collection)value).isEmpty()));
		}
		else if(value instanceof Map)
		{
			return (value == null || (((Map)value).isEmpty()));
		}
		else
		{
			return (value == null);
		}
	}

}
