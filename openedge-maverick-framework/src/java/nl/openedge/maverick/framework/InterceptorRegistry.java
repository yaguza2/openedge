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
 
package nl.openedge.maverick.framework;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.openedge.maverick.framework.interceptors.Interceptor;

/**
 * Registry for interceptors. Each instance of FormBeanCtrl has its own instance.
 * 
 * @author Eelco Hillenius
 */
final class InterceptorRegistry
{

	private List interceptors = null;
	
	private List[] flowInterceptors;
	

	/**
	 * add an interceptor to the current list of interceptors
	 * @param interceptor the interceptor to add to the current list of interceptors
	 */
	public void addInterceptor(Interceptor interceptor)
	{
		if(interceptors == null)
		{
			interceptors = new ArrayList();
		}
		interceptors.add(interceptor);
	}
	
	/**
	 * add an interceptor to the current list of interceptors at the specified position
	 * @param index index position where to insert the interceptor
	 * @param interceptor the interceptor to add to the current list of interceptors
	 */
	public void addInterceptor(int index, Interceptor interceptor)
	{
		if(interceptors == null)
		{
			interceptors = new ArrayList();
		}
		interceptors.add(index, interceptor);
	}

	
	/**
	 * remove an interceptor from the current list of interceptors
	 * @param interceptor the interceptor to remove from the current list of interceptors
	 */
	public void removeInterceptor(Interceptor interceptor)
	{
		if(interceptors != null)
		{
			interceptors.remove(interceptor);
			if(interceptors.isEmpty()) interceptors = null;
		}
	}
	
	/**
	 * get all registered interceptors of the provided type 
	 * @param type the type
	 * @return array of Interceptors or null if none
	 */
	public Interceptor[] getInterceptors(Class type)
	{
		Interceptor[] result = null;
		if(interceptors != null && (!interceptors.isEmpty()))
		{
			List temp = new ArrayList();
			for(Iterator i = interceptors.listIterator(); i.hasNext(); )
			{
				Interceptor intc = (Interceptor)i.next();
				if(type.isAssignableFrom(intc.getClass()))
				{
					temp.add(intc);
				}
			}
			if(!temp.isEmpty())
			{
				result = (Interceptor[])temp.toArray(new Interceptor[temp.size()]);
			}
		}
		return result;
	}
	
	/**
	 * get the flow interceptors for the provided interceptionPoint
	 * @param interceptionPoint the interception point
	 * @return List flow interceptors or null if none were registered
	 */
	public List getInterceptorsAtPoint(int interceptionPoint)
	{
		return (flowInterceptors != null) ?
			flowInterceptors[interceptionPoint] : null;
	}
	
	/* check the interception point */
	private void checkInterceptionPoint(int interceptionPoint)
	{
		if(interceptionPoint < 0 || interceptionPoint > 5)
		{
			throw new IllegalArgumentException(
				"interceptionPoint must be the value of one " +
				"of the public variables of FlowInterceptor");
		}		
	}
	
	/* get or create the list of flow interceptors at the given point */
	private List getOrCreateInterceptorsAtPoint(int interceptionPoint)
	{
		List interceptorsAtPoint = null;
		if(flowInterceptors == null)
		{
			flowInterceptors = new List[5];
			interceptorsAtPoint = new ArrayList(1);
			flowInterceptors[interceptionPoint] = interceptorsAtPoint;
		}
		else
		{
			interceptorsAtPoint = flowInterceptors[interceptionPoint];
			if(interceptorsAtPoint == null)
			{
				interceptorsAtPoint = new ArrayList(1);
				flowInterceptors[interceptionPoint] = interceptorsAtPoint;			
			}
		}
		return interceptorsAtPoint;
	}

}
