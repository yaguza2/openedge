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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.config.ConfigException;

/**
 * Command that populates instances using BeanUtils
 * @author Eelco Hillenius
 */
public class DependentTypeInitCommand implements InitCommand
{
	
	private ComponentRepository moduleFactory = null;
	
	private List namedDependencies = null;
	
	private String componentName;
	

	/**
	 * initialize
	 * @see nl.openedge.components.types.decorators.InitCommand#init(java.lang.String, org.jdom.Element, nl.openedge.components.ComponentRepository)
	 */
	public void init(
		String componentName, 
		Element componentNode,
		ComponentRepository moduleFactory)
		throws ConfigException
	{
		
		this.moduleFactory = moduleFactory;
		this.componentName = componentName;
		loadDependencies(componentNode);
	}
	
	/**
	 * load dependencies
	 * @param componentNode configuration node
	 */
	protected void loadDependencies(Element componentNode)
	{
		List namedDeps = componentNode.getChildren("dependency");
		namedDependencies = new ArrayList(namedDeps.size());
		
		for(Iterator i = namedDeps.iterator(); i.hasNext(); )
		{
			
			Element node = (Element)i.next();
			String moduleName = node.getAttributeValue("componentName");
			String propertyName = node.getAttributeValue("propertyName");
			
			namedDependencies.add(
				new NamedDependency(moduleName, propertyName));
		}
			
	}

	/**
	 * create decorator that tries to solve the dependencies when all components
	 * are loaded
	 * @see nl.openedge.components.types.decorators.InitCommand#execute(java.lang.Object)
	 */
	public void execute(Object componentInstance) 
		throws InitCommandException, ConfigException, CyclicDependencyException
	{

		if(componentInstance instanceof DependentType)
		{
			// create decorator with instance
			DependentTypeWrapper solver = new DependentTypeWrapper();
			solver.setComponentName(this.componentName);
			solver.setComponentInstance((DependentType)componentInstance);
			solver.setNamedDependencies(this.namedDependencies);
			solver.setModuleFactory(this.moduleFactory);

			solver.execute(componentInstance);

		}
		else
		{
			throw new InitCommandException(
			"component is not of type " + DependentType.class.getName());	
		}
		
	}

}
