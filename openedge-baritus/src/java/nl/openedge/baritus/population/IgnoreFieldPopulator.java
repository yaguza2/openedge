/*
 * $Id: IgnoreFieldPopulator.java,v 1.2 2004-03-29 15:26:53 eelco12 Exp $
 * $Revision: 1.2 $
 * $Date: 2004-03-29 15:26:53 $
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
package nl.openedge.baritus.population;

import java.util.Locale;

import nl.openedge.baritus.FormBeanContext;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * Ignores the population of fields.
 * Register IgnoreFieldPopulators if you want to ignore the population of certain properties,
 * for instance id's of persistent objects.
 * @author Eelco Hillenius
 */
public final class IgnoreFieldPopulator implements FieldPopulator
{

	private boolean fail;
	
	/**
	 * construct.
	 * fail is false
	 */
	public IgnoreFieldPopulator()
	{
		setFail(false);
	}
	
	/**
	 * construct with parameter fail
	 * @param fail If fail == true, setProperty will allways return false,
	 *  and thus the population process is flagged as failed, if fail == false,
	 * 	setProperty will allways return true, and thus has no effect on the total population.
	 */
	public IgnoreFieldPopulator(boolean fail)
	{
		setFail(fail);
	}

	/**
	 * Does nothing at all.
	 * Register IgnoreFieldPopulators if you want to ignore the population of certain properties,
 	 * for instance id's of persistent objects.
	 * 
	 * @param cctx maverick context
	 * @param form instance of the form to set the property on
	 * @param name name of the property
	 * @param requestValue the value from the request. This is either a String or a String array (String[])
	 * @param targetPropertyMeta an extra wrapper for the target
	 * @param locale
	 * @return boolean value of property fail. By default fail == false, which means that this method
	 * 		allways returns true. If you set fail to true, this method will allways return false,
	 * 		and thus the population process is flagged as failed.
	 * @throws Exception never
	 * 
	 * @see nl.openedge.baritus.population.FieldPopulator#setProperty(org.infohazard.maverick.flow.ControllerContext, nl.openedge.baritus.FormBeanContext, java.lang.String, java.lang.Object, nl.openedge.baritus.population.TargetPropertyMeta, java.util.Locale)
	 */
	public boolean setProperty(
		ControllerContext cctx,
		FormBeanContext form,
		String name,
		Object requestValue,
		TargetPropertyMeta targetPropertyMeta,
		Locale locale)
		throws Exception
	{
		return (!fail);
	}

	/**
	 * get the value of property fail
	 * @return boolean value of property fail. By default fail == false, which means that this method
	 * 		allways returns true. If you set fail to true, this method will allways return false,
	 * 		and thus the population process is flagged as failed.
	 */
	public boolean isFail()
	{
		return fail;
	}

	/**
	 * set the value of property fail
	 * @param b value of property fail. By default fail == false, which means that this method
	 * 		allways returns true. If you set fail to true, this method will allways return false,
	 * 		and thus the population process is flagged as failed.
	 */
	public void setFail(boolean b)
	{
		fail = b;
	}

}
