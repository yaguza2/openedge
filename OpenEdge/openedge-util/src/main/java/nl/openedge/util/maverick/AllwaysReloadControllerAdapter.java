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
package nl.openedge.util.maverick;

import javax.servlet.ServletException;

import org.infohazard.maverick.flow.ConfigException;
import org.infohazard.maverick.flow.Controller;
import org.infohazard.maverick.flow.ControllerContext;
import org.infohazard.maverick.flow.ControllerSingleton;
import org.jdom.Element;

/**
 * This adapter masquerades as a singleton controller but actually creates single-use instance
 * controllers AND initializes the controllers if they are of type ControllerSingleton.
 * 
 * @author Eelco Hillenius
 */
public final class AllwaysReloadControllerAdapter implements ControllerSingleton
{
	/** class of controller. */
	private Class controllerClass;

	/** reference to the xml node of the controller. */
	private Element controllerNode;

	/**
	 * Create the adapter.
	 * 
	 * @param controllerClass
	 *            the controller class
	 */
	public AllwaysReloadControllerAdapter(Class controllerClass)
	{
		this.controllerClass = controllerClass;
	}

	/**
	 * Save the reference to the controller node so we can use it later on.
	 * 
	 * @see org.infohazard.maverick.flow.ControllerSingleton#init(org.jdom.Element)
	 */
	public void init(Element configControllerNode) throws ConfigException
	{
		this.controllerNode = configControllerNode;
	}

	/**
	 * Instantiates a single-use controller, executes it, and returns the result. If the controller
	 * is of type ControllerSingleton, it is initialized with the saved reference of the xml node of
	 * the controller first.
	 * 
	 * @param cctx
	 *            the controller context.
	 * @return String logical view name (result of command method call of controller)
	 * @throws ServletException when the decorated controller threw a
	 * 		servlet exception
	 */
	public String go(ControllerContext cctx) throws ServletException
	{
		try
		{
			Controller instance = (Controller) this.controllerClass.newInstance();
			if (instance instanceof ControllerSingleton)
			{
				((ControllerSingleton) instance).init(controllerNode);
			}
			return instance.go(cctx);
		}
		catch (InstantiationException ex)
		{
			throw new ServletException(ex);
		}
		catch (IllegalAccessException ex)
		{
			throw new ServletException(ex);
		}
	}
}

