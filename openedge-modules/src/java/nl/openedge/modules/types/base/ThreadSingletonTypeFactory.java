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
package nl.openedge.modules.types.base;

import nl.openedge.modules.ComponentLookupException;
import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.observers.ComponentObserver;
import nl.openedge.modules.observers.ComponentsLoadedEvent;
import nl.openedge.modules.types.AbstractComponentFactory;
import nl.openedge.modules.types.initcommands.InitCommandException;

/**
 * wrapper for singleton components per Thread
 * @author Eelco Hillenius
 */
public final class ThreadSingletonTypeFactory extends AbstractComponentFactory
	implements ComponentObserver
{
	
	protected ThreadLocal singletonInstanceHolder = new ThreadLocal();

	private boolean executeInitCommands = true;

	/**
	 * get instance of module
	 * @return new instance for each request
	 * @see nl.openedge.components.AbstractComponentFactory#getModule()
	 */
	public Object getComponent() throws ComponentLookupException
	{
		Object singletonInstance = singletonInstanceHolder.get();
		
		synchronized(this)
		{
			if(singletonInstance == null)
			{
				try
				{
					singletonInstance = componentClass.newInstance();	
					singletonInstanceHolder.set(singletonInstance);
				}
				catch (InstantiationException e)
				{
					throw new ComponentLookupException(e);
				}
				catch (IllegalAccessException e)
				{
					throw new ComponentLookupException(e);
				}
				
				if(executeInitCommands)
				{
					try
					{
						executeInitCommands(singletonInstance);	
					}
					catch (InitCommandException e)
					{
						e.printStackTrace();
						throw new ComponentLookupException(e);
					}
					catch (ConfigException e)
					{
						e.printStackTrace();
						throw new ComponentLookupException(e);
					}
				}
			}
			
			try
			{
				executeRequestLevelInitCommands(singletonInstance);	
			}
			catch (InitCommandException e)
			{
				e.printStackTrace();
				throw new ComponentLookupException(e);
			}
			catch (ConfigException e)
			{
				e.printStackTrace();
				throw new ComponentLookupException(e);
			}
			
		}
		
		return singletonInstance;
	}

	/**
	 * set component factory
	 * @param componentRepository component repository
	 */
	public void setComponentRepository(ComponentRepository componentRepository)
	{
		this.componentRepository = componentRepository;
		componentRepository.addObserver(this);
	}
	
	/**
	 * fired after all components are (re)loaded; 
	 * @param evt event
	 */
	public void modulesLoaded(ComponentsLoadedEvent evt)
	{
		//noop
	}


}
