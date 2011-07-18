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
package nl.openedge.modules.impl.menumodule;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class for menu filters.
 * 
 * @author Maurice Marrink
 */
public abstract class AbstractMenuFilter implements MenuFilter
{

	/** attributes of this filter. */
	private HashMap attributes = new HashMap(2);

	/**
	 * Construct.
	 */
	public AbstractMenuFilter()
	{
		// nothing here
	}

	/**
	 * @see nl.openedge.modules.impl.menumodule.MenuFilter#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String name)
	{
		return attributes.get(name);
	}

	/**
	 * @see nl.openedge.modules.impl.menumodule.MenuFilter#getAttributes()
	 */
	@Override
	public Map getAttributes()
	{
		return attributes;
	}

	/**
	 * @see nl.openedge.modules.impl.menumodule.MenuFilter#putAttribute(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void putAttribute(String name, Object value)
	{
		attributes.put(name, value);
	}

	/**
	 * @see nl.openedge.modules.impl.menumodule.MenuFilter#putAllAttributes(java.util.Map)
	 */
	@Override
	public void putAllAttributes(Map newAttributes)
	{
		this.attributes.putAll(newAttributes);
	}

	/**
	 * @see nl.openedge.modules.impl.menumodule.AttributeEnabledObject#removeAttribute(java.lang.String)
	 */
	@Override
	public void removeAttribute(String name)
	{
		attributes.remove(name);
	}

}
