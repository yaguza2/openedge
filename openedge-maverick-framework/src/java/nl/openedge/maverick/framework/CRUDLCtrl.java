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

import javax.servlet.ServletException;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * @author Eelco Hillenius
 * base control for default Create Read Update Delete List handling
 * Use with CRUDLForm
 */
public abstract class CRUDLCtrl extends AbstractCtrl
{

	/**
	 * perform CRUDL action
	 * @see org.infohazard.maverick.ctl.CommonCtrlBean#perform()
	 */
	protected final String perform(Object formBean, ControllerContext cctx) 
		throws Exception
	{
		CRUDLForm form = (CRUDLForm)formBean;
		String action = form.getAction();
		if(action == null)
		{
			throw new ServletException("no action specified");
		}
		action = action.toLowerCase();
		
		if("list".equals(action))
		{
			performList(form, cctx);
		}
		else if("read".equals(action))
		{
			performRead(form, cctx);
		}
		else if("update".equals(action))
		{
			performUpdate(form, cctx);
		}
		else if("delete".equals(action))
		{
			performDelete(form, cctx);
		}
		else if("create".equals(action))
		{
			performCreate(form, cctx);
		} 
		else
		{
			throw new ServletException("invallid action '" + action + "' specified"); 		
		}

		return SUCCESS;
	}
	
	protected abstract String performCreate(Object formBean, ControllerContext cctx);
	
	protected abstract String performRead(Object formBean, ControllerContext cctx);
	
	protected abstract String performUpdate(Object formBean, ControllerContext cctx);
	
	protected abstract String performDelete(Object formBean, ControllerContext cctx);
	
	protected abstract String performList(Object formBean, ControllerContext cctx);

}
