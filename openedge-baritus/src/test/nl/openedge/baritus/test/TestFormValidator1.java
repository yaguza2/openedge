/*
 * $Id: TestFormValidator1.java,v 1.1 2004-04-09 09:47:35 eelco12 Exp $
 * $Revision: 1.1 $
 * $Date: 2004-04-09 09:47:35 $
 *
 * ====================================================================
 * Copyright (c) 2003
 * All rights reserved.
 */
package nl.openedge.baritus.test;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.validation.AbstractFormValidator;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * @author hillenius
 */
public class TestFormValidator1 extends AbstractFormValidator
{

	/**
	 * @param cctx
	 * @param formBeanContext
	 * @return
	 * @see nl.openedge.baritus.validation.FormValidator#isValid(org.infohazard.maverick.flow.ControllerContext, nl.openedge.baritus.FormBeanContext)
	 */
	public boolean isValid(
		ControllerContext cctx,
		FormBeanContext formBeanContext)
	{
		boolean valid = true;
		TestBean bean = (TestBean)formBeanContext.getBean();
		
		// test runtime exception
		if(bean.getToValidate4().equals("kill"))
		{
			throw new RuntimeException("big mistake");
		}
		
		valid = bean.getToValidate4().equals("validValue");
		if(!valid)
		{
			formBeanContext.setError("toValidate4", "wrong input");
		}
		
		return valid;
	}

}
