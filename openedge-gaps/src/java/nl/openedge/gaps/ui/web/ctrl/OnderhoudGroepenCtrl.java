/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) 2004, Levob Bank en Verzekeringen Alle rechten voorbehouden.
 */
package nl.openedge.gaps.ui.web.ctrl;

import nl.openedge.gaps.ui.web.AbstractBaseCtrl;
import nl.openedge.baritus.FormBeanContext;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * Dummy control zodat interceptors etc worden afgevuurd.
 */
public class OnderhoudGroepenCtrl extends AbstractBaseCtrl
{

	/**
	 * Doet niets.
	 * @param formBeanContext baritus context
	 * @param cctx maverick context
	 * @return String altijd success
	 * @throws java.lang.Exception
	 * @see nl.openedge.baritus.FormBeanCtrlBase#perform(nl.openedge.baritus.FormBeanContext,
	 *      org.infohazard.maverick.flow.ControllerContext)
	 */
	protected String perform(final FormBeanContext formBeanContext,
			final ControllerContext cctx)
	{

		return SUCCESS;
	}

	/**
	 * @param formBeanContext baritus context
	 * @param cctx maverick context
	 * @return Object plain object; zetten properties niet mogelijk
	 * @see nl.openedge.baritus.FormBeanCtrlBase#makeFormBean(nl.openedge.baritus.FormBeanContext,
	 *      org.infohazard.maverick.flow.ControllerContext)
	 */
	protected Object makeFormBean(final FormBeanContext formBeanContext,
			final ControllerContext cctx)
	{

		return new Object();
	}

}