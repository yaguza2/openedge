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
package nl.openedge.modules;

import javax.servlet.ServletContext;

import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.observers.CriticalEventObserver;
import nl.openedge.modules.observers.ModuleFactoryObserver;


import org.jdom.Element;
import org.quartz.Scheduler;

/**
 * The ModuleFactory constructs and initialises objects.
 * 
 * @author Eelco Hillenius
 */
public interface ModuleFactory extends CriticalEventObserver
{
	
	/**
	 * initialize the module factory
	 * @param factoryNode
	 * @param servletContext
	 * @throws ConfigException
	 */
	public void init(
			Element factoryNode, 
			ServletContext servletContext) 
			throws ConfigException;

	/**
	 * add observer of module factory events
	 * @param observer
	 */
	public abstract void addObserver(ModuleFactoryObserver observer);

	/**
	 * remove observer of module factory events
	 * @param observer
	 */
	public abstract void removeObserver(ModuleFactoryObserver observer);

	/**
	 * returns instance of module
	 * can throw ModuleLookpupException (runtime exception) if a loading or 
	 * initialisation error occured or when no module was found stored 
	 * under the given name
	 * @param name the name (alias) of module
	 * @return Object module instance
	 */
	public abstract Object getModule(String name);

	/**
	 * get the quartz sceduler
	 * @return Scheduler
	 */
	public abstract Scheduler getScheduler();
}