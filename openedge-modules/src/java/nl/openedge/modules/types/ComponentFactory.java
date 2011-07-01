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
package nl.openedge.modules.types;

import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.types.initcommands.InitCommand;

import org.jdom.Element;

/**
 * A component factory knows how to construct components of a certain type.
 * 
 * @author Eelco Hillenius
 */
public interface ComponentFactory
{
	/**
	 * construct with class.
	 * 
	 * @param componentClass class of component
	 * @throws ConfigException when an configuration error occurs
	 */

	/**
	 * gets the name from config.
	 * 
	 * @return String
	 */
	String getName();

	/**
	 * sets the name from config.
	 * 
	 * @param name alias for this instance
	 */
	void setName(String name);

	/**
	 * get class of the component.
	 * 
	 * @return Class of component
	 */
	Class getComponentClass();

	/**
	 * construct with class.
	 * 
	 * @param componentClass class of component
	 * @throws ConfigException when an configuration error occurs
	 */
	void setComponentClass(Class componentClass) throws ConfigException;

	/**
	 * get component factory.
	 * 
	 * @return ComponentRepository
	 */
	ComponentRepository getComponentRepository();

	/**
	 * set component factory.
	 * 
	 * @param factory component factory
	 */
	void setComponentRepository(ComponentRepository factory);

	/**
	 * get init commands.
	 * 
	 * @return InitCommand[] array of init commands
	 */
	InitCommand[] getInitCommands();

	/**
	 * set init commands.
	 * @param commands array of init commands
	 */
	void setInitCommands(InitCommand[] commands);

	/**
	 * get instantiated component.
	 * 
	 * @return Object instance
	 */
	Object getComponent();

	/**
	 * set config node.
	 * 
	 * @param componentNode config node
	 * @throws ConfigException when an configuration error occurs
	 */
	void setComponentNode(Element componentNode) throws ConfigException;

}