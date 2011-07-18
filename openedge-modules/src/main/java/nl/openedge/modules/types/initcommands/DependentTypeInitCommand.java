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

import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.config.ConfigException;

import org.jdom.Element;

/**
 * Command that populates instances using BeanUtils.
 * 
 * @author Eelco Hillenius
 */
public final class DependentTypeInitCommand implements RequestLevelInitCommand
{
	/** component repository. */
	private ComponentRepository componentRepository = null;

	/** logical dependencies. */
	private List namedDependencies = null;

	/** current component name. */
	private String componentName;

	/**
	 * @see nl.openedge.components.types.decorators.InitCommand#init(java.lang.String,
	 *      org.jdom.Element, nl.openedge.components.ComponentRepository)
	 */
	@Override
	public void init(String cName, Element cNode, ComponentRepository cRepo) throws ConfigException
	{

		this.componentRepository = cRepo;
		this.componentName = cName;
		loadDependencies(cNode);
	}

	/**
	 * load dependencies.
	 * 
	 * @param componentNode
	 *            configuration node
	 */
	protected void loadDependencies(Element componentNode)
	{
		List namedDeps = componentNode.getChildren("dependency");
		namedDependencies = new ArrayList(namedDeps.size());

		for (Iterator i = namedDeps.iterator(); i.hasNext();)
		{

			Element node = (Element) i.next();
			String moduleName = node.getAttributeValue("componentName");
			String propertyName = node.getAttributeValue("propertyName");

			namedDependencies.add(new NamedDependency(moduleName, propertyName));
		}

	}

	/**
	 * @see nl.openedge.components.types.decorators.InitCommand#execute(java.lang.Object)
	 */
	@Override
	public void execute(Object componentInstance) throws InitCommandException, ConfigException
	{

		DependentTypeWrapper solver = new DependentTypeWrapper();
		solver.setComponentName(this.componentName);
		solver.setComponentInstance(componentInstance);
		solver.setNamedDependencies(this.namedDependencies);
		solver.setModuleFactory(this.componentRepository);

		solver.execute(componentInstance);

	}

}
