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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infohazard.maverick.flow.ControllerContext;

/**
 * An abstract <strong>Control</strong> that dispatches to a public
 * method that is named in the form
 * @author Eelco Hillenius; loosely based on Strut's DispatchAction
 */

public abstract class DispatchCtrl extends AbstractCtrl 
{

	private static Log log = LogFactory.getLog(DispatchCtrl.class);

	/** Method cache */
	protected Map methods = new HashMap();

	/** types for method calls */
	protected Class types[] = { Object.class, ControllerContext.class };


	/**
	 * perform dispached action
	 * @see org.infohazard.maverick.ctl.CommonCtrlBean#perform()
	 */
	protected final String perform(Object formBean, ControllerContext cctx) 
		throws Exception
	{
		DispatchForm form = (DispatchForm)formBean;
		String view = ERROR;
		String methodName = form.getMethod();
		if(methodName == null)
		{
			throw new ServletException("no method was specified");
		}
		
		try
		{
			Method method = getMethod(methodName);
			Object args[] = { formBean, cctx };
			view = (String)method.invoke(this, args);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			throw e;
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
			throw new ServletException("method " + methodName + " is not a valid method");
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			throw e;
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
			throw e;
		}

		return view;
	}

	/**
	 * get method with name
	 * @param name method name
	 * @exception NoSuchMethodException if no such method can be found
	 */
	protected Method getMethod(String name)
		throws NoSuchMethodException 
	{

		synchronized (methods) 
		{
			Method method = (Method) methods.get(name);
			if (method == null) 
			{
				method = this.getClass().getMethod(name, types);
				methods.put(name, method);
			}
			return (method);
		}
	}

}

