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

import java.util.Map;

/**
 * common interface for all things accepting attributes.
 * 
 * @author Maurice Marrink
 */
public interface AttributeEnabledObject
{
	/**
	 * Get attribute.
	 * @param name the name of attribute
	 * @return the value of the attribute or null if it does not exist
	 */
	public Object getAttribute(String name);
	/**
	 * Get attributes.
	 * @return the (possibly empty) Map with all the attributes
	 */
	public Map getAttributes();
	/**
	 * Registers a new Attribute with this filter, overriding any attribute 
	 * already registered under that name.
	 * 
	 * @param name
	 * @param value
	 */
	public void putAttribute(String name, Object value);
	/**
	 * Registers all attributes in the Map, overriding any existing attribute with
	 * the same name.
	 * 
	 * @param attributes
	 */
	public void putAllAttributes(Map attributes);
	/**
	 * Removes the attribute with the specified name if it exists, 
	 * if it doesnt exist nothing happens.
	 * 
	 * @param name the name of the attribute
	 */
	public void removeAttribute(String name);
}