/*
 * $Id: ToUpperCasePopulator.java,v 1.1.1.1 2004-02-24 20:34:19 eelco12 Exp $
 * $Revision: 1.1.1.1 $
 * $Date: 2004-02-24 20:34:19 $
 *
 * ====================================================================
 * Copyright (c) 2003
 * All rights reserved.
 */
package nl.openedge.baritus.test;

import java.util.Locale;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.population.AbstractFieldPopulator;
import nl.openedge.baritus.population.TargetPropertyMeta;

import org.apache.commons.beanutils.ConvertUtils;
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
		Object requestValue,
		TargetPropertyMeta targetPropertyMeta,
		Locale locale)
		throws Exception
	{

		if(requestValue != null)
		{
			String val = ConvertUtils.convert(requestValue);
			val = val.toUpperCase();
			
			setTargetProperty(targetPropertyMeta, formBeanContext.getBean(), val);
		}

		return true;
	}

}
