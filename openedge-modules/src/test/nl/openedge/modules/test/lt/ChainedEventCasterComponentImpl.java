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
package nl.openedge.modules.test.lt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.openedge.modules.observers.ChainedEventObserver;
import nl.openedge.modules.observers.ChainedExceptionEvent;

/**
 * @author Eelco Hillenius
 */
public class ChainedEventCasterComponentImpl
{

	// observers
	private List observers = new ArrayList();

	/**
	 * construct
	 */
	public ChainedEventCasterComponentImpl()
	{
		System.out.println(getClass().getName() + ": created");
	}

	/**
	 * @see nl.openedge.components.ChainedEventCaster#addObserver(nl.openedge.components.ChainedEventObserver)
	 */
	public void addObserver(ChainedEventObserver observer)
	{
		observers.add(observer);
	}

	/**
	 * test method; this method will fire a critical event
	 */
	public void doFoo()
	{
		fireCriticalEvent();
	}

	/**
	 * fire event
	 */
	protected void fireCriticalEvent()
	{
		Exception e = new Exception("I am a critical event!");
		for (Iterator i = observers.iterator(); i.hasNext();)
		{

			ChainedEventObserver observer = (ChainedEventObserver)i.next();
			observer.recieveChainedEvent(new ChainedExceptionEvent(this, e));
		}
	}

}
