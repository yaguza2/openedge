/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003
 * All rights reserved.
 */
package nl.openedge.maverick.framework.interceptors;

import java.util.Locale;

import nl.openedge.maverick.framework.FormBeanContext;
import nl.openedge.maverick.framework.population.AbstractFieldPopulator;
import nl.openedge.maverick.framework.population.TargetPropertyMeta;

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
	 * @see nl.openedge.maverick.framework.population.FieldPopulator#setProperty(org.infohazard.maverick.flow.ControllerContext, nl.openedge.maverick.framework.FormBeanContext, java.lang.String, java.lang.Object, nl.openedge.maverick.framework.population.TargetPropertyMeta, java.util.Locale)
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
