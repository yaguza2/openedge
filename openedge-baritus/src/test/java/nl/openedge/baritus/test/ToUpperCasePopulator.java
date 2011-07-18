/*
 * $Id: ToUpperCasePopulator.java,v 1.3 2004-04-06 07:43:37 eelco12 Exp $
 * $Revision: 1.3 $
 * $Date: 2004-04-06 07:43:37 $
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

package nl.openedge.baritus.test;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.population.AbstractFieldPopulator;
import nl.openedge.baritus.util.ValueUtils;
import ognl.Ognl;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * simple test populator that converts input to a uppercase
 * 
 * @author Eelco Hillenius
 */
public class ToUpperCasePopulator extends AbstractFieldPopulator
{

	/**
	 * @see nl.openedge.baritus.population.FieldPopulator#setProperty(org.infohazard.maverick.flow.ControllerContext,
	 *      nl.openedge.baritus.FormBeanContext, java.lang.String, java.lang.Object,
	 *      nl.openedge.baritus.population.TargetPropertyMeta, java.util.Locale)
	 */
	@Override
	public boolean setProperty(ControllerContext cctx, FormBeanContext formBeanContext,
			String name, Object value) throws Exception
	{

		if (value != null)
		{
			String val = ValueUtils.convertToString(value);
			val = val.toUpperCase();

			Ognl.setValue(name, formBeanContext.getBean(), val);
		}

		return true;
	}

}
