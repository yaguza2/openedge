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

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import nl.openedge.modules.ComponentLookupException;
import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.observers.ComponentObserver;
import nl.openedge.modules.observers.ComponentsLoadedEvent;
import nl.openedge.modules.types.AbstractComponentFactory;
import nl.openedge.modules.types.initcommands.InitCommandException;

/**
 * wrapper for remote components
 * @author Eelco Hillenius
 */
public final class RemoteTypeFactory extends AbstractComponentFactory
	implements ComponentObserver
{

	/** the remote instance */
	protected Remote remoteInstance;
	protected boolean register = false;
	protected boolean registeringDone = false;
	protected String rmiName = null;

	private boolean executeInitCommands = true;
	private static Log log = LogFactory.getLog(RemoteTypeFactory.class);

	/**
	 * get instance of module
	 * @return remote instance
	 * @see nl.openedge.components.AbstractComponentFactory#getModule()
	 */
	public Object getComponent() throws ComponentLookupException
	{
		synchronized(this)
		{
			if(this.remoteInstance == null)
			{
				
				try
				{
					remoteInstance = (Remote)componentClass.newInstance();
					
					if(register && (!registeringDone))
					{
						Naming.rebind(name, remoteInstance);
						registeringDone = true;
					}
				}
				
				catch (InstantiationException e)
				{
					throw new ComponentLookupException(e);
				}
				catch (IllegalAccessException e)
				{
					throw new ComponentLookupException(e);
				}
				catch (RemoteException e)
				{
					throw new ComponentLookupException(e);
				}
				catch (MalformedURLException e)
				{
					throw new ComponentLookupException(e);
				}

			}
			
			if(executeInitCommands)
			{
				executeInitCommands = false;
			
				try
				{
					executeInitCommands(remoteInstance);	
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
			
			try
			{
				executeRequestLevelInitCommands(remoteInstance);	
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
		
		return remoteInstance;
	}
	
	/**
	 * set config node
	 * @param componentNode config node
	 * @throws ConfigException
	 */
	public void setComponentNode(Element componentNode) 
		throws ConfigException
	{
		super.setComponentNode(componentNode);
		rmiName = componentNode.getAttributeValue("rmiName");
		if(name == null)
		{
			throw new ConfigException(
				"components of type RemoteType must have attribute rmiName defined");
		}
		String reg = componentNode.getAttributeValue("register");
		if("true".equalsIgnoreCase(reg))
		{
			register = true;
		}
	}
	
	/**
	 * set component factory
	 * @param componentRepository component repository
	 */
	public void setComponentRepository(ComponentRepository componentRepository)
	{
		this.componentRepository = componentRepository;
		componentRepository.addObserver(this);
		
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}
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
