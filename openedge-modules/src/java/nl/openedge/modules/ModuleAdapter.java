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

import java.util.Map;

import org.jdom.Element;

/**
 * common base for module wrappers
 * @author Eelco Hillenius
 */
abstract class ModuleAdapter {

	/** modulefactory for two way navigation */
	protected ModuleFactory moduleFactory = null;

	/** class of module */
	protected Class moduleClass = null;
	
	/** name (alias) of module */
	protected String name = null;
	
	/** 
	 * if the module wants to have the possiblity to configure from the
	 * configuration file, this is it's node
	 */
	protected Element configNode = null;

	/**
	 * if the module is a bean, store a map of (string) properties
	 * for later use
	 */
	protected Map properties = null;

	/**
	 * set instance of moduleFactory
	 * @param moduleFactory
	 */
	public void setModuleFactory(ModuleFactory moduleFactory) {
		this.moduleFactory = moduleFactory;
	}

	/**
	 * construct with class
	 * @param moduleClass	class of module
	 */
	protected void setModuleClass(Class moduleClass) throws ConfigException {
		// test first
		Object instance = null;
		try {	
			instance = moduleClass.newInstance();
		} catch (InstantiationException ex) {		
			throw new ConfigException(ex);
		} catch (IllegalAccessException ex) {	
			throw new ConfigException(ex);
		}
		// class is ok so far
		// test configuration as well
		if(instance instanceof Configurable) {
			((Configurable)instance).init(configNode);		
		}
		// all's ok
		this.moduleClass = moduleClass;
	}
	
	/**
	 * sets the name from config
	 * @param name	alias for this instance
	 */
	protected void setName(String name) {
		this.name = name;
	}
	
	/**
	 * gets the name from config
	 * @return String
	 */
	public final String getName() {
		return name;
	}
	
	/**
	 * set configuration node of this module instance
	 * @param configNode XML (JDOM) node
	 */
	public final void setConfigNode(Element configNode) {
		this.configNode = configNode;
	}

	/** get instantiated module */
	public abstract Object getModule() throws ModuleException;

	/**
	 * @return Class of module
	 */
	public Class getModuleClass() {
		return moduleClass;
	}

	/**
	 * @return Map
	 */
	public Map getProperties() {
		return properties;
	}

	/**
	 * @param properties
	 */
	public void setProperties(Map properties) {
		this.properties = properties;
	}

}
