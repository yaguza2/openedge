/*
 * $Id: TestFieldValidator.java,v 1.1 2004-04-01 09:20:35 eelco12 Exp $
 * $Revision: 1.1 $
 * $Date: 2004-04-01 09:20:35 $
 *
 * ====================================================================
 * Copyright (c) 2003
 * All rights reserved.
 */
package nl.openedge.baritus.test;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.validation.AbstractFieldValidator;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * @author hillenius
 */
public class TestFieldValidator extends AbstractFieldValidator
{

	/**
	 * @param cctx
	 * @param formBeanContext
	 * @param fieldName
	 * @param value
	 * @return boolean
	 * @see nl.openedge.baritus.validation.FieldValidator#isValid(org.infohazard.maverick.flow.ControllerContext, nl.openedge.baritus.FormBeanContext, java.lang.String, java.lang.Object)
	 */
	public boolean isValid(
		ControllerContext cctx,
		FormBeanContext formBeanContext,
		String fieldName,
		Object value)
	{
		boolean valid = false;
		
		String testVal = (String)value;
		valid = "validValue".equals(testVal);
		
		return valid;

	}

}
