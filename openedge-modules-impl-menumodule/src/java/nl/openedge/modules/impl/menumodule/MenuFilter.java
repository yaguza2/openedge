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
 * A menu filter can be used to filter menu items from the menu tree
 * @author Eelco Hillenius
 */
public interface MenuFilter extends AttributeEnabledObject
{
	/** special key to store a JAAS subject with in the context */
	public final static String CONTEXT_KEY_SUBJECT = "subject";
	public final static String CONTEXT_KEY_REQUEST_FILTERS = "request_filters";
	public final static String CONTEXT_KEY_SESSION_FILTERS = "session_filters";
	
	/**
	 * should the provided menu item, based on the given context, be a part of the result tree
	 * @param menuItem the current menu item. This is a reference to the item that will
	 * 		be part of the result tree if accepted. You can change attributes/ properties
	 * 		if this item for later use without affecting the original tree.
	 * @param context the current context. This context is unique for this thread, but is 
	 * 		global within this thread.
	 * @return boolean true if the item should be part of the result tree, false if not
	 */
	public boolean accept(MenuItem menuItem, Map context);
}
