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

import java.io.Serializable;
import java.util.List;

import javax.servlet.ServletContext;

import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.observers.ChainedEventObserver;
import nl.openedge.modules.observers.ComponentRepositoryObserver;


import org.jdom.Element;
import org.quartz.Scheduler;

/**
 * The ComponentRepository functions as the repository for components.
 * This is the main class that clients have to deal with. The common
 * pattern is to get an instance of the ComponentRepository, and
 * then get the component by name/ alias. E.g:
 * &lt;code&gt; 
 * 	ComponentRepository rep = RepositoryFactory.getRepository();
 *	rModule = (MyComponent)rep.getComponent("myAlias");
 * &lt;/code&gt;
 * 
 * The repository also is the the point to get the Quartz scheduler
 * from, and where members of the <code>ComponentRepositoryObserver</code>
 * family can register as observers.
 * 
 * @author Eelco Hillenius
 */
public interface ComponentRepository 
	extends ChainedEventObserver, 
			Serializable
{
	
	/**
	 * initialize the component repository
	 * @param rootNode
	 * @param servletContext
	 * @throws ConfigException
	 */
	public void start(
			Element rootNode, 
			ServletContext servletContext) 
			throws ConfigException;

	/**
	 * add observer of component repository events
	 * @param observer
	 */
	public void addObserver(ComponentRepositoryObserver observer);

	/**
	 * remove observer of component repository events
	 * @param observer
	 */
	public void removeObserver(ComponentRepositoryObserver observer);

	/**
	 * returns instance of component
	 * can throw ComponentLookupException (runtime exception) if a loading or 
	 * initialisation error occured or when no component was found stored 
	 * under the given name
	 * @param name the name (alias) of component
	 * @return Object component instance
	 */
	public Object getComponent(String name);
	
	/**
	 * get all components that are instance of the given type
	 * @param type the class
	 * @param exact If true, only exact matches will be returned. If 
	 * false, superclasses and interfaces will be taken into account
	 * @return List list of components. Never null, possibly empty
	 */
	public List getComponentsByType(Class type, boolean exact);
	
	/**
	 * returns all known names
	 * @return String[] names
	 */
	public String[] getComponentNames();

	/**
	 * get the quartz sceduler
	 * @return Scheduler
	 */
	public Scheduler getScheduler();
	
	/**
	 * get the servlet context this component repository was
	 * possibly started with
	 * @return ServletContext the servlet context this repository
	 * 		was started with, or null if this repository was not started
	 * 		within a servlet environment (or does not know about it)
	 */
	public ServletContext getServletContext();
}