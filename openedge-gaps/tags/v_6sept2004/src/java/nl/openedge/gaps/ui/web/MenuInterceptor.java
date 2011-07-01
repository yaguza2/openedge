/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) 2004, Levob Bank en Verzekeringen Alle rechten voorbehouden.
 */
package nl.openedge.gaps.ui.web;

import java.util.List;

import javax.servlet.ServletException;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.interceptors.AfterPerformInterceptor;
import nl.openedge.baritus.interceptors.AfterPopulationInterceptor;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * Interceptor voor menu afhandeling.
 */
public final class MenuInterceptor implements AfterPopulationInterceptor,
		AfterPerformInterceptor
{

	/**
	 * De naam van usermenu attribuut.
	 */
	private String attribKeyMenu;

	/**
	 * @see nl.openedge.baritus.interceptors.AfterPopulationInterceptor#doAfterPopulation(org.infohazard.maverick.flow.ControllerContext,
	 *      nl.openedge.baritus.FormBeanContext)
	 */
	public void doAfterPopulation(final ControllerContext cctx,
			final FormBeanContext formBeanContext) throws ServletException
	{

	}

	/**
	 * @see nl.openedge.baritus.interceptors.AfterPerformInterceptor#doAfterPerform(org.infohazard.maverick.flow.ControllerContext,
	 *      nl.openedge.baritus.FormBeanContext)
	 */
	public void doAfterPerform(final ControllerContext cctx,
			final FormBeanContext formBeanContext) throws ServletException
	{

		List[] menuItems = null;
		formBeanContext.put(attribKeyMenu, menuItems);
	}

	/**
	 * Get attribKeyMenu.
	 * @return the attribKeyMenu.
	 */
	public String getAttribKeyMenu()
	{
		return attribKeyMenu;
	}

	/**
	 * Set attribKeyMenu.
	 * @param newAttribKeyMenu attribKeyMenu to set.
	 */
	public void setAttribKeyMenu(final String newAttribKeyMenu)
	{
		this.attribKeyMenu = newAttribKeyMenu;
	}

}