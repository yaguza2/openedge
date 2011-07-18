/*
 * $Id: InterceptorRegistry.java,v 1.3 2004-03-29 15:26:52 eelco12 Exp $
 * $Revision: 1.3 $
 * $Date: 2004-03-29 15:26:52 $
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
 
package nl.openedge.baritus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.openedge.baritus.interceptors.Interceptor;

/**
 * Registry for interceptors. Each instance of FormBeanBase has its own instance.
 * 
 * @author Eelco Hillenius
 */
final class InterceptorRegistry
{

	private List<Interceptor> interceptors = null;
	
	private List[] flowInterceptors;
	

	/**
	 * add an interceptor to the current list of interceptors
	 * @param interceptor the interceptor to add to the current list of interceptors
	 */
	public void addInterceptor(Interceptor interceptor)
	{
		if(interceptors == null)
		{
			interceptors = new ArrayList<Interceptor>();
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
			interceptors = new ArrayList<Interceptor>();
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
			List<Interceptor> temp = new ArrayList<Interceptor>();
			for(Iterator<Interceptor> i = interceptors.listIterator(); i.hasNext(); )
			{
				Interceptor intc = i.next();
				if(type.isAssignableFrom(intc.getClass()))
				{
					temp.add(intc);
				}
			}
			if(!temp.isEmpty())
			{
				result = temp.toArray(new Interceptor[temp.size()]);
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

}
