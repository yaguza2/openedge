/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003
 * All rights reserved.
 */
package nl.openedge.maverick.framework.population;

import java.util.Locale;

import nl.openedge.maverick.framework.AbstractForm;

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
	 * @param value the value from the request. This is either a String or a String array (String[])
	 * @param targetPropertyMeta an extra wrapper for the target
	 * @param locale
	 * @return boolean value of property fail. By default fail == false, which means that this method
	 * 		allways returns true. If you set fail to true, this method will allways return false,
	 * 		and thus the population process is flagged as failed.
	 * @throws Exception never
	 * 
	 * @see nl.openedge.maverick.framework.population.FieldPopulator#setProperty(org.infohazard.maverick.flow.ControllerContext, nl.openedge.maverick.framework.AbstractForm, java.lang.String, java.lang.Object, nl.openedge.maverick.framework.population.TargetPropertyMeta, java.util.Locale)
	 */
	public boolean setProperty(
		ControllerContext cctx,
		AbstractForm form,
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
