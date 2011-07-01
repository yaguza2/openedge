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
package nl.openedge.modules.types.initcommands;

import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.config.ConfigException;

import org.jdom.Element;

/**
 * do some additional processing (like configurating) with types at initialization time. An
 * initCommand is executed exactely once for each instance.
 * 
 * @author Eelco Hillenius
 */
public interface InitCommand
{

	/**
	 * initialize the command.
	 * 
	 * @param componentName name of component
	 * @param componentNode xml config node of component
	 * @param componentRepository instance of componentRepository
	 * @throws ConfigException when an configuration error occurs
	 */
	void init(String componentName, Element componentNode,
			ComponentRepository componentRepository) throws ConfigException;

	/**
	 * execute the command.
	 * 
	 * @param componentInstance instance of the component
	 * @throws InitCommandException when init command failed
	 * @throws ConfigException when an configuration error occurs
	 */
	void execute(Object componentInstance) throws InitCommandException,
			ConfigException;
}