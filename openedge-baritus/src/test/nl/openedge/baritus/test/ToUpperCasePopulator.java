/*
 * $Id: ToUpperCasePopulator.java,v 1.2 2004-04-04 18:24:08 eelco12 Exp $
 * $Revision: 1.2 $
 * $Date: 2004-04-04 18:24:08 $
 *
 * ====================================================================
 * Copyright (c) 2003
 * All rights reserved.
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
	 * @see nl.openedge.baritus.population.FieldPopulator#setProperty(org.infohazard.maverick.flow.ControllerContext, nl.openedge.baritus.FormBeanContext, java.lang.String, java.lang.Object, nl.openedge.baritus.population.TargetPropertyMeta, java.util.Locale)
	 */
	public boolean setProperty(
		ControllerContext cctx,
		FormBeanContext formBeanContext,
		String name,
		Object value)
		throws Exception
	{

		if(value != null)
		{
			String val = ValueUtils.convertToString(value);
			val = val.toUpperCase();
			
			Ognl.setValue(name, formBeanContext.getBean(), val);
		}

		return true;
	}

}
