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
package nl.openedge.modules;

import nl.openedge.util.config.*;

import org.apache.commons.beanutils.BeanUtils;

/**
 * wrapper for throw away modules
 * @author Eelco Hillenius
 */
class ThrowAwayAdapter extends ModuleAdapter {
	
	/**
	 * get instance of module
	 * @return new instance for each request
	 * @see nl.openedge.modules.ModuleAdapter#getModule()
	 */
	public Object getModule() throws ModuleException {	
		
		Object instance = null;
		try {	
			instance = moduleClass.newInstance();
		} catch (InstantiationException ex) {		
			throw new ModuleException(ex);
		} catch (IllegalAccessException ex) {	
			throw new ModuleException(ex);
		}
		if(instance instanceof BeanModule) {
			// try to set its properties
			try {
				BeanUtils.populate(instance, this.properties);
			} catch(Exception e) {
				throw new ModuleException(e);	
			}
		}
		// do we have to configure?
		if(instance instanceof Configurable) {
			try {
				((Configurable)instance).init(configNode);		
			} catch(ConfigException e) {
				throw new ModuleException(e);
			}
		}
		// register as CriticalEventCaster?
		if(instance instanceof CriticalEventCaster) {
			((CriticalEventCaster)instance).addObserver(this.moduleFactory);
		}
		
		return instance;
	}
}
