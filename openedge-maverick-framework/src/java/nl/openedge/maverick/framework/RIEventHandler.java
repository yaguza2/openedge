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
package nl.openedge.maverick.framework;

import java.util.Map;


import org.apache.commons.beanutils.ConvertUtils;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.Runtime;
import org.apache.velocity.runtime.RuntimeConstants;

/**
 * @author Eelco Hillenius
 */
public class RIEventHandler implements ReferenceInsertionEventHandler
{
	private AbstractForm model;
	private Context context;
	
	/**
	 * construct with model
	 * @param model current model
	 * @param context velocity context
	 */
	public RIEventHandler(
		AbstractForm model, Context context)
	{
		this.model = model;
		this.context = context;
	}

	/**
	 * A call-back which is executed during Velocity merge before a
	 * reference value is inserted into the output stream.
	 * 
	 * Intercept override values if any are set
	 *
	 * @param reference Reference from template about to be inserted.
	 * @param value Value about to be inserted (after its
	 * <code>toString()</code> method is called).
	 * @return Object on which <code>toString()</code> should be
	 * called for output.
	 */
	public Object referenceInsert( String reference, Object value  ) 
	{
		
		Map overrideFields = model.getOverrideFields();
		if( overrideFields != null ) 
		{
			int endChar = reference.length();
			if(endChar > 0) 
			{
				int startChar = 1;
				if(reference.charAt(1) == '!') 
				{
					startChar = 2;
				}
				if(reference.charAt(startChar) == '{') 
				{
					startChar++;
					endChar--;
				}
				
				String name = reference.substring(startChar, endChar);
				
				// if we're inside a loop, try indexed value
				String counterName = Runtime.getString(RuntimeConstants.COUNTER_NAME);
				Integer counter = (Integer)context.get(counterName);
				if(counter != null)
				{
					name = name + '|' + (counter.intValue() - 1);
				}
				
				Object storedRawValue = overrideFields.get(name);
				String storedValue = null;
				
				if(storedRawValue != null)
				{
					storedValue = ConvertUtils.convert(storedRawValue);	
				}
				if(storedValue != null) 
				{
					value = storedValue;
				}
			}

		}
		return value;
	}

}
