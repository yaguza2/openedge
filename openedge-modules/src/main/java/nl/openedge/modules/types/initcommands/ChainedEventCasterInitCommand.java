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

import java.lang.reflect.Method;

import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.observers.ChainedEventCaster;
import nl.openedge.modules.observers.ChainedEventObserver;
import nl.openedge.modules.observers.ComponentObserver;
import nl.openedge.modules.observers.ComponentsLoadedEvent;

import org.jdom.Element;

/**
 * Command that populates instances using BeanUtils.
 * 
 * @author Eelco Hillenius
 */
public class ChainedEventCasterInitCommand implements InitCommand, ComponentObserver
{
	private ComponentRepository componentRepository = null;

	private boolean executeInitCommands = true;

	@Override
	public void init(String componentName, Element componentNode, ComponentRepository cRepo)
	{
		this.componentRepository = cRepo;
	}

	@Override
	public void execute(Object componentInstance) throws ConfigException
	{
		if (executeInitCommands)
		{
			executeInitCommands = false;

			if (componentInstance instanceof ChainedEventCaster)
			{
				((ChainedEventCaster) componentInstance).addObserver(this.componentRepository);
			}
			else
			{
				Class< ? > clazz = componentInstance.getClass();
				try
				{
					Method initMethod =
						clazz.getMethod("addObserver", new Class[] {ChainedEventObserver.class});
					initMethod.invoke(componentInstance, new Object[] {this.componentRepository});
				}
				catch (Exception e)
				{
					throw new ConfigException(e);
				}
			}
		}
	}

	@Override
	public void modulesLoaded(ComponentsLoadedEvent evt)
	{
	}
}
