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
package nl.openedge.modules.test;

import org.jdom.Element;

import nl.openedge.modules.Configurable;
import nl.openedge.modules.SingletonModule;
import nl.openedge.util.config.ConfigException;

/**
 * @author Eelco Hillenius
 */
public class ConfigurableModuleImpl implements SingletonModule, Configurable {

	public ConfigurableModuleImpl() {
		System.out.println(getClass().getName() + ": created");
	}
	
	public void init(Element configNode) throws ConfigException {
		System.out.println(getClass().getName() + ": initialised with " + configNode);	
		Element p1 = configNode.getChild("param1");
		if(p1 == null) throw new ConfigException("where's param1?");
		String attr = p1.getAttributeValue("attr");
		if(attr == null) throw new ConfigException("where's param1['attr']?");
		Element p2 = configNode.getChild("param2");
		if(p2 == null) throw new ConfigException("where's param2?");
		String val = p2.getTextNormalize();
		if(val == null || (!val.equals("Bar"))) 
				throw new ConfigException("value of param2 should be Bar!");
	}

}
